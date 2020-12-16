package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import com.google.common.collect.Lists;

import java.util.List;

public class GetSingleConceptionEntityTransformer implements DataTransformer<ConceptionEntity>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String targetConceptionKindName;

    public GetSingleConceptionEntityTransformer(String targetConceptionKindName,GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        this.targetConceptionKindName = targetConceptionKindName;
    }

    @Override
    public ConceptionEntity transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
                boolean isMatchedConceptionKind = true;
                if(allConceptionKindNames.size()>0 && targetConceptionKindName != null){
                    isMatchedConceptionKind = allConceptionKindNames.contains(targetConceptionKindName);
                }
                if(isMatchedConceptionKind){
                    long nodeUID = resultNode.id();
                    String conceptionEntityUID = ""+nodeUID;
                    Neo4JConceptionEntityImpl neo4jConceptionEntityImpl = targetConceptionKindName != null ?
                            new Neo4JConceptionEntityImpl(targetConceptionKindName,conceptionEntityUID):
                            new Neo4JConceptionEntityImpl(allConceptionKindNames.get(0),conceptionEntityUID);
                    neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
                    neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    return neo4jConceptionEntityImpl;
                }else{
                    return null;
                }
            }
        }
        return null;
    }
}
