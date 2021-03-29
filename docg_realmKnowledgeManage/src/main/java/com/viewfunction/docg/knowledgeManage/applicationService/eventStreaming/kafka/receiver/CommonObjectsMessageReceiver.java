package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;

public class CommonObjectsMessageReceiver extends AvroMessageReceiver{

    public CommonObjectsMessageReceiver(CommonObjectsMessageHandler messageHandler) throws ConfigurationErrorException {
        super(messageHandler);
    }

    public CommonObjectsMessageReceiver(String consumerGroupId, CommonObjectsMessageHandler messageHandler) throws ConfigurationErrorException {
        super(consumerGroupId,messageHandler);
    }
}
