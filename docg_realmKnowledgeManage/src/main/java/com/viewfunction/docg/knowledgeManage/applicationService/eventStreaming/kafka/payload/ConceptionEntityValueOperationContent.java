package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload;

public class ConceptionEntityValueOperationContent {

    private String senderIP;
    private String senderId;
    private long sendTime;
    private String coreRealmName;
    private String conceptionKindName;
    private String conceptionEntityUID;
    private ConceptionEntityValueOperationType operationType;
    private String entityAttributesValue;
    private boolean addPerDefinedRelation;

    public String getSenderIP() {
        return senderIP;
    }

    public void setSenderIP(String senderIP) {
        this.senderIP = senderIP;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getCoreRealmName() {
        return coreRealmName;
    }

    public void setCoreRealmName(String coreRealmName) {
        this.coreRealmName = coreRealmName;
    }

    public String getConceptionKindName() {
        return conceptionKindName;
    }

    public void setConceptionKindName(String conceptionKindName) {
        this.conceptionKindName = conceptionKindName;
    }

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public void setConceptionEntityUID(String conceptionEntityUID) {
        this.conceptionEntityUID = conceptionEntityUID;
    }

    public ConceptionEntityValueOperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(ConceptionEntityValueOperationType operationType) {
        this.operationType = operationType;
    }

    public String getEntityAttributesValue() {
        return entityAttributesValue;
    }

    public void setEntityAttributesValue(String entityAttributesValue) {
        this.entityAttributesValue = entityAttributesValue;
    }

    public boolean isAddPerDefinedRelation() {
        return addPerDefinedRelation;
    }

    public void setAddPerDefinedRelation(boolean addPerDefinedRelation) {
        this.addPerDefinedRelation = addPerDefinedRelation;
    }
}
