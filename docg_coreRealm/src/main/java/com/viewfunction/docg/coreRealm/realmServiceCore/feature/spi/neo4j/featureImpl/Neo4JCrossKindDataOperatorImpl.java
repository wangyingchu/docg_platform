package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public class Neo4JCrossKindDataOperatorImpl implements CrossKindDataOperator {

    private String coreRealmName;
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public Neo4JCrossKindDataOperatorImpl(String coreRealmName){
        this.coreRealmName = coreRealmName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public List<RelationEntity> getRelationInfoOfConceptionEntityPairs(List<String> conceptionEntityUIDs) {
        return null;
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
