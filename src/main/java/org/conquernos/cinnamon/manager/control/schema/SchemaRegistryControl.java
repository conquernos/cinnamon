package org.conquernos.cinnamon.manager.control.schema;

import org.conquernos.cinnamon.exception.schema.CinnamonSchemaException;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SchemaRegistryControl {

    private final RestService restService;


    public SchemaRegistryControl(String baseUrl) {
        restService = new RestService(baseUrl);
    }

    public SchemaRegistryControl(List<String> baseUrls) {
        restService = new RestService(baseUrls);
    }

    public String getUrl() {
        return restService.getBaseUrls().current();
    }

    public int register(String subject, Schema schema) throws IOException, RestClientException {
        return restService.registerSchema(schema.toString(), subject);
    }

    public int register(String subject, String schema) throws IOException, RestClientException {
        return restService.registerSchema(schema, subject);
    }

    public Schema getByID(int id) throws IOException, RestClientException {
        SchemaString restSchema = restService.getId(id);
        return new Schema.Parser().parse(restSchema.getSchemaString());
    }

    public SchemaMetadata getLatestSchemaMetadata(String subject) throws CinnamonSchemaException {
        try {
            return toSchemaMetadata(restService.getLatestVersion(subject));
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
    }

    public SchemaMetadata getSchemaMetadata(String subject, int version) throws CinnamonSchemaException {
        try {
            return toSchemaMetadata(restService.getVersion(subject, version));
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
    }

    public int getVersion(String subject, Schema schema) throws CinnamonSchemaException {
        try {
            return restService.lookUpSubjectVersion(schema.toString(), subject).getVersion();
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
    }

    public int getLatestVersion(String subject) throws CinnamonSchemaException {
        try {
            List<Integer> versions = restService.getAllVersions(subject);
            return (versions == null || versions.size() == 0) ? 0 : Collections.max(versions, Comparator.comparingInt(o -> o));
        } catch (RestClientException e) {
            return 0;
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
    }

    public boolean testCompatibility(String subject, Schema schema) throws CinnamonSchemaException {
        try {
            return restService.testCompatibility(schema.toString(), subject, "latest");
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
    }

    public String updateCompatibility(String subject, String compatibility) throws CinnamonSchemaException {
        try {
            return restService.updateCompatibility(compatibility, subject).getCompatibilityLevel();
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
    }

    public String getCompatibility(String subject) throws CinnamonSchemaException {
        try {
            return restService.getConfig(subject).getCompatibilityLevel();
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
    }

    public Collection<String> getAllSubjects() throws CinnamonSchemaException {
        try {
            return restService.getAllSubjects();
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
    }

//    public static String makeSchema(String subject, String fields) {
//        return "{i\"type\":\"record\", \"name\":\"" + subject + "\", \"fields\"" +  + "}";
//    }

    private static SchemaMetadata toSchemaMetadata(io.confluent.kafka.schemaregistry.client.rest.entities.Schema response) {
        return new SchemaMetadata(response.getId(), response.getVersion(), response.getSchema());
    }

}
