package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class GetListConceptionKindTransformer  implements DataTransformer<List<ConceptionKind>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetListConceptionKindTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<ConceptionKind> transformResult(Result result) {
        List<ConceptionKind> conceptionKindList = new ArrayList<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                    List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
                    boolean isMatchedKind = true;
                    if(allLabelNames.size()>0){
                        isMatchedKind = allLabelNames.contains(RealmConstant.ConceptionKindClass);
                    }
                    if(isMatchedKind){
                        long nodeUID = resultNode.id();
                        String coreRealmName = this.currentCoreRealmName;
                        String conceptionKindName = resultNode.get(RealmConstant._NameProperty).asString();
                        String conceptionKindDesc = null;
                        if(resultNode.get(RealmConstant._DescProperty) != null){
                            conceptionKindDesc = resultNode.get(RealmConstant._DescProperty).asString();
                        }
                        String conceptionKindUID = ""+nodeUID;
                        Neo4JConceptionKindImpl neo4JConceptionKindImpl =
                                new Neo4JConceptionKindImpl(coreRealmName,conceptionKindName,conceptionKindDesc,conceptionKindUID);
                        neo4JConceptionKindImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                        conceptionKindList.add(neo4JConceptionKindImpl);
                    }
                }
            }
        }
        return conceptionKindList;
    }
}
