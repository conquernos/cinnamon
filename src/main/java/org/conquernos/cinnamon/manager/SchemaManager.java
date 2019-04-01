package org.conquernos.cinnamon.manager;


import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.manager.control.schema.CachedSchemas;
import org.conquernos.cinnamon.manager.control.schema.SchemaRegistryControl;
import org.conquernos.cinnamon.manager.control.schema.Subject;
import org.conquernos.cinnamon.exception.schema.CinnamonSchemaException;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;
import org.conquernos.cinnamon.message.Message;
import org.conquernos.cinnamon.message.schema.SchemaMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.conquernos.cinnamon.utils.json.JsonUtils.*;


public class SchemaManager extends Manager {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final ActorRef master;

    private final SchemaRegistryControl schemaClient;
    private final CachedSchemas cachedSchemas;


    public SchemaManager(ActorRef master, MasterConfig config) throws CinnamonSchemaException {
        this.master = master;
        schemaClient = new SchemaRegistryControl(config.getKafkaSchemaServer());
        cachedSchemas = new CachedSchemas(schemaClient);

        logger.debug("SchemaManager({})", getSelf().path());
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        logger.debug("SchemaManager({}) onReceive - {}", getSelf(), message);

        if (message instanceof SchemaMessage.SchemaRegistryUrlRequest) {
            inquireSchemaRegistryUrls((SchemaMessage.SchemaRegistryUrlRequest) message, getSender());

        } else if (message instanceof SchemaMessage.SubjectsInquiryRequest) {
            inquireAllSubjects((SchemaMessage.SubjectsInquiryRequest) message, getSender());

        } else if (message instanceof SchemaMessage.SchemaInquiryRequest) {
            SchemaMessage.SchemaInquiryRequest request = (SchemaMessage.SchemaInquiryRequest) message;
            if (request.isAllVersion()) {
                inquireSchemaAllVersion(request, getSender());
            } else {
                inquireSchema((SchemaMessage.SchemaInquiryRequest) message, getSender());
            }

        } else if (message instanceof SchemaMessage.SchemaRegistrationRequest) {
            SchemaMessage.SchemaRegistrationRequest request = (SchemaMessage.SchemaRegistrationRequest) message;
            registerSchema(request, getSender());

        } else if (message instanceof SchemaMessage.SchemaUpdateRequest) {
            SchemaMessage.SchemaUpdateRequest request = (SchemaMessage.SchemaUpdateRequest) message;
            updateSchema(request, getSender());

        } else if (message instanceof Terminated) {
            logger.debug("TERMINATED : {}", ((Terminated) message).getActor().path());
        }

    }

    private void inquireSchemaRegistryUrls(SchemaMessage.SchemaRegistryUrlRequest request, ActorRef requester) {
        List<String> urls = new ArrayList<>();
        urls.add(schemaClient.getUrl());

        SchemaMessage.SchemaRegistryUrlResponse response = urls.size() > 0 ?
            new SchemaMessage.SchemaRegistryUrlResponse(Message.Result.SUCCESS, urls)
            : new SchemaMessage.SchemaRegistryUrlResponse(Message.Result.FAIL, null);

        requester.tell(response, master);
    }

    private void inquireAllSubjects(SchemaMessage.SubjectsInquiryRequest request, ActorRef requester) {
        Collection<String> subjects = cachedSchemas.getSubjects();

        SchemaMessage.SubjectsInquiryResponse response = subjects != null ?
            new SchemaMessage.SubjectsInquiryResponse(Message.Result.SUCCESS, subjects)
            : new SchemaMessage.SubjectsInquiryResponse(Message.Result.FAIL, null);

        requester.tell(response, master);
    }

    private void inquireSchema(SchemaMessage.SchemaInquiryRequest request, ActorRef requester) {
        Schema schema = cachedSchemas.getCachedSchema(new Subject(request.getSubject(), request.getVersion()));

        SchemaMessage.SchemaInquiryResponse response = schema != null ?
            new SchemaMessage.SchemaInquiryResponse(Message.Result.SUCCESS, toJsonNode(schema.toString()))
            : new SchemaMessage.SchemaInquiryResponse(Message.Result.FAIL, null);

        requester.tell(response, master);
    }

