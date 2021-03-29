package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;

public class PlainTextMessageSender extends UniversalMessageSender {

    public PlainTextMessageSender(MessageSentEventHandler messageSentEventHandler) throws ConfigurationErrorException {
        super(messageSentEventHandler);
        this.setKeyDeserializer("org.apache.kafka.common.serialization.StringSerializer");
        this.setValueDeserializer("org.apache.kafka.common.serialization.StringSerializer");
    }

    public void sendTextMessage(String topicName,Object key, String value) throws MessageHandleErrorException {
        super.sendMessage(topicName,key,value);
    }
}


