package com.viewfunction.docg.dataCollector.neo4j.dataGenerator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.event.PropertyEntry;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.logging.Log;

public class CoreRealmDataGenerator {

    private enum DataOperationType {CREATE_NODE,DELETE_NODE,CREATE_RELATIONSHIP,DELETE_RELATIONSHIP,UPDATE_NODE,UPDATE_RELATIONSHIP}

    public static void generateCoreRealmDataPayload(final TransactionData data, final Object state, Log messageLog){
        DataOperationType currentDataOperationType = null;

        messageLog.info("================================");
        messageLog.info("STEP -> 0");
        if(state != null){
            messageLog.info(state.toString());
        }
        messageLog.info(""+data.getCommitTime());
        messageLog.info(data.metaData().toString());


        messageLog.info("STEP -> 1");
        Iterable<Node> createdNodes = data.createdNodes();
        if(createdNodes != null){
            currentDataOperationType = DataOperationType.CREATE_NODE;
            for(Node currentCreatedNode : createdNodes){
                messageLog.info("CREATE NODE:"+currentCreatedNode.getId()+"/n/r");                        //+currentCreatedNode.getAllProperties());
            }
        }

        messageLog.info("STEP -> 2");
        Iterable<Relationship> createdRelations = data.createdRelationships();
        if(createdRelations != null){
            currentDataOperationType = DataOperationType.CREATE_RELATIONSHIP;
            for(Relationship currentCreatedRelationship : createdRelations){
                messageLog.info("CREATE RELATION:"+currentCreatedRelationship.getId()+"/n/r");
                        //currentCreatedRelationship.getAllProperties());
            }
        }


        messageLog.info("STEP -> 3");
        Iterable<Node> deletedNodesIter = data.deletedNodes();
        if(deletedNodesIter != null){
            currentDataOperationType = DataOperationType.DELETE_NODE;
            for(Node currentDeleteNode : deletedNodesIter){
                messageLog.info("DELETE NODE:"+currentDeleteNode.getId());
            }
        }
        messageLog.info("STEP -> 4");
        Iterable<Relationship> deletedRelations = data.deletedRelationships();
        if(deletedRelations != null){
            currentDataOperationType = DataOperationType.DELETE_RELATIONSHIP;
            for(Relationship currentDeleteRelationship : deletedRelations){
                messageLog.info("DELETE RELATION:"+currentDeleteRelationship.getId());
            }
        }

        if(!currentDataOperationType.equals(DataOperationType.DELETE_NODE) & currentDataOperationType.equals(DataOperationType.DELETE_RELATIONSHIP)){
            messageLog.info("STEP -> 5");
            Iterable<PropertyEntry<Node>> assignedNodeProperties = data.assignedNodeProperties();
            if(assignedNodeProperties != null){
                currentDataOperationType = DataOperationType.UPDATE_NODE;
                for(PropertyEntry<Node> currentAssignedNodeProperties : assignedNodeProperties){
                    if(currentAssignedNodeProperties.entity() != null){
                        messageLog.info("ASSIGNED NODE PROPERTY:"+currentAssignedNodeProperties.entity().getId()+"/n/r"+currentAssignedNodeProperties.key()+" - "+currentAssignedNodeProperties.value());

                    }
                }
            }
            messageLog.info("STEP -> 6");
            Iterable<PropertyEntry<Relationship>> assignedRelationshipProperties = data.assignedRelationshipProperties();
            if(assignedRelationshipProperties != null){
                currentDataOperationType = DataOperationType.UPDATE_RELATIONSHIP;
                for(PropertyEntry<Relationship> currentAssignedRelationProperties : assignedRelationshipProperties){
                    if(currentAssignedRelationProperties.entity() != null){
                        messageLog.info("ASSIGNED RELATIONSHIP PROPERTY:"+currentAssignedRelationProperties.entity().getId()+"/n/r"+currentAssignedRelationProperties.key()+" - "+currentAssignedRelationProperties.value());
                    }
                }
            }
            messageLog.info("STEP -> 7");
            Iterable<PropertyEntry<Node>> removedNodeProperties = data.removedNodeProperties();
            if(removedNodeProperties != null){
                currentDataOperationType = DataOperationType.UPDATE_NODE;
                for(PropertyEntry<Node> currentRemovedNodeProperties : removedNodeProperties){
                    if(currentRemovedNodeProperties.entity() != null){
                        messageLog.info("REMOVED NODE PROPERTY:"+currentRemovedNodeProperties.entity().getId()+"/n/r"+currentRemovedNodeProperties.key()+" - "+currentRemovedNodeProperties.value());
                    }
                }
            }
            messageLog.info("STEP -> 8");
            Iterable<PropertyEntry<Relationship>> removedRelationshipProperties = data.removedRelationshipProperties();
            if(removedRelationshipProperties != null) {
                currentDataOperationType = DataOperationType.UPDATE_RELATIONSHIP;
                for (PropertyEntry<Relationship> currentRemovedRelationProperties : removedRelationshipProperties) {
                    if(currentRemovedRelationProperties.entity() != null){
                        messageLog.info("REMOVED RELATIONSHIP PROPERTY:" + currentRemovedRelationProperties.entity().getId() + "/n/r" + currentRemovedRelationProperties.key() + " - " + currentRemovedRelationProperties.value());

                    }
                }
            }
        }

        messageLog.info(" "+currentDataOperationType.toString());
        messageLog.info("================================");
    }
}
