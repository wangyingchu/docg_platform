package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JRelationEntity;

public class Neo4jRelationEntityImpl extends Neo4JAttributesMeasurableImpl implements Neo4JRelationEntity {

    private String relationEntityUID;
    private String relationKindName;
    private String fromEntityUID;
    private String toEntityUID;

    public Neo4jRelationEntityImpl(String relationKindName,String relationEntityUID,String fromEntityUID,String toEntityUID){
        super(relationEntityUID);
        this.relationKindName = relationKindName;
        this.relationEntityUID = relationEntityUID;
        this.fromEntityUID = fromEntityUID;
        this.toEntityUID = toEntityUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getRelationEntityUID() {
        return relationEntityUID;
    }

    @Override
    public String getRelationKindName() {
        return relationKindName;
    }

    @Override
    public String getFromConceptionEntityUID() {
        return fromEntityUID;
    }

    @Override
    public String getToConceptionEntityUID() {
        return toEntityUID;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        super.setGlobalGraphOperationExecutor(graphOperationExecutor);
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
