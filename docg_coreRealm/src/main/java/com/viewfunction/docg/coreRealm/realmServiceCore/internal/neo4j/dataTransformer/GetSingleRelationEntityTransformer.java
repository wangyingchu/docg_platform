package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Relationship;

public class GetSingleRelationEntityTransformer implements DataTransformer<RelationEntity>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String targetRelationKindName;

    public GetSingleRelationEntityTransformer(String targetRelationKindName,GraphOperationExecutor workingGraphOperationExecutor){
        this.targetRelationKindName = targetRelationKindName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public RelationEntity transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
                String relationType = resultRelationship.type();
                boolean isMatchedKind;
                // if the relationEntity is come from a DELETE relation operation,the relationType will be empty string,
                // make isMatchedKind to be true at this case
                if(this.targetRelationKindName == null || relationType.equals("")){
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
                            new Neo4JRelationEntityImpl(targetRelationKindName,relationEntityUID,fromEntityUID,toEntityUID);
                    neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    return neo4jRelationEntityImpl;
                }else{
                    return null;
                }
            }
        }
        return null;
    }
}
