package com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender;

import com.viewfunction.docg.dataCollector.eventStreaming.exception.ConfigurationErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.exception.MessageHandleErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.util.EventStreamingServicePropertiesHandler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;

import java.util.Properties;
import java.util.UUID;

public class UniversalMessageSender {

    private Properties configProps;
    private MessageSentEventHandler messageSentEventHandler;
    private Producer producer;

    public UniversalMessageSender(MessageSentEventHandler messageSentEventHandler) throws ConfigurationErrorException {
        this.messageSentEventHandler = messageSentEventHandler;
        initProducerConfig();
    }

    public void setKeyDeserializer(Object keyDeserializer){
        this.configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,keyDeserializer);
    }

    public void setValueDeserializer(Object valueDeserializer){
        this.configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,valueDeserializer);
    }

    public void setTransactionalId(String transactionalId){
        this.configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,transactionalId);
    }

    public void beginMessageSendBatch(){
        if(!this.configProps.containsKey(ProducerConfig.TRANSACTIONAL_ID_CONFIG)){
            this.configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,UUID.randomUUID().toString());
        }
        this.producer = new KafkaProducer<>(this.configProps);
        this.producer.initTransactions();
        this.producer.beginTransaction();
    }

    protected void sendMessage(String topicName,Object key, Object value) throws MessageHandleErrorException {
        if(this.producer == null){
            throw new MessageHandleErrorException();
        }
        ProducerRecord<Object, Object> record = new ProducerRecord<>(topicName, key,value);
        try {
            this.producer.send(record,
                    (metadata, e) -> messageSentEventHandler.handleMessageSendCompleteEvent(metadata,e));
        } catch(SerializationException e) {
            e.printStackTrace();
            producer.abortTransaction();
            producer.close();
        }
    }

    public void finishMessageSendBatch(){
        if(this.producer != null){
            producer.commitTransaction();
            producer.close();
        }
    }

    public void sendUniversalMessage(String topicName,Object key, String value) throws MessageHandleErrorException {
        this.sendMessage(topicName,key,value);
    }

    private void initProducerConfig() throws ConfigurationErrorException {
        if(configProps==null){
            configProps = new Properties();
            configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PRODUCER_BOOTSTRAP_SERVERS));
            configProps.put(EventStreamingServicePropertiesHandler.SCHEMA_REGISTRY_URL, EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PRODUCER_SCHEMA_REGISTRY));
            configProps.put(ProducerConfig.ACKS_CONFIG, EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PRODUCER_ACKS));
            int retries=Integer.parseInt(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PRODUCER_RETRIES));
            configProps.put(ProducerConfig.RETRIES_CONFIG,retries);
            int batchSize=Integer.parseInt(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PRODUCER_BATCH_SIZE));
            configProps.put(ProducerConfig.BATCH_SIZE_CONFIG,batchSize);
            int lingerMs=Integer.parseInt(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PRODUCER_LINGER_MS));
            configProps.put(ProducerConfig.LINGER_MS_CONFIG,lingerMs);
            int bufferMemory=Integer.parseInt(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PRODUCER_BUFFER_MEMORY));
            configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG,bufferMemory);
            int maxRequestSize=Integer.parseInt(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PRODUCER_MAX_REQUEST_SIZE));
            configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,maxRequestSize);
        }
    }

    public void setAdditionalConfigItem(String configKey,Object configItem){
        if(this.configProps!=null){
            this.configProps.put(configKey,configItem);
        }
    }
}
