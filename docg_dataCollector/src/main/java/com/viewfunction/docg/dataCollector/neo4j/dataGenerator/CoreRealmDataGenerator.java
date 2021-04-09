package com.viewfunction.docg.dataCollector.neo4j.dataGenerator;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.viewfunction.docg.dataCollector.eventStreaming.exception.ConfigurationErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.exception.MessageFormatErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.exception.MessageHandleErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.exception.SchemaFormatErrorException;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.CommonObjectsMessageSender;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.MessageSentEventHandler;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload.CommonObjectsMessageTargetInfo;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload.CommonObjectsPayloadContent;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload.CommonObjectsPayloadContentType;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload.CommonObjectsPayloadMetaInfo;
import com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.util.EventStreamingServicePropertiesHandler;
import com.viewfunction.docg.dataCollector.payload.RelationEntityMetaInfo;
import org.apache.commons.codec.binary.Base64;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.event.LabelEntry;
import org.neo4j.graphdb.event.PropertyEntry;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.logging.Log;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.*;

public class CoreRealmDataGenerator {

    private static String senderId =null;
    private static String senderIP =null;
    private static final String textContentEncodeAlgorithm = "BASE64";

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

    static{
        InetAddress ia=null;
        try {
            ia=ia.getLocalHost();
            senderId=ia.getHostName();
            senderIP=ia.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateCoreRealmDataPayload(final TransactionData data, final Object state, Log messageLog){
        List<String> DELETE_NODE_ID_List = new ArrayList<>();
        List<String> DELETE_RELATION_ID_List = new ArrayList<>();
        List<String> CREATE_NODE_ID_List = new ArrayList<>();
        List<String> CREATE_RELATION_ID_List = new ArrayList<>();
        Map<String,List<String>> ASSIGNED_NODE_LABELS_MAP = new HashMap<>();
        Map<String,List<String>> REMOVED_NODE_LABELS_MAP = new HashMap<>();

        Map<String,Map<String,Object>> ASSIGNED_NODE_PROPERTIES_MAP = new HashMap<>();
        Map<String,Map<String,Object>> UPDATED_NODE_PROPERTIES_MAP = new HashMap<>();
        Map<String,Map<String,Object>> REMOVED_NODE_PROPERTIES_MAP = new HashMap<>();

        Map<String,Map<String,Object>> ASSIGNED_RELATION_PROPERTIES_MAP = new HashMap<>();
        Map<String,Map<String,Object>> UPDATED_RELATION_PROPERTIES_MAP = new HashMap<>();
        Map<String,Map<String,Object>> REMOVED_RELATION_PROPERTIES_MAP = new HashMap<>();

        Map<String, RelationEntityMetaInfo> ASSIGNED_RELATION_METAINFO_MAP =  new HashMap<>();
        Map<String, RelationEntityMetaInfo> REMOVED_RELATION_METAINFO_MAP =  new HashMap<>();

        Iterable<Node> deletedNodesIter = data.deletedNodes();
        if(deletedNodesIter != null){
            for(Node currentDeleteNode : deletedNodesIter){
                DELETE_NODE_ID_List.add(""+currentDeleteNode.getId());
            }
        }

        Iterable<Relationship> deletedRelations = data.deletedRelationships();
        if(deletedRelations != null){
            for(Relationship currentDeleteRelationship : deletedRelations){
                DELETE_RELATION_ID_List.add(""+currentDeleteRelationship.getId());
                RelationEntityMetaInfo relationEntityMetaInfo = new RelationEntityMetaInfo(
                        currentDeleteRelationship.getType().name(),""+currentDeleteRelationship.getId(),
                        ""+currentDeleteRelationship.getStartNodeId(),""+currentDeleteRelationship.getEndNodeId()
                );
                REMOVED_RELATION_METAINFO_MAP.put(""+currentDeleteRelationship.getId(),relationEntityMetaInfo);
            }
        }

        Iterable<LabelEntry> assignedLabels = data.assignedLabels();
        if(assignedLabels != null){
            for(LabelEntry currentAssignedLabelEntry : assignedLabels){
                String labelName = currentAssignedLabelEntry.label().name();
                String nodeId = ""+currentAssignedLabelEntry.node().getId();
                if(ASSIGNED_NODE_LABELS_MAP.containsKey(nodeId)){
                    ASSIGNED_NODE_LABELS_MAP.get(nodeId).add(labelName);
                }else{
                    List<String> labelNameList = new ArrayList<>();
                    labelNameList.add(labelName);
                    ASSIGNED_NODE_LABELS_MAP.put(nodeId,labelNameList);
                }
            }
        }

        Iterable<LabelEntry> removedLabels = data.removedLabels();
        if(removedLabels != null){
            for(LabelEntry currentRemovedLabelEntry : removedLabels){
                String labelName = currentRemovedLabelEntry.label().name();
                String nodeId = ""+currentRemovedLabelEntry.node().getId();
                if(REMOVED_NODE_LABELS_MAP.containsKey(nodeId)){
                    REMOVED_NODE_LABELS_MAP.get(nodeId).add(labelName);
                }else{
                    List<String> labelNameList = new ArrayList<>();
                    labelNameList.add(labelName);
                    REMOVED_NODE_LABELS_MAP.put(nodeId,labelNameList);
                }
            }
        }

        Iterable<Node> createdNodes = data.createdNodes();
        if(createdNodes != null){
            for(Node currentCreatedNode : createdNodes){
                CREATE_NODE_ID_List.add(""+currentCreatedNode.getId());
            }
        }

        Iterable<Relationship> createdRelations = data.createdRelationships();
        if(createdRelations != null){
            for(Relationship currentCreatedRelationship : createdRelations){
                CREATE_RELATION_ID_List.add(""+currentCreatedRelationship.getId());
                RelationEntityMetaInfo relationEntityMetaInfo = new RelationEntityMetaInfo(
                        currentCreatedRelationship.getType().name(),""+currentCreatedRelationship.getId(),
                        ""+currentCreatedRelationship.getStartNodeId(),""+currentCreatedRelationship.getEndNodeId()
                );
                ASSIGNED_RELATION_METAINFO_MAP.put(""+currentCreatedRelationship.getId(),relationEntityMetaInfo);
            }
        }

        Iterable<PropertyEntry<Node>> assignedNodeProperties = data.assignedNodeProperties();
        if(assignedNodeProperties != null){
            for(PropertyEntry<Node> currentAssignedNodeProperties : assignedNodeProperties){
                if(currentAssignedNodeProperties.entity() != null){
                    String propertyNodeID = ""+currentAssignedNodeProperties.entity().getId();
                    if(CREATE_NODE_ID_List.contains(propertyNodeID)){
                        //new created node's property
                        setupTransactionDataProperty(ASSIGNED_NODE_PROPERTIES_MAP,""+currentAssignedNodeProperties.entity().getId(),
                                currentAssignedNodeProperties.key(),currentAssignedNodeProperties.value());
                    }else{
                        //update exist node's property
                        setupTransactionDataProperty(UPDATED_NODE_PROPERTIES_MAP,""+currentAssignedNodeProperties.entity().getId(),
                                currentAssignedNodeProperties.key(),currentAssignedNodeProperties.value());
                    }
                }
            }
        }

        Iterable<PropertyEntry<Relationship>> assignedRelationshipProperties = data.assignedRelationshipProperties();
        if(assignedRelationshipProperties != null){
            for(PropertyEntry<Relationship> currentAssignedRelationProperties : assignedRelationshipProperties){
                if(currentAssignedRelationProperties.entity() != null){
                    String propertyRelationID = ""+currentAssignedRelationProperties.entity().getId();
                    if(CREATE_RELATION_ID_List.contains(propertyRelationID)){
                        //new created relation's property
                        setupTransactionDataProperty(ASSIGNED_RELATION_PROPERTIES_MAP,""+currentAssignedRelationProperties.entity().getId(),
                                currentAssignedRelationProperties.key(),currentAssignedRelationProperties.value());
                    }else{
                        //update exist relation's property
                        setupTransactionDataProperty(UPDATED_RELATION_PROPERTIES_MAP,""+currentAssignedRelationProperties.entity().getId(),
                                currentAssignedRelationProperties.key(),currentAssignedRelationProperties.value());
                    }
                }
            }
        }

        Iterable<PropertyEntry<Node>> removedNodeProperties = data.removedNodeProperties();
        if (removedNodeProperties != null) {
            for (PropertyEntry<Node> currentRemovedNodeProperties : removedNodeProperties) {
                if (currentRemovedNodeProperties.entity() != null) {
                    String propertyNodeID = ""+currentRemovedNodeProperties.entity().getId();
                    if(DELETE_NODE_ID_List.contains(propertyNodeID)){
                        //already removed node's property
                    }else{
                        //update exist node's property
                        setupTransactionDataProperty(REMOVED_NODE_PROPERTIES_MAP,""+currentRemovedNodeProperties.entity().getId(),
                                currentRemovedNodeProperties.key(),currentRemovedNodeProperties.value());
                    }
                }
            }
        }

        Iterable<PropertyEntry<Relationship>> removedRelationshipProperties = data.removedRelationshipProperties();
        if (removedRelationshipProperties != null) {
            for (PropertyEntry<Relationship> currentRemovedRelationProperties : removedRelationshipProperties) {
                if (currentRemovedRelationProperties.entity() != null) {
                    String propertyRelationID = ""+currentRemovedRelationProperties.entity().getId();
                    if(DELETE_RELATION_ID_List.contains(propertyRelationID)){
                        //already removed relation's property
                    }else{
                        //update exist relation's property
                        setupTransactionDataProperty(REMOVED_RELATION_PROPERTIES_MAP,""+currentRemovedRelationProperties.entity().getId(),
                                currentRemovedRelationProperties.key(),currentRemovedRelationProperties.value());
                    }
                }
            }
        }

        CommonObjectsPayloadMetaInfo commonObjectsPayloadMetaInfo =new CommonObjectsPayloadMetaInfo();
        commonObjectsPayloadMetaInfo.setSenderId(senderId);
        commonObjectsPayloadMetaInfo.setSenderIP(senderIP);
        try {
            commonObjectsPayloadMetaInfo.setSenderGroup(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.SenderGroup));
            commonObjectsPayloadMetaInfo.setSenderCategory(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.SenderCategory));
            commonObjectsPayloadMetaInfo.setPayloadType(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PayloadType));
            commonObjectsPayloadMetaInfo.setPayloadTypeDesc(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PayloadTypeDesc));
            commonObjectsPayloadMetaInfo.setPayloadProcessor(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PayloadProcessor));
            commonObjectsPayloadMetaInfo.setPayloadClassification(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PayloadClassification));
        } catch (ConfigurationErrorException e) {
            e.printStackTrace();
        }

        CommonObjectsMessageTargetInfo commonObjectsMessageTargetInfo =new CommonObjectsMessageTargetInfo();
        try {
            commonObjectsMessageTargetInfo.setDestinationTopic(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.DestinationTopic));
            commonObjectsMessageTargetInfo.setPayloadKey(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.PayloadKey));
        } catch (ConfigurationErrorException e) {
            e.printStackTrace();
        }

        CommonObjectsMessageSender commonObjectsMessageSender = null;
        try {
            commonObjectsMessageSender = new CommonObjectsMessageSender(new MessageSentEventHandler(){
                @Override
                public void operateMetaData(long offset, long timestamp, String topic, int partition) {
                    //messageLog.info(offset+" - "+timestamp+" - "+topic+" - "+partition);
                }
            });

            commonObjectsMessageSender.beginMessageSendBatch();

            //Send Delete ConceptionEntity message
            for(String currentEntityUID:DELETE_NODE_ID_List){
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put(OperationTypeProperty,OperationType_Delete_ConceptionEntity);
                node.put(OperationTimeProperty,data.getCommitTime());
                node.put(EntityUIDProperty,currentEntityUID);
                if(REMOVED_NODE_LABELS_MAP.containsKey(currentEntityUID) && REMOVED_NODE_LABELS_MAP.get(currentEntityUID).size() > 0){
                    node.put(EntityKindProperty,REMOVED_NODE_LABELS_MAP.get(currentEntityUID).get(0));
                }
                sendMessage(commonObjectsMessageSender,commonObjectsPayloadMetaInfo, node, commonObjectsMessageTargetInfo);
            }
            //Send Delete RelationEntity message
            for(String currentEntityUID:DELETE_RELATION_ID_List){
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put(OperationTypeProperty,OperationType_Delete_RelationEntity);
                node.put(OperationTimeProperty,data.getCommitTime());
                node.put(EntityUIDProperty,currentEntityUID);
                if(REMOVED_RELATION_METAINFO_MAP.containsKey(currentEntityUID)){
                    RelationEntityMetaInfo relationEntityMetaInfo = REMOVED_RELATION_METAINFO_MAP.get(currentEntityUID);
                    node.put(EntityKindProperty,relationEntityMetaInfo.getRelationKind());
                    node.put(RelationSourceEntityUIDProperty,relationEntityMetaInfo.getSourceEntityUID());
                    node.put(RelationTargetEntityUIDProperty,relationEntityMetaInfo.getTargetEntityUID());
                }
                sendMessage(commonObjectsMessageSender,commonObjectsPayloadMetaInfo, node, commonObjectsMessageTargetInfo);
            }
            //Send Create ConceptionEntity message
            for(String currentEntityUID:CREATE_NODE_ID_List){
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put(OperationTypeProperty,OperationType_Create_ConceptionEntity);
                node.put(OperationTimeProperty,data.getCommitTime());
                node.put(EntityUIDProperty,currentEntityUID);
                if(ASSIGNED_NODE_LABELS_MAP.containsKey(currentEntityUID) && ASSIGNED_NODE_LABELS_MAP.get(currentEntityUID).size() > 0){
                    node.put(EntityKindProperty,ASSIGNED_NODE_LABELS_MAP.get(currentEntityUID).toString());
                }
                if(ASSIGNED_NODE_PROPERTIES_MAP.containsKey(currentEntityUID)){
                    Map<String,Object> nodePropertiesMap = ASSIGNED_NODE_PROPERTIES_MAP.get(currentEntityUID);
                    for (Map.Entry<String, Object> entry : nodePropertiesMap.entrySet()) {
                        setNodeProperty(node,entry.getKey(),entry.getValue());
                    }
                }
                sendMessage(commonObjectsMessageSender,commonObjectsPayloadMetaInfo, node, commonObjectsMessageTargetInfo);
            }
            //Send Create RelationEntity message
            for(String currentEntityUID:CREATE_RELATION_ID_List){
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put(OperationTypeProperty,OperationType_Create_RelationEntity);
                node.put(OperationTimeProperty,data.getCommitTime());
                node.put(EntityUIDProperty,currentEntityUID);
                if(ASSIGNED_RELATION_METAINFO_MAP.containsKey(currentEntityUID)){
                    RelationEntityMetaInfo relationEntityMetaInfo = ASSIGNED_RELATION_METAINFO_MAP.get(currentEntityUID);
                    node.put(EntityKindProperty,relationEntityMetaInfo.getRelationKind());
                    node.put(RelationSourceEntityUIDProperty,relationEntityMetaInfo.getSourceEntityUID());
                    node.put(RelationTargetEntityUIDProperty,relationEntityMetaInfo.getTargetEntityUID());
                }
                if(ASSIGNED_RELATION_PROPERTIES_MAP.containsKey(currentEntityUID)){
                    Map<String,Object> relationPropertiesMap = ASSIGNED_RELATION_PROPERTIES_MAP.get(currentEntityUID);
                    for (Map.Entry<String, Object> entry : relationPropertiesMap.entrySet()) {
                        setNodeProperty(node,entry.getKey(),entry.getValue());
                    }
                }
                sendMessage(commonObjectsMessageSender,commonObjectsPayloadMetaInfo, node, commonObjectsMessageTargetInfo);
            }
            //Send update ConceptionEntity Property message
            for (Map.Entry<String, Map<String,Object>> entry : UPDATED_NODE_PROPERTIES_MAP.entrySet()) {
                String currentEntityUID = entry.getKey();
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put(OperationTypeProperty,OperationType_Update_ConceptionEntityProperty);
                node.put(OperationTimeProperty,data.getCommitTime());
                node.put(EntityUIDProperty,currentEntityUID);
                Map<String,Object> currentEntityUpdatedProperties = entry.getValue();
                for (Map.Entry<String, Object> propertiesEntry : currentEntityUpdatedProperties.entrySet()){
                    setNodeProperty(node,propertiesEntry.getKey(),propertiesEntry.getValue());
                }
                sendMessage(commonObjectsMessageSender,commonObjectsPayloadMetaInfo, node, commonObjectsMessageTargetInfo);
            }
            //Send update RelationEntity Property message
            for (Map.Entry<String, Map<String,Object>> entry : UPDATED_RELATION_PROPERTIES_MAP.entrySet()) {
                String currentEntityUID = entry.getKey();
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put(OperationTypeProperty,OperationType_Update_RelationEntityProperty);
                node.put(OperationTimeProperty,data.getCommitTime());
                node.put(EntityUIDProperty,currentEntityUID);
                Map<String,Object> currentEntityUpdatedProperties = entry.getValue();
                for (Map.Entry<String, Object> propertiesEntry : currentEntityUpdatedProperties.entrySet()){
                    setNodeProperty(node,propertiesEntry.getKey(),propertiesEntry.getValue());
                }
                sendMessage(commonObjectsMessageSender,commonObjectsPayloadMetaInfo, node, commonObjectsMessageTargetInfo);
            }
            //Send delete ConceptionEntity Property message
            for (Map.Entry<String, Map<String,Object>> entry : REMOVED_NODE_PROPERTIES_MAP.entrySet()) {
                String currentEntityUID = entry.getKey();
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put(OperationTypeProperty,OperationType_Remove_ConceptionEntityProperty);
                node.put(OperationTimeProperty,data.getCommitTime());
                node.put(EntityUIDProperty,currentEntityUID);
                Map<String,Object> currentEntityUpdatedProperties = entry.getValue();
                for (Map.Entry<String, Object> propertiesEntry : currentEntityUpdatedProperties.entrySet()){
                    setNodeProperty(node,propertiesEntry.getKey(),propertiesEntry.getValue());
                }
                sendMessage(commonObjectsMessageSender,commonObjectsPayloadMetaInfo, node, commonObjectsMessageTargetInfo);
            }
            //Send delete RelationEntity Property message
            for (Map.Entry<String, Map<String,Object>> entry : REMOVED_RELATION_PROPERTIES_MAP.entrySet()) {
                String currentEntityUID = entry.getKey();
                ObjectNode node = JsonNodeFactory.instance.objectNode();
                node.put(OperationTypeProperty,OperationType_Remove_RelationEntityProperty);
                node.put(OperationTimeProperty,data.getCommitTime());
                node.put(EntityUIDProperty,currentEntityUID);
                Map<String,Object> currentEntityUpdatedProperties = entry.getValue();
                for (Map.Entry<String, Object> propertiesEntry : currentEntityUpdatedProperties.entrySet()){
                    setNodeProperty(node,propertiesEntry.getKey(),propertiesEntry.getValue());
                }
                sendMessage(commonObjectsMessageSender,commonObjectsPayloadMetaInfo, node, commonObjectsMessageTargetInfo);
            }

            commonObjectsMessageSender.finishMessageSendBatch();
        } catch (ConfigurationErrorException | SchemaFormatErrorException | MessageFormatErrorException | MessageHandleErrorException e) {
            messageLog.info(e.getMessage());
        }

        boolean displayMessageInNeo4jLog = false;
        try {
            displayMessageInNeo4jLog = Boolean.parseBoolean(EventStreamingServicePropertiesHandler.getPropertyValue(EventStreamingServicePropertiesHandler.ShowOperationMessage));
        } catch (ConfigurationErrorException e) {
            e.printStackTrace();
        }

        if(displayMessageInNeo4jLog){
            messageLog.info("==== Executes CoreRealm Data Collector Listener:");
            messageLog.info("  DELETE_NODE_IDS: "+DELETE_NODE_ID_List.toString());
            messageLog.info("  DELETE_RELATION_IDS: "+DELETE_RELATION_ID_List.toString());
            messageLog.info("  CREATE_NODE_IDS: "+CREATE_NODE_ID_List.toString());
            messageLog.info("  CREATE_RELATION_IDS: "+CREATE_RELATION_ID_List.toString());
            messageLog.info("  ASSIGNED_NODE_LABELS: "+ASSIGNED_NODE_LABELS_MAP.toString());
            messageLog.info("  REMOVED_NODE_LABELS: "+REMOVED_NODE_LABELS_MAP.toString());
            messageLog.info("  ASSIGNED_NODE_PROPERTIES: "+ASSIGNED_NODE_PROPERTIES_MAP.toString());
            messageLog.info("  UPDATED_NODE_PROPERTIES: "+UPDATED_NODE_PROPERTIES_MAP.toString());
            messageLog.info("  REMOVED_NODE_PROPERTIES: "+REMOVED_NODE_PROPERTIES_MAP.toString());
            messageLog.info("  ASSIGNED_RELATION_PROPERTIES: "+ASSIGNED_RELATION_PROPERTIES_MAP.toString());
            messageLog.info("  UPDATED_RELATION_PROPERTIES: "+UPDATED_RELATION_PROPERTIES_MAP.toString());
            messageLog.info("  REMOVED_RELATION_PROPERTIES: "+REMOVED_RELATION_PROPERTIES_MAP.toString());
            messageLog.info("  ASSIGNED_RELATION_NUMBER: "+ASSIGNED_RELATION_METAINFO_MAP.size());
        }

        DELETE_NODE_ID_List.clear();
        DELETE_NODE_ID_List = null;
        DELETE_RELATION_ID_List.clear();
        DELETE_RELATION_ID_List = null;
        CREATE_NODE_ID_List.clear();
        CREATE_NODE_ID_List = null;
        CREATE_RELATION_ID_List.clear();
        CREATE_RELATION_ID_List = null;
        ASSIGNED_NODE_LABELS_MAP.clear();
        ASSIGNED_NODE_LABELS_MAP = null;
        REMOVED_NODE_LABELS_MAP.clear();
        REMOVED_NODE_LABELS_MAP = null;
        ASSIGNED_NODE_PROPERTIES_MAP.clear();
        ASSIGNED_NODE_PROPERTIES_MAP = null;
        UPDATED_NODE_PROPERTIES_MAP.clear();
        UPDATED_NODE_PROPERTIES_MAP = null;
        REMOVED_NODE_PROPERTIES_MAP.clear();
        REMOVED_NODE_PROPERTIES_MAP = null;
        ASSIGNED_RELATION_PROPERTIES_MAP.clear();
        ASSIGNED_RELATION_PROPERTIES_MAP = null;
        UPDATED_RELATION_PROPERTIES_MAP.clear();
        UPDATED_RELATION_PROPERTIES_MAP = null;
        REMOVED_RELATION_PROPERTIES_MAP.clear();
        REMOVED_RELATION_PROPERTIES_MAP = null;
        ASSIGNED_RELATION_METAINFO_MAP.clear();
        ASSIGNED_RELATION_METAINFO_MAP = null;
        REMOVED_RELATION_METAINFO_MAP.clear();
        REMOVED_RELATION_METAINFO_MAP = null;
    }

    private static void setupTransactionDataProperty(Map<String,Map<String,Object>> dataMap,String entityId,String propertyName,Object propertyValue){
        if(dataMap.containsKey(entityId)){
            dataMap.get(entityId).put(propertyName,propertyValue);
        }else{
            Map<String,Object> propertyMap = new HashMap<>();
            propertyMap.put(propertyName,propertyValue);
            dataMap.put(entityId,propertyMap);
        }
    }

    private static void sendMessage(CommonObjectsMessageSender commonObjectsMessageSender,CommonObjectsPayloadMetaInfo commonObjectsPayloadMetaInfo,
                                    ObjectNode node,CommonObjectsMessageTargetInfo commonObjectsMessageTargetInfo) throws SchemaFormatErrorException, MessageHandleErrorException, MessageFormatErrorException {
        CommonObjectsPayloadContent commonObjectsPayloadContent =new CommonObjectsPayloadContent();
        commonObjectsPayloadContent.setIncludingContent(CommonObjectsPayloadContentType.TEXT);
        commonObjectsPayloadContent.setTextContentEncoded(true);
        commonObjectsPayloadContent.setTextContentEncodeAlgorithm(textContentEncodeAlgorithm);
        byte[] encodedBytes = Base64.encodeBase64(node.toString().getBytes());
        commonObjectsPayloadContent.setTextContent(new String(encodedBytes));
        commonObjectsMessageSender.sendCommonObjectsMessage(commonObjectsPayloadMetaInfo, commonObjectsPayloadContent, commonObjectsMessageTargetInfo);
    }

    private static void setNodeProperty(ObjectNode node,String propertyName,Object propertyOrgValue){
        if(propertyOrgValue instanceof String){
            node.put(propertyName,(String)propertyOrgValue);
        }else if(propertyOrgValue instanceof Integer){
            node.put(propertyName,(Integer)propertyOrgValue);
        }else if(propertyOrgValue instanceof Long){
            node.put(propertyName,(Long)propertyOrgValue);
        }else if(propertyOrgValue instanceof Float){
            node.put(propertyName,(Float)propertyOrgValue);
        }else if(propertyOrgValue instanceof Double){
            node.put(propertyName,(Double)propertyOrgValue);
        }else if(propertyOrgValue instanceof Boolean){
            node.put(propertyName,(Boolean)propertyOrgValue);
        }else if(propertyOrgValue instanceof Short){
            node.put(propertyName,(Short)propertyOrgValue);
        }else if(propertyOrgValue instanceof BigDecimal){
            node.put(propertyName,(BigDecimal)propertyOrgValue);
        }else if(propertyOrgValue instanceof BigInteger){
            node.put(propertyName,(BigInteger)propertyOrgValue);
        }else if(propertyOrgValue instanceof byte[]){
            node.put(propertyName,(byte[])propertyOrgValue);
        }else{
            node.put(propertyName,propertyOrgValue.toString());
        }
    }
}
