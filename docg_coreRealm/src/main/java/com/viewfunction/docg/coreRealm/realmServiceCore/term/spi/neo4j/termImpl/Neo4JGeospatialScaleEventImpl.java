package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JGeospatialScaleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4JGeospatialScaleEventImpl extends Neo4JAttributesMeasurableImpl implements Neo4JClassificationAttachable, Neo4JGeospatialScaleEvent {

    private static Logger logger = LoggerFactory.getLogger(Neo4JGeospatialScaleEventImpl.class);
    private String geospatialScaleEventUID;

    public Neo4JGeospatialScaleEventImpl(String entityUID) {
        super(entityUID);
    }

    @Override
    public String getGeospatialRegionName() {
        return null;
    }

    @Override
    public TimeFlow.TimeScaleGrade getGeospatialScaleGrade() {
        return null;
    }

    @Override
    public String getGeospatialScaleEventUID() {
        return null;
    }

    @Override
    public String getEventComment() {
        return null;
    }

    @Override
    public GeospatialScaleEntity getReferGeospatialScaleEntity() {
        return null;
    }

    @Override
    public ConceptionEntity getAttachConceptionEntity() {
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        super.setGlobalGraphOperationExecutor(graphOperationExecutor);
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return this.geospatialScaleEventUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}