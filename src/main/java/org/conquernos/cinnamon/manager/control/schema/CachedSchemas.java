package org.conquernos.cinnamon.manager.control.schema;

import org.conquernos.cinnamon.exception.schema.CinnamonSchemaException;
import io.confluent.kafka.schemaregistry.client.SchemaMetadata;
import org.apache.avro.Schema;
import org.codehaus.jackson.node.IntNode;

import java.util.*;


public class CachedSchemas {

    private final SchemaRegistryControl client;

    private final Map<Subject, Schema> subjectCache = new HashMap<>();


    public CachedSchemas(String schemaRegistryUrl) throws CinnamonSchemaException {
        this(new SchemaRegistryControl(schemaRegistryUrl));
    }

    public CachedSchemas(String schemaRegistryUrl, List<Subject> subjects) throws CinnamonSchemaException {
        this(new SchemaRegistryControl(schemaRegistryUrl), subjects);
    }

    public CachedSchemas(SchemaRegistryControl client) throws CinnamonSchemaException {
        this(client, null);
    }

    public CachedSchemas(SchemaRegistryControl client, List<Subject> subjects) throws CinnamonSchemaException {
        this.client = client;

        initCache(subjects != null ? subjects : getAllSubjects());
    }

    /*
    Cached
     */
    public Schema getCachedLatestSchema(String topic) {
        return subjectCache.get(new Subject(topic));
    }

    /*
    Cached
     */
    public Schema getCachedSchema(Subject subject) {
        return subjectCache.get(subject);
    }

    public Collection<String> getSubjects() {
        try {
            return client.getAllSubjects();
        } catch (CinnamonSchemaException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Schema> getCachedAllVersionSchema(String topic) {
        try {
            int latestVersion = client.getLatestVersion(topic);
            List<Schema> schemas = new ArrayList<>(latestVersion);
            for (int version=1; version<=latestVersion; version++) {
                Schema schema = getCachedSchema(new Subject(topic, version));
                if (schema != null) {
                    schema.addProp("version", new IntNode(version));
                    schemas.add(schema);
                }
            }
            return schemas;
        } catch (CinnamonSchemaException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateCache() throws CinnamonSchemaException {
        initCache(getAllSubjects());
    }

    private void initCache(List<Subject> subjects) throws CinnamonSchemaException {
        try {
            for (Subject subject : subjects) {
                subjectCache.put(subject, subject.isLatestVersion()
                    ? getLatestSchema(client, subject.getSubject()) : getSchema(client, subject));
            }
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
    }

    private List<Subject> getAllSubjects() throws CinnamonSchemaException {
        List<Subject> subjects = new ArrayList<>();
        for (String subject : client.getAllSubjects()) {
            for (int version=1; version<=client.getLatestVersion(subject); version++) {
                subjects.add(new Subject(subject, version));
            }
        }

        return subjects;
    }

    private Schema getLatestSchema(SchemaRegistryControl client, String subject) throws CinnamonSchemaException {
        SchemaMetadata meta;
        try {
            meta = client.getLatestSchemaMetadata(subject);
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
        return new Schema.Parser().parse(meta.getSchema());
    }

    private Schema getSchema(SchemaRegistryControl client, Subject subject) throws CinnamonSchemaException {
        SchemaMetadata meta;
        try {
            meta = client.getSchemaMetadata(subject.getSubject(), subject.getVersion());
        } catch (Exception e) {
            throw new CinnamonSchemaException(e);
        }
        return new Schema.Parser().parse(meta.getSchema());
    }

}
