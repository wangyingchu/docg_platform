package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public abstract class AvroMessageHandler implements MessageHandler{

    @Override
    public void handleMessage(ConsumerRecord<Object, Object> consumerRecord) {
        Object recordKey = consumerRecord.key();
        Object recordValue=consumerRecord.value();
        GenericRecord genericRecordValue=(GenericRecord)recordValue;
        long recordOffset = consumerRecord.offset();
        operateRecord(recordKey,genericRecordValue,recordOffset);
    }

    @Override
    public void handleMessages(ConsumerRecords<Object, Object> consumerRecord) {}

    protected abstract void operateRecord(Object recordKey,GenericRecord recordValue,long recordOffset);
}
