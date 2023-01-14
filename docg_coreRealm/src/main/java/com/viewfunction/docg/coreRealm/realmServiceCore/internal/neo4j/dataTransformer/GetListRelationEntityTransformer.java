package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.ArrayList;
import java.util.List;

public class GetListRelationEntityTransformer implements DataTransformer<List<RelationEntity>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String targetRelationKindName;
    private boolean isDistinctMode;

    public GetListRelationEntityTransformer(String targetRelationKindName,GraphOperationExecutor workingGraphOperationExecutor,boolean isDistinctMode){
        this.targetRelationKindName = targetRelationKindName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        this.isDistinctMode = isDistinctMode;
    }

    @Override
    public List<RelationEntity> transformResult(Result result) {
        List<RelationEntity> relationEntityList = new ArrayList<>();
        List<String> alreadyExistRelationEntityUIDList = this.isDistinctMode ? new ArrayList<>(): null;
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    if(nodeRecord.containsKey(CypherBuilder.operationResultName) && !nodeRecord.get(CypherBuilder.operationResultName).isNull()){
                        Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
                        Node sourceNode = nodeRecord.containsKey(CypherBuilder.sourceNodeName) ? nodeRecord.get(CypherBuilder.sourceNodeName).asNode():null;
                        Node targetNode = nodeRecord.containsKey(CypherBuilder.targetNodeName) ? nodeRecord.get(CypherBuilder.targetNodeName).asNode():null;
                        String relationType = resultRelationship.type();
                        boolean isMatchedKind;
                        if(this.targetRelationKindName == null){
                            isMatchedKind = true;
                        }else{
                            isMatchedKind = relationType.equals(targetRelationKindName)? true : false;
                        }
                        if(isMatchedKind){
                            long relationUID = resultRelationship.id();
                            String relationEntityUID = ""+relationUID;
                            String fromEntityUID = ""+resultRelationship.startNodeId();
                            String toEntityUID = ""+resultRelationship.endNodeId();
                            if(alreadyExistRelationEntityUIDList != null){
                                if(!alreadyExistRelationEntityUIDList.contains(relationEntityUID)){
                                    Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                                            new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
                                    neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                                    if(sourceNode != null){
                                        Iterable<String> sourceNodeLabels = sourceNode.labels();
                                        List<String> sourceNodeLabelList = Lists.newArrayList(sourceNodeLabels);
                                        String sourceNodeId = ""+sourceNode.id();
                                        if(sourceNodeId.equals(fromEntityUID)){
                                            neo4jRelationEntityImpl.setFromEntityConceptionKindList(sourceNodeLabelList);
                                        }else{
                                            neo4jRelationEntityImpl.setToEntityConceptionKindList(sourceNodeLabelList);
                                        }
                                    }
                                    if(targetNode != null){
                                        Iterable<String> targetNodeLabels = targetNode.labels();
                                        List<String> targetNodeLabelList = Lists.newArrayList(targetNodeLabels);
                                        String targetNodeId = ""+targetNode.id();
                                        if(targetNodeId.equals(toEntityUID)){
                                            neo4jRelationEntityImpl.setToEntityConceptionKindList(targetNodeLabelList);
                                        }else{
                                            neo4jRelationEntityImpl.setFromEntityConceptionKindList(targetNodeLabelList);
                                        }
                                    }
                                    relationEntityList.add(neo4jRelationEntityImpl);
                                    alreadyExistRelationEntityUIDList.add(relationEntityUID);
                                }
                            }else{
                                Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                                        new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
                                neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                                if(sourceNode != null){
                                    Iterable<String> sourceNodeLabels = sourceNode.labels();
                                    List<String> sourceNodeLabelList = Lists.newArrayList(sourceNodeLabels);
                                    String sourceNodeId = ""+sourceNode.id();
                                    if(sourceNodeId.equals(fromEntityUID)){
                                        neo4jRelationEntityImpl.setFromEntityConceptionKindList(sourceNodeLabelList);
                                    }else{
                                        neo4jRelationEntityImpl.setToEntityConceptionKindList(sourceNodeLabelList);
                                    }
                                }
                                if(targetNode != null){
                                    Iterable<String> targetNodeLabels = targetNode.labels();
                                    List<String> targetNodeLabelList = Lists.newArrayList(targetNodeLabels);
                                    String targetNodeId = ""+targetNode.id();
                                    if(targetNodeId.equals(toEntityUID)){
                                        neo4jRelationEntityImpl.setToEntityConceptionKindList(targetNodeLabelList);
                                    }else{
                                        neo4jRelationEntityImpl.setFromEntityConceptionKindList(targetNodeLabelList);
                                    }
                                }
                                relationEntityList.add(neo4jRelationEntityImpl);
                            }
                        }
                    }
                }
            }
        }
        return relationEntityList;
    }
}
