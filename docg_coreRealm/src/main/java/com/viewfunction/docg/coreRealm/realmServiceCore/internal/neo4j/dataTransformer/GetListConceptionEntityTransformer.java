package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class GetListConceptionEntityTransformer implements DataTransformer<List<ConceptionEntity>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String targetConceptionKindName;

    public GetListConceptionEntityTransformer(String targetConceptionKindName,GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        this.targetConceptionKindName = targetConceptionKindName;
    }

    @Override
    public List<ConceptionEntity> transformResult(Result result) {
        List<ConceptionEntity> conceptionEntityList = new ArrayList<>();
        while(result.hasNext()){
            Record nodeRecord = result.next();
            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
            List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
            boolean isMatchedConceptionKind = true;
            if(allConceptionKindNames.size()>0){
                if(targetConceptionKindName != null){
                    isMatchedConceptionKind = allConceptionKindNames.contains(targetConceptionKindName);
                }else{
                    isMatchedConceptionKind = true;
                }
            }
            if(isMatchedConceptionKind){
                long nodeUID = resultNode.id();
                String conceptionEntityUID = ""+nodeUID;
                String resultConceptionKindName = targetConceptionKindName != null? targetConceptionKindName:allConceptionKindNames.get(0);
                Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                        new Neo4JConceptionEntityImpl(resultConceptionKindName,conceptionEntityUID);
                neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
                neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                conceptionEntityList.add(neo4jConceptionEntityImpl);
            }
        }
        return conceptionEntityList;
    }
}
