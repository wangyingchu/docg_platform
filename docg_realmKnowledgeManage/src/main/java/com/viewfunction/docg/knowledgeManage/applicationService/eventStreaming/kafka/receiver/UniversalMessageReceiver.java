package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;

import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.EventStreamingServicePropertiesHandler;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.*;

public class UniversalMessageReceiver {

    private Properties configProps;
    private KafkaConsumer<Object, Object> consumer;
    private MessageHandler messageHandler;
    private boolean runningReceiverFlag = true;
    private boolean isBatchHandleMode = false;

    public UniversalMessageReceiver(MessageHandler messageHandler) throws ConfigurationErrorException {
        this.messageHandler = messageHandler;
        initReceiverConfig();
    }

    public UniversalMessageReceiver(String consumerGroupId, MessageHandler messageHandler) throws ConfigurationErrorException {
        this.messageHandler = messageHandler;
        initReceiverConfig();
        this.configProps.put(ConsumerConfig.GROUP_ID_CONFIG,consumerGroupId);
    }

    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    protected boolean isBatchHandleMode() {
        return isBatchHandleMode;
    }

    protected void setBatchHandleMode(boolean batchHandleMode) {
        isBatchHandleMode = batchHandleMode;
    }

    private class HandleRebalance implements ConsumerRebalanceListener {
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            /*
            for(TopicPartition partition: partitions){
                //consumer.seek(partition, getOffsetFromDB(partition));
            }
            */
        }
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            consumer.commitSync(currentOffsets);
        }
    }

    public void setKeyDeserializer(Object keyDeserializer){
        this.configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,keyDeserializer);
    }

    public void setValueDeserializer(Object valueDeserializer){
        this.configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,valueDeserializer);
    }

    public void startMessageReceive(String[] topicNameArrays)throws ConfigurationErrorException, MessageHandleErrorException {
        if(this.configProps==null||!this.configProps.containsKey(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)||
                !this.configProps.containsKey(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)){
            throw new ConfigurationErrorException();
        }
        if(this.messageHandler == null){
            throw new MessageHandleErrorException();
        }
        boolean enableAutoCommit = Boolean.parseBoolean(this.configProps.getProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
        this.consumer = new KafkaConsumer(this.configProps);
        this.consumer.subscribe(Arrays.asList(topicNameArrays),new HandleRebalance());
        int pollInterMs=Integer.parseInt(EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_POLL_MS));
        try {
            while (runningReceiverFlag) {
                ConsumerRecords<Object, Object> records = this.consumer.poll(Duration.ofMillis(pollInterMs));
                for (ConsumerRecord<Object, Object> record : records) {
                    currentOffsets.put(new TopicPartition(record.topic(),record.partition()), new OffsetAndMetadata(record.offset()+1, "no metadata"));
                    if(!isBatchHandleMode()) {
                        this.messageHandler.handleMessage(record);
                    }
                }
                if(isBatchHandleMode() && !records.isEmpty()){
                    this.messageHandler.handleMessages(records);
                }
                if(!enableAutoCommit) {
                    consumer.commitAsync(currentOffsets,(offsets, exception) -> {
                        if (exception != null) {
                            //log.error("Commit failed for offsets {}", offsets, exception);
                        }
                    });
                }
            }
        }catch(WakeupException e){
            // ignore for shutdown
        }finally {
            try {
                this.consumer.commitSync(currentOffsets);
            } finally {
                this.consumer.close();
            }
        }
    }

    public void stopMessageReceive(){
        //call wakeup from out thread to quit loop and shutdown consumer
        this.consumer.wakeup();
        //this.runningReceiverFlag = false;
    }

    public void initReceiverConfig() throws ConfigurationErrorException {
        this.configProps = new Properties();
        //basic config
        this.configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_BOOTSTRAP_SERVERS));
        this.configProps.put(EventStreamingServicePropertiesHandler.SCHEMA_REGISTRY_URL, EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_SCHEMA_REGISTRY));
        //additional config
        this.configProps.put(ConsumerConfig.GROUP_ID_CONFIG, EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_GROUP_ID));
        boolean enableAutoCommit=Boolean.parseBoolean(EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_ENABLE_AUTO_COMMIT));
        this.configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,enableAutoCommit);
        int autoCommitIntervalMs=Integer.parseInt(EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_AUTO_COMMIT_INTERVAL_MS));
        this.configProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,autoCommitIntervalMs);
        int sessionTimeoutMs=Integer.parseInt(EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_SESSION_TIMEOUT_MS));
        this.configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,sessionTimeoutMs);
        int maxPartitionFetchBytes=Integer.parseInt(EventStreamingServicePropertiesHandler.getPerportyValue(EventStreamingServicePropertiesHandler.CONSUMER_MAX_PARTITION_FETCH_BYTES));
        this.configProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,maxPartitionFetchBytes);
    }

    public void setAdditionalConfigItem(String configKey,Object configItem){
        if(this.configProps != null){
            this.configProps.put(configKey,configItem);
        }
    }

    public Properties getReceiverConfig(){
        return this.configProps;
    }
}
