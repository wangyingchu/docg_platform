package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload;

public class MetaDataContent {
    private String senderIP;
    private String senderId;
    private long sendTime;
    private String infoObjectSpaceName;
    private String tenantId;
    private String content;
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

    public String getInfoObjectSpaceName() {
        return infoObjectSpaceName;
    }

    public void setInfoObjectSpaceName(String infoObjectSpaceName) {
        this.infoObjectSpaceName = infoObjectSpaceName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
