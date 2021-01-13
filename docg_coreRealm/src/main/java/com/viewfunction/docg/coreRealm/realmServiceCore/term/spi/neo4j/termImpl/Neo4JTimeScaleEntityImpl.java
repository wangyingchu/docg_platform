package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class Neo4JTimeScaleEntityImpl implements TimeScaleEntity {

    private static Logger logger = LoggerFactory.getLogger(Neo4JTimeScaleEntityImpl.class);
    private String coreRealmName;
    private String timeScaleEntityUID;
    private String timeFlowName;
    private TimeFlow.TimeScaleGrade timeScaleGrade;
    private int entityValue;

    public Neo4JTimeScaleEntityImpl(String coreRealmName,String timeFlowName,String timeScaleEntityUID,TimeFlow.TimeScaleGrade timeScaleGrade,int entityValue){
        this.coreRealmName = coreRealmName;
        this.timeFlowName = timeFlowName;
        this.timeScaleEntityUID = timeScaleEntityUID;
        this.timeScaleGrade = timeScaleGrade;
        this.entityValue = entityValue;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public TimeFlow.TimeScaleGrade getTimeScaleGrade() {
        return this.timeScaleGrade;
    }

    @Override
    public int getEntityValue() {
        return this.entityValue;
    }

    @Override
    public TimeScaleEntity getNextSameScaleEntity() {
        return null;
    }

    @Override
    public TimeScaleEntity getPreviousSameScaleEntity() {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getFellowEntities() {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getChildEntities() {
        return null;
    }

    @Override
    public TimeScaleEntity getFirstChildEntity() {
        return null;
    }

    @Override
    public TimeScaleEntity getLastChildEntity() {
        return null;
    }

    @Override
    public InheritanceTree<TimeScaleEntity> getOffspringEntities() {
        return null;
    }

    @Override
    public Long countAttachedTimeScaleEvents(TimeScaleLevel timeScaleLevel) {
        return null;
    }

    @Override
    public Long countAttachedTimeScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode, TimeScaleLevel timeScaleLevel) {
        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getAttachedTimeScaleEvents(QueryParameters queryParameters, TimeScaleLevel timeScaleLevel) {
        return null;
    }







    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    /*
    @Override
    public String getEntityUID() {
        return conceptionKindUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
    */

}
