package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationAttachKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationAttachKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class GetListRelationAttachKindTransformer  implements DataTransformer<List<RelationAttachKind>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetListRelationAttachKindTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<RelationAttachKind> transformResult(Result result) {
        List<RelationAttachKind> relationAttachKindsList = new ArrayList<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                    List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
                    boolean isMatchedKind = true;
                    if(allLabelNames.size()>0){
                        isMatchedKind = allLabelNames.contains(RealmConstant.RelationAttachKindClass);
                    }
                    if(isMatchedKind){
                        long nodeUID = resultNode.id();
                        String coreRealmName = this.currentCoreRealmName;
                        String relationAttachKindName = resultNode.get(RealmConstant._NameProperty).asString();
                        String relationAttachKindNameDesc = null;
                        if(resultNode.get(RealmConstant._DescProperty) != null){
                            relationAttachKindNameDesc = resultNode.get(RealmConstant._DescProperty).asString();
                        }

                        String relationAttachSourceKind = resultNode.get(RealmConstant._relationAttachSourceKind).asString();
                        String relationAttachTargetKind = resultNode.get(RealmConstant._relationAttachTargetKind).asString();
                        String relationAttachRelationKind = resultNode.get(RealmConstant._relationAttachRelationKind).asString();
                        boolean relationAttachRepeatableRelationKind = resultNode.get(RealmConstant._relationAttachRepeatableRelationKind).asBoolean();

                        String relationAttachKindUID = ""+nodeUID;
                        Neo4JRelationAttachKindImpl neo4JRelationAttachKindImpl =
                                new Neo4JRelationAttachKindImpl(coreRealmName,relationAttachKindName,relationAttachKindNameDesc,relationAttachKindUID,
                                        relationAttachSourceKind,relationAttachTargetKind,relationAttachRelationKind,relationAttachRepeatableRelationKind);
                        neo4JRelationAttachKindImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                        relationAttachKindsList.add(neo4JRelationAttachKindImpl);
                    }
                }
            }
        }
        return relationAttachKindsList;
    }
}
