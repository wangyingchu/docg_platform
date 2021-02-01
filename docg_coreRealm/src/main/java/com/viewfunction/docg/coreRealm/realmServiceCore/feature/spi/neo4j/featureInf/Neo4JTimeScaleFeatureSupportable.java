package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.TimeScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleDataPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEventImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public interface Neo4JTimeScaleFeatureSupportable extends TimeScaleFeatureSupportable,Neo4JKeyResourcesRetrievable {

    static Logger logger = LoggerFactory.getLogger(Neo4JTimeScaleFeatureSupportable.class);

    public default TimeScaleEvent attachTimeScaleEvent(long dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEventInnerLogic(RealmConstant._defaultTimeFlowName,dateTime,eventComment,eventData,timeScaleGrade);
    }

    public default TimeScaleEvent attachTimeScaleEvent(String timeFlowName,long dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEventInnerLogic(timeFlowName,dateTime,eventComment,eventData,timeScaleGrade);
    }

    public default boolean detachTimeScaleEvent(String timeScaleEventUID) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(timeScaleEventUID), null, null);
                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(RealmConstant.TimeScaleEventClass, getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getSingleConceptionEntityTransformer, queryCql);
                if(resEntityRes == null){
                    logger.error("TimeScaleEvent does not contains entity with UID {}.", timeScaleEventUID);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("TimeScaleEvent does not contains entity with UID " + timeScaleEventUID + ".");
                    throw exception;
                }else{
                    Neo4JTimeScaleEventImpl neo4JTimeScaleEventImpl = new Neo4JTimeScaleEventImpl(null,null,0l,null,timeScaleEventUID);
                    neo4JTimeScaleEventImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    if(neo4JTimeScaleEventImpl.getAttachConceptionEntity().getConceptionEntityUID().equals(this.getEntityUID())){
                        String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(timeScaleEventUID),null,null);
                        Object deletedEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, deleteCql);
                        if(deletedEntityRes == null){
                            throw new CoreRealmServiceRuntimeException();
                        }else{
                            return true;
                        }
                    }else{
                        logger.error("TimeScaleEvent with entity UID {} doesn't attached to current ConceptionEntity with UID {}.", timeScaleEventUID,this.getEntityUID());
                        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                        exception.setCauseMessage("TimeScaleEvent with entity UID " + timeScaleEventUID + " doesn't attached to current ConceptionEntity with UID "+ this.getEntityUID()+ ".");
                        throw exception;
                    }
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    public default List<TimeScaleEvent> getAttachedTimeScaleEvents(){
        if(this.getEntityUID() != null) {
            String queryCql = "MATCH(currentEntity)-[:`" + RealmConstant.TimeScale_AttachToRelationClass + "`]->(timeScaleEvents:DOCG_TimeScaleEvent) WHERE id(currentEntity) = " + this.getEntityUID() + " \n" +
                    "RETURN timeScaleEvents as operationResult";
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                GetListTimeScaleEventTransformer getListTimeScaleEventTransformer = new GetListTimeScaleEventTransformer(null,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListTimeScaleEventTransformer,queryCql);
                if(queryRes != null){
                    List<TimeScaleEvent> res = (List<TimeScaleEvent>)queryRes;
                    return res;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return new ArrayList<>();
    }

    public default List<TimeScaleEntity> getAttachedTimeScaleEntities(){
        if(this.getEntityUID() != null) {
            String queryCql = "MATCH(currentEntity)-[:`" + RealmConstant.TimeScale_AttachToRelationClass + "`]->(timeScaleEvents:DOCG_TimeScaleEvent)<-[:`"+RealmConstant.TimeScale_TimeReferToRelationClass+"`]-(timeScaleEntities:`DOCG_TimeScaleEntity`) WHERE id(currentEntity) = " + this.getEntityUID() + " \n" +
                    "RETURN timeScaleEntities as operationResult";
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                GetLinkedListTimeScaleEntityTransformer getLinkedListTimeScaleEntityTransformer =
                        new GetLinkedListTimeScaleEntityTransformer(null,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getLinkedListTimeScaleEntityTransformer,queryCql);
                if(queryRes != null){
                    return (LinkedList<TimeScaleEntity>)queryRes;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return new ArrayList<>();
    }

    public default List<TimeScaleDataPair> getAttachedTimeScaleDataPairs(){
        List<TimeScaleDataPair> timeScaleDataPairList = new ArrayList<>();
        if(this.getEntityUID() != null) {
            String queryCql = "MATCH(currentEntity)-[:`" + RealmConstant.TimeScale_AttachToRelationClass + "`]->(timeScaleEvents:DOCG_TimeScaleEvent)<-[:`"+RealmConstant.TimeScale_TimeReferToRelationClass+"`]-(timeScaleEntities:`DOCG_TimeScaleEntity`) WHERE id(currentEntity) = " + this.getEntityUID() + " \n" +
                    "RETURN timeScaleEntities ,timeScaleEvents";
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                DataTransformer<Object> _DataTransformer = new DataTransformer<Object>() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()) {
                            Record record = result.next();

                            Neo4JTimeScaleEntityImpl neo4JTimeScaleEntityImpl = null;
                            Neo4JTimeScaleEventImpl neo4JTimeScaleEventImpl = null;

                            Node timeScaleEntityNode = record.get("timeScaleEntities").asNode();
                            List<String> allLabelNames = Lists.newArrayList(timeScaleEntityNode.labels());
                            boolean isMatchedEntity = true;
                            if (allLabelNames.size() > 0) {
                                isMatchedEntity = allLabelNames.contains(RealmConstant.TimeScaleEntityClass);
                            }
                            if (isMatchedEntity) {
                                TimeFlow.TimeScaleGrade timeScaleGrade = null;
                                long nodeUID = timeScaleEntityNode.id();
                                String entityUID = "" + nodeUID;
                                int value = timeScaleEntityNode.get("id").asInt();
                                String timeFlowName = timeScaleEntityNode.get("timeFlow").asString();

                                if (timeScaleEntityNode.get("year").asObject() != null) {
                                    timeScaleGrade = TimeFlow.TimeScaleGrade.YEAR;
                                } else if (timeScaleEntityNode.get("month").asObject() != null) {
                                    timeScaleGrade = TimeFlow.TimeScaleGrade.MONTH;
                                } else if (timeScaleEntityNode.get("day").asObject() != null) {
                                    timeScaleGrade = TimeFlow.TimeScaleGrade.DAY;
                                } else if (timeScaleEntityNode.get("hour").asObject() != null) {
                                    timeScaleGrade = TimeFlow.TimeScaleGrade.HOUR;
                                } else if (timeScaleEntityNode.get("minute").asObject() != null) {
                                    timeScaleGrade = TimeFlow.TimeScaleGrade.MINUTE;
                                }
                                neo4JTimeScaleEntityImpl = new Neo4JTimeScaleEntityImpl(
                                        null, timeFlowName, entityUID, timeScaleGrade, value);
                                neo4JTimeScaleEntityImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                            }

                            Node timeScaleEventNode = record.get("timeScaleEvents").asNode();
                            List<String> allConceptionKindNames = Lists.newArrayList(timeScaleEventNode.labels());
                            boolean isMatchedEvent = false;
                            if(allConceptionKindNames.size()>0){
                                isMatchedEvent = allConceptionKindNames.contains(RealmConstant.TimeScaleEventClass);
                            }
                            if(isMatchedEvent){
                                long nodeUID = timeScaleEventNode.id();
                                String timeScaleEventUID = ""+nodeUID;
                                String eventComment = timeScaleEventNode.get(RealmConstant._TimeScaleEventComment).asString();
                                String timeScaleGrade = timeScaleEventNode.get(RealmConstant._TimeScaleEventScaleGrade).asString();
                                long referTime = timeScaleEventNode.get(RealmConstant._TimeScaleEventReferTime).asLong();
                                neo4JTimeScaleEventImpl = new Neo4JTimeScaleEventImpl(null,eventComment,referTime,getTimeScaleGrade(timeScaleGrade),timeScaleEventUID);
                                neo4JTimeScaleEventImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                            }

                            if(neo4JTimeScaleEntityImpl != null && neo4JTimeScaleEventImpl != null){
                                timeScaleDataPairList.add(
                                        new TimeScaleDataPair(neo4JTimeScaleEventImpl,neo4JTimeScaleEntityImpl)
                                );
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(_DataTransformer,queryCql);
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return timeScaleDataPairList;
    }

    private TimeScaleEvent attachTimeScaleEventInnerLogic(String timeFlowName,long dateTime, String eventComment,
                                                       Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException {
        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                Map<String, Object> propertiesMap = eventData != null ? eventData : new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                propertiesMap.put(RealmConstant._TimeScaleEventReferTime,dateTime);
                propertiesMap.put(RealmConstant._TimeScaleEventComment,eventComment);
                propertiesMap.put(RealmConstant._TimeScaleEventScaleGrade,""+timeScaleGrade);
                propertiesMap.put(RealmConstant._TimeScaleEventTimeFlow,timeFlowName);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.TimeScaleEventClass}, propertiesMap);
                logger.debug("Generated Cypher Statement: {}", createCql);
                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(RealmConstant.TimeScaleEventClass, workingGraphOperationExecutor);
                Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, createCql);
                if(newEntityRes != null) {
                    ConceptionEntity timeScaleEventEntity = (ConceptionEntity) newEntityRes;
                    timeScaleEventEntity.attachToRelation(this.getEntityUID(), RealmConstant.TimeScale_AttachToRelationClass, null, true);
                    RelationEntity linkToTimeScaleEntityRelation = linkTimeScaleEntity(dateTime,timeFlowName,timeScaleGrade,timeScaleEventEntity,workingGraphOperationExecutor);
                    if(linkToTimeScaleEntityRelation != null){
                        Neo4JTimeScaleEventImpl neo4JTimeScaleEventImpl = new Neo4JTimeScaleEventImpl(timeFlowName,
                                eventComment,dateTime,timeScaleGrade,timeScaleEventEntity.getConceptionEntityUID());
                        return neo4JTimeScaleEventImpl;
                    }
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    private RelationEntity linkTimeScaleEntity(long dateTime, String timeFlowName, TimeFlow.TimeScaleGrade timeScaleGrade,
                                               ConceptionEntity timeScaleEventEntity, GraphOperationExecutor workingGraphOperationExecutor){
        Calendar eventCalendar=Calendar.getInstance();
        eventCalendar.setTimeInMillis(dateTime);

        int year = eventCalendar.get(Calendar.YEAR) ;
        int month = eventCalendar.get(Calendar.MONTH)+1;
        int day = eventCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = eventCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = eventCalendar.get(Calendar.MINUTE);
        int second = eventCalendar.get(Calendar.SECOND);

        String queryCql = null;
        switch (timeScaleGrade) {
            case YEAR:
                queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Year{year:"+year+"})";
                break;
            case MONTH:
                queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Month{month:"+month+"})";
                break;
            case DAY:
                queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Day{day:"+day+"})";
                break;
            case HOUR:
                queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+day+"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Hour{hour:"+hour+"})";
                break;
            case MINUTE:
                queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+day+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour{hour:"+hour+"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Minute{minute:"+minute+"})";
                break;
            case SECOND:
                break;
        }
        String createCql = queryCql + ",(timeScaleEvent:DOCG_TimeScaleEvent) WHERE id(timeScaleEvent) = "+ timeScaleEventEntity.getConceptionEntityUID() +" CREATE (timeScaleEntity)-[r:"+RealmConstant.TimeScale_TimeReferToRelationClass+"]->(timeScaleEvent) return r as operationResult";
        logger.debug("Generated Cypher Statement: {}", createCql);
        GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer(RealmConstant.TimeScale_TimeReferToRelationClass,null);
        Object linkRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer,createCql);
        return linkRes != null? (RelationEntity)linkRes : null;
    }

    private TimeFlow.TimeScaleGrade getTimeScaleGrade(String timeScaleGradeValue){
        if(timeScaleGradeValue.equals("YEAR")){
            return TimeFlow.TimeScaleGrade.YEAR;
        }else if(timeScaleGradeValue.equals("MONTH")){
            return TimeFlow.TimeScaleGrade.MONTH;
        }else if(timeScaleGradeValue.equals("DAY")){
            return TimeFlow.TimeScaleGrade.DAY;
        }else if(timeScaleGradeValue.equals("HOUR")){
            return TimeFlow.TimeScaleGrade.HOUR;
        }else if(timeScaleGradeValue.equals("MINUTE")){
            return TimeFlow.TimeScaleGrade.MINUTE;
        }else if(timeScaleGradeValue.equals("SECOND")){
            return TimeFlow.TimeScaleGrade.SECOND;
        }
        return null;
    }
}
