package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.TimeScaleFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleDataPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEventImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;

public interface Neo4JTimeScaleFeatureSupportable extends TimeScaleFeatureSupportable,Neo4JKeyResourcesRetrievable {

    static Logger logger = LoggerFactory.getLogger(Neo4JTimeScaleFeatureSupportable.class);
    static ZoneId zone = ZoneId.systemDefault();

    public default TimeScaleEvent attachTimeScaleEvent(long dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEvent(dateTime,eventComment,eventData,timeScaleGrade,null);
    }

    public default TimeScaleEvent attachTimeScaleEvent(long dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException {
        Instant instant = Instant.ofEpochMilli(dateTime);
        LocalDateTime timeStamp = LocalDateTime.ofInstant(instant,zone);
        return attachTimeScaleEventInnerLogic(RealmConstant._defaultTimeFlowName,getReferTime(timeStamp,timeScaleGrade),eventComment,eventData,timeScaleGrade,eventAdditionalConceptionKind);
    }

    public default TimeScaleEvent attachTimeScaleEvent(String timeFlowName,long dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEvent(timeFlowName,dateTime,eventComment,eventData,timeScaleGrade,null);
    }

    public default TimeScaleEvent attachTimeScaleEvent(String timeFlowName,long dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException {
        Instant instant = Instant.ofEpochMilli(dateTime);
        LocalDateTime timeStamp = LocalDateTime.ofInstant(instant,zone);
        return attachTimeScaleEventInnerLogic(timeFlowName,getReferTime(timeStamp,timeScaleGrade),eventComment,eventData,timeScaleGrade,eventAdditionalConceptionKind);
    }

    public default TimeScaleEvent attachTimeScaleEvent(LocalDateTime dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEvent(dateTime,eventComment,eventData,timeScaleGrade,null);
    }

    public default TimeScaleEvent attachTimeScaleEvent(LocalDateTime dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEventInnerLogic(RealmConstant._defaultTimeFlowName,getReferTime(dateTime,timeScaleGrade),eventComment,eventData,timeScaleGrade,eventAdditionalConceptionKind);
    }

    public default TimeScaleEvent attachTimeScaleEvent(String timeFlowName,LocalDateTime dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEvent(timeFlowName,dateTime,eventComment,eventData,timeScaleGrade,null);
    }

    public default TimeScaleEvent attachTimeScaleEvent(String timeFlowName,LocalDateTime dateTime, String eventComment, Map<String, Object> eventData,
                                                       TimeFlow.TimeScaleGrade timeScaleGrade,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEventInnerLogic(timeFlowName,getReferTime(dateTime,timeScaleGrade),eventComment,eventData,timeScaleGrade,eventAdditionalConceptionKind);
    }

    public default TimeScaleEvent attachTimeScaleEvent(LocalDate date, String eventComment, Map<String, Object> eventData) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEvent(date,eventComment,eventData,null);
    }

    public default TimeScaleEvent attachTimeScaleEvent(LocalDate date, String eventComment, Map<String, Object> eventData,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEventInnerLogic(RealmConstant._defaultTimeFlowName,getReferTime(date,TimeFlow.TimeScaleGrade.DAY),eventComment,eventData,TimeFlow.TimeScaleGrade.DAY,eventAdditionalConceptionKind);
    }

    public default TimeScaleEvent attachTimeScaleEvent(String timeFlowName,LocalDate date, String eventComment, Map<String, Object> eventData) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEvent(timeFlowName,date,eventComment,eventData,null);
    }

