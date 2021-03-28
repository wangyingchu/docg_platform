package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public abstract class PlainTextMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(ConsumerRecord<Object, Object> consumerRecord) {
        Object recordKey = consumerRecord.key();
        String recordValue = consumerRecord.value().toString();
        long recordOffset = consumerRecord.offset();
        operateRecord(recordKey,recordValue,recordOffset);
    }

    @Override
    public void handleMessages(ConsumerRecords<Object, Object> consumerRecord) {}

    protected abstract void operateRecord(Object recordKey,String recordValue,long recordOffset);
}
