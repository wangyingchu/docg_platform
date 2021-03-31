package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationPayload;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.ConceptionEntityValueOperationType;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.codec.binary.Base64;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ConceptionEntityValueOperationsMessageHandler extends AvroMessageHandler{

    @Override
    protected void operateRecord(Object recordKey, GenericRecord recordValue, long recordOffset) {}

    @Override
    public void handleMessages(ConsumerRecords<Object, Object> consumerRecords) {
        List<ConceptionEntityValueOperationPayload> conceptionEntityValueOperationPayloadList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for(ConsumerRecord<Object, Object> consumerRecord:consumerRecords){
            Object recordKey = consumerRecord.key();
            Object recordValue = consumerRecord.value();
            GenericRecord genericRecordValue=(GenericRecord)recordValue;
            long recordOffset = consumerRecord.offset();

            ConceptionEntityValueOperationPayload conceptionEntityValueOperationPayload = new ConceptionEntityValueOperationPayload();
            conceptionEntityValueOperationPayload.setPayloadOffset(recordOffset);
            conceptionEntityValueOperationPayload.setPayloadKey(recordKey);

            ConceptionEntityValueOperationContent conceptionEntityValueOperationContent = new ConceptionEntityValueOperationContent();
            conceptionEntityValueOperationPayload.setConceptionEntityValueOperationContent(conceptionEntityValueOperationContent);
            conceptionEntityValueOperationPayloadList.add(conceptionEntityValueOperationPayload);

            if(genericRecordValue.get("senderId")!=null) {
                conceptionEntityValueOperationContent.setSenderId(genericRecordValue.get("senderId").toString());
            }
            if(genericRecordValue.get("senderIP")!=null) {
                conceptionEntityValueOperationContent.setSenderIP(genericRecordValue.get("senderIP").toString());
            }
            conceptionEntityValueOperationContent.setSendTime((Long)genericRecordValue.get("sendTime"));
            conceptionEntityValueOperationContent.setConceptionKindName(genericRecordValue.get("conceptionKindName").toString());
            conceptionEntityValueOperationContent.setCoreRealmName(genericRecordValue.get("coreRealmName").toString());

            String operationTypeValue = genericRecordValue.get("operationType").toString();
            switch(operationTypeValue){
                case "INSERT": conceptionEntityValueOperationContent.setOperationType(ConceptionEntityValueOperationType.INSERT); break;
                case "DELETE": conceptionEntityValueOperationContent.setOperationType(ConceptionEntityValueOperationType.DELETE); break;
                case "UPDATE": conceptionEntityValueOperationContent.setOperationType(ConceptionEntityValueOperationType.UPDATE); break;
            }
            if(genericRecordValue.get("addPerDefinedRelation")!=null){
                conceptionEntityValueOperationContent.setAddPerDefinedRelation((Boolean)genericRecordValue.get("addPerDefinedRelation"));
            }
            if(genericRecordValue.get("conceptionEntityUID")!=null) {
                conceptionEntityValueOperationContent.setConceptionEntityUID(genericRecordValue.get("conceptionEntityUID").toString());
            }
            if(genericRecordValue.get("entityAttributesValue")!=null) {
                conceptionEntityValueOperationContent.setEntityAttributesValue(genericRecordValue.get("entityAttributesValue").toString());
            }

            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue();
            conceptionEntityValue.setConceptionEntityUID(conceptionEntityValueOperationContent.getConceptionEntityUID());
            conceptionEntityValueOperationPayload.setConceptionEntityValue(conceptionEntityValue);
            try {
                if(conceptionEntityValueOperationContent.getEntityAttributesValue() != null) {
                    byte[] entityPropertiesValueDecodedBytes = Base64.decodeBase64(conceptionEntityValueOperationContent.getEntityAttributesValue().getBytes());
                    HashMap entityPropertiesMap = mapper.readValue(entityPropertiesValueDecodedBytes, HashMap.class);
                    conceptionEntityValue.setEntityAttributesValue(entityPropertiesMap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        handleConceptionEntityOperationContents(conceptionEntityValueOperationPayloadList);
    }

    public abstract void handleConceptionEntityOperationContents(List<? extends ConceptionEntityValueOperationPayload> infoObjectValueOperationPayloads);
}
