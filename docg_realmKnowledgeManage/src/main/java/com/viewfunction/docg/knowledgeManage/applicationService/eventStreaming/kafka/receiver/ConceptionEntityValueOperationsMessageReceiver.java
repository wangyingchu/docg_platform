package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;

public class ConceptionEntityValueOperationsMessageReceiver extends AvroMessageReceiver{

    public ConceptionEntityValueOperationsMessageReceiver(ConceptionEntityValueOperationsMessageHandler messageHandler) throws ConfigurationErrorException {
        super(messageHandler);
        super.setBatchHandleMode(true);
        this.setKeyDeserializer(org.apache.kafka.common.serialization.StringDeserializer.class);
        this.setValueDeserializer(io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
    }

    public ConceptionEntityValueOperationsMessageReceiver(String consumerGroupId, ConceptionEntityValueOperationsMessageHandler messageHandler) throws ConfigurationErrorException {
        super(consumerGroupId,messageHandler);
        super.setBatchHandleMode(true);
        this.setKeyDeserializer(org.apache.kafka.common.serialization.StringDeserializer.class);
        this.setValueDeserializer(io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
    }
}
