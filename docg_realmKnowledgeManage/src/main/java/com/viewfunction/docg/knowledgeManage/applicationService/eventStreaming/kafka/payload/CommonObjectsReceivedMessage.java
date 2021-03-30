package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload;

public class CommonObjectsReceivedMessage {
    private CommonObjectsPayloadMetaInfo messageCommonObjectsPayloadMetaInfo;
    private CommonObjectsPayloadContent commonObjectsPayloadContent;
    private long messageSendTime;
    private long messageReceivedTime;

    public CommonObjectsPayloadMetaInfo getMessageCommonObjectsPayloadMetaInfo() {
        return messageCommonObjectsPayloadMetaInfo;
    }

    public void setMessageCommonObjectsPayloadMetaInfo(CommonObjectsPayloadMetaInfo messageCommonObjectsPayloadMetaInfo) {
        this.messageCommonObjectsPayloadMetaInfo = messageCommonObjectsPayloadMetaInfo;
    }

    public CommonObjectsPayloadContent getCommonObjectsPayloadContent() {
        return commonObjectsPayloadContent;
    }

    public void setCommonObjectsPayloadContent(CommonObjectsPayloadContent commonObjectsPayloadContent) {
        this.commonObjectsPayloadContent = commonObjectsPayloadContent;
    }

    public long getMessageSendTime() {
        return messageSendTime;
    }

    public void setMessageSendTime(long messageSendTime) {
        this.messageSendTime = messageSendTime;
    }

    public long getMessageReceivedTime() {
        return messageReceivedTime;
    }

    public void setMessageReceivedTime(long messageReceivedTime) {
        this.messageReceivedTime = messageReceivedTime;
    }
}
