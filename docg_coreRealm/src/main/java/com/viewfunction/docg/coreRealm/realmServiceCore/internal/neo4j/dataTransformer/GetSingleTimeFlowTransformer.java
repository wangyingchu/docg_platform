package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeFlowImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.List;

public class GetSingleTimeFlowTransformer implements DataTransformer<TimeFlow>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetSingleTimeFlowTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public TimeFlow transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
                boolean isMatchedKind = true;
                if(allLabelNames.size()>0){
                    isMatchedKind = allLabelNames.contains(RealmConstant.TimeFlowClass);
                }
                if(isMatchedKind){
                    String coreRealmName = this.currentCoreRealmName;
                    String timeFlowName = resultNode.get(RealmConstant._NameProperty).asString();
                    long nodeUID = resultNode.id();
                    String timeFlowUID = ""+nodeUID;
                    Neo4JTimeFlowImpl neo4JTimeFlowImpl = new Neo4JTimeFlowImpl(coreRealmName,timeFlowName,timeFlowUID);
                    neo4JTimeFlowImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                    return neo4JTimeFlowImpl;
                }
            }
        }
        return null;
    }
}
