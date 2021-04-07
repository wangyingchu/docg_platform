package com.viewfunction.docg.dataCollector.neo4j.dataGenerator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.event.LabelEntry;
import org.neo4j.graphdb.event.PropertyEntry;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreRealmDataGenerator {

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

                messageLog.info(currentCreatedRelationship.getType().name());
                messageLog.info(""+currentCreatedRelationship.getStartNodeId());
                messageLog.info(""+currentCreatedRelationship.getEndNodeId());
            }
        }

        Iterable<PropertyEntry<Node>> assignedNodeProperties = data.assignedNodeProperties();
        if(assignedNodeProperties != null){
            for(PropertyEntry<Node> currentAssignedNodeProperties : assignedNodeProperties){
                if(currentAssignedNodeProperties.entity() != null){
                    String propertyNodeID = ""+currentAssignedNodeProperties.entity().getId();
                    if(CREATE_NODE_ID_List.contains(propertyNodeID)){
                        //new created node's property
                        //messageLog.info("New NODE PROPERTY:"+currentAssignedNodeProperties.entity().getId()+" - "+currentAssignedNodeProperties.key()+" - " + currentAssignedNodeProperties.value());
                        setupTransactionDataProperty(ASSIGNED_NODE_PROPERTIES_MAP,""+currentAssignedNodeProperties.entity().getId(),
                                currentAssignedNodeProperties.key(),currentAssignedNodeProperties.value());
                    }else{
                        //update exist node's property
                        //messageLog.info("EXIST NODE PROPERTY:"+currentAssignedNodeProperties.entity().getId()+" - "+currentAssignedNodeProperties.key()+" - " + currentAssignedNodeProperties.value());
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
                        //messageLog.info("NEW RELATIONSHIP PROPERTY:"+currentAssignedRelationProperties.entity().getId()+"/n/r"+currentAssignedRelationProperties.key()+" - "+currentAssignedRelationProperties.value());
                        setupTransactionDataProperty(ASSIGNED_RELATION_PROPERTIES_MAP,""+currentAssignedRelationProperties.entity().getId(),
                                currentAssignedRelationProperties.key(),currentAssignedRelationProperties.value());
                    }else{
                        //update exist relation's property
                        //messageLog.info("EXIST RELATIONSHIP PROPERTY:"+currentAssignedRelationProperties.entity().getId()+"/n/r"+currentAssignedRelationProperties.key()+" - "+currentAssignedRelationProperties.value());
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
                        //messageLog.info("EXIST NODE PROPERTY:"+currentRemovedNodeProperties.entity().getId()+" "+currentRemovedNodeProperties.key()+" - "+currentRemovedNodeProperties.value());
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
                        messageLog.info("EXIST RELATIONSHIP PROPERTY:"+currentRemovedRelationProperties.entity().getId()+" "+currentRemovedRelationProperties.key()+" - "+currentRemovedRelationProperties.value());
                        setupTransactionDataProperty(REMOVED_RELATION_PROPERTIES_MAP,""+currentRemovedRelationProperties.entity().getId(),
                                currentRemovedRelationProperties.key(),currentRemovedRelationProperties.value());
                    }
                }
            }
        }
        messageLog.info("=================================");
        messageLog.info(DELETE_NODE_ID_List.toString());
        messageLog.info(DELETE_RELATION_ID_List.toString());
        messageLog.info(CREATE_NODE_ID_List.toString());
        messageLog.info(CREATE_RELATION_ID_List.toString());
        messageLog.info(ASSIGNED_NODE_LABELS_MAP.toString());
        messageLog.info(REMOVED_NODE_LABELS_MAP.toString());
        messageLog.info(ASSIGNED_NODE_PROPERTIES_MAP.toString());
        messageLog.info(UPDATED_NODE_PROPERTIES_MAP.toString());
        messageLog.info(REMOVED_NODE_PROPERTIES_MAP.toString());
        messageLog.info(ASSIGNED_RELATION_PROPERTIES_MAP.toString());
        messageLog.info(UPDATED_RELATION_PROPERTIES_MAP.toString());
        messageLog.info(REMOVED_RELATION_PROPERTIES_MAP.toString());

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
}
