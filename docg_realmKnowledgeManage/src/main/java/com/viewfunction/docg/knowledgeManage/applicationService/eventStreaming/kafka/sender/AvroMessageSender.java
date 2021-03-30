package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender;

import com.fasterxml.jackson.databind.JsonNode;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.SchemaFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.AvroUtils;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.MessageUtils;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.PayloadMetaInfo;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.io.File;
import java.io.IOException;

public class AvroMessageSender extends UniversalMessageSender{

    public AvroMessageSender(MessageSentEventHandler messageSentEventHandler) throws ConfigurationErrorException {
        super(messageSentEventHandler);
        this.setKeyDeserializer(org.apache.kafka.common.serialization.StringSerializer.class);
        this.setValueDeserializer(io.confluent.kafka.serializers.KafkaAvroSerializer.class);
    }

    public void sendAvroMessage(PayloadMetaInfo payloadMetaInfo, GenericRecord avroRecord) throws MessageHandleErrorException, MessageFormatErrorException, SchemaFormatErrorException {
        Schema payloadSchema = getSchema(payloadMetaInfo);
        boolean isValidateDataFormat= new GenericData().validate(payloadSchema,avroRecord);
        if(!isValidateDataFormat){
            throw new MessageFormatErrorException();
        }
        sendMessage(payloadMetaInfo.getDestinationTopic(),payloadMetaInfo.getPayloadKey(),avroRecord);
    }

    public void sendAvroMessage(PayloadMetaInfo payloadMetaInfo, JsonNode payloadContent) throws MessageFormatErrorException, MessageHandleErrorException, SchemaFormatErrorException {
        Schema payloadSchema = getSchema(payloadMetaInfo);
        GenericRecord avroRecord = MessageUtils.generateGenericRecordWithSchema(payloadSchema, payloadContent);
        //verify if data match schema definition
        boolean isValidateDataFormat= new GenericData().validate(payloadSchema,avroRecord);
        if(!isValidateDataFormat){
            throw new MessageFormatErrorException();
        }
        sendMessage(payloadMetaInfo.getDestinationTopic(),payloadMetaInfo.getPayloadKey(),avroRecord);
    }

    private Schema getSchema(PayloadMetaInfo payloadMetaInfo) throws MessageFormatErrorException, SchemaFormatErrorException {
        String schemaName = payloadMetaInfo.getPayloadSchema();
        Schema payloadSchema = null;
        if(schemaName!=null){
            payloadSchema= AvroUtils.getSchema(schemaName);
        }
        if(payloadSchema==null){
            if(payloadMetaInfo.getSchemaDefinitionLocation()==null){
                throw new MessageFormatErrorException();
            }else{
                try {
                    payloadSchema = AvroUtils.parseSchema(new File(payloadMetaInfo.getSchemaDefinitionLocation()));
                } catch (IOException e) {
                    e.printStackTrace();
                    SchemaFormatErrorException sfee=new SchemaFormatErrorException();
                    sfee.initCause(e);
                    throw sfee;
                }
            }
        }
        return payloadSchema;
    }
}
