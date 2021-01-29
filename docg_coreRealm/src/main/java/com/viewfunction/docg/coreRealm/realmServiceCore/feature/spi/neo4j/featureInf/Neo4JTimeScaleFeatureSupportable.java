package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.TimeScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
                        TimeScaleEvent resultTimeScaleEvent = new TimeScaleEvent();
                        resultTimeScaleEvent.setReferTime(dateTime);
                        resultTimeScaleEvent.setEventData(eventData);
                        resultTimeScaleEvent.setTimeScaleGrade(timeScaleGrade);
                        resultTimeScaleEvent.setTimeFlowName(timeFlowName);
                        resultTimeScaleEvent.setEventComment(eventComment);
                        resultTimeScaleEvent.setTimeScaleEventUID(timeScaleEventEntity.getConceptionEntityUID());
                        return resultTimeScaleEvent;
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
}
