package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class GetListRelationKindTransformer implements DataTransformer<List<RelationKind>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetListRelationKindTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<RelationKind> transformResult(Result result) {
        List<RelationKind> relationKindList = new ArrayList<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                    List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
                    boolean isMatchedKind = true;
                    if(allLabelNames.size()>0){
                        isMatchedKind = allLabelNames.contains(RealmConstant.RelationKindClass);
                    }
                    if(isMatchedKind){
                        long nodeUID = resultNode.id();
                        String coreRealmName = this.currentCoreRealmName;
                        String relationKindName = resultNode.get(RealmConstant._NameProperty).asString();
                        String relationKindNameDesc = null;
                        if(resultNode.get(RealmConstant._DescProperty) != null){
                            relationKindNameDesc = resultNode.get(RealmConstant._DescProperty).asString();
                        }
                        String relationKindUID = ""+nodeUID;
                        Neo4JRelationKindImpl neo4JRelationKindImpl =
                                new Neo4JRelationKindImpl(coreRealmName,relationKindName,relationKindNameDesc,relationKindUID);
                        neo4JRelationKindImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                        relationKindList.add(neo4JRelationKindImpl);
                    }
                }
            }
        }
        return relationKindList;
    }
}
