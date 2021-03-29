package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload;

public class CommonObjectsPayloadMetaInfo {
    private String senderId;
    private String senderGroup;
    private String senderCategory;
    private String senderIP;

    private String payloadClassification;
    private String payloadType;
    private String payloadTypeDesc;
    private String payloadProcessor;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderGroup() {
        return senderGroup;
    }

    public void setSenderGroup(String senderGroup) {
        this.senderGroup = senderGroup;
    }

    public String getSenderCategory() {
        return senderCategory;
    }

    public void setSenderCategory(String senderCategory) {
        this.senderCategory = senderCategory;
    }

    public String getSenderIP() {
        return senderIP;
    }

    public void setSenderIP(String senderIP) {
        this.senderIP = senderIP;
    }

    public String getPayloadClassification() {
        return payloadClassification;
    }

    public void setPayloadClassification(String payloadClassification) {
        this.payloadClassification = payloadClassification;
    }

    public String getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(String payloadType) {
        this.payloadType = payloadType;
    }

    public String getPayloadTypeDesc() {
        return payloadTypeDesc;
    }

    public void setPayloadTypeDesc(String payloadTypeDesc) {
        this.payloadTypeDesc = payloadTypeDesc;
    }

    public String getPayloadProcessor() {
        return payloadProcessor;
    }

    public void setPayloadProcessor(String payloadProcessor) {
        this.payloadProcessor = payloadProcessor;
    }
}
