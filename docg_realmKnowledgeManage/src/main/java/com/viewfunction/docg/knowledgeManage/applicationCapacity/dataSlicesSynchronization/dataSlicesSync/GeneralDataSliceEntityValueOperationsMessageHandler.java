package com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.dataSlicesSync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.DataSlicesSynchronizationApplication;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadMetaInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsReceivedMessage;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.CommonObjectsMessageHandler;
import com.viewfunction.docg.knowledgeManage.consoleApplication.util.ApplicationLauncherUtil;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GeneralDataSliceEntityValueOperationsMessageHandler extends CommonObjectsMessageHandler {

    private static final String EntityUIDProperty = "EntityUID";
    private static final String EntityKindProperty = "EntityKind";
    private static final String RelationSourceEntityUIDProperty = "RelationSourceEntityUIDProperty";
    private static final String RelationTargetEntityUIDProperty = "RelationTargetEntityUIDProperty";
    private static final String OperationTypeProperty = "OperationType";
    private static final String OperationTimeProperty = "OperationTime";
    private static final String OperationType_Delete_ConceptionEntity ="Delete_ConceptionEntity";
    private static final String OperationType_Delete_RelationEntity ="Delete_RelationEntity";
    private static final String OperationType_Create_ConceptionEntity ="Create_ConceptionEntity";
    private static final String OperationType_Create_RelationEntity ="Create_RelationEntity";
    private static final String OperationType_Update_ConceptionEntityProperty ="Update_ConceptionEntityProperty";
    private static final String OperationType_Update_RelationEntityProperty ="Update_RelationEntityProperty";
    private static final String OperationType_Remove_ConceptionEntityProperty ="Remove_ConceptionEntityProperty";
    private static final String OperationType_Remove_RelationEntityProperty ="Remove_RelationEntityProperty";

    private Map<Object,Object> commandContextDataMap;
    private ObjectMapper objectMapper;
    private String targetPayloadClassification;
    private String targetPayloadType;
    private long SYNC_LISTENING_START_TIME_LONG_VALUE;

    public GeneralDataSliceEntityValueOperationsMessageHandler(Map<Object,Object> commandContextDataMap){
        this.commandContextDataMap = commandContextDataMap;
        this.objectMapper = new ObjectMapper();
        this.targetPayloadClassification = ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.payloadClassification") != null ?
                ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.payloadClassification").trim() : null;
        this.targetPayloadType = ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.payloadType") != null ?
                ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.payloadType").trim() : null;
        this.SYNC_LISTENING_START_TIME_LONG_VALUE = ((Date)this.commandContextDataMap.get(DataSlicesSynchronizationApplication.SYNC_LISTENING_START_TIME)).getTime();
    }

    @Override
    protected void operateRecord(Object recordKey, CommonObjectsReceivedMessage receivedMessage, long recordOffset) {
        long messageSendTime = receivedMessage.getMessageSendTime();
        if(messageSendTime < SYNC_LISTENING_START_TIME_LONG_VALUE){
            //the data contained in this message already processed by application startup data process logic
            return;
        }
        CommonObjectsPayloadMetaInfo commonObjectsPayloadMetaInfo = receivedMessage.getMessageCommonObjectsPayloadMetaInfo();
        //commonObjectsPayloadMetaInfo.getPayloadProcessor();
        //commonObjectsPayloadMetaInfo.getPayloadTypeDesc();
        //commonObjectsPayloadMetaInfo.getSenderCategory();
        //commonObjectsPayloadMetaInfo.getSenderGroup();
        //commonObjectsPayloadMetaInfo.getSenderId();
        //commonObjectsPayloadMetaInfo.getSenderIP();
        String payloadType = commonObjectsPayloadMetaInfo.getPayloadType();
        String payloadClassification = commonObjectsPayloadMetaInfo.getPayloadClassification();
        if(payloadType != null && payloadClassification !=null && payloadType.equals(this.targetPayloadType) && payloadClassification.equals(this.targetPayloadClassification)){
            CommonObjectsPayloadContent commonObjectsPayloadContent = receivedMessage.getCommonObjectsPayloadContent();
            if(commonObjectsPayloadContent.getTextContentEncoded()){
                if(commonObjectsPayloadContent.getTextContentEncodeAlgorithm().equals("BASE64")){
                    byte[] decodedBytes = Base64.decodeBase64(commonObjectsPayloadContent.getTextContent().getBytes());
                    try {
                        ObjectNode node = (ObjectNode) this.objectMapper.readTree(decodedBytes);
                        if(node.has(OperationTypeProperty) && node.has(EntityUIDProperty)){
                            String OperationType = node.get(OperationTypeProperty).textValue();
                            String targetEntityUID = node.get(EntityUIDProperty).textValue();
                            String targetEntityKind = node.has(EntityKindProperty) ? node.get(EntityKindProperty).textValue() : null;

                            switch(OperationType){
                                case OperationType_Delete_ConceptionEntity:
                                    doDeleteConceptionEntity(targetEntityKind,targetEntityUID);
                                    break;
                                case OperationType_Delete_RelationEntity:
                                    doDeleteRelationEntity(targetEntityKind,targetEntityUID);
                                    break;
                                case OperationType_Create_ConceptionEntity:
                                    doCreateConceptionEntity(targetEntityKind,targetEntityUID,generateEntityProperties(node));
                                    break;
                                case OperationType_Create_RelationEntity:
                                    doCreateRelationEntity(targetEntityKind,targetEntityUID,generateEntityProperties(node));
                                    break;
                                case OperationType_Update_ConceptionEntityProperty:
                                    doUpdateConceptionEntityProperty(targetEntityKind,targetEntityUID,generateEntityProperties(node));
                                    break;
                                case OperationType_Update_RelationEntityProperty:
                                    doUpdateRelationEntityProperty(targetEntityKind,targetEntityUID,generateEntityProperties(node));
                                    break;
                                case OperationType_Remove_ConceptionEntityProperty:
                                    doRemoveConceptionEntityProperty(targetEntityKind,targetEntityUID,generateEntityProperties(node));
                                    break;
                                case OperationType_Remove_RelationEntityProperty:
                                    doRemoveRelationEntityProperty(targetEntityKind,targetEntityUID,generateEntityProperties(node));
                                    break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    //System.out.println("TextContent: "+ commonObjectsPayloadContent.getTextContent());
                }
            }
        }
    }

    private Map<String,Object> generateEntityProperties(ObjectNode node) throws IOException {
        Map<String,Object> targetPropertiesMap = new HashMap<>();
        Iterator<String> fieldNames = node.fieldNames();
        //System.out.println("-----------------------------");
        while(fieldNames.hasNext()){
            String currentField = fieldNames.next();
            if(!currentField.equals(EntityUIDProperty) && !currentField.equals(EntityKindProperty) &&
                    !currentField.equals(RelationSourceEntityUIDProperty) && !currentField.equals(RelationTargetEntityUIDProperty) &&
                    !currentField.equals(OperationTypeProperty) && !currentField.equals(OperationTimeProperty)){
                JsonNode propertyValueNode = node.get(currentField);
                if(propertyValueNode.isBigDecimal()){
                    targetPropertiesMap.put(currentField,node.decimalValue());
                }else if(propertyValueNode.isBigInteger()){
                    targetPropertiesMap.put(currentField,node.bigIntegerValue());
                }else if(propertyValueNode.isBinary()){
                    targetPropertiesMap.put(currentField,node.binaryValue());
                }else if(propertyValueNode.isBoolean()){
                    targetPropertiesMap.put(currentField,node.booleanValue());
                }else if(propertyValueNode.isDouble()){
                    targetPropertiesMap.put(currentField,node.doubleValue());
                }else if(propertyValueNode.isFloat()){
                    targetPropertiesMap.put(currentField,node.floatValue());
                }else if(propertyValueNode.isInt()){
                    targetPropertiesMap.put(currentField,node.intValue());
                }else if(propertyValueNode.isLong()){
                    targetPropertiesMap.put(currentField,node.longValue());
                }else if(propertyValueNode.isShort()){
                    targetPropertiesMap.put(currentField,node.shortValue());
                }else if(propertyValueNode.isTextual()){
                    targetPropertiesMap.put(currentField,node.textValue());
                }
            }
        }
        return targetPropertiesMap;
    }

    private void doDeleteConceptionEntity(String targetEntityKind,String targetEntityUID){
        System.out.println("doDeleteConceptionEntity");
    }

    private void doDeleteRelationEntity(String targetEntityKind,String targetEntityUID){
        System.out.println("doDeleteRelationEntity");
    }

    private void doCreateConceptionEntity(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        System.out.println("doCreateConceptionEntity");
    }

    private void doCreateRelationEntity(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        System.out.println("doCreateRelationEntity");
    }

    private void doUpdateConceptionEntityProperty(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        System.out.println("doUpdateConceptionEntityProperty");
    }

    private void doUpdateRelationEntityProperty(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        System.out.println("doUpdateRelationEntityProperty");
    }

    private void doRemoveConceptionEntityProperty(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        System.out.println("doRemoveConceptionEntityProperty");
    }

    private void doRemoveRelationEntityProperty(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        System.out.println("doRemoveRelationEntityProperty");
    }
}