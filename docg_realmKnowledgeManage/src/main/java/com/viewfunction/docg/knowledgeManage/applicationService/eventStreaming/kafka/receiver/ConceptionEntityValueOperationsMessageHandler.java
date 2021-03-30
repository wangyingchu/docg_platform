package com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

public class ConceptionEntityValueOperationsMessageHandler extends AvroMessageHandler{

    @Override
    protected void operateRecord(Object recordKey, GenericRecord recordValue, long recordOffset) {}

    @Override
    public void handleMessages(ConsumerRecords<Object, Object> consumerRecords) {
        /*
        List<InfoObjectValueOperationPayload> infoObjectValueOperationPayloadList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for(ConsumerRecord<Object, Object> consumerRecord:consumerRecords){
            //Object recordKey = consumerRecord.key();
            Object recordValue=consumerRecord.value();
            GenericRecord genericRecordValue=(GenericRecord)recordValue;
            long recordOffset = consumerRecord.offset();

            InfoObjectValueOperationPayload infoObjectValueOperationPayload = new InfoObjectValueOperationPayload();
            infoObjectValueOperationPayload.setPayloadOffset(recordOffset);
            InfoObjectValueOperationContent infoObjectValueOperationContent = new InfoObjectValueOperationContent();
            infoObjectValueOperationPayload.setInfoObjectValueOperationContent(infoObjectValueOperationContent);
            infoObjectValueOperationPayloadList.add(infoObjectValueOperationPayload);

            infoObjectValueOperationContent.setInfoObjectSpaceName(genericRecordValue.get("infoObjectSpaceName").toString());
            infoObjectValueOperationContent.setInfoObjectTypeName(genericRecordValue.get("infoObjectTypeName").toString());
            infoObjectValueOperationContent.setTenantId(genericRecordValue.get("tenantId").toString());

            if(genericRecordValue.get("senderId")!=null) {
                infoObjectValueOperationContent.setSenderId(genericRecordValue.get("senderId").toString());
            }
            if(genericRecordValue.get("senderIP")!=null) {
                infoObjectValueOperationContent.setSenderIP(genericRecordValue.get("senderIP").toString());
            }
            infoObjectValueOperationContent.setSendTime((Long)genericRecordValue.get("sendTime"));

            String operationTypeValue = genericRecordValue.get("operationType").toString();
            switch(operationTypeValue){
                case "INSERT": infoObjectValueOperationContent.setOperationType(InfoObjectValueOperationType.INSERT); break;
                case "DELETE": infoObjectValueOperationContent.setOperationType(InfoObjectValueOperationType.DELETE); break;
                case "UPDATE": infoObjectValueOperationContent.setOperationType(InfoObjectValueOperationType.UPDATE); break;
            }

            if(genericRecordValue.get("addPerDefinedRelation")!=null){
                infoObjectValueOperationContent.setAddPerDefinedRelation((Boolean)genericRecordValue.get("addPerDefinedRelation"));
            }

            if(genericRecordValue.get("objectInstanceRID")!=null) {
                infoObjectValueOperationContent.setObjectInstanceRID(genericRecordValue.get("objectInstanceRID").toString());
            }

            if(genericRecordValue.get("baseDatasetPropertiesValue")!=null) {
                infoObjectValueOperationContent.setBaseDatasetPropertiesValue(genericRecordValue.get("baseDatasetPropertiesValue").toString());
            }

            if(genericRecordValue.get("generalDatasetsPropertiesValue")!=null) {
                infoObjectValueOperationContent.setGeneralDatasetsPropertiesValue(genericRecordValue.get("generalDatasetsPropertiesValue").toString());
            }

            InfoObjectValue infoObjectValue =new InfoObjectValue();
            infoObjectValue.setObjectInstanceRID(infoObjectValueOperationContent.getObjectInstanceRID());
            infoObjectValueOperationPayload.setInfoObjectValue(infoObjectValue);

            try {
                if(infoObjectValueOperationContent.getBaseDatasetPropertiesValue() != null) {
                    byte[] baseDatasetPropertiesValueDecodedBytes = Base64.decodeBase64(infoObjectValueOperationContent.getBaseDatasetPropertiesValue().getBytes());
                    HashMap baseDatasetPropertiesMap = mapper.readValue(baseDatasetPropertiesValueDecodedBytes, HashMap.class);
                    infoObjectValue.setBaseDatasetPropertiesValue(baseDatasetPropertiesMap);
                }

                if(infoObjectValueOperationContent.getGeneralDatasetsPropertiesValue() != null) {
                    byte[] generalDatasetPropertiesValueDecodedBytes = Base64.decodeBase64(infoObjectValueOperationContent.getGeneralDatasetsPropertiesValue().getBytes());
                    HashMap generalDatasetPropertiesMap = mapper.readValue(generalDatasetPropertiesValueDecodedBytes, HashMap.class);
                    infoObjectValue.setGeneralDatasetsPropertiesValue(generalDatasetPropertiesMap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        handleInfoObjectOperationContents(infoObjectValueOperationPayloadList);
        */
    }

    //public abstract void handleInfoObjectOperationContents(List<? extends InfoObjectValueOperationPayload> infoObjectValueOperationPayloads);
}
