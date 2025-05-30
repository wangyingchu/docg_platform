package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleEventsRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleRelationsInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonConceptionEntitiesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonTimeScaleEventsRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.spi.common.structureImpl.CommonInheritanceTreeImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JTimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant.TimeScaleEventClass;

public class Neo4JTimeScaleEntityImpl implements Neo4JTimeScaleEntity {

    private static Logger logger = LoggerFactory.getLogger(Neo4JTimeScaleEntityImpl.class);
    private String coreRealmName;
    private String timeScaleEntityUID;
    private String timeFlowName;
    private TimeFlow.TimeScaleGrade timeScaleGrade;
    private int entityValue;
    private String entityDescription;

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
    public String getTimeFlowName() {
        return this.timeFlowName;
    }

    @Override
    public String getTimeScaleEntityDesc() {
        return entityDescription;
    }

    public void setEntityDescription(String entityDescription){
        this.entityDescription = entityDescription;
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

                            List<String> allLabelNames = Lists.newArrayList(resultNode.labels());

                            String timeScaleEntityDesc = null;
                            if(allLabelNames.contains(RealmConstant.TimeScaleYearEntityClass)){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.YEAR;
                                if(resultNode.containsKey("year")){
                                    timeScaleEntityDesc = ""+resultNode.get("year").asInt();
                                }
                            }else if(allLabelNames.contains(RealmConstant.TimeScaleMonthEntityClass)){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.MONTH;
                                if(resultNode.containsKey("year") && resultNode.containsKey("month")){
                                    timeScaleEntityDesc = ""+resultNode.get("year").asInt()+
                                            "-"+resultNode.get("month").asInt();
                                }
                            }else if(allLabelNames.contains(RealmConstant.TimeScaleDayEntityClass)){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.DAY;
                                if(resultNode.containsKey("year") && resultNode.containsKey("month")
                                        && resultNode.containsKey("day")){
                                    timeScaleEntityDesc = ""+resultNode.get("year").asInt()+
                                            "-"+resultNode.get("month").asInt()+
                                            "-"+resultNode.get("day").asInt();
                                }
                            }else if(allLabelNames.contains(RealmConstant.TimeScaleHourEntityClass)){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.HOUR;
                                if(resultNode.containsKey("year") && resultNode.containsKey("month")
                                        && resultNode.containsKey("day") && resultNode.containsKey("hour")){
                                    timeScaleEntityDesc = ""+resultNode.get("year").asInt()+
                                            "-"+resultNode.get("month").asInt()+
                                            "-"+resultNode.get("day").asInt()+
                                            " "+resultNode.get("hour").asInt();
                                }
                            }else if(allLabelNames.contains(RealmConstant.TimeScaleMinuteEntityClass)){
                                timeScaleGrade = TimeFlow.TimeScaleGrade.MINUTE;
                                if(resultNode.containsKey("year") && resultNode.containsKey("month")
                                        && resultNode.containsKey("day") && resultNode.containsKey("hour")
                                        && resultNode.containsKey("minute")){
                                    timeScaleEntityDesc = ""+resultNode.get("year").asInt()+
                                            "-"+resultNode.get("month").asInt()+
                                            "-"+resultNode.get("day").asInt()+
                                            " "+resultNode.get("hour").asInt()+
                                            ":"+resultNode.get("minute").asInt();
                                }
                            }

                            Neo4JTimeScaleEntityImpl neo4JTimeScaleEntityImpl = new Neo4JTimeScaleEntityImpl(currentCoreRealmName,timeFlowName,entityUID,timeScaleGrade,value);
                            neo4JTimeScaleEntityImpl.setEntityDescription(timeScaleEntityDesc);
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
    public Long countAttachedTimeScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode, TimeScaleLevel timeScaleLevel) {
        if(attributesParameters != null){
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
            try {
                String eventEntitiesQueryCql = CypherBuilder.matchNodesWithQueryParameters(TimeScaleEventClass,queryParameters,CypherBuilder.CypherFunctionType.COUNT);
                eventEntitiesQueryCql = eventEntitiesQueryCql.replace("(operationResult:`DOCG_TimeScaleEvent`)","(childEntities)-[:`DOCG_TS_TimeReferTo`]->(operationResult:`DOCG_TimeScaleEvent`)");
                String queryCql = addTimeScaleGradeTravelLogic(timeScaleLevel,eventEntitiesQueryCql);
                logger.debug("Generated Cypher Statement: {}", queryCql);

                DataTransformer<Long> _DataTransformer = new DataTransformer<Long>() {
                    @Override
                    public Long transformResult(Result result) {
                        if (result.hasNext()) {
                            Record record = result.next();
                            if (record.containsKey("count("+CypherBuilder.operationResultName+")")) {
                                return record.get("count("+CypherBuilder.operationResultName+")").asLong();
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
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }
            return null;
        }else{
            return countAttachedConceptionEntities(timeScaleLevel);
        }
    }

    @Override
    public TimeScaleEventsRetrieveResult getAttachedTimeScaleEvents(QueryParameters queryParameters, TimeScaleLevel timeScaleLevel) {
        try {
            CommonTimeScaleEventsRetrieveResultImpl commonTimeScaleEventsRetrieveResultImpl = new CommonTimeScaleEventsRetrieveResultImpl();
            commonTimeScaleEventsRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String eventEntitiesQueryCql = CypherBuilder.matchNodesWithQueryParameters(TimeScaleEventClass,queryParameters,null);
            eventEntitiesQueryCql = eventEntitiesQueryCql.replace("(operationResult:`DOCG_TimeScaleEvent`)","(childEntities)-[:`DOCG_TS_TimeReferTo`]->(operationResult:`DOCG_TimeScaleEvent`)");
            String queryCql = addTimeScaleGradeTravelLogic(timeScaleLevel,eventEntitiesQueryCql);
            logger.debug("Generated Cypher Statement: {}", queryCql);

            try{
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                GetListTimeScaleEventTransformer getListTimeScaleEventTransformer = new GetListTimeScaleEventTransformer(timeFlowName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListTimeScaleEventTransformer,queryCql);
                if(queryRes != null){
                    List<TimeScaleEvent> res = (List<TimeScaleEvent>)queryRes;
                    commonTimeScaleEventsRetrieveResultImpl.addTimeScaleEvents(res);
                    commonTimeScaleEventsRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(res.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonTimeScaleEventsRetrieveResultImpl.finishEntitiesRetrieving();
            return commonTimeScaleEventsRetrieveResultImpl;
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long countAttachedConceptionEntities(TimeScaleLevel timeScaleLevel) {
        String relationTravelLogic = "relationResult:`DOCG_TS_Contains`*1..3";
        switch (timeScaleLevel){
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
        if(timeScaleLevel.equals(TimeScaleLevel.SELF)){
            queryCql = "MATCH(currentEntity:DOCG_TimeScaleEntity) WHERE id(currentEntity) = "+this.getTimeScaleEntityUID()+" \n" +
                    "MATCH (currentEntity)-[:`DOCG_TS_TimeReferTo`]->(relatedEvents:`DOCG_TimeScaleEvent`) RETURN count(relatedEvents) as operationResult";
        }
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
    public Long countAttachedConceptionEntities(String conceptionKindName,AttributesParameters attributesParameters, boolean isDistinctMode, TimeScaleLevel timeScaleLevel) {
        if(attributesParameters != null){
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
            try {
                CommonConceptionEntitiesRetrieveResultImpl commonConceptionEntitiesRetrieveResultImpl = new CommonConceptionEntitiesRetrieveResultImpl();
                commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
                String eventEntitiesQueryCql = CypherBuilder.matchNodesWithQueryParameters(TimeScaleEventClass,queryParameters,CypherBuilder.CypherFunctionType.COUNT);
                if(conceptionKindName != null){
                    eventEntitiesQueryCql = eventEntitiesQueryCql.replace("(operationResult:`DOCG_TimeScaleEvent`)","(childEntities)-[:`DOCG_TS_TimeReferTo`]->(timeScaleEvents:`DOCG_TimeScaleEvent`)<-[:`DOCG_AttachToTimeScale`]-(operationResult:`"+conceptionKindName+"`)");
                }else{
                    eventEntitiesQueryCql = eventEntitiesQueryCql.replace("(operationResult:`DOCG_TimeScaleEvent`)","(childEntities)-[:`DOCG_TS_TimeReferTo`]->(timeScaleEvents:`DOCG_TimeScaleEvent`)<-[:`DOCG_AttachToTimeScale`]-(operationResult)");
                }
                String queryCql = addTimeScaleGradeTravelLogic(timeScaleLevel,eventEntitiesQueryCql);
                logger.debug("Generated Cypher Statement: {}", queryCql);

                DataTransformer<Long> _DataTransformer = new DataTransformer<Long>() {
                    @Override
                    public Long transformResult(Result result) {
                        if (result.hasNext()) {
                            Record record = result.next();
                            if (record.containsKey("count("+CypherBuilder.operationResultName+")")) {
                                return record.get("count("+CypherBuilder.operationResultName+")").asLong();
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
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            }
            return null;
        }else{
            return countAttachedConceptionEntities(timeScaleLevel);
        }
    }

    @Override
    public ConceptionEntitiesRetrieveResult getAttachedConceptionEntities(String conceptionKindName,QueryParameters queryParameters, TimeScaleLevel timeScaleLevel) {
        try {
            CommonConceptionEntitiesRetrieveResultImpl commonConceptionEntitiesRetrieveResultImpl = new CommonConceptionEntitiesRetrieveResultImpl();
            commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String eventEntitiesQueryCql = CypherBuilder.matchNodesWithQueryParameters(TimeScaleEventClass,queryParameters,null);
            if(conceptionKindName != null){
                eventEntitiesQueryCql = eventEntitiesQueryCql.replace("(operationResult:`DOCG_TimeScaleEvent`)","(childEntities)-[:`DOCG_TS_TimeReferTo`]->(timeScaleEvents:`DOCG_TimeScaleEvent`)<-[:`DOCG_AttachToTimeScale`]-(operationResult:`"+conceptionKindName+"`)");
            }else{
                eventEntitiesQueryCql = eventEntitiesQueryCql.replace("(operationResult:`DOCG_TimeScaleEvent`)","(childEntities)-[:`DOCG_TS_TimeReferTo`]->(timeScaleEvents:`DOCG_TimeScaleEvent`)<-[:`DOCG_AttachToTimeScale`]-(operationResult)");
            }
            String queryCql = addTimeScaleGradeTravelLogic(timeScaleLevel,eventEntitiesQueryCql);
            logger.debug("Generated Cypher Statement: {}", queryCql);

            try{
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,
                        this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
                if(queryRes != null){
                    List<ConceptionEntity> resultConceptionEntityList = (List<ConceptionEntity>)queryRes;
                    commonConceptionEntitiesRetrieveResultImpl.addConceptionEntities(resultConceptionEntityList);
                    commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultConceptionEntityList.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonConceptionEntitiesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonConceptionEntitiesRetrieveResultImpl;
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TimeScaleRelationsInfo> sampleSelfAttachedTimeScaleRelationsInfos(int resultCount, int skipCount) throws CoreRealmServiceEntityExploreException{
        if(resultCount < 0 ){
            String exceptionMessage = "result Count must great then zero";
            CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
            coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
            throw coreRealmServiceEntityExploreException;
        }
        if(skipCount < 0 ){
            String exceptionMessage = "skip Count must great then zero";
            CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
            coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
            throw coreRealmServiceEntityExploreException;
        }

        String queryCql = "MATCH(currentTimeScaleEntity:DOCG_TimeScaleEntity)-[timeReferToRelation:`DOCG_TS_TimeReferTo`]->(timeScaleEvent:`DOCG_TimeScaleEvent`) " +
                "<-[attachToTimeScaleRelation:`DOCG_AttachToTimeScale`]-(conceptionEntity)" +
                " WHERE id(currentTimeScaleEntity) = "+this.getTimeScaleEntityUID()+" \n" +
                "RETURN currentTimeScaleEntity,timeReferToRelation,timeScaleEvent,attachToTimeScaleRelation,conceptionEntity" +
                " SKIP " +skipCount + " LIMIT "+resultCount+" \n";
        logger.debug("Generated Cypher Statement: {}", queryCql);

        List<TimeScaleRelationsInfo> timeScaleRelationsInfoList = new ArrayList<>();
        DataTransformer<Object> _DataTransformer = new DataTransformer<Object>() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record record = result.next();
                    Node timeScaleEntityNode = record.get("currentTimeScaleEntity").asNode();
                    Relationship timeReferToRelation = record.get("timeReferToRelation").asRelationship();
                    Node timeScaleEventNode = record.get("timeScaleEvent").asNode();
                    Relationship attachToTimeScaleRelation = record.get("attachToTimeScaleRelation").asRelationship();
                    Node conceptionEntityNode = record.get("conceptionEntity").asNode();

                    String timeScaleEntityUID = ""+timeScaleEntityNode.id();
                    String timeReferToRelationEntityUID = ""+timeReferToRelation.id();
                    String timeScaleEventUID = ""+timeScaleEventNode.id();
                    String attachToTimeScaleRelationEntityUID = ""+attachToTimeScaleRelation.id();
                    String conceptionEntityUID = ""+conceptionEntityNode.id();
                    String conceptionKindName = conceptionEntityNode.labels().iterator().next();
                    TimeFlow.TimeScaleGrade currentTimeScaleGrade = timeScaleGrade;
                    LocalDateTime referTime = timeScaleEventNode.get(RealmConstant._TimeScaleEventReferTime).asLocalDateTime();
                    String timeScaleEventComment = timeScaleEventNode.get(RealmConstant._TimeScaleEventComment).asString();

                    TimeScaleRelationsInfo timeScaleRelationsInfo = new TimeScaleRelationsInfo(timeScaleEntityUID,
                            timeReferToRelationEntityUID, timeScaleEventUID,attachToTimeScaleRelationEntityUID,
                            conceptionEntityUID,conceptionKindName,currentTimeScaleGrade,referTime,timeScaleEventComment);
                    timeScaleRelationsInfoList.add(timeScaleRelationsInfo);
                }
                return null;
            }
        };

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            workingGraphOperationExecutor.executeRead(_DataTransformer,queryCql);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return timeScaleRelationsInfoList;
    }


    @Override
    public String getTimeScaleEntityUID() {
        return timeScaleEntityUID;
    }

    private String addTimeScaleGradeTravelLogic(TimeScaleLevel timeScaleLevel,String eventEntitiesQueryCql){
        String relationTravelLogic = "relationResult:`DOCG_TS_Contains`*1..3";
        switch (timeScaleLevel){
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
                        relationTravelLogic = "relationResult:`DOCG_TS_Contains`*1";
                        break;
                }
        }
        String queryCql = "MATCH(currentEntity:DOCG_TimeScaleEntity)-["+relationTravelLogic+"]->(childEntities:`DOCG_TimeScaleEntity`) WHERE id(currentEntity) = "+this.getTimeScaleEntityUID()+" \n" +
                eventEntitiesQueryCql;
        if(timeScaleLevel.equals(TimeScaleLevel.SELF)){
            queryCql = "MATCH(childEntities:DOCG_TimeScaleEntity) WHERE id(childEntities) = "+this.getTimeScaleEntityUID()+" \n" +
                    eventEntitiesQueryCql;
        }
        return queryCql;
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
