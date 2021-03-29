package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender;

import org.apache.kafka.clients.producer.RecordMetadata;

public abstract  class MessageSentEventHandler {

    public void handleMessageSendCompleteEvent(RecordMetadata metadata, Exception e){
        long offset = metadata.offset();
        long timestamp = metadata.timestamp();
        String topic = metadata.topic();
        int partition = metadata.partition();
        operateMetaData(offset,timestamp,topic,partition);
    }

    public abstract void operateMetaData(long offset,long timestamp,String topic,int partition);
}
