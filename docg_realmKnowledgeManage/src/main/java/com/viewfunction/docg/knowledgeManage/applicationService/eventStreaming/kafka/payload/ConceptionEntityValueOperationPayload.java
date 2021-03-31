package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;

public class ConceptionEntityValueOperationPayload {
    private ConceptionEntityValueOperationContent conceptionEntityValueOperationContent;
    private long payloadOffset;
    private Object payloadKey;
    private ConceptionEntityValue conceptionEntityValue;

    public ConceptionEntityValueOperationContent getConceptionEntityValueOperationContent() {
        return conceptionEntityValueOperationContent;
    }

    public void setConceptionEntityValueOperationContent(ConceptionEntityValueOperationContent conceptionEntityValueOperationContent) {
        this.conceptionEntityValueOperationContent = conceptionEntityValueOperationContent;
    }

    public long getPayloadOffset() {
        return payloadOffset;
    }

    public void setPayloadOffset(long payloadOffset) {
        this.payloadOffset = payloadOffset;
    }

    public ConceptionEntityValue getConceptionEntityValue() {
        return conceptionEntityValue;
    }

    public void setConceptionEntityValue(ConceptionEntityValue conceptionEntityValue) {
        this.conceptionEntityValue = conceptionEntityValue;
    }

    public Object getPayloadKey() {
        return payloadKey;
    }

    public void setPayloadKey(Object payloadKey) {
        this.payloadKey = payloadKey;
    }
}
