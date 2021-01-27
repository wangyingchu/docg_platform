package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetLinkedListTimeScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleTimeScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.spi.common.structureImpl.CommonInheritanceTreeImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant.TimeScaleEventClass;

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
        Table<String,String, TimeScaleEntity> treeElementsTable = HashBasedTable.create();
        treeElementsTable.put(InheritanceTree.Virtual_ParentID_Of_Root_Node,this.getTimeScaleEntityUID(),this);
        final String currentCoreRealmName = this.coreRealmName;

        String queryCql = "MATCH (currentEntity:DOCG_TimeScaleEntity)-[relationResult:`DOCG_TS_Contains`*1..5]->(operationResult:`DOCG_TimeScaleEntity`) WHERE id(currentEntity) = "+this.getTimeScaleEntityUID()+" RETURN operationResult,relationResult";
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            logger.debug("Generated Cypher Statement: {}", queryCql);

            DataTransformer offspringTimeScaleEntitiesDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    List<Record> recordList = result.list();

                    if(recordList != null){
                        for(Record nodeRecord : recordList){
                            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                            long nodeUID = resultNode.id();

                            TimeFlow.TimeScaleGrade timeScaleGrade = null;

                            String entityUID = ""+nodeUID;
                            int value = resultNode.get("id").asInt();
                            String timeFlowName = resultNode.get("timeFlow").asString();
                            if(resultNode.get("year").asObject() != null){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.YEAR;
                            }else if(resultNode.get("month").asObject() != null){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.MONTH;
                            }else if(resultNode.get("day").asObject() != null){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.DAY;
                            }else if(resultNode.get("hour").asObject() != null){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.HOUR;
                            }else if(resultNode.get("minute").asObject() != null){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.MINUTE;
                            }
                            Neo4JTimeScaleEntityImpl neo4JTimeScaleEntityImpl = new Neo4JTimeScaleEntityImpl(currentCoreRealmName,timeFlowName,entityUID,timeScaleGrade,value);
                            neo4JTimeScaleEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);

                            List<Object> relationships = nodeRecord.get(CypherBuilder.relationResultName).asList();
                            String parentClassificationUID = null;
                            for(Object currentRelationship : relationships){
                                Relationship currentTargetRelationship = (Relationship)currentRelationship;
                                String startNodeUID = "" + currentTargetRelationship.startNodeId();
                                String endNodeUID = "" + currentTargetRelationship.endNodeId();
                                if(endNodeUID.equals(entityUID)){
                                    parentClassificationUID = startNodeUID;
                                    break;
                                }
                            }
                            treeElementsTable.put(parentClassificationUID,entityUID,neo4JTimeScaleEntityImpl);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(offspringTimeScaleEntitiesDataTransformer,queryCql);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        CommonInheritanceTreeImpl<TimeScaleEntity> resultInheritanceTree = new CommonInheritanceTreeImpl(this.getTimeScaleEntityUID(),treeElementsTable);
        return resultInheritanceTree;
    }

    @Override
    public Long countAttachedTimeScaleEvents(TimeScaleLevel timeScaleLevel) {
        String relationTravelLogic = "relationResult:`DOCG_TS_Contains`*1..3";
        switch (timeScaleLevel){
            case SELF: relationTravelLogic = "relationResult:`DOCG_TS_Contains`";
            break;
            case CHILD: relationTravelLogic = "relationResult:`DOCG_TS_Contains`*1";
            break;
            case OFFSPRING:
                switch(this.timeScaleGrade){
                    case YEAR:
                        relationTravelLogic = "relationResult:`DOCG_TS_Contains`*1..4";
                        break;
                    case MONTH:
                        relationTravelLogic = "relationResult:`DOCG_TS_Contains`*1..3";
                        break;
                    case DAY:
                        relationTravelLogic = "relationResult:`DOCG_TS_Contains`*1..2";
                        break;
                    case HOUR:
                        relationTravelLogic = "relationResult:`DOCG_TS_Contains`*1";
                        break;
                    case MINUTE:
                        return 0l;
                }
        }
        switch(this.timeScaleGrade){
            case YEAR: break;
            case MONTH: break;
            case DAY:break;
            case HOUR:break;
            case MINUTE:
                switch (timeScaleLevel){
                    case CHILD:return 0l;
                    case OFFSPRING:return 0l;
                }
                break;
        }
        String queryCql = "MATCH(currentEntity:DOCG_TimeScaleEntity)-["+relationTravelLogic+"]->(childEntities:`DOCG_TimeScaleEntity`) WHERE id(currentEntity) = "+this.getTimeScaleEntityUID()+" \n" +
                "MATCH (childEntities)-[:`DOCG_TS_TimeReferTo`]->(relatedEvents:`DOCG_TimeScaleEvent`) RETURN count(relatedEvents) as operationResult";
        logger.debug("Generated Cypher Statement: {}", queryCql);

        DataTransformer<Long> _DataTransformer = new DataTransformer<Long>() {
            @Override
            public Long transformResult(Result result) {

                if (result.hasNext()) {
                    Record record = result.next();
                    if (record.containsKey(CypherBuilder.operationResultName)) {
                        return record.get(CypherBuilder.operationResultName).asLong();
                    }
                    return null;
                }
                return null;
            }
        };
        Long resultNumber = 0l;
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            Object countRes = workingGraphOperationExecutor.executeRead(_DataTransformer,queryCql);
            resultNumber = countRes != null ? (Long) countRes: 0l;
            return resultNumber;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Long countAttachedTimeScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode, TimeScaleLevel timeScaleLevel) {
        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getAttachedTimeScaleEvents(QueryParameters queryParameters, TimeScaleLevel timeScaleLevel) {
        try {
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(TimeScaleEventClass,queryParameters,null);
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
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
