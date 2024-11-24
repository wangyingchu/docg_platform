package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.TemporalScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BatchDataOperationUtil {

    public enum CPUUsageRate {Low, Middle, High}
    private static Logger logger = LoggerFactory.getLogger(BatchDataOperationUtil.class);
    private static ZoneId zone = ZoneId.systemDefault();

    public static Map<String,Object> batchAddNewEntities(String targetConceptionTypeName,List<ConceptionEntityValue> conceptionEntityValuesList,CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        return batchAddNewEntities(targetConceptionTypeName,conceptionEntityValuesList,degreeOfParallelism);
    }

    public static Map<String,Object> batchAddNewEntities(String targetConceptionTypeName,List<ConceptionEntityValue> conceptionEntityValuesList,int degreeOfParallelism){
        int singlePartitionSize = (conceptionEntityValuesList.size()/degreeOfParallelism)+1;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValuesList, singlePartitionSize);
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<ConceptionEntityValue> currentConceptionEntityValueList:rsList){
            InsertRecordThread insertRecordThread = new InsertRecordThread(targetConceptionTypeName,currentConceptionEntityValueList,threadReturnDataMap);
            executor.execute(insertRecordThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        return threadReturnDataMap;
    }

    private static class InsertRecordThread implements Runnable{
        private List<ConceptionEntityValue> conceptionEntityValueList;
        private String conceptionKindName;
        private Map<String,Object> threadReturnDataMap;

        public InsertRecordThread(String conceptionKindName,List<ConceptionEntityValue> conceptionEntityValueList,Map<String,Object> threadReturnDataMap){
            this.conceptionEntityValueList = conceptionEntityValueList;
            this.conceptionKindName = conceptionKindName;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run(){
            String currentThreadName = Thread.currentThread().getName();
            CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
            long successfulCount = 0;
            ConceptionKind conceptionKind = coreRealm.getConceptionKind(conceptionKindName);
            int singleBatchLoopInsertCount = 1000;
            if(conceptionEntityValueList.size() <= singleBatchLoopInsertCount){
                coreRealm.openGlobalSession();
                for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
                    ConceptionEntity resultEntity = conceptionKind.newEntity(currentConceptionEntityValue,false);
                    if(resultEntity != null){
                        successfulCount ++;
                    }
                }
                coreRealm.closeGlobalSession();
            }else{
                GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(this.conceptionKindName, graphOperationExecutor);
                List<List<ConceptionEntityValue>> cutSubLists = Lists.partition(conceptionEntityValueList, 100);
                ZonedDateTime currentDateTime = ZonedDateTime.now();
                for(List<ConceptionEntityValue> currentCutList:cutSubLists){
                    List<String> entityParas = new ArrayList<>();
                    for(ConceptionEntityValue currentConceptionEntityValue:currentCutList){
                        Map<String, Object> relationPropertiesValue = currentConceptionEntityValue.getEntityAttributesValue();
                        CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesValue,currentDateTime);
                        String propertiesCQLPart = CypherBuilder.createEntityProperties(relationPropertiesValue);
                        entityParas.add(propertiesCQLPart);
                    }
                    String cql = "UNWIND  " + entityParas +" AS entityParas"+"\n "+
                            "CREATE (operationResult:`"+this.conceptionKindName+"`)"
                            +"SET operationResult = entityParas";
                    graphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,cql);
                    successfulCount = conceptionEntityValueList.size();
                }
                graphOperationExecutor.close();
            }
            threadReturnDataMap.put(currentThreadName,successfulCount);
        }
    }

    public static Map<String,Object> batchAttachTimeScaleEvents(List<ConceptionEntityValue> conceptionEntityValueList, String timeEventAttributeName,String timeFlowName,String eventComment,
                                                                Map<String,Object> globalEventData, TimeFlow.TimeScaleGrade timeScaleGrade,CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        return batchAttachTimeScaleEvents(conceptionEntityValueList,timeEventAttributeName,timeFlowName,eventComment,globalEventData,timeScaleGrade,degreeOfParallelism);
    }

    public static Map<String,Object> batchAttachTimeScaleEvents(List<ConceptionEntityValue> conceptionEntityValueList, String timeEventAttributeName,String timeFlowName,String eventComment,
                                                  Map<String,Object> globalEventData, TimeFlow.TimeScaleGrade timeScaleGrade,int degreeOfParallelism){
        int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);
        Map<String,String> timeScaleEntitiesMetaInfoMapping = new HashMap<>();
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<ConceptionEntityValue> currentConceptionEntityValueList:rsList){
            LinkTimeScaleEventThread linkTimeScaleEventThread = new LinkTimeScaleEventThread(timeEventAttributeName,timeFlowName,
                    eventComment,globalEventData,timeScaleGrade,currentConceptionEntityValueList,timeScaleEntitiesMetaInfoMapping,threadReturnDataMap);
            executor.execute(linkTimeScaleEventThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        return threadReturnDataMap;
    }

    private static class LinkTimeScaleEventThread implements Runnable{
        private String timeEventAttributeName;
        private String eventComment;
        private Map<String,Object> globalEventData;
        private TimeFlow.TimeScaleGrade timeScaleGrade;
        private List<ConceptionEntityValue> conceptionEntityValueList;
        private Map<String,String> timeScaleEntitiesMetaInfoMapping;
        private Map<String,Object> threadReturnDataMap;
        private DateTimeFormatter formatter;
        private String timeFlowName;
        private String timeEventYearAttributeName;
        private String timeEventMonthAttributeName;
        private String timeEventDayAttributeName;
        private String timeEventHourAttributeName;
        private String timeEventMinuteAttributeName;
        private boolean isMultiAttributesMode = false;

        public LinkTimeScaleEventThread(String timeEventAttributeName,String timeFlowName,String eventComment,Map<String,Object> globalEventData,
                                        TimeFlow.TimeScaleGrade timeScaleGrade,List<ConceptionEntityValue> conceptionEntityValueList,
                                        Map<String,String> timeScaleEntitiesMetaInfoMapping,Map<String,Object> threadReturnDataMap){
            this.timeEventAttributeName = timeEventAttributeName;
            this.eventComment = eventComment;
            this.globalEventData = globalEventData;
            this.timeScaleGrade = timeScaleGrade;
            this.conceptionEntityValueList = conceptionEntityValueList;
            this.timeScaleEntitiesMetaInfoMapping = timeScaleEntitiesMetaInfoMapping;
            this.threadReturnDataMap = threadReturnDataMap;
            this.timeFlowName = timeFlowName;
        }

        public LinkTimeScaleEventThread(String timeEventAttributeName,DateTimeFormatter formatter,String timeFlowName,String eventComment,Map<String,Object> globalEventData,
                                        TimeFlow.TimeScaleGrade timeScaleGrade,List<ConceptionEntityValue> conceptionEntityValueList,
                                        Map<String,String> timeScaleEntitiesMetaInfoMapping,Map<String,Object> threadReturnDataMap){
            this.timeEventAttributeName = timeEventAttributeName;
            this.eventComment = eventComment;
            this.globalEventData = globalEventData;
            this.timeScaleGrade = timeScaleGrade;
            this.conceptionEntityValueList = conceptionEntityValueList;
            this.timeScaleEntitiesMetaInfoMapping = timeScaleEntitiesMetaInfoMapping;
            this.threadReturnDataMap = threadReturnDataMap;
            this.formatter = formatter;
            this.timeFlowName = timeFlowName;
        }

        public LinkTimeScaleEventThread(String timeEventYearAttributeName, String timeEventMonthAttributeName, String timeEventDayAttributeName, String timeEventHourAttributeName, String timeEventMinuteAttributeName,
                                        String timeFlowName,String eventComment,Map<String,Object> globalEventData,TimeFlow.TimeScaleGrade timeScaleGrade,List<ConceptionEntityValue> conceptionEntityValueList,
                                        Map<String,String> timeScaleEntitiesMetaInfoMapping,Map<String,Object> threadReturnDataMap){
            this.timeEventYearAttributeName = timeEventYearAttributeName;
            this.timeEventMonthAttributeName = timeEventMonthAttributeName;
            this.timeEventDayAttributeName = timeEventDayAttributeName;
            this.timeEventHourAttributeName = timeEventHourAttributeName;
            this.timeEventMinuteAttributeName = timeEventMinuteAttributeName;
            this.eventComment = eventComment;
            this.globalEventData = globalEventData;
            this.timeScaleGrade = timeScaleGrade;
            this.conceptionEntityValueList = conceptionEntityValueList;
            this.timeScaleEntitiesMetaInfoMapping = timeScaleEntitiesMetaInfoMapping;
            this.threadReturnDataMap = threadReturnDataMap;
            this.timeFlowName = timeFlowName;
            this.isMultiAttributesMode = true;
        }

        @Override
        public void run() {
            String currentThreadName = Thread.currentThread().getName();
            long successfulCount = 0;
            GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
            for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
                if(this.isMultiAttributesMode){
                    Map<String, Object> entityAttributesValue = currentConceptionEntityValue.getEntityAttributesValue();
                    boolean attachResult=false;
                    switch (timeScaleGrade){
                        case YEAR :
                            if(entityAttributesValue.containsKey(this.timeEventYearAttributeName)){
                                Object yearValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventYearAttributeName);
                                try{
                                    int yearValueInt = Integer.parseInt(yearValue.toString());
                                    LocalDateTime localDateTime = LocalDateTime.of(yearValueInt,1,1,0,0);
                                    attachResult = attachTimeScaleEventLogic(timeScaleEntitiesMetaInfoMapping,currentConceptionEntityValue.getConceptionEntityUID(),timeFlowName,localDateTime,
                                            eventComment,globalEventData,timeScaleGrade,graphOperationExecutor);
                                }catch(NumberFormatException e){
                                    e.printStackTrace();
                                }catch(DateTimeException e){
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case MONTH :
                            if(entityAttributesValue.containsKey(this.timeEventYearAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventMonthAttributeName)){
                                Object yearValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventYearAttributeName);
                                Object monthValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventMonthAttributeName);
                                try{
                                    int yearValueInt = Integer.parseInt(yearValue.toString());
                                    int monthValueInt = Integer.parseInt(monthValue.toString());
                                    LocalDateTime localDateTime = LocalDateTime.of(yearValueInt,monthValueInt,1,0,0);
                                    attachResult = attachTimeScaleEventLogic(timeScaleEntitiesMetaInfoMapping,currentConceptionEntityValue.getConceptionEntityUID(),timeFlowName,localDateTime,
                                            eventComment,globalEventData,timeScaleGrade,graphOperationExecutor);
                                }catch(NumberFormatException e){
                                    e.printStackTrace();
                                }catch(DateTimeException e){
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case DAY :
                            if(entityAttributesValue.containsKey(this.timeEventYearAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventMonthAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventDayAttributeName)){
                                Object yearValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventYearAttributeName);
                                Object monthValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventMonthAttributeName);
                                Object dayValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventDayAttributeName);
                                try{
                                    int yearValueInt = Integer.parseInt(yearValue.toString());
                                    int monthValueInt = Integer.parseInt(monthValue.toString());
                                    int dayValueInt = Integer.parseInt(dayValue.toString());
                                    LocalDateTime localDateTime = LocalDateTime.of(yearValueInt,monthValueInt,dayValueInt,0,0);
                                    attachResult = attachTimeScaleEventLogic(timeScaleEntitiesMetaInfoMapping,currentConceptionEntityValue.getConceptionEntityUID(),timeFlowName,localDateTime,
                                            eventComment,globalEventData,timeScaleGrade,graphOperationExecutor);
                                }catch(NumberFormatException e){
                                    e.printStackTrace();
                                }catch(DateTimeException e){
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case HOUR :
                            if(entityAttributesValue.containsKey(this.timeEventYearAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventMonthAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventDayAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventHourAttributeName)){
                                Object yearValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventYearAttributeName);
                                Object monthValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventMonthAttributeName);
                                Object dayValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventDayAttributeName);
                                Object hourValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventHourAttributeName);
                                try{
                                    int yearValueInt = Integer.parseInt(yearValue.toString());
                                    int monthValueInt = Integer.parseInt(monthValue.toString());
                                    int dayValueInt = Integer.parseInt(dayValue.toString());
                                    int hourValueInt = Integer.parseInt(hourValue.toString());
                                    LocalDateTime localDateTime = LocalDateTime.of(yearValueInt,monthValueInt,dayValueInt,hourValueInt,0);
                                    attachResult = attachTimeScaleEventLogic(timeScaleEntitiesMetaInfoMapping,currentConceptionEntityValue.getConceptionEntityUID(),timeFlowName,localDateTime,
                                            eventComment,globalEventData,timeScaleGrade,graphOperationExecutor);
                                }catch(NumberFormatException e){
                                    e.printStackTrace();
                                }catch(DateTimeException e){
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case MINUTE :
                            if(entityAttributesValue.containsKey(this.timeEventYearAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventMonthAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventDayAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventHourAttributeName) &
                                    entityAttributesValue.containsKey(this.timeEventMinuteAttributeName)){
                                Object yearValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventYearAttributeName);
                                Object monthValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventMonthAttributeName);
                                Object dayValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventDayAttributeName);
                                Object hourValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventHourAttributeName);
                                Object minuteValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.timeEventMinuteAttributeName);
                                try{
                                    int yearValueInt = Integer.parseInt(yearValue.toString());
                                    int monthValueInt = Integer.parseInt(monthValue.toString());
                                    int dayValueInt = Integer.parseInt(dayValue.toString());
                                    int hourValueInt = Integer.parseInt(hourValue.toString());
                                    int minuteValueInt = Integer.parseInt(minuteValue.toString());
                                    LocalDateTime localDateTime = LocalDateTime.of(yearValueInt,monthValueInt,dayValueInt,hourValueInt,minuteValueInt);
                                    attachResult = attachTimeScaleEventLogic(timeScaleEntitiesMetaInfoMapping,currentConceptionEntityValue.getConceptionEntityUID(),timeFlowName,localDateTime,
                                            eventComment,globalEventData,timeScaleGrade,graphOperationExecutor);
                                }catch(NumberFormatException e){
                                    e.printStackTrace();
                                }catch(DateTimeException e){
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }
                    if(attachResult){
                        successfulCount++;
                    }
                }else{
                    if(currentConceptionEntityValue.getEntityAttributesValue().get(timeEventAttributeName) != null){
                        Object targetDateValue = currentConceptionEntityValue.getEntityAttributesValue().get(timeEventAttributeName);
                        boolean attachResult;
                        if(formatter != null){
                            attachResult = attachTimeScaleEventLogic(timeScaleEntitiesMetaInfoMapping,currentConceptionEntityValue.getConceptionEntityUID(),timeFlowName,targetDateValue,formatter,
                                    eventComment,globalEventData,timeScaleGrade,graphOperationExecutor);
                        }else{
                            attachResult = attachTimeScaleEventLogic(timeScaleEntitiesMetaInfoMapping,currentConceptionEntityValue.getConceptionEntityUID(),timeFlowName,targetDateValue,
                                    eventComment,globalEventData,timeScaleGrade,graphOperationExecutor);
                        }
                        if(attachResult){
                            successfulCount++;
                        }
                    }
                }
            }
            graphOperationExecutor.close();
            threadReturnDataMap.put(currentThreadName,successfulCount);
        }
    }

    private static boolean attachTimeScaleEventLogic(Map<String,String> timeScaleEntitiesMetaInfoMapping,String conceptionEntityUID,String timeFlowName,Object dateTime,String eventComment,
                                                 Map<String,Object> globalEventData,TimeFlow.TimeScaleGrade timeScaleGrade,GraphOperationExecutor workingGraphOperationExecutor){
        Map<String, Object> propertiesMap = new HashMap<>();
        if(globalEventData != null){
            propertiesMap.putAll(globalEventData);
        }
        CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
        TimeFlow.TimeScaleGrade referTimeScaleGrade = timeScaleGrade;
        LocalDateTime eventReferTime = null;
        if(dateTime instanceof ZonedDateTime){
            eventReferTime = ((ZonedDateTime)dateTime).toLocalDateTime();
        }else if(dateTime instanceof LocalDateTime){
            eventReferTime = (LocalDateTime)dateTime;
        }else if(dateTime instanceof LocalDate){
            eventReferTime = ((LocalDate) dateTime).atTime(LocalTime.of(0,0,0));
        }else if(dateTime instanceof Date){
            Instant instant = ((Date)dateTime).toInstant();
            eventReferTime = LocalDateTime.ofInstant(instant, zone);
        }

        if(eventReferTime != null){
            String realTimeFlowName = timeFlowName != null ? timeFlowName : RealmConstant._defaultTimeFlowName;
            propertiesMap.put(RealmConstant._TimeScaleEventReferTime,eventReferTime);
            propertiesMap.put(RealmConstant._TimeScaleEventComment,eventComment);
            propertiesMap.put(RealmConstant._TimeScaleEventScaleGrade,""+referTimeScaleGrade);
            propertiesMap.put(RealmConstant._TimeScaleEventTimeFlow,realTimeFlowName);

            String createEventCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.TimeScaleEventClass}, propertiesMap);
            GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                    new GetSingleConceptionEntityTransformer(RealmConstant.TimeScaleEventClass, workingGraphOperationExecutor);
            Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, createEventCql);
            if(newEntityRes != null) {
                String timeEventUID = ((ConceptionEntity)newEntityRes).getConceptionEntityUID();
                Map<String,Object> relationPropertiesMap = new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
                String createCql = CypherBuilder.createNodesRelationshipByIdsMatch(Long.parseLong(conceptionEntityUID),Long.parseLong(timeEventUID), RealmConstant.TimeScale_AttachToRelationClass,relationPropertiesMap);
                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.TimeScale_AttachToRelationClass,workingGraphOperationExecutor);
                Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createCql);
                if(newRelationEntityRes != null){
                    String timeScaleEntityUID = getTimeScaleEntityUID(timeScaleEntitiesMetaInfoMapping,eventReferTime,realTimeFlowName, referTimeScaleGrade, workingGraphOperationExecutor);
                    if(timeScaleEntityUID != null){
                        String linkToTimeScaleEntityCql = CypherBuilder.createNodesRelationshipByIdsMatch(Long.parseLong(timeScaleEntityUID),Long.parseLong(timeEventUID),RealmConstant.TimeScale_TimeReferToRelationClass,relationPropertiesMap);
                        workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, linkToTimeScaleEntityCql);
                    }
                }
            }
            return true;
        }else{
            return false;
        }
    }

    private static boolean attachTimeScaleEventLogic(Map<String,String> timeScaleEntitiesMetaInfoMapping,String conceptionEntityUID,String timeFlowName,Object dateTime,DateTimeFormatter formatter,String eventComment,
                                                     Map<String,Object> globalEventData,TimeFlow.TimeScaleGrade timeScaleGrade,GraphOperationExecutor workingGraphOperationExecutor){
        Map<String, Object> propertiesMap = new HashMap<>();
        if(globalEventData != null){
            propertiesMap.putAll(globalEventData);
        }
        CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
        TimeFlow.TimeScaleGrade referTimeScaleGrade = timeScaleGrade;
        try {
            LocalDateTime eventReferTime = LocalDateTime.parse(dateTime.toString(), formatter);
            propertiesMap.put(RealmConstant._TimeScaleEventReferTime, eventReferTime);
            propertiesMap.put(RealmConstant._TimeScaleEventComment,eventComment);
            propertiesMap.put(RealmConstant._TimeScaleEventScaleGrade,""+referTimeScaleGrade);
            String realTimeFlowName = timeFlowName != null ? timeFlowName : RealmConstant._defaultTimeFlowName;
            propertiesMap.put(RealmConstant._TimeScaleEventTimeFlow,realTimeFlowName);

            /* batch execute cypher example
            CREATE (timeScaleEvent:`DOCG_TimeScaleEvent`) SET timeScaleEvent.DOCG_TimeScaleEventReferTime = localdatetime('2013-01-01T00:00:00'), timeScaleEvent.DOCG_TimeScaleEventScaleGrade = 'DAY', timeScaleEvent.dataOrigin = 'dataOrigin001', timeScaleEvent.lastModifyDate = datetime('2024-11-24T10:37:02.247185171+08:00[Asia/Shanghai]'), timeScaleEvent.DOCG_TimeScaleEventTimeFlow = 'DefaultTimeFlow', timeScaleEvent.createDate = datetime('2024-11-24T10:37:02.247185171+08:00[Asia/Shanghai]'), timeScaleEvent.DOCG_TimeScaleEventComment = 'chamberFoundedAt'
            WITH timeScaleEvent
            MATCH (conceptionEntity) WHERE id(conceptionEntity) = 692233 CREATE (conceptionEntity)-[attachToTimeScale:`DOCG_AttachToTimeScale` {dataOrigin: 'dataOrigin001', lastModifyDate: datetime('2024-11-24T10:37:02.247322451+08:00[Asia/Shanghai]'), createDate: datetime('2024-11-24T10:37:02.247322451+08:00[Asia/Shanghai]')}]->(timeScaleEvent)
            WITH attachToTimeScale ,timeScaleEvent,conceptionEntity
            MATCH (timeScaleEntity) WHERE id(timeScaleEntity) = 39823009 CREATE (timeScaleEntity)-[timeReferTo:`DOCG_TS_TimeReferTo` {dataOrigin: 'dataOrigin001', lastModifyDate: datetime('2024-11-24T10:37:02.247322451+08:00[Asia/Shanghai]'), createDate: datetime('2024-11-24T10:37:02.247322451+08:00[Asia/Shanghai]')}]->(timeScaleEvent) RETURN attachToTimeScale ,timeScaleEvent,conceptionEntity,timeReferTo,timeScaleEntity
            */

            String timeScaleEntityUID = getTimeScaleEntityUID(timeScaleEntitiesMetaInfoMapping,eventReferTime,realTimeFlowName, referTimeScaleGrade, workingGraphOperationExecutor);
            if(timeScaleEntityUID!= null){
                String createTimeScaleEventCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.TimeScaleEventClass}, propertiesMap);
                createTimeScaleEventCql =
                        createTimeScaleEventCql.replace("RETURN operationResult","").replaceAll("operationResult","timeScaleEvent");

                Map<String,Object> relationPropertiesMap = new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
                String createAttachToTimeScaleRelationshipCql = CypherBuilder.createNodesRelationshipBySingleIdMatch(Long.parseLong(conceptionEntityUID),"timeScaleEvent", RealmConstant.TimeScale_AttachToRelationClass,relationPropertiesMap);
                createAttachToTimeScaleRelationshipCql =
                        createAttachToTimeScaleRelationshipCql.replace("RETURN operationResult","").replaceAll("sourceNode","conceptionEntity").replaceAll("operationResult","attachToTimeScale");

                String createTimeReferToRelationshipCql = CypherBuilder.createNodesRelationshipBySingleIdMatch(Long.parseLong(timeScaleEntityUID), "timeScaleEvent",RealmConstant.TimeScale_TimeReferToRelationClass,relationPropertiesMap);
                createTimeReferToRelationshipCql = createTimeReferToRelationshipCql.replace("RETURN operationResult","").replaceAll("sourceNode","timeScaleEntity").replaceAll("operationResult","timeReferTo");

                String mergedCql = createTimeScaleEventCql+"\n"+
                        "WITH timeScaleEvent \n"+
                        createAttachToTimeScaleRelationshipCql+"\n"+
                        "WITH attachToTimeScale ,timeScaleEvent,conceptionEntity \n"+
                        createTimeReferToRelationshipCql+ " RETURN attachToTimeScale ,timeScaleEvent,conceptionEntity,timeReferTo,timeScaleEntity";

                logger.debug("Generated Cypher Statement: {}", mergedCql);

                GetSingleTimeScaleEventEntirePathTransformer getSingleTimeScaleEventEntirePathTransformer = new GetSingleTimeScaleEventEntirePathTransformer();
                Object operationRes = workingGraphOperationExecutor.executeWrite(getSingleTimeScaleEventEntirePathTransformer, mergedCql);
                if(operationRes != null){
                    return true;
                }else{
                    return false;
                }
            }
        }catch(DateTimeParseException e){
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String,Object> batchAttachTimeScaleEventsWithStringDateAttributeValue(List<ConceptionEntityValue> conceptionEntityValueList, String timeEventAttributeName,String timeFlowName,String eventComment,
                                       DateTimeFormatter formatter,Map<String,Object> globalEventData, TimeFlow.TimeScaleGrade timeScaleGrade,CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        return batchAttachTimeScaleEvents(conceptionEntityValueList,timeEventAttributeName,timeFlowName,eventComment,formatter,globalEventData,timeScaleGrade,degreeOfParallelism);
    }

    public static Map<String,Object> batchAttachTimeScaleEvents(List<ConceptionEntityValue> conceptionEntityValueList, String timeEventAttributeName,String timeFlowName,String eventComment,
                                        DateTimeFormatter formatter,Map<String,Object> globalEventData, TimeFlow.TimeScaleGrade timeScaleGrade,int degreeOfParallelism){
        int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);
        Map<String,String> timeScaleEntitiesMetaInfoMapping = new HashMap<>();
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<ConceptionEntityValue> currentConceptionEntityValueList:rsList){
            LinkTimeScaleEventThread linkTimeScaleEventThread = new LinkTimeScaleEventThread(timeEventAttributeName,formatter,timeFlowName,
                    eventComment,globalEventData,timeScaleGrade,currentConceptionEntityValueList,timeScaleEntitiesMetaInfoMapping,threadReturnDataMap);
            executor.execute(linkTimeScaleEventThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        return threadReturnDataMap;
    }

    public static Map<String,Object> batchAttachTimeScaleEventsWithDateAttributesValue(List<ConceptionEntityValue> conceptionEntityValueList,
                                              String timeEventYearAttributeName, String timeEventMonthAttributeName, String timeEventDayAttributeName, String timeEventHourAttributeName, String timeEventMinuteAttributeName,
                                              String timeFlowName, String eventComment, Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade,CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        return batchAttachTimeScaleEventsWithDateAttributesValue(conceptionEntityValueList,
                timeEventYearAttributeName, timeEventMonthAttributeName, timeEventDayAttributeName, timeEventHourAttributeName, timeEventMinuteAttributeName,
                timeFlowName, eventComment, eventData, timeScaleGrade,degreeOfParallelism);
    }

    public static Map<String,Object> batchAttachTimeScaleEventsWithDateAttributesValue(List<ConceptionEntityValue> conceptionEntityValueList,
                                      String timeEventYearAttributeName, String timeEventMonthAttributeName, String timeEventDayAttributeName, String timeEventHourAttributeName, String timeEventMinuteAttributeName,
                                      String timeFlowName, String eventComment, Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade,int degreeOfParallelism){
        int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);
        Map<String,String> timeScaleEntitiesMetaInfoMapping = new HashMap<>();
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<ConceptionEntityValue> currentConceptionEntityValueList:rsList){
            LinkTimeScaleEventThread linkTimeScaleEventThread = new LinkTimeScaleEventThread(timeEventYearAttributeName,timeEventMonthAttributeName,timeEventDayAttributeName,timeEventHourAttributeName,timeEventMinuteAttributeName
                    ,timeFlowName,eventComment,eventData,timeScaleGrade,currentConceptionEntityValueList,timeScaleEntitiesMetaInfoMapping,threadReturnDataMap);
            executor.execute(linkTimeScaleEventThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        return threadReturnDataMap;
    }

    private static String getTimeScaleEntityUID(Map<String,String> timeScaleEntitiesMetaInfoMapping,LocalDateTime eventReferTime, String timeFlowName, TimeFlow.TimeScaleGrade timeScaleGrade,GraphOperationExecutor workingGraphOperationExecutor){
        int year = eventReferTime.getYear();
        int month = eventReferTime.getMonthValue();
        int day = eventReferTime.getDayOfMonth();
        int hour = eventReferTime.getHour();
        int minute = eventReferTime.getMinute();
        //int second = eventReferTime.getSecond();

        String TimeScaleEntityKey = timeFlowName+":"+year+"_"+month+"_"+day+"_"+hour+"_"+minute;

        if(timeScaleEntitiesMetaInfoMapping.containsKey(TimeScaleEntityKey)){
            return timeScaleEntitiesMetaInfoMapping.get(TimeScaleEntityKey);
        }else{
            String queryCql = null;
            switch (timeScaleGrade) {
                case YEAR:
                    queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Year{year:"+year+"}) RETURN timeScaleEntity as operationResult";
                    break;
                case MONTH:
                    queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Month{month:"+month+"}) RETURN timeScaleEntity as operationResult";
                    break;
                case DAY:
                    queryCql ="MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Day{day:"+day+"}) RETURN timeScaleEntity as operationResult";
                    break;
                case HOUR:
                    queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+day+"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Hour{hour:"+hour+"}) RETURN timeScaleEntity as operationResult";
                    break;
                case MINUTE:
                    queryCql = "MATCH(timeFlow:DOCG_TimeFlow{name:\""+timeFlowName+"\"})-[:DOCG_TS_Contains]->(year:DOCG_TS_Year{year:"+year+"})-[:DOCG_TS_Contains]->(month:DOCG_TS_Month{month:"+month+"})-[:DOCG_TS_Contains]->(day:DOCG_TS_Day{day:"+day+"})-[:DOCG_TS_Contains]->(hour:DOCG_TS_Hour{hour:"+hour+"})-[:DOCG_TS_Contains]->(timeScaleEntity:DOCG_TS_Minute{minute:"+minute+"}) RETURN timeScaleEntity as operationResult";
                    break;
                case SECOND:
                    break;
            }
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetSingleTimeScaleEntityTransformer getSingleTimeScaleEntityTransformer =
                    new GetSingleTimeScaleEntityTransformer(null,workingGraphOperationExecutor);
            Object queryRes = workingGraphOperationExecutor.executeRead(getSingleTimeScaleEntityTransformer,queryCql);
            if(queryRes != null){
                String timeScaleEntityUID = ((Neo4JTimeScaleEntityImpl)queryRes).getTimeScaleEntityUID();
                timeScaleEntitiesMetaInfoMapping.put(TimeScaleEntityKey,timeScaleEntityUID);
                return timeScaleEntityUID;
            }
        }
        return null;
    }

    public static Map<String,Object> batchAttachNewRelations(List<RelationEntityValue> relationEntityValueList, String relationKindName,CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        return batchAttachNewRelations(relationEntityValueList,relationKindName,degreeOfParallelism);
    }

    public static Map<String,Object> batchAttachNewRelations(List<RelationEntityValue> relationEntityValueList, String relationKindName, int degreeOfParallelism){
        int singlePartitionSize = (relationEntityValueList.size()/degreeOfParallelism)+1;
        List<List<RelationEntityValue>> rsList = Lists.partition(relationEntityValueList, singlePartitionSize);
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        if(rsList.size() > 0){
            ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
            for(List<RelationEntityValue> currentRelationEntityValueList:rsList){
                AttachRelationThread attachRelationThread = new AttachRelationThread(currentRelationEntityValueList,relationKindName,threadReturnDataMap);
                executor.execute(attachRelationThread);
            }
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        }
        return threadReturnDataMap;
    }

    private static class AttachRelationThread implements Runnable{
        private List<RelationEntityValue> relationEntityValueList;
        private String relationKindName;
        private Map<String,Object> threadReturnDataMap;

        public AttachRelationThread(List<RelationEntityValue> relationEntityValueList,String relationKindName,Map<String,Object> threadReturnDataMap){
            this.relationEntityValueList = relationEntityValueList;
            this.relationKindName = relationKindName;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run(){
            if(this.relationEntityValueList != null && this.relationEntityValueList.size() >0){
                String currentThreadName = Thread.currentThread().getName();
                long successfulCount = 0;
                CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
                if(coreRealm.getStorageImplTech().equals(CoreRealmStorageImplTech.NEO4J)){
                    GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
                    GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                            (this.relationKindName,graphOperationExecutor);
                    int singleBatchLoopInsertCount = 1000;

                    if(this.relationEntityValueList.size() <= singleBatchLoopInsertCount){
                        for(RelationEntityValue currentRelationEntityValue:this.relationEntityValueList){
                            String sourceEntityUID = currentRelationEntityValue.getFromConceptionEntityUID();
                            String targetEntityUID = currentRelationEntityValue.getToConceptionEntityUID();
                            Map<String, Object> relationPropertiesValue = currentRelationEntityValue.getEntityAttributesValue();
                            String attachRelationCQL = CypherBuilder.createNodesRelationshipByIdsMatch(Long.parseLong(sourceEntityUID),Long.parseLong(targetEntityUID),this.relationKindName,relationPropertiesValue);
                            Object returnObj = graphOperationExecutor.executeWrite(getSingleRelationEntityTransformer,attachRelationCQL);
                            if(returnObj != null){
                                successfulCount++;
                            }
                        }
                    }else{
                        List<List<RelationEntityValue>> cutSubLists = Lists.partition(relationEntityValueList, 100);
                        for(List<RelationEntityValue> currentCutList:cutSubLists){
                            List<List<String>> relationParas = new ArrayList<>();
                            for(RelationEntityValue currentRelationEntityValue:currentCutList){
                                String sourceEntityUID = currentRelationEntityValue.getFromConceptionEntityUID();
                                String targetEntityUID = currentRelationEntityValue.getToConceptionEntityUID();
                                Map<String, Object> relationPropertiesValue = currentRelationEntityValue.getEntityAttributesValue();
                                String propertiesCQLPart = CypherBuilder.createEntityProperties(relationPropertiesValue);
                                List<String> currentPairList = new ArrayList<>();
                                currentPairList.add(sourceEntityUID);
                                currentPairList.add(targetEntityUID);
                                currentPairList.add(propertiesCQLPart);
                                relationParas.add(currentPairList);
                            }
                            String cql = "UNWIND  "+relationParas +" AS relationPair"+"\n "+
                            "MATCH (sourceNode), (targetNode) WHERE (id(sourceNode) = relationPair[0] AND id(targetNode) = relationPair[1]) CREATE (sourceNode)-[operationResult:`"+this.relationKindName+"`]->(targetNode)"+" \n "+
                                    "SET operationResult = relationPair[2]";
                            graphOperationExecutor.executeWrite(getSingleRelationEntityTransformer,cql);
                            successfulCount = relationEntityValueList.size();
                        }
                    }
                    graphOperationExecutor.close();
                    threadReturnDataMap.put(currentThreadName,successfulCount);
                }
            }
        }
    }

    public static Map<String,Object> batchAttachNewRelationsWithSinglePropertyValueMatch(
            String fromConceptionKindName,QueryParameters fromExploreParameters,String fromAttributeName,
            String toConceptionKindName, QueryParameters toExploreParameters,String toAttributeName,
            String relationKindName,CPUUsageRate _CPUUsageRate){
        LocalDateTime wholeStartDateTime = LocalDateTime.now();
        List<RelationEntityValue> relationEntityValueList = new ArrayList<>();
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        coreRealm.openGlobalSession();

        QueryParameters exeFromExploreParameters;
        if(fromExploreParameters != null){
            exeFromExploreParameters = fromExploreParameters;
        }else{
            exeFromExploreParameters = new QueryParameters();
            exeFromExploreParameters.setResultNumber(10000000);
        }

        QueryParameters exeToExploreParameters;
        if(toExploreParameters != null){
            exeToExploreParameters = toExploreParameters;
        }else{
            exeToExploreParameters = new QueryParameters();
            exeToExploreParameters.setResultNumber(10000000);
        }

        try {
            ConceptionKind fromConceptionKind = coreRealm.getConceptionKind(fromConceptionKindName);
            List<String> fromConceptionKindAttributeList = new ArrayList<>();
            fromConceptionKindAttributeList.add(fromAttributeName);
            ConceptionEntitiesAttributesRetrieveResult fromConceptionEntitiesAttributesRetrieveResult =
                    fromConceptionKind.getSingleValueEntityAttributesByAttributeNames(fromConceptionKindAttributeList,exeFromExploreParameters);

            List<ConceptionEntityValue> fromConceptionEntityValues = fromConceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues();
            Multimap<Object,String> fromConceptionEntitiesValue_UIDMapping = ArrayListMultimap.create();
            for(ConceptionEntityValue currentConceptionEntityValue:fromConceptionEntityValues){
                String conceptionEntityUID = currentConceptionEntityValue.getConceptionEntityUID();
                Object fromAttributeValue = currentConceptionEntityValue.getEntityAttributesValue().get(fromAttributeName);
                fromConceptionEntitiesValue_UIDMapping.put(fromAttributeValue,conceptionEntityUID);
            }

            ConceptionKind toConceptionKind = coreRealm.getConceptionKind(toConceptionKindName);
            List<String> toConceptionKindAttributeList = new ArrayList<>();
            toConceptionKindAttributeList.add(toAttributeName);
            ConceptionEntitiesAttributesRetrieveResult toConceptionEntitiesAttributesRetrieveResult =
                    toConceptionKind.getSingleValueEntityAttributesByAttributeNames(toConceptionKindAttributeList,exeToExploreParameters);

            List<ConceptionEntityValue> toConceptionEntityValues = toConceptionEntitiesAttributesRetrieveResult.getConceptionEntityValues();
            for(ConceptionEntityValue currentConceptionEntityValue:toConceptionEntityValues){
                String conceptionEntityUID = currentConceptionEntityValue.getConceptionEntityUID();
                Object toAttributeValue = currentConceptionEntityValue.getEntityAttributesValue().get(toAttributeName);
                Collection<String> fromConceptionEntityUIDCollection = fromConceptionEntitiesValue_UIDMapping.get(toAttributeValue);
                if(fromConceptionEntityUIDCollection != null & fromConceptionEntityUIDCollection.size()>0){
                    for(String currentFromEntityUID:fromConceptionEntityUIDCollection){
                        RelationEntityValue relationEntityValue = new RelationEntityValue(null,currentFromEntityUID,conceptionEntityUID,null);
                        relationEntityValueList.add(relationEntityValue);
                    }
                }
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        coreRealm.closeGlobalSession();
        if(relationEntityValueList.size()>0){
            Map<String,Object> batchLoadResultMap = BatchDataOperationUtil.batchAttachNewRelations(relationEntityValueList,relationKindName,_CPUUsageRate);
            batchLoadResultMap.put("StartTime", wholeStartDateTime);
            return batchLoadResultMap;
        }else{
            return null;
        }
    }

    public static Map<String,Object> batchDeleteEntities(List<String> conceptionEntityUIDs,CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        return batchDeleteEntities(conceptionEntityUIDs,degreeOfParallelism);
    }

    public static Map<String,Object> batchDeleteEntities(List<String> conceptionEntityUIDs,int degreeOfParallelism){
        int singlePartitionSize = (conceptionEntityUIDs.size()/degreeOfParallelism)+1;
        List<List<String>> rsList = Lists.partition(conceptionEntityUIDs, singlePartitionSize);
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<String> currentConceptionEntityUIDList:rsList){
            DeleteConceptionEntityThread deleteConceptionEntityThread = new DeleteConceptionEntityThread(currentConceptionEntityUIDList,threadReturnDataMap);
            executor.execute(deleteConceptionEntityThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        return threadReturnDataMap;
    }

    private static class DeleteConceptionEntityThread implements Runnable{
        private List<String> conceptionEntityUIDList;
        private Map<String,Object> threadReturnDataMap;

        public DeleteConceptionEntityThread(List<String> conceptionEntityUIDList,Map<String,Object> threadReturnDataMap){
            this.conceptionEntityUIDList = conceptionEntityUIDList;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run(){
            if(this.conceptionEntityUIDList != null && this.conceptionEntityUIDList.size() >0){
                String currentThreadName = Thread.currentThread().getName();
                long successfulCount = 0;
                CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
                if(coreRealm.getStorageImplTech().equals(CoreRealmStorageImplTech.NEO4J)){
                    GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
                    DataTransformer<Boolean> singleDeleteDataTransformer = new DataTransformer() {
                        @Override
                        public Object transformResult(Result result) {
                            if(result != null & result.hasNext()){
                                if(result.next()!=null){
                                    return true;
                                }
                            }
                            return false;
                        }
                    };
                    for(String currentConceptionEntityUID:this.conceptionEntityUIDList){
                        String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(currentConceptionEntityUID),null,null);
                        Object returnObj = graphOperationExecutor.executeWrite(singleDeleteDataTransformer,deleteCql);
                        if(returnObj != null){
                            successfulCount++;
                        }
                    }
                    graphOperationExecutor.close();
                    threadReturnDataMap.put(currentThreadName,successfulCount);
                }
            }
        }
    }

    public static Map<String,Object> batchAddNewOrUpdateEntityAttributes(String conceptionEntityUIDKeyName,List<Map<String,Object>> entityPropertiesValueList,CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        return batchAddNewOrUpdateEntityAttributes(conceptionEntityUIDKeyName,entityPropertiesValueList,degreeOfParallelism);
    }

    public static Map<String,Object> batchAddNewOrUpdateEntityAttributes(String conceptionEntityUIDKeyName,List<Map<String,Object>> entityPropertiesValueList,int degreeOfParallelism){
        int singlePartitionSize = (entityPropertiesValueList.size()/degreeOfParallelism)+1;
        List<List<Map<String,Object>>> rsList = Lists.partition(entityPropertiesValueList, singlePartitionSize);
        Map<String,Object> threadReturnDataMap = new Hashtable<>();

        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<Map<String,Object>> currentEntityPropertiesValueList:rsList){
            AddNewOrUpdateEntityAttributeThread addNewOrUpdateEntityAttributeThread = new AddNewOrUpdateEntityAttributeThread(conceptionEntityUIDKeyName,currentEntityPropertiesValueList,threadReturnDataMap);
            executor.execute(addNewOrUpdateEntityAttributeThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        return threadReturnDataMap;
    }

    private static class AddNewOrUpdateEntityAttributeThread implements Runnable{
        private String conceptionEntityUIDKeyName;
        private List<Map<String,Object>> entityPropertiesValueList;
        private Map<String,Object> threadReturnDataMap;

        public AddNewOrUpdateEntityAttributeThread(String conceptionEntityUIDKeyName,List<Map<String,Object>> entityPropertiesValueList,Map<String,Object> threadReturnDataMap){
            this.conceptionEntityUIDKeyName = conceptionEntityUIDKeyName;
            this.entityPropertiesValueList = entityPropertiesValueList;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run() {
            if(this.conceptionEntityUIDKeyName != null && this.entityPropertiesValueList.size() >0){
                String currentThreadName = Thread.currentThread().getName();
                long successfulCount = 0;
                CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
                coreRealm.openGlobalSession();
                CrossKindDataOperator crossKindDataOperator = coreRealm.getCrossKindDataOperator();

                if(coreRealm.getStorageImplTech().equals(CoreRealmStorageImplTech.NEO4J)){

                    Map<String,Map<String,Object>> entityUID_EntityMapping = new HashMap<>();
                    List<String> conceptionEntityUIDList = new ArrayList<>();
                    for(Map<String,Object> currentConceptionEntityProperties:this.entityPropertiesValueList){
                        Object currentConceptionEntityUIDObj = currentConceptionEntityProperties.get(this.conceptionEntityUIDKeyName);
                        if(currentConceptionEntityUIDObj != null){
                            String currentConceptionEntityUID = currentConceptionEntityUIDObj.toString();
                            currentConceptionEntityProperties.remove(this.conceptionEntityUIDKeyName);
                            entityUID_EntityMapping.put(currentConceptionEntityUID,currentConceptionEntityProperties);
                            conceptionEntityUIDList.add(currentConceptionEntityUID);
                        }
                    }
                    try {
                        List<ConceptionEntity> resultConceptionEntityList = crossKindDataOperator.getConceptionEntitiesByUIDs(conceptionEntityUIDList);
                        if(resultConceptionEntityList != null){
                            for(ConceptionEntity currentEntity:resultConceptionEntityList){
                                String currentUID = currentEntity.getConceptionEntityUID();
                                Map<String,Object> currentEntityProperties = entityUID_EntityMapping.get(currentUID);
                                List<String> updatedProperties = currentEntity.addNewOrUpdateAttributes(currentEntityProperties);
                                if(updatedProperties != null & updatedProperties.size() == currentEntityProperties.size()){
                                    successfulCount++;
                                }
                            }
                        }
                    } catch (CoreRealmServiceEntityExploreException e) {
                        e.printStackTrace();
                    }

                    coreRealm.closeGlobalSession();
                    threadReturnDataMap.put(currentThreadName,successfulCount);
                }
            }
        }
    }

    public static Map<String,Object> batchAttachGeospatialScaleEvents(List<RelationEntityValue> relationEntityValueList,String geospatialRegionName,String eventComment,Map<String,Object> globalEventData,
                                                                      GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade, CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        return batchAttachGeospatialScaleEvents(relationEntityValueList,geospatialRegionName,eventComment,globalEventData,geospatialScaleGrade,degreeOfParallelism);
    }

    public static Map<String,Object> batchAttachGeospatialScaleEvents(List<RelationEntityValue> relationEntityValueList,String geospatialRegionName,String eventComment,Map<String,Object> globalEventData,
                                                                      GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade, int degreeOfParallelism){
        int singlePartitionSize = (relationEntityValueList.size()/degreeOfParallelism)+1;
        List<List<RelationEntityValue>> rsList = Lists.partition(relationEntityValueList, singlePartitionSize);
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        String realGeospatialRegionName = geospatialRegionName != null ? geospatialRegionName : RealmConstant._defaultGeospatialRegionName;
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<RelationEntityValue> currentRelationEntityValueList:rsList){
            LinkGeospatialScaleEventThread linkGeospatialScaleEventThread = new LinkGeospatialScaleEventThread(realGeospatialRegionName,
                    eventComment,globalEventData,geospatialScaleGrade,currentRelationEntityValueList,threadReturnDataMap);
            executor.execute(linkGeospatialScaleEventThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        return threadReturnDataMap;
    }

    private static class LinkGeospatialScaleEventThread implements Runnable{
        private String geospatialRegionName;
        private String eventComment;
        private Map<String,Object> globalEventData;
        private GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade;
        private List<RelationEntityValue> relationEntityValueList;

        private Map<String,Object> threadReturnDataMap;
        public LinkGeospatialScaleEventThread(String geospatialRegionName,String eventComment,Map<String,Object> globalEventData,
                                              GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade,List<RelationEntityValue> relationEntityValueList,
                                              Map<String,Object> threadReturnDataMap){
            this.geospatialRegionName = geospatialRegionName;
            this.eventComment = eventComment;
            this.globalEventData = globalEventData;
            this.geospatialScaleGrade = geospatialScaleGrade;
            this.relationEntityValueList = relationEntityValueList;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run() {
            String currentThreadName = Thread.currentThread().getName();
            long successfulCount = 0;
            GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
            try{
                for(RelationEntityValue currentRelationEntityValue:relationEntityValueList){
                    String conceptionEntityUID = currentRelationEntityValue.getFromConceptionEntityUID();
                    String geospatialScaleEntityUID = currentRelationEntityValue.getToConceptionEntityUID();
                    Map<String,Object> linkDataMap = currentRelationEntityValue.getEntityAttributesValue();
                    if(conceptionEntityUID != null && geospatialScaleEntityUID != null && linkDataMap != null){
                        Map<String, Object> propertiesMap = linkDataMap ;

                        if(linkDataMap.containsKey(RealmConstant.GeospatialCodeProperty)){
                            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                            propertiesMap.put(RealmConstant._GeospatialScaleEventReferLocation,linkDataMap.get(RealmConstant.GeospatialCodeProperty));
                            propertiesMap.put(RealmConstant._GeospatialScaleEventComment,this.eventComment);
                            propertiesMap.put(RealmConstant._GeospatialScaleEventScaleGrade,this.geospatialScaleGrade.toString());
                            propertiesMap.put(RealmConstant._GeospatialScaleEventGeospatialRegion,this.geospatialRegionName);
                            if(this.globalEventData!=null){
                                propertiesMap.putAll(this.globalEventData);
                            }
                            propertiesMap.remove(RealmConstant.GeospatialCodeProperty);
                            String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.GeospatialScaleEventClass}, propertiesMap);
                            logger.debug("Generated Cypher Statement: {}", createCql);
                            GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                                    new GetSingleConceptionEntityTransformer(RealmConstant.GeospatialScaleEventClass, graphOperationExecutor);
                            Object newEntityRes = graphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, createCql);
                            if(newEntityRes != null) {
                                ConceptionEntity geospatialScaleEventEntity = (ConceptionEntity) newEntityRes;
                                RelationEntity linkToConceptionEntityRelation = geospatialScaleEventEntity.attachToRelation(conceptionEntityUID, RealmConstant.GeospatialScale_AttachToRelationClass, null, true);
                                RelationEntity linkToGeospatialScaleEntityRelation = geospatialScaleEventEntity.attachToRelation(geospatialScaleEntityUID, RealmConstant.GeospatialScale_GeospatialReferToRelationClass, null, true);
                                if(linkToGeospatialScaleEntityRelation != null && linkToConceptionEntityRelation!= null){
                                    successfulCount++;
                                }
                            }
                        }
                    }
                }
            } catch (CoreRealmServiceRuntimeException e) {
                e.printStackTrace();
            }finally {
                graphOperationExecutor.close();
            }
            threadReturnDataMap.put(currentThreadName,successfulCount);
        }
    }

    public static Map<String,Object> batchAttachGeospatialScaleEventsByGeospatialCode(Map<String,String> entityUIDAndGeospatialCodeMap,String geospatialRegionName,
                                                    String eventComment,Map<String,Object> globalEventData,GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade, CPUUsageRate _CPUUsageRate){
        Map<String,String> geospatialScaleEntityUIDAndCodeMap = new HashMap<>();
        GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
        try{
            QueryParameters geospatialScaleEntityQueryParameters = new QueryParameters();
            geospatialScaleEntityQueryParameters.setResultNumber(10000000);
            String realGeospatialRegionName = geospatialRegionName != null ? geospatialRegionName : RealmConstant._defaultGeospatialRegionName;
            geospatialScaleEntityQueryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,realGeospatialRegionName));
            List<String> attributeNamesList = new ArrayList<>();
            attributeNamesList.add(RealmConstant.GeospatialCodeProperty);
            String queryCql = CypherBuilder.matchAttributesWithQueryParameters(RealmConstant.GeospatialScaleEntityClass,geospatialScaleEntityQueryParameters,attributeNamesList);
            DataTransformer geospatialCodeSearchDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        Map<String,Object> valueMap = nodeRecord.asMap();
                        String idKey = "id("+CypherBuilder.operationResultName+")";
                        Long uidValue = (Long)valueMap.get(idKey);
                        String geospatialScaleEntityUID = ""+uidValue.longValue();
                        String geospatialCodeKey = CypherBuilder.operationResultName+"."+RealmConstant.GeospatialCodeProperty;
                        String geospatialCodeValue = valueMap.get(geospatialCodeKey).toString();
                        geospatialScaleEntityUIDAndCodeMap.put(geospatialCodeValue,geospatialScaleEntityUID);
                    }
                    return null;
                }
            };
            graphOperationExecutor.executeRead(geospatialCodeSearchDataTransformer, queryCql);
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }finally {
            graphOperationExecutor.close();
        }
        List<RelationEntityValue> attachEntityMetaDataList = new ArrayList<>();
        Iterator<Map.Entry<String, String>> entries = entityUIDAndGeospatialCodeMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            String conceptionEntityUID = entry.getKey();
            String targetGeospatialCode = entry.getValue();

            if(geospatialScaleEntityUIDAndCodeMap.containsKey(targetGeospatialCode)) {
                RelationEntityValue relationEntityValue = new RelationEntityValue();
                relationEntityValue.setFromConceptionEntityUID(conceptionEntityUID);
                relationEntityValue.setToConceptionEntityUID(geospatialScaleEntityUIDAndCodeMap.get(targetGeospatialCode));
                Map<String,Object> geospatialCodePropertyDataMap = new HashMap<>();
                geospatialCodePropertyDataMap.put(RealmConstant.GeospatialCodeProperty,targetGeospatialCode);
                relationEntityValue.setEntityAttributesValue(geospatialCodePropertyDataMap);
                attachEntityMetaDataList.add(relationEntityValue);
            }
        }
        return batchAttachGeospatialScaleEvents(attachEntityMetaDataList,geospatialRegionName,eventComment,globalEventData,geospatialScaleGrade,_CPUUsageRate);
    }

    public static Map<String,Object> batchAttachGeospatialScaleEventsByChineseNames(Map<String,String> entityUIDAndGeospatialChinaNamesMap,String geospatialRegionName,
                                                                                      String eventComment,Map<String,Object> globalEventData,GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade, CPUUsageRate _CPUUsageRate){
        Map<String,String> geospatialScaleEntityUIDAndChinaNamesMap = new HashMap<>();
        Map<String,String> geospatialScaleEntityUIDAndCodeMap = new HashMap<>();
        GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
        try{
            QueryParameters geospatialScaleEntityQueryParameters = new QueryParameters();
            geospatialScaleEntityQueryParameters.setResultNumber(10000000);
            String realGeospatialRegionName = geospatialRegionName != null ? geospatialRegionName : RealmConstant._defaultGeospatialRegionName;
            geospatialScaleEntityQueryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,realGeospatialRegionName));
            List<String> attributeNamesList = new ArrayList<>();
            attributeNamesList.add(RealmConstant.GeospatialChineseNameProperty);
            attributeNamesList.add(RealmConstant.GeospatialCodeProperty);

            String _GeospatialScaleEntityClassName = RealmConstant.GeospatialScaleEntityClass;
            switch(geospatialScaleGrade){
                case PROVINCE: _GeospatialScaleEntityClassName = RealmConstant.GeospatialScaleProvinceEntityClass;
                    break;
                case PREFECTURE:_GeospatialScaleEntityClassName = RealmConstant.GeospatialScalePrefectureEntityClass;
                    attributeNamesList.add("ChinaProvinceName");
                    break;
                case COUNTY:_GeospatialScaleEntityClassName = RealmConstant.GeospatialScaleCountyEntityClass;
                    attributeNamesList.add("ChinaProvinceName");
                    attributeNamesList.add("ChinaPrefectureName");
                    break;
                case TOWNSHIP:_GeospatialScaleEntityClassName = RealmConstant.GeospatialScaleTownshipEntityClass;
                    attributeNamesList.add("ChinaProvinceName");
                    attributeNamesList.add("ChinaPrefectureName");
                    attributeNamesList.add("ChinaCountyName");
                    break;
                case VILLAGE:_GeospatialScaleEntityClassName = RealmConstant.GeospatialScaleVillageEntityClass;
                    attributeNamesList.add("ChinaProvinceName");
                    attributeNamesList.add("ChinaPrefectureName");
                    attributeNamesList.add("ChinaCountyName");
                    attributeNamesList.add("ChinaTownshipName");
                    break;
            }

            String queryCql = CypherBuilder.matchAttributesWithQueryParameters(_GeospatialScaleEntityClassName,geospatialScaleEntityQueryParameters,attributeNamesList);
            DataTransformer geospatialCodeSearchDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        Map<String,Object> valueMap = nodeRecord.asMap();
                        String idKey = "id("+CypherBuilder.operationResultName+")";
                        Long uidValue = (Long)valueMap.get(idKey);
                        String geospatialScaleEntityUID = ""+uidValue.longValue();
                        String geospatialScaleEntityCode = CypherBuilder.operationResultName+"."+RealmConstant.GeospatialCodeProperty;
                        String geospatialChinaProvinceKey = CypherBuilder.operationResultName+"."+"ChinaProvinceName";
                        String geospatialChinaPrefectureKey = CypherBuilder.operationResultName+"."+"ChinaPrefectureName";
                        String geospatialChinaCountyKey = CypherBuilder.operationResultName+"."+"ChinaCountyName";
                        String geospatialChinaTownKey = CypherBuilder.operationResultName+"."+"ChinaTownshipName";
                        String geospatialEntitySelfKey = CypherBuilder.operationResultName+"."+RealmConstant.GeospatialChineseNameProperty;

                        String geospatialChinaNameValue = "";
                        switch(geospatialScaleGrade){
                            case PROVINCE:
                                geospatialChinaNameValue = valueMap.get(geospatialEntitySelfKey).toString();
                                break;
                            case PREFECTURE:
                                geospatialChinaNameValue = valueMap.get(geospatialChinaProvinceKey)+"-"+
                                        valueMap.get(geospatialEntitySelfKey).toString();
                                break;
                            case COUNTY:
                                geospatialChinaNameValue = valueMap.get(geospatialChinaProvinceKey)+"-"+
                                        valueMap.get(geospatialChinaPrefectureKey).toString()+"-"+
                                        valueMap.get(geospatialEntitySelfKey).toString();
                                break;
                            case TOWNSHIP:
                                geospatialChinaNameValue = valueMap.get(geospatialChinaProvinceKey)+"-"+
                                        valueMap.get(geospatialChinaPrefectureKey).toString()+"-"+
                                        valueMap.get(geospatialChinaCountyKey).toString()+"-"+
                                        valueMap.get(geospatialEntitySelfKey).toString();
                                break;
                            case VILLAGE:
                                geospatialChinaNameValue = valueMap.get(geospatialChinaProvinceKey)+"-"+
                                        valueMap.get(geospatialChinaPrefectureKey).toString()+"-"+
                                        valueMap.get(geospatialChinaCountyKey).toString()+"-"+
                                        valueMap.get(geospatialChinaTownKey).toString()+"-"+
                                        valueMap.get(geospatialEntitySelfKey).toString();
                                break;
                        }
                        geospatialScaleEntityUIDAndChinaNamesMap.put(geospatialChinaNameValue,geospatialScaleEntityUID);
                        String entityGeoCode =
                                valueMap.containsKey(geospatialScaleEntityCode) ? valueMap.get(geospatialScaleEntityCode).toString():"-";
                        geospatialScaleEntityUIDAndCodeMap.put(geospatialScaleEntityUID,entityGeoCode);
                    }
                    return null;
                }
            };
            graphOperationExecutor.executeRead(geospatialCodeSearchDataTransformer, queryCql);
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }finally {
            graphOperationExecutor.close();
        }
        List<RelationEntityValue> attachEntityMetaDataList = new ArrayList<>();
        Iterator<Map.Entry<String, String>> entries = entityUIDAndGeospatialChinaNamesMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            String conceptionEntityUID = entry.getKey();
            String targetGeospatialChinaNames = entry.getValue();

            if(geospatialScaleEntityUIDAndChinaNamesMap.containsKey(targetGeospatialChinaNames)) {
                RelationEntityValue relationEntityValue = new RelationEntityValue();
                relationEntityValue.setFromConceptionEntityUID(conceptionEntityUID);
                relationEntityValue.setToConceptionEntityUID(geospatialScaleEntityUIDAndChinaNamesMap.get(targetGeospatialChinaNames));
                Map<String,Object> geospatialCodePropertyDataMap = new HashMap<>();
                geospatialCodePropertyDataMap.put("GeospatialChinaNames",targetGeospatialChinaNames);
                geospatialCodePropertyDataMap.put(RealmConstant.GeospatialCodeProperty,
                        geospatialScaleEntityUIDAndCodeMap.get(geospatialScaleEntityUIDAndChinaNamesMap.get(targetGeospatialChinaNames)));
                relationEntityValue.setEntityAttributesValue(geospatialCodePropertyDataMap);
                attachEntityMetaDataList.add(relationEntityValue);
            }
        }
        return batchAttachGeospatialScaleEvents(attachEntityMetaDataList,geospatialRegionName,eventComment,globalEventData,geospatialScaleGrade,_CPUUsageRate);
    }

    public static Map<String,Object> batchConvertConceptionEntityAttributeToTemporalType(String attributeName,
                                                                                         List<ConceptionEntityValue> conceptionEntityValueList, DateTimeFormatter dateTimeFormatter,
                                                                                         TemporalScaleCalculable.TemporalScaleLevel temporalScaleType, CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<ConceptionEntityValue> currentRelationEntityValueList:rsList){
            ConvertConceptionEntityAttributeToTemporalTypeThread convertConceptionEntityAttributeToTemporalTypeThread =
                    new ConvertConceptionEntityAttributeToTemporalTypeThread(attributeName,dateTimeFormatter,
                    temporalScaleType,currentRelationEntityValueList,threadReturnDataMap);
            executor.execute(convertConceptionEntityAttributeToTemporalTypeThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        return threadReturnDataMap;
    }

    private static class ConvertConceptionEntityAttributeToTemporalTypeThread implements Runnable{
        private String attributeName;
        private DateTimeFormatter dateTimeFormatter;
        private TemporalScaleCalculable.TemporalScaleLevel temporalScaleType;
        private List<ConceptionEntityValue> conceptionEntityValueList;
        private Map<String,Object> threadReturnDataMap;

        public ConvertConceptionEntityAttributeToTemporalTypeThread(String attributeName,DateTimeFormatter dateTimeFormatter,
                     TemporalScaleCalculable.TemporalScaleLevel temporalScaleType,List<ConceptionEntityValue> conceptionEntityValueList,
                     Map<String,Object> threadReturnDataMap){
            this.attributeName = attributeName;
            this.dateTimeFormatter = dateTimeFormatter;
            this.temporalScaleType = temporalScaleType;
            this.conceptionEntityValueList = conceptionEntityValueList;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run() {
            String currentThreadName = Thread.currentThread().getName();
            long successfulCount = 0;
            List<String> validConceptionEntityUIDList = new ArrayList<>();
            Map<String,String> conceptionEntityUID_attributeValueMap = new HashMap<>();
            for(ConceptionEntityValue currentConceptionEntityValue:this.conceptionEntityValueList){
                if(currentConceptionEntityValue.getEntityAttributesValue().containsKey(this.attributeName)){
                    String attributeStringValue = currentConceptionEntityValue.getEntityAttributesValue().get(this.attributeName).toString();
                    String conceptionEntityUID = currentConceptionEntityValue.getConceptionEntityUID();
                    validConceptionEntityUIDList.add(conceptionEntityUID);
                    conceptionEntityUID_attributeValueMap.put(conceptionEntityUID,attributeStringValue);
                }
            }

            CoreRealm targetCoreRealm = null;
            try {
                targetCoreRealm = RealmTermFactory.getDefaultCoreRealm();
                targetCoreRealm.openGlobalSession();
                CrossKindDataOperator targetCrossKindDataOperator = targetCoreRealm.getCrossKindDataOperator();
                List<ConceptionEntity> conceptionEntityList = targetCrossKindDataOperator.getConceptionEntitiesByUIDs(validConceptionEntityUIDList);
                for(ConceptionEntity currentConceptionEntity : conceptionEntityList){
                    String currentConceptionEntityUID = currentConceptionEntity.getConceptionEntityUID();
                    currentConceptionEntity.removeAttribute(attributeName);
                    String temporalStringValue = conceptionEntityUID_attributeValueMap.get(currentConceptionEntityUID);
                    switch(temporalScaleType){
                        case Date :
                            try {
                                LocalDate localDateAttributeValue = LocalDate.parse(temporalStringValue, dateTimeFormatter);
                                currentConceptionEntity.addAttribute(attributeName,localDateAttributeValue);
                                successfulCount++;
                            }catch(DateTimeParseException e){
                                e.printStackTrace();
                            }
                            break;
                        case Time:
                            try {
                                LocalTime localTimeAttributeValue = LocalTime.parse(temporalStringValue,dateTimeFormatter);
                                currentConceptionEntity.addAttribute(attributeName,localTimeAttributeValue);
                                successfulCount++;
                            } catch(DateTimeParseException e){
                                e.printStackTrace();
                            }
                            break;
                        case Datetime:
                            try {
                                LocalDateTime localDateTimeAttributeValue = LocalDateTime.parse(temporalStringValue,dateTimeFormatter);
                                currentConceptionEntity.addAttribute(attributeName,localDateTimeAttributeValue);
                                successfulCount++;
                            }catch(DateTimeParseException e){
                                e.printStackTrace();
                            }
                            break;
                        case Timestamp:
                            try {
                                ZonedDateTime zonedDateTimeAttributeValue = ZonedDateTime.parse(temporalStringValue,dateTimeFormatter);
                                Date timeStampDateAttributeValue = Date.from(zonedDateTimeAttributeValue.toInstant());
                                currentConceptionEntity.addAttribute(attributeName,timeStampDateAttributeValue);
                                successfulCount++;
                            }catch(DateTimeParseException e){
                                e.printStackTrace();
                            }
                    }
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                throw new RuntimeException(e);
            } catch (CoreRealmServiceRuntimeException e) {
                throw new RuntimeException(e);
            } finally {
                if(targetCoreRealm != null){
                    targetCoreRealm.closeGlobalSession();
                }
            }
            threadReturnDataMap.put(currentThreadName,successfulCount);
        }
    }

    public static Map<String,Object> batchConvertRelationEntityAttributeToTemporalType(String attributeName,
                                                                                       List<RelationEntityValue> relationEntityValueList, DateTimeFormatter dateTimeFormatter,
                                                                                       TemporalScaleCalculable.TemporalScaleLevel temporalScaleType, CPUUsageRate _CPUUsageRate){
        int degreeOfParallelism = calculateRuntimeCPUCoresByUsageRate(_CPUUsageRate);
        int singlePartitionSize = (relationEntityValueList.size()/degreeOfParallelism)+1;
        List<List<RelationEntityValue>> rsList = Lists.partition(relationEntityValueList, singlePartitionSize);
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<RelationEntityValue> currentRelationEntityValueList:rsList){
            ConvertRelationEntityAttributeToTemporalTypeThread convertRelationEntityAttributeToTemporalTypeThread =
                    new ConvertRelationEntityAttributeToTemporalTypeThread(attributeName,dateTimeFormatter,
                            temporalScaleType,currentRelationEntityValueList,threadReturnDataMap);
            executor.execute(convertRelationEntityAttributeToTemporalTypeThread);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadReturnDataMap.put("FinishTime", LocalDateTime.now());
        return threadReturnDataMap;
    }

    private static class ConvertRelationEntityAttributeToTemporalTypeThread implements Runnable{
        private String attributeName;
        private DateTimeFormatter dateTimeFormatter;
        private TemporalScaleCalculable.TemporalScaleLevel temporalScaleType;
        private List<RelationEntityValue> relationEntityValueList;
        private Map<String,Object> threadReturnDataMap;

        public ConvertRelationEntityAttributeToTemporalTypeThread(String attributeName,DateTimeFormatter dateTimeFormatter,
                                                                    TemporalScaleCalculable.TemporalScaleLevel temporalScaleType,List<RelationEntityValue> relationEntityValueList,
                                                                    Map<String,Object> threadReturnDataMap){
            this.attributeName = attributeName;
            this.dateTimeFormatter = dateTimeFormatter;
            this.temporalScaleType = temporalScaleType;
            this.relationEntityValueList = relationEntityValueList;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run() {
            String currentThreadName = Thread.currentThread().getName();
            long successfulCount = 0;
            List<String> validRelationEntityUIDList = new ArrayList<>();
            Map<String,String> relationEntityUID_attributeValueMap = new HashMap<>();
            for(RelationEntityValue currentRelationEntityValue:this.relationEntityValueList){
                if(currentRelationEntityValue.getEntityAttributesValue().containsKey(this.attributeName)){
                    String attributeStringValue = currentRelationEntityValue.getEntityAttributesValue().get(this.attributeName).toString();
                    String relationEntityUID = currentRelationEntityValue.getRelationEntityUID();
                    validRelationEntityUIDList.add(relationEntityUID);
                    relationEntityUID_attributeValueMap.put(relationEntityUID,attributeStringValue);
                }
            }

            CoreRealm targetCoreRealm = null;
            try {
                targetCoreRealm = RealmTermFactory.getDefaultCoreRealm();
                targetCoreRealm.openGlobalSession();
                CrossKindDataOperator targetCrossKindDataOperator = targetCoreRealm.getCrossKindDataOperator();
                List<RelationEntity> relationEntityList = targetCrossKindDataOperator.getRelationEntitiesByUIDs(validRelationEntityUIDList);
                for(RelationEntity currentRelationEntity : relationEntityList){
                    String currentRelationEntityUID = currentRelationEntity.getRelationEntityUID();
                    currentRelationEntity.removeAttribute(attributeName);
                    String temporalStringValue = relationEntityUID_attributeValueMap.get(currentRelationEntityUID);
                    switch(temporalScaleType){
                        case Date :
                            try {
                                LocalDate localDateAttributeValue = LocalDate.parse(temporalStringValue, dateTimeFormatter);
                                currentRelationEntity.addAttribute(attributeName,localDateAttributeValue);
                                successfulCount++;
                            }catch(DateTimeParseException e){
                                e.printStackTrace();
                            }
                            break;
                        case Time:
                            try {
                                LocalTime localTimeAttributeValue = LocalTime.parse(temporalStringValue,dateTimeFormatter);
                                currentRelationEntity.addAttribute(attributeName,localTimeAttributeValue);
                                successfulCount++;
                            } catch(DateTimeParseException e){
                                e.printStackTrace();
                            }
                            break;
                        case Datetime:
                            try {
                                LocalDateTime localDateTimeAttributeValue = LocalDateTime.parse(temporalStringValue,dateTimeFormatter);
                                currentRelationEntity.addAttribute(attributeName,localDateTimeAttributeValue);
                                successfulCount++;
                            }catch(DateTimeParseException e){
                                e.printStackTrace();
                            }
                            break;
                        case Timestamp:
                            try {
                                ZonedDateTime zonedDateTimeAttributeValue = ZonedDateTime.parse(temporalStringValue,dateTimeFormatter);
                                Date timeStampDateAttributeValue = Date.from(zonedDateTimeAttributeValue.toInstant());
                                currentRelationEntity.addAttribute(attributeName,timeStampDateAttributeValue);
                                successfulCount++;
                            }catch(DateTimeParseException e){
                                e.printStackTrace();
                            }
                    }
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                throw new RuntimeException(e);
            } catch (CoreRealmServiceRuntimeException e) {
                throw new RuntimeException(e);
            } finally {
                if(targetCoreRealm != null){
                    targetCoreRealm.closeGlobalSession();
                }
            }
            threadReturnDataMap.put(currentThreadName,successfulCount);
        }
    }

    public static int calculateRuntimeCPUCoresByUsageRate(CPUUsageRate _CPUUsageRate){
        int availableCoreNumber = Runtime.getRuntime().availableProcessors();
        if(availableCoreNumber<=4){
            return 4;
        }else if(availableCoreNumber<=8){
            switch(_CPUUsageRate){
                case Low:
                    return 4;
                case Middle:
                    return 6;
                case High:
                    return 8;
            }
        }else if(availableCoreNumber<=16){
            switch(_CPUUsageRate){
                case Low:
                    return 4;
                case Middle:
                    return 8;
                case High:
                    return 16;
            }
        }else{
            int lowCoreNumber = availableCoreNumber/4;
            int middleCoreNumber = availableCoreNumber/2;
            int highCoreNumber = availableCoreNumber -4;
            switch(_CPUUsageRate){
                case Low:
                    return lowCoreNumber;
                case Middle:
                    return middleCoreNumber;
                case High:
                    return highCoreNumber;
            }
        }
        return 4;
    }

    public static boolean importConceptionEntitiesFromCSV(String csvLocation,String conceptionKind,Map<String,String> attributesMapping){
        if(csvLocation == null || conceptionKind == null || attributesMapping == null){
            return false;
        }else{
            if(attributesMapping.size()>0){
                String propertyInsertStr="";
                Set<String> attributeNames = attributesMapping.keySet();
                for(String currentAttributeName:attributeNames){
                    String propertyNameOfCSV = attributesMapping.get(currentAttributeName);
                    propertyInsertStr = propertyInsertStr+currentAttributeName+":row."+propertyNameOfCSV+",";
                }
                propertyInsertStr = propertyInsertStr.substring(0,propertyInsertStr.length()-1);

                String csvFileLocation="file:///"+csvLocation.replaceFirst("file:///","");
                String cql = "LOAD CSV WITH HEADERS FROM \""+csvFileLocation+"\" AS row CREATE (:"+conceptionKind+" {"+propertyInsertStr+"})";
                logger.debug("Generated Cypher Statement: {}", cql);
                GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
                try{
                    DataTransformer<Boolean> geospatialCodeSearchDataTransformer = new DataTransformer() {
                        @Override
                        public Object transformResult(Result result) {
                            return true;
                        }
                    };
                    Object result = graphOperationExecutor.executeWrite(geospatialCodeSearchDataTransformer, cql);
                    if(result != null){
                        return (Boolean)result;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    graphOperationExecutor.close();
                }
            }
        }
        return false;
    }

    public static boolean importConceptionEntitiesFromCSV(String csvLocation,String conceptionKind,Map<String,String> attributesMapping,String lineSplitChar){
        if(csvLocation == null || conceptionKind == null || attributesMapping == null){
            return false;
        }else{
            if(attributesMapping.size()>0){
                String propertyInsertStr="";
                Set<String> attributeNames = attributesMapping.keySet();
                for(String currentAttributeName:attributeNames){
                    String propertyNameOfCSV = attributesMapping.get(currentAttributeName);
                    propertyInsertStr = propertyInsertStr+currentAttributeName+":row."+propertyNameOfCSV+",";
                }
                propertyInsertStr = propertyInsertStr.substring(0,propertyInsertStr.length()-1);

                String csvFileLocation="file:///"+csvLocation.replaceFirst("file:///","");
                String cql = "LOAD CSV WITH HEADERS FROM \""+csvFileLocation+"\" AS row  FIELDTERMINATOR '"+lineSplitChar+"' CREATE (:"+conceptionKind+" {"+propertyInsertStr+"})";
                logger.debug("Generated Cypher Statement: {}", cql);
                GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
                try{
                    DataTransformer<Boolean> geospatialCodeSearchDataTransformer = new DataTransformer() {
                        @Override
                        public Object transformResult(Result result) {
                            return true;
                        }
                    };
                    Object result = graphOperationExecutor.executeWrite(geospatialCodeSearchDataTransformer, cql);
                    if(result != null){
                        return (Boolean)result;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    graphOperationExecutor.close();
                }
            }
        }
        return false;
    }

    public static Map<String,String>  getAttributesMappingFromHeaderCSV(String csvLocation){
        if(csvLocation == null){
            return null;
        }else{
            try {
                BufferedReader reader = new BufferedReader(new FileReader(csvLocation));
                String header = reader.readLine();
                Map<String,String> attributesMapping=new HashMap<>();
                String[] attributesArray = header.split(",");
                for(String currentStr : attributesArray){
                    attributesMapping.put(currentStr.replaceAll("\"",""),currentStr.replaceAll("\"",""));
                }
                return attributesMapping;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public interface ConceptionEntityAttributesProcess {
        void doConceptionEntityAttributesProcess(Map<String,Object> entityValueMap);
    }

    public static boolean importConceptionEntitiesFromExternalCSV(String csvLocation, String conceptionKind, ConceptionEntityAttributesProcess conceptionEntityAttributesProcess){
        if(csvLocation == null || conceptionKind == null){
            return false;
        }else{
            try{
                List<ConceptionEntityValue> _conceptionEntityValueList = Lists.newArrayList();
                BufferedReader reader = new BufferedReader(new FileReader(csvLocation));
                String header = reader.readLine();
                List<String> attributeNameList = new ArrayList<>();
                String[] attributesArray = header.split(",");
                for(String currentStr : attributesArray){
                    attributeNameList.add(currentStr.replaceAll("\"",""));
                }
                reader.close();
                File file = new File(csvLocation);
                reader = new BufferedReader(new FileReader(file));
                String tempStr;
                int lineCount = 0;

                while ((tempStr = reader.readLine()) != null) {
                    if(lineCount > 0){
                        Map<String,Object> newEntityValueMap = new HashMap<>();
                        String[] dataItems = tempStr.split(",");
                        if(dataItems.length == attributeNameList.size()) {
                            for (int i = 0; i < dataItems.length; i++) {
                                String attributeName = attributeNameList.get(i);
                                String attributeOriginalValue = dataItems[i];
                                newEntityValueMap.put(attributeName, attributeOriginalValue);
                            }
                            if(conceptionEntityAttributesProcess != null){
                                conceptionEntityAttributesProcess.doConceptionEntityAttributesProcess(newEntityValueMap);
                            }
                            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                            conceptionEntityValue.setEntityAttributesValue(newEntityValueMap);
                            _conceptionEntityValueList.add(conceptionEntityValue);
                        }
                    }
                    lineCount ++;
                }
                reader.close();

                BatchDataOperationUtil.batchAddNewEntities(conceptionKind,_conceptionEntityValueList, BatchDataOperationUtil.CPUUsageRate.High);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }

    public static EntitiesOperationStatistics exportConceptionEntitiesToCypher(String conceptionKindName,String cypherFileLocation){
        //https://neo4j.com/docs/apoc/current/overview/apoc.export/apoc.export.cypher.query/
        /*
            CALL apoc.export.cypher.query("MATCH (m:TestLoa) return m", "export/directors.cypher", {});
        */
        String cql = "CALL apoc.export.cypher.query(\"MATCH (entityRow:"+conceptionKindName+") return entityRow\", \""+cypherFileLocation+"\", {})\n" +
                "YIELD file, batches, source, format, nodes, relationships, time, rows, batchSize\n" +
                "RETURN file, batches, source, format, nodes, relationships, time, rows, batchSize;";
        logger.debug("Generated Cypher Statement: {}", cql);

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
        try{
            DataTransformer<Boolean> dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record operationResultRecord = result.next();
                        if(operationResultRecord.containsKey("nodes")){
                            entitiesOperationStatistics.setSuccessItemsCount(operationResultRecord.get("nodes").asLong());
                        }
                    }
                    return true;
                }
            };
            graphOperationExecutor.executeWrite(dataTransformer, cql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            graphOperationExecutor.close();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("exportConceptionEntitiesToCypher operation execute finish. conceptionKindName is "
                +conceptionKindName+", cypherFileLocation is "+cypherFileLocation);
        return entitiesOperationStatistics;
    }
}