    public default TimeScaleEvent attachTimeScaleEvent(String timeFlowName,LocalDate date, String eventComment, Map<String, Object> eventData,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException {
        return attachTimeScaleEventInnerLogic(timeFlowName,getReferTime(date,TimeFlow.TimeScaleGrade.DAY),eventComment,eventData,TimeFlow.TimeScaleGrade.DAY,eventAdditionalConceptionKind);
    }

    public default TimeScaleEvent attachTimeScaleEventByTimeScaleEntityUID(String timeScaleEntityUID, String eventComment,
                                                                   Map<String, Object> eventData) throws CoreRealmServiceRuntimeException{
        return attachTimeScaleEventByTimeScaleEntityUID(timeScaleEntityUID,eventComment,eventData,null);
    }

    public default TimeScaleEvent attachTimeScaleEventByTimeScaleEntityUID(String timeScaleEntityUID, String eventComment,
                                                                           Map<String, Object> eventData,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException{
        if(this.getEntityUID() != null & timeScaleEntityUID != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(timeScaleEntityUID), null, null);
                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(RealmConstant.TimeScaleEntityClass, getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getSingleConceptionEntityTransformer, queryCql);
                if(resEntityRes == null){
                    logger.error("TimeScaleEntity does not contains entity with UID {}.", timeScaleEntityUID);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("TimeScaleEntity does not contains entity with UID " + timeScaleEntityUID + ".");
                    throw exception;
                }
                ConceptionEntity targetTimeScaleEntity = (ConceptionEntity)resEntityRes;
                List<String> conceptionKindNames = targetTimeScaleEntity.getAllConceptionKindNames();

                String timeFlowName = targetTimeScaleEntity.getAttribute("timeFlow").getAttributeValue().toString();
                TimeFlow.TimeScaleGrade timeScaleGrade = null;
                List<AttributeValue> entityAttributesList = targetTimeScaleEntity.getAttributes();

                LocalDateTime referTime = null;
                if(conceptionKindNames.contains(RealmConstant.TimeScaleYearEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.YEAR;
                    int yearValue = ((Long) getAttributeValue(entityAttributesList,"year")).intValue();
                    referTime = LocalDateTime.of(yearValue,1,1,0,0,0);
                }
                if(conceptionKindNames.contains(RealmConstant.TimeScaleMonthEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.MONTH;
                    int yearValue = ((Long) getAttributeValue(entityAttributesList,"year")).intValue();
                    int monthValue = ((Long) getAttributeValue(entityAttributesList,"month")).intValue();
                    referTime = LocalDateTime.of(yearValue,monthValue,1,0,0,0);
                }
                if(conceptionKindNames.contains(RealmConstant.TimeScaleDayEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.DAY;
                    int yearValue = ((Long) getAttributeValue(entityAttributesList,"year")).intValue();
                    int monthValue = ((Long) getAttributeValue(entityAttributesList,"month")).intValue();
                    int dayValue = ((Long) getAttributeValue(entityAttributesList,"day")).intValue();
                    referTime = LocalDateTime.of(yearValue,monthValue,dayValue,0,0,0);
                }
                if(conceptionKindNames.contains(RealmConstant.TimeScaleHourEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.HOUR;
                    int yearValue = ((Long) getAttributeValue(entityAttributesList,"year")).intValue();
                    int monthValue = ((Long) getAttributeValue(entityAttributesList,"month")).intValue();
                    int dayValue = ((Long) getAttributeValue(entityAttributesList,"day")).intValue();
                    int hourValue = ((Long) getAttributeValue(entityAttributesList,"hour")).intValue();
                    referTime = LocalDateTime.of(yearValue,monthValue,dayValue,hourValue,0,0);
                }
                if(conceptionKindNames.contains(RealmConstant.TimeScaleMinuteEntityClass)){
                    timeScaleGrade = TimeFlow.TimeScaleGrade.MINUTE;
                    int yearValue = ((Long) getAttributeValue(entityAttributesList,"year")).intValue();
                    int monthValue = ((Long) getAttributeValue(entityAttributesList,"month")).intValue();
                    int dayValue = ((Long) getAttributeValue(entityAttributesList,"day")).intValue();
                    int hourValue = ((Long) getAttributeValue(entityAttributesList,"hour")).intValue();
                    int minuteValue = ((Long) getAttributeValue(entityAttributesList,"minute")).intValue();
                    referTime = LocalDateTime.of(yearValue,monthValue,dayValue,hourValue,minuteValue,0);
                }

                Map<String, Object> propertiesMap = eventData != null ? eventData : new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                propertiesMap.put(RealmConstant._TimeScaleEventReferTime,referTime);
                propertiesMap.put(RealmConstant._TimeScaleEventComment,eventComment);
                propertiesMap.put(RealmConstant._TimeScaleEventScaleGrade,""+timeScaleGrade);
                propertiesMap.put(RealmConstant._TimeScaleEventTimeFlow,timeFlowName);
                String[] eventEntityLabelArray = eventAdditionalConceptionKind == null ? new String[]{RealmConstant.TimeScaleEventClass}:
                        new String[]{RealmConstant.TimeScaleEventClass,eventAdditionalConceptionKind};
                String createCql = CypherBuilder.createLabeledNodeWithProperties(eventEntityLabelArray, propertiesMap);
                logger.debug("Generated Cypher Statement: {}", createCql);
                getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(RealmConstant.TimeScaleEventClass, workingGraphOperationExecutor);
                Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, createCql);
                if(newEntityRes != null) {
                    ConceptionEntity timeScaleEventEntity = (ConceptionEntity) newEntityRes;
                    timeScaleEventEntity.attachToRelation(this.getEntityUID(), RealmConstant.TimeScale_AttachToRelationClass, null, true);
                    RelationEntity linkToTimeScaleEntityRelation = linkTimeScaleEntity(referTime,timeFlowName,timeScaleGrade,timeScaleEventEntity,workingGraphOperationExecutor);
                    if(linkToTimeScaleEntityRelation != null){
                        Neo4JTimeScaleEventImpl neo4JTimeScaleEventImpl = new Neo4JTimeScaleEventImpl(timeFlowName,
                                eventComment,referTime,timeScaleGrade,timeScaleEventEntity.getConceptionEntityUID());
                        neo4JTimeScaleEventImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                        return neo4JTimeScaleEventImpl;
                    }
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
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
                    Neo4JTimeScaleEventImpl neo4JTimeScaleEventImpl = new Neo4JTimeScaleEventImpl(null,null,null,null,timeScaleEventUID);
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

    public default List<TimeScaleEvent> getAttachedTimeScaleEvents(QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException{
        if(this.getEntityUID() != null) {
            if (queryParameters != null) {
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.TimeScaleEventClass,queryParameters,null);
                queryCql = queryCql.replace("MATCH (operationResult:`"+RealmConstant.TimeScaleEventClass+"`)",
                        "MATCH(currentEntity)-[:`" + RealmConstant.TimeScale_AttachToRelationClass + "`]->(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)");
                if(queryCql.contains("WHERE")){
                    queryCql = queryCql.replace("WHERE",
                            "WHERE id(currentEntity) = "+ this.getEntityUID() + " AND ");
                }else{
                    queryCql = queryCql.replace("RETURN",
                            "WHERE id(currentEntity) = "+ this.getEntityUID() + " RETURN ");
                }

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
        }
        return new ArrayList<>();
    }

    public default Long countAttachedTimeScaleEvents(AttributesParameters attributesParameters) throws CoreRealmServiceEntityExploreException{
        if(this.getEntityUID() != null) {
            if (attributesParameters != null) {
                QueryParameters queryParameters = new QueryParameters();
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
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.TimeScaleEventClass,queryParameters,CypherBuilder.CypherFunctionType.COUNT);
                queryCql = queryCql.replace("MATCH (operationResult:`"+RealmConstant.TimeScaleEventClass+"`)",
                        "MATCH(currentEntity)-[:`" + RealmConstant.TimeScale_AttachToRelationClass + "`]->(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)");
                if(queryCql.contains("WHERE")){
                    queryCql = queryCql.replace("WHERE",
                            "WHERE id(currentEntity) = "+ this.getEntityUID() + " AND ");
                }else{
                    queryCql = queryCql.replace("RETURN",
                            "WHERE id(currentEntity) = "+ this.getEntityUID() + " RETURN ");
                }
                logger.debug("Generated Cypher Statement: {}", queryCql);

                GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
                try{
                    GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
                    Object queryRes = workingGraphOperationExecutor.executeRead(GetLongFormatAggregatedReturnValueTransformer,queryCql);
                    if(queryRes != null){
                        return (Long)queryRes;
                    }
                }finally {
                    getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
                }
            }
        }
        return 0L;
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
        if(this.getEntityUID() != null) {
            String queryCql = "MATCH(currentEntity)-[:`" + RealmConstant.TimeScale_AttachToRelationClass + "`]->(timeScaleEvents:DOCG_TimeScaleEvent)<-[:`"+RealmConstant.TimeScale_TimeReferToRelationClass+"`]-(timeScaleEntities:`DOCG_TimeScaleEntity`) WHERE id(currentEntity) = " + this.getEntityUID() + " \n" +
                    "RETURN timeScaleEntities ,timeScaleEvents";
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                GetListTimeScaleDataPairTransformer getListTimeScaleDataPairTransformer = new GetListTimeScaleDataPairTransformer(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListTimeScaleDataPairTransformer,queryCql);
                if(queryRes != null){
                    return (List<TimeScaleDataPair>)queryRes;
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return new ArrayList<TimeScaleDataPair>();
    }

    public default List<TimeScaleDataPair> getAttachedTimeScaleDataPairs(QueryParameters queryParameters){



        return null;
    }

    private TimeScaleEvent attachTimeScaleEventInnerLogic(String timeFlowName,LocalDateTime dateTime, String eventComment,
                                                       Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException {
        if(this.getEntityUID() != null){
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try{
                Map<String, Object> propertiesMap = eventData != null ? eventData : new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                propertiesMap.put(RealmConstant._TimeScaleEventReferTime,dateTime);
                propertiesMap.put(RealmConstant._TimeScaleEventComment,eventComment);
                propertiesMap.put(RealmConstant._TimeScaleEventScaleGrade,""+timeScaleGrade);
                propertiesMap.put(RealmConstant._TimeScaleEventTimeFlow,timeFlowName);
                String[] eventEntityLabelArray = eventAdditionalConceptionKind == null ? new String[]{RealmConstant.TimeScaleEventClass}:
                        new String[]{RealmConstant.TimeScaleEventClass,eventAdditionalConceptionKind};
                String createCql = CypherBuilder.createLabeledNodeWithProperties(eventEntityLabelArray, propertiesMap);
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
                        neo4JTimeScaleEventImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                        return neo4JTimeScaleEventImpl;
                    }
                }
            }finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    private RelationEntity linkTimeScaleEntity(LocalDateTime temporal, String timeFlowName, TimeFlow.TimeScaleGrade timeScaleGrade,
                                               ConceptionEntity timeScaleEventEntity, GraphOperationExecutor workingGraphOperationExecutor){
        int year = temporal.getYear();
        int month = temporal.getMonthValue();
        int day = temporal.getDayOfMonth();
        int hour = temporal.getHour();
        int minute = temporal.getMinute();
        int second = temporal.getSecond();
        return linkTimeScaleEntity(year,month,day,hour,minute,second,timeFlowName,timeScaleGrade,timeScaleEventEntity,workingGraphOperationExecutor);
    }

    private RelationEntity linkTimeScaleEntity(int year, int month,int day, int hour,int minute,int second,
                                               String timeFlowName, TimeFlow.TimeScaleGrade timeScaleGrade,
                                               ConceptionEntity timeScaleEventEntity, GraphOperationExecutor workingGraphOperationExecutor){
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

    private LocalDateTime getReferTime(Temporal dateTime,TimeFlow.TimeScaleGrade timeScaleGrade){
        if(dateTime instanceof LocalDate){
            LocalDateTime referTime = ((LocalDate) dateTime).atTime(LocalTime.of(0,0,0));
            return referTime;
        }else if(dateTime instanceof LocalDateTime){
            LocalDateTime timeStampDateTime = (LocalDateTime)dateTime;
            int year = timeStampDateTime.getYear();
            int month = timeStampDateTime.getMonthValue();
            int day = timeStampDateTime.getDayOfMonth();
            int hour = timeStampDateTime.getHour();
            int minute = timeStampDateTime.getMinute();
            int second = timeStampDateTime.getSecond();
            LocalDateTime referTime = null;
            switch (timeScaleGrade){
                case YEAR:
                    referTime = LocalDateTime.of(year,1,1,0,0,0);
                    break;
                case MONTH:
                    referTime = LocalDateTime.of(year,month,1,0,0,0);
                    break;
                case DAY:
                    referTime = LocalDateTime.of(year,month,day,0,0,0);
                    break;
                case HOUR:
                    referTime = LocalDateTime.of(year,month,day,hour,0,0);
                    break;
                case MINUTE:
                    referTime = LocalDateTime.of(year,month,day,hour,minute,0);
                    break;
                case SECOND:
                    referTime = LocalDateTime.of(year,month,day,hour,minute,second);
                    break;
            }
            return referTime;
        }else{
            return null;
        }
    }

    private Object getAttributeValue(List<AttributeValue> entityAttributesList, String attributeName){
        if(entityAttributesList != null & attributeName != null){
            for(AttributeValue currentAttributeValue:entityAttributesList){
                if(attributeName.equals(currentAttributeValue.getAttributeName())){
                    return currentAttributeValue.getAttributeValue();
                }
            }
        }
        return null;
    }
}