    private void inquireSchemaAllVersion(SchemaMessage.SchemaInquiryRequest request, ActorRef requester) {
        List<Schema> schemas = cachedSchemas.getCachedAllVersionSchema(request.getSubject());

        SchemaMessage.SchemaAllVersionInquiryResponse response;
        if (schemas == null) {
            response = new SchemaMessage.SchemaAllVersionInquiryResponse(Message.Result.FAIL, null);
        } else {
            List<JsonNode> schemaNodes = new ArrayList<>(schemas.size());

            for (Schema schema : schemas) {
                schemaNodes.add(toJsonNode(schema.toString()));
            }

            response = new SchemaMessage.SchemaAllVersionInquiryResponse(Message.Result.SUCCESS, schemaNodes);
        }

        requester.tell(response, master);
    }

    private synchronized void registerSchema(SchemaMessage.SchemaRegistrationRequest request, ActorRef requester) {
        SchemaMessage.SchemaRegistrationResponse response;
        try {
            if (schemaClient.getLatestVersion(request.getSubject()) == 0) {
                int id = schemaClient.register(request.getSubject(), toSchema(request.getSubject(), request.getFields()));
                cachedSchemas.updateCache();
                response = new SchemaMessage.SchemaRegistrationResponse(Message.Result.SUCCESS, id);
            } else {
                // if the topic exist
                response = new SchemaMessage.SchemaRegistrationResponse(Message.Result.FAIL, "pre-existing topic", 0);
            }
        } catch (CinnamonSchemaException e) {
            response = new SchemaMessage.SchemaRegistrationResponse(Message.Result.FAIL, "wrong schema : " + e.getMessage(), 0);
            e.printStackTrace();
        } catch (RestClientException e) {
            response = new SchemaMessage.SchemaRegistrationResponse(Message.Result.FAIL, "schema-registry fail", 0);
            e.printStackTrace();
        } catch (Exception e) {
            response = new SchemaMessage.SchemaRegistrationResponse(Message.Result.FAIL, "registration fail", 0);
            e.printStackTrace();
        }

        requester.tell(response, master);
    }

    private synchronized void updateSchema(SchemaMessage.SchemaUpdateRequest request, ActorRef requester) {
        SchemaMessage.SchemaUpdateResponse response;
        try {
            int beforeVersion = schemaClient.getLatestVersion(request.getSubject());
            if (beforeVersion > 0) {
                int id = schemaClient.register(request.getSubject(), toSchema(request.getSubject(), request.getFields()));
                int version = schemaClient.getLatestVersion(request.getSubject());
                if (version > beforeVersion) {
                    response = new SchemaMessage.SchemaUpdateResponse(Message.Result.SUCCESS, id, version);

                    cachedSchemas.updateCache();
                } else {
                    response = new SchemaMessage.SchemaUpdateResponse(Message.Result.FAIL, "same schema as before version", id, version);
                }
            } else {
                // if the topic not exist
                response = new SchemaMessage.SchemaUpdateResponse(Message.Result.FAIL, "not existing topic", 0, 0);
            }
        } catch (CinnamonSchemaException e) {
            response = new SchemaMessage.SchemaUpdateResponse(Message.Result.FAIL, "wrong schema", 0, 0);
            e.printStackTrace();
        } catch (RestClientException e) {
            response = new SchemaMessage.SchemaUpdateResponse(Message.Result.FAIL, "schema-registry fail", 0, 0);
            e.printStackTrace();
        } catch (IOException e) {
            response = new SchemaMessage.SchemaUpdateResponse(Message.Result.FAIL, "registration fail", 0, 0);
            e.printStackTrace();
        }

        requester.tell(response, master);
    }

    private void deleteSchema() {

    }

    private void deleteSchemaAllVersion() {

    }

    private static Schema toSchema(String name, ArrayNode fieldsNode) throws CinnamonSchemaException {
        try {
            List<Schema.Field> fields = new ArrayList<>(fieldsNode.size());
            for (Iterator<JsonNode> it = fieldsNode.elements(); it.hasNext(); ) {
                JsonNode field = it.next();
                fields.add(new Schema.Field(field.get("name").asText()
                    , Schema.create(Schema.Type.valueOf(field.get("type").asText().toUpperCase()))
                    , null, toCodeHaus(field.get("default"))));
            }


            Schema schema = Schema.createRecord(name, null, null, false);
            schema.setFields(fields);

            return schema;
        } catch (Exception e) {
            throw new CinnamonSchemaException(e.getMessage(), e.getCause());
        }
    }

}
