package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.ConfigurationErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.MessageHandleErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.exception.SchemaFormatErrorException;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationType;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.AvroUtils;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.utils.PayloadMetaInfo;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.Map;

public class ConceptionEntityValueOperationsMessageSender  extends AvroMessageSender{

    private ObjectMapper mapper = new ObjectMapper();

    public ConceptionEntityValueOperationsMessageSender(MessageSentEventHandler messageSentEventHandler) throws ConfigurationErrorException {
        super(messageSentEventHandler);
    }

    public void sendConceptionEntityValueOperationMessage(ConceptionEntityValueOperationContent conceptionEntityValueOperationContent,
                                                    ConceptionEntityValue conceptionEntityValue) throws SchemaFormatErrorException, MessageFormatErrorException, MessageHandleErrorException {
        AvroUtils.initPayloadSchemas();
        Schema payloadSchema= AvroUtils.getSchema(AvroUtils.ConceptionEntityValueOperationContentSchemaName);
        GenericRecord payloadRecord = new GenericData.Record(payloadSchema);

        if(conceptionEntityValueOperationContent.getSenderIP() != null){
            payloadRecord.put("senderIP",conceptionEntityValueOperationContent.getSenderIP());
        }
        if(conceptionEntityValueOperationContent.getSenderId() != null){
            payloadRecord.put("senderId",conceptionEntityValueOperationContent.getSenderId());
        }
        payloadRecord.put("sendTime",conceptionEntityValueOperationContent.getSendTime());

        payloadRecord.put("coreRealmName",conceptionEntityValueOperationContent.getCoreRealmName());
        payloadRecord.put("conceptionKindName",conceptionEntityValueOperationContent.getConceptionKindName());
        if(conceptionEntityValueOperationContent.getConceptionEntityUID() != null){
            payloadRecord.put("conceptionEntityUID",conceptionEntityValueOperationContent.getConceptionEntityUID());
        }

        ConceptionEntityValueOperationType operationType= conceptionEntityValueOperationContent.getOperationType();
        switch(operationType){
            case INSERT:
                payloadRecord.put("operationType","INSERT");
                payloadRecord.put("addPerDefinedRelation",conceptionEntityValueOperationContent.isAddPerDefinedRelation());
                populatePayloadData(payloadRecord, conceptionEntityValue);
                break;
            case UPDATE:
                payloadRecord.put("operationType","UPDATE");
                populatePayloadData(payloadRecord, conceptionEntityValue);
                payloadRecord.put("conceptionEntityUID",conceptionEntityValueOperationContent.getConceptionEntityUID());
                break;
            case DELETE:
                payloadRecord.put("operationType","DELETE");
                payloadRecord.put("conceptionEntityUID",conceptionEntityValueOperationContent.getConceptionEntityUID());
                break;
        }

        PayloadMetaInfo pmi=new PayloadMetaInfo();
        pmi.setPayloadSchema(AvroUtils.ConceptionEntityValueOperationContentSchemaName);
        pmi.setDestinationTopic(conceptionEntityValueOperationContent.getCoreRealmName());
        pmi.setPayloadKey(conceptionEntityValueOperationContent.getConceptionKindName());
        this.sendAvroMessage(pmi,payloadRecord);
    }

    private void populatePayloadData(GenericRecord payloadRecord,ConceptionEntityValue conceptionEntityValue) {
        if (conceptionEntityValue != null) {
            try {
                Map<String, Object> entityAttributesValueMap = conceptionEntityValue.getEntityAttributesValue();
                if (entityAttributesValueMap != null) {
                    String baseDatasetJsonInString = this.mapper.writeValueAsString(entityAttributesValueMap);
                    byte[] encodedBytes = Base64.encodeBase64(baseDatasetJsonInString.getBytes());
                    String baseDatasetValueString = new String(encodedBytes);
                    payloadRecord.put("entityAttributesValue", baseDatasetValueString);
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
