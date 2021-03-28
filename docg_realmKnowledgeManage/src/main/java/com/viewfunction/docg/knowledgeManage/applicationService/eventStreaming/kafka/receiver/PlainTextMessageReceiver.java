package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;


import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;

public class PlainTextMessageReceiver extends UniversalMessageReceiver{

    public PlainTextMessageReceiver(PlainTextMessageHandler messageHandler) throws ConfigurationErrorException {
        super(messageHandler);
        this.setKeyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        this.setValueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
    }

    public PlainTextMessageReceiver(String consumerGroupId, PlainTextMessageHandler messageHandler) throws ConfigurationErrorException {
        super(consumerGroupId,messageHandler);
        this.setKeyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        this.setValueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
    }
}
