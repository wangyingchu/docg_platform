package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;

import java.util.Map;

public class Neo4JTimeScaleEventImpl extends Neo4JAttributesMeasurableImpl implements Neo4JClassificationAttachable,TimeScaleEvent {

    public Neo4JTimeScaleEventImpl(String entityUID) {
        super(entityUID);
    }

    @Override
    public String getTimeFlowName() {
        return null;
    }

    @Override
    public long getReferTime() {
        return 0;
    }

    @Override
    public Map<String, Object> getEventData() {
        return null;
    }

    @Override
    public TimeFlow.TimeScaleGrade getTimeScaleGrade() {
        return null;
    }

    @Override
    public String getTimeScaleEventUID() {
        return null;
    }

    @Override
    public String getEventComment() {
        return null;
    }

    @Override
    public String getEntityUID() {
        return null;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return null;
    }
}
