package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface MessageHandler {
    public void handleMessage(ConsumerRecord<Object, Object> consumerRecord);
    public void handleMessages(ConsumerRecords<Object, Object> records);
}
