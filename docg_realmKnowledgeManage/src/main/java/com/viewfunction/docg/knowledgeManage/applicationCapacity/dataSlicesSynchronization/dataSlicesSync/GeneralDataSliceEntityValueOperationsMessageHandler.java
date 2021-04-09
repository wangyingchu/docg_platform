package com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.dataSlicesSync;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsReceivedMessage;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.CommonObjectsMessageHandler;

import java.util.Map;

public class GeneralDataSliceEntityValueOperationsMessageHandler extends CommonObjectsMessageHandler {

    private Map<Object,Object> commandContextDataMap;

    public GeneralDataSliceEntityValueOperationsMessageHandler(Map<Object,Object> commandContextDataMap){
        this.commandContextDataMap = commandContextDataMap;
    }


    @Override
    protected void operateRecord(Object recordKey, CommonObjectsReceivedMessage receivedMessage, long recordOffset) {
        System.out.println(" "+recordKey+receivedMessage.getMessageSendTime()+" "+recordOffset);
    }
}
