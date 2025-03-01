package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSingleEntitiesGraphTransformer implements DataTransformer<EntitiesGraph>{

    private GraphOperationExecutor workingGraphOperationExecutor;

    public GetSingleEntitiesGraphTransformer(GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor =  workingGraphOperationExecutor;
    }

    @Override
    public EntitiesGraph transformResult(Result result) {
        Map<String,List<String>> conceptionEntitiesConceptionKindsMap = new HashMap<>();
        if(result.hasNext()){
            Record currentRecord = result.next();
            List<Object> nodeObjectList =  currentRecord.get("nodes").asList();
            List<Object> relationObjectList =  currentRecord.get("relationships").asList();

            List<ConceptionEntity> graphConceptionEntities = new ArrayList<>();
            List<RelationEntity> graphRelationEntities = new ArrayList<>();
            EntitiesGraph entitiesGraph = new EntitiesGraph(graphConceptionEntities,graphRelationEntities);

            if(nodeObjectList != null){
                for(Object currentNodeObject:nodeObjectList){
                    Node currentNode = (Node)currentNodeObject;
                    List<String> allConceptionKindNames = Lists.newArrayList(currentNode.labels());
                    long nodeUID = currentNode.id();
                    String conceptionEntityUID = ""+nodeUID;
                    if(!conceptionEntitiesConceptionKindsMap.containsKey(conceptionEntityUID)){
                        conceptionEntitiesConceptionKindsMap.put(conceptionEntityUID,allConceptionKindNames);
                    }
                    Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                            new Neo4JConceptionEntityImpl(allConceptionKindNames.get(0),conceptionEntityUID);
                    neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
                    neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    graphConceptionEntities.add(neo4jConceptionEntityImpl);
                    entitiesGraph.countConceptionKindsData(allConceptionKindNames.get(0));
                }
            }
            if(relationObjectList != null){
                for(Object currentRelationObject:relationObjectList){
                    Relationship resultRelationship = (Relationship)currentRelationObject;
                    String relationType = resultRelationship.type();
                    long relationUID = resultRelationship.id();
                    String relationEntityUID = ""+relationUID;
                    String fromEntityUID = ""+resultRelationship.startNodeId();
                    String toEntityUID = ""+resultRelationship.endNodeId();
                    Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                            new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
                    neo4jRelationEntityImpl.setFromEntityConceptionKindList(conceptionEntitiesConceptionKindsMap.get(fromEntityUID));
                    neo4jRelationEntityImpl.setToEntityConceptionKindList(conceptionEntitiesConceptionKindsMap.get(toEntityUID));
                    neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    graphRelationEntities.add(neo4jRelationEntityImpl);
                    entitiesGraph.countRelationKindsData(relationType);
                }
            }
            return entitiesGraph;
        }
        return null;
    }
}
