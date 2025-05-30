package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JConceptionEntity;

import java.util.ArrayList;
import java.util.List;

public class Neo4JConceptionEntityImpl extends Neo4JAttributesMeasurableImpl implements Neo4JConceptionEntity, Neo4JEntityRelationable,
        Neo4JClassificationAttachable, Neo4JMultiConceptionKindsSupportable, Neo4JTimeScaleFeatureSupportable, Neo4JGeospatialScaleFeatureSupportable,
        Neo4JPathTravelable, Neo4JGeospatialScaleCalculable,Neo4JExternalAttributesValueAccessible{

    private String conceptionEntityUID;
    private String conceptionKindName;
    private List<String> allConceptionKindNames;

    public Neo4JConceptionEntityImpl(String conceptionKindName, String conceptionEntityUID){
        super(conceptionEntityUID);
        this.conceptionKindName = conceptionKindName;
        this.conceptionEntityUID = conceptionEntityUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getConceptionEntityUID() {
        return this.conceptionEntityUID;
    }

    @Override
    public String getConceptionKindName() {
        return this.conceptionKindName;
    }

    @Override
    public List<String> getAllConceptionKindNames() {
        return this.allConceptionKindNames != null ? this.allConceptionKindNames : new ArrayList<>();
    }

    public void setAllConceptionKindNames(List<String> allConceptionKindNames) {
        this.allConceptionKindNames = allConceptionKindNames;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        super.setGlobalGraphOperationExecutor(graphOperationExecutor);
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return conceptionEntityUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
