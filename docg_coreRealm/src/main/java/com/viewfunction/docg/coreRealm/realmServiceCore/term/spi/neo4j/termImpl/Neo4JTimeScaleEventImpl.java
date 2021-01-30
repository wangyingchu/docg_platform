package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4JTimeScaleEventImpl extends Neo4JAttributesMeasurableImpl implements Neo4JClassificationAttachable,TimeScaleEvent {

    private static Logger logger = LoggerFactory.getLogger(Neo4JAttributesMeasurableImpl.class);

    private String timeFlowName;
    private String eventComment;
    private long referTime;
    private TimeFlow.TimeScaleGrade timeScaleGrade;
    private String timeScaleEventUID;

    public Neo4JTimeScaleEventImpl(String timeFlowName,String eventComment,long referTime,TimeFlow.TimeScaleGrade timeScaleGrade,
                                   String timeScaleEventUID) {
        super(timeScaleEventUID);
        this.timeFlowName = timeFlowName;
        this.eventComment = eventComment;
        this.referTime = referTime;
        this.timeScaleGrade = timeScaleGrade;
        this.timeScaleEventUID = timeScaleEventUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getTimeFlowName() {
        return this.timeFlowName;
    }

    @Override
    public long getReferTime() {
        return this.referTime;
    }

    @Override
    public TimeFlow.TimeScaleGrade getTimeScaleGrade() {
        return this.timeScaleGrade;
    }

    @Override
    public String getTimeScaleEventUID() {
        return this.timeScaleEventUID;
    }

    @Override
    public String getEventComment() {
        return this.eventComment;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        super.setGlobalGraphOperationExecutor(graphOperationExecutor);
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return this.timeScaleEventUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
