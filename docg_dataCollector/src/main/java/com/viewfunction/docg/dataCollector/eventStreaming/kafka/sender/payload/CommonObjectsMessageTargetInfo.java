package com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload;

public class CommonObjectsMessageTargetInfo {

    private String destinationTopic;
    private String payloadKey;

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
}
