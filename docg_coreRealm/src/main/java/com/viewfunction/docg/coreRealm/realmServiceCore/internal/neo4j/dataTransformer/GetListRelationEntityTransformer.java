package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Relationship;

import java.util.ArrayList;
import java.util.List;

public class GetListRelationEntityTransformer implements DataTransformer<List<RelationEntity>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String targetRelationKindName;

    public GetListRelationEntityTransformer(String targetRelationKindName,GraphOperationExecutor workingGraphOperationExecutor){
        this.targetRelationKindName = targetRelationKindName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<RelationEntity> transformResult(Result result) {
        List<RelationEntity> relationEntityList = new ArrayList<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
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
                        Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                                new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
                        neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                        relationEntityList.add(neo4jRelationEntityImpl);
                    }
                }
            }
        }
        return relationEntityList;
    }
}
