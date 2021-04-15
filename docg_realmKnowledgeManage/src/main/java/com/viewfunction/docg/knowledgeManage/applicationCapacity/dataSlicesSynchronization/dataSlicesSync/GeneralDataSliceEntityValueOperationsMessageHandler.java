package com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.dataSlicesSync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.DataSlicesSynchronizationApplication;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsPayloadMetaInfo;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.payload.CommonObjectsReceivedMessage;
import com.viewfunction.docg.knowledgeManage.applicationService.eventStreaming.kafka.receiver.CommonObjectsMessageHandler;
import com.viewfunction.docg.knowledgeManage.consoleApplication.util.ApplicationLauncherUtil;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
    private Map<String, List<DataPropertyInfo>> conceptionKindDataPropertiesMap;
    private Map<String,List<DataPropertyInfo>> relationKindDataPropertiesMap;
    private DataServiceInvoker dataServiceInvoker;

    public GeneralDataSliceEntityValueOperationsMessageHandler(Map<Object,Object> commandContextDataMap,DataServiceInvoker dataServiceInvoker){
        this.commandContextDataMap = commandContextDataMap;
        this.objectMapper = new ObjectMapper();
        this.targetPayloadClassification = ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.payloadClassification") != null ?
                ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.payloadClassification").trim() : null;
        this.targetPayloadType = ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.payloadType") != null ?
                ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.payloadType").trim() : null;
        this.SYNC_LISTENING_START_TIME_LONG_VALUE = ((Date)this.commandContextDataMap.get(DataSlicesSynchronizationApplication.SYNC_LISTENING_START_TIME)).getTime();
        this.dataServiceInvoker = dataServiceInvoker;
        setUpEntityKindsMetaInfo();
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
        if(conceptionKindDataPropertiesMap.containsKey(targetEntityKind)){
            DataSliceSyncUtil.deleteDataFromSlice(this.dataServiceInvoker,targetEntityKind,targetEntityUID);
        }
    }

    private void doDeleteRelationEntity(String targetEntityKind,String targetEntityUID){
        if(relationKindDataPropertiesMap.containsKey(targetEntityKind)){
            DataSliceSyncUtil.deleteDataFromSlice(this.dataServiceInvoker,targetEntityKind,targetEntityUID);
        }
    }

    private void doCreateConceptionEntity(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        if(conceptionKindDataPropertiesMap.containsKey(targetEntityKind)){
            DataSliceSyncUtil.createDataInSlice(this.dataServiceInvoker,targetEntityKind,targetEntityUID,entityProperties,conceptionKindDataPropertiesMap,"_CONCEPTION");
        }
    }

    private void doCreateRelationEntity(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        if(relationKindDataPropertiesMap.containsKey(targetEntityKind)){
            DataSliceSyncUtil.createDataInSlice(this.dataServiceInvoker,targetEntityKind,targetEntityUID,entityProperties,relationKindDataPropertiesMap,"_RELATION");
        }
    }

    private void doUpdateConceptionEntityProperty(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        if(conceptionKindDataPropertiesMap.containsKey(targetEntityKind)){
            DataSliceSyncUtil.updateDataInSlice(this.dataServiceInvoker,targetEntityKind,targetEntityUID,entityProperties);
        }
    }

    private void doUpdateRelationEntityProperty(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        if(relationKindDataPropertiesMap.containsKey(targetEntityKind)){
            DataSliceSyncUtil.updateDataInSlice(this.dataServiceInvoker,targetEntityKind,targetEntityUID,entityProperties);
        }
    }

    private void doRemoveConceptionEntityProperty(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        //Not Support Yet
    }

    private void doRemoveRelationEntityProperty(String targetEntityKind,String targetEntityUID,Map<String,Object> entityProperties){
        //Not Support Yet
    }

    private void setUpEntityKindsMetaInfo(){
        conceptionKindDataPropertiesMap = new HashMap<>();
        relationKindDataPropertiesMap = new HashMap<>();
        String lastConceptionKindName = null;
        String lastRelationKindName = null;
        String currentHandleType = "ConceptionKind";

        File file = new File("DataSlicesSyncKindList");
        if(file.exists() && file.isFile()){
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempStr;
                while ((tempStr = reader.readLine()) != null) {
                    String currentLine = tempStr.trim();
                    if(currentLine.startsWith("ConceptionKind.")){
                        //handle ConceptionKind define
                        currentHandleType = "ConceptionKind";
                        String currentConceptionKindName = currentLine.replace("ConceptionKind.","");
                        lastConceptionKindName = currentConceptionKindName;
                    }else if(currentLine.startsWith("RelationKind.")){
                        //handle ConceptionKind define
                        currentHandleType = "RelationKind";
                        String currentRelationKindName = currentLine.replace("RelationKind.","");
                        lastRelationKindName = currentRelationKindName;
                    }else{
                        String[] propertyDefineArray = currentLine.split("    ");
                        String propertyName = propertyDefineArray[0];
                        String propertyType = propertyDefineArray[1];
                        if(currentHandleType.equals("ConceptionKind")){
                            DataSliceSyncUtil.initKindPropertyDefine(conceptionKindDataPropertiesMap,lastConceptionKindName,propertyName,propertyType);
                        }
                        if(currentHandleType.equals("RelationKind")){
                            DataSliceSyncUtil.initKindPropertyDefine(relationKindDataPropertiesMap,lastRelationKindName,propertyName,propertyType);
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}
