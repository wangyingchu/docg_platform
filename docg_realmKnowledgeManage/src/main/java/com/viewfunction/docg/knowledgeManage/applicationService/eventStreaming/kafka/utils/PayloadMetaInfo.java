package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils;

public class PayloadMetaInfo {

    private String destinationTopic;
    private String payloadKey;
    private String payloadSchema;
    private String schemaDefinitionLocation;

    public String getDestinationTopic() {
        return destinationTopic;
    }

    public void setDestinationTopic(String destinationTopic) {
        this.destinationTopic = destinationTopic;
    }

    public String getPayloadKey() {
        return payloadKey;
    }

    public void setPayloadKey(String payloadKey) {
        this.payloadKey = payloadKey;
    }

    public String getPayloadSchema() {
        return payloadSchema;
    }

    public void setPayloadSchema(String payloadSchema) {
        this.payloadSchema = payloadSchema;
    }

    public String getSchemaDefinitionLocation() {
        return schemaDefinitionLocation;
    }

    public void setSchemaDefinitionLocation(String schemaDefinitionLocation) {
        this.schemaDefinitionLocation = schemaDefinitionLocation;
    }
}
