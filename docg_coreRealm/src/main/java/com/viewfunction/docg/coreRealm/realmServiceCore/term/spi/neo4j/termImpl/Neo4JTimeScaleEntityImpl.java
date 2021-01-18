package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetLinkedListTimeScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleTimeScaleEntityTransformer;
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
    public TimeScaleEntity getParentEntity() {
        String queryCql = "MATCH(currentEntity:DOCG_TimeScaleEntity)<-[:DOCG_TS_Contains]-(parentEntity:DOCG_TimeScaleEntity) WHERE id(currentEntity) = "+ this.getTimeScaleEntityUID() +" RETURN parentEntity as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }

    @Override
    public TimeScaleEntity getNextSameScaleEntity() {
        String queryCql = "MATCH(currentEntity:DOCG_TimeScaleEntity)-[:DOCG_TS_NextIs]->(targetEntity:DOCG_TimeScaleEntity) WHERE id(currentEntity) = "+ this.getTimeScaleEntityUID() +" RETURN targetEntity as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }

    @Override
    public TimeScaleEntity getPreviousSameScaleEntity() {
        String queryCql = "MATCH(currentEntity:DOCG_TimeScaleEntity)<-[:DOCG_TS_NextIs]-(targetEntity:DOCG_TimeScaleEntity) WHERE id(currentEntity) = "+ this.getTimeScaleEntityUID() +" RETURN targetEntity as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }

    @Override
    public LinkedList<TimeScaleEntity> getFellowEntities() {
        TimeScaleEntity parentTimeScaleEntity = getParentEntity();
        if(parentTimeScaleEntity == null){
            //this is a YEAR level TimeScaleEntity, need query all year entities, these years maybe not all linked together
            String queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+this.timeFlowName+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year) RETURN year as operationResult ORDER BY year.year\n";
            return getListTimeScaleEntity(queryCql);
        }else{
            String parentEntityUID = ((Neo4JTimeScaleEntityImpl)parentTimeScaleEntity).getTimeScaleEntityUID();
            String queryCql = "MATCH(parentEntity:DOCG_TimeScaleEntity)-[:DOCG_TS_Contains]->(fellowEntities:DOCG_TimeScaleEntity) WHERE id(parentEntity) = "+ parentEntityUID +" RETURN fellowEntities as operationResult ORDER BY fellowEntities.id";
            return getListTimeScaleEntity(queryCql);
        }
    }

    @Override
    public LinkedList<TimeScaleEntity> getChildEntities() {
        String queryCql = "MATCH(currentEntity:DOCG_TimeScaleEntity)-[:DOCG_TS_Contains]->(targetEntities:DOCG_TimeScaleEntity) WHERE id(currentEntity) = "+ this.getTimeScaleEntityUID() +" RETURN targetEntities as operationResult ORDER BY targetEntities.id";
        return getListTimeScaleEntity(queryCql);
    }

    @Override
    public TimeScaleEntity getFirstChildEntity() {
        String queryCql = "MATCH(currentEntity:DOCG_TimeScaleEntity)-[:DOCG_TS_FirstChildIs]->(targetEntity:DOCG_TimeScaleEntity) WHERE id(currentEntity) = "+ this.getTimeScaleEntityUID() +" RETURN targetEntity as operationResult";
        return getSingleTimeScaleEntity(queryCql);
    }

    @Override
    public TimeScaleEntity getLastChildEntity() {
        String queryCql = "MATCH(currentEntity:DOCG_TimeScaleEntity)-[:DOCG_TS_LastChildIs]->(targetEntity:DOCG_TimeScaleEntity) WHERE id(currentEntity) = "+ this.getTimeScaleEntityUID() +" RETURN targetEntity as operationResult";
        return getSingleTimeScaleEntity(queryCql);
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

    public String getTimeScaleEntityUID() {
        return timeScaleEntityUID;
    }

    private TimeScaleEntity getSingleTimeScaleEntity(String queryCql){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetSingleTimeScaleEntityTransformer getSingleTimeScaleEntityTransformer =
                    new GetSingleTimeScaleEntityTransformer(this.coreRealmName,graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getSingleTimeScaleEntityTransformer,queryCql);
            if(queryRes != null){
                return (TimeScaleEntity)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    private LinkedList<TimeScaleEntity> getListTimeScaleEntity(String queryCql){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetLinkedListTimeScaleEntityTransformer getLinkedListTimeScaleEntityTransformer =
                    new GetLinkedListTimeScaleEntityTransformer(this.coreRealmName,graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getLinkedListTimeScaleEntityTransformer,queryCql);
            if(queryRes != null){
                return (LinkedList<TimeScaleEntity>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
