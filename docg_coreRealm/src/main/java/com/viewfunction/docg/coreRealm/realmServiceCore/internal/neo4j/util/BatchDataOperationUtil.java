package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleTimeScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BatchDataOperationUtil {

    private static Logger logger = LoggerFactory.getLogger(BatchDataOperationUtil.class);
    private static ZoneId zone = ZoneId.systemDefault();

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
            coreRealm.openGlobalSession();
            long successfulCount = 0;
            ConceptionKind conceptionKind = coreRealm.getConceptionKind(conceptionKindName);
            for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
                ConceptionEntity resultEntity = conceptionKind.newEntity(currentConceptionEntityValue,false);
                if(resultEntity != null){
                    successfulCount ++;
                }
            }
            coreRealm.closeGlobalSession();
            threadReturnDataMap.put(currentThreadName,successfulCount);
        }
    }

    public static Map<String,Object> batchAttachTimeScaleEvents(List<ConceptionEntityValue> conceptionEntityValueList, String timeEventAttributeName,String eventComment,
                                                  Map<String,Object> globalEventData, TimeFlow.TimeScaleGrade timeScaleGrade,int degreeOfParallelism){
        int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);
        Map<String,String> timeScaleEntitiesMetaInfoMapping = new HashMap<>();
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<ConceptionEntityValue> currentConceptionEntityValueList:rsList){
            LinkTimeScaleEventThread linkTimeScaleEventThread = new LinkTimeScaleEventThread(timeEventAttributeName,
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
        public LinkTimeScaleEventThread(String timeEventAttributeName,String eventComment,Map<String,Object> globalEventData,
                                        TimeFlow.TimeScaleGrade timeScaleGrade,List<ConceptionEntityValue> conceptionEntityValueList,
                                        Map<String,String> timeScaleEntitiesMetaInfoMapping,Map<String,Object> threadReturnDataMap
                                         ){
            this.timeEventAttributeName = timeEventAttributeName;
            this.eventComment = eventComment;
            this.globalEventData = globalEventData;
            this.timeScaleGrade = timeScaleGrade;
            this.conceptionEntityValueList = conceptionEntityValueList;
            this.timeScaleEntitiesMetaInfoMapping = timeScaleEntitiesMetaInfoMapping;
            this.threadReturnDataMap = threadReturnDataMap;
        }

        @Override
        public void run() {
            String currentThreadName = Thread.currentThread().getName();
            long successfulCount = 0;
            GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
            for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
                if(currentConceptionEntityValue.getEntityAttributesValue().get(timeEventAttributeName) != null){
                    Object targetDateValue = currentConceptionEntityValue.getEntityAttributesValue().get(timeEventAttributeName);
                    boolean attachResult = attachTimeScaleEventLogic(timeScaleEntitiesMetaInfoMapping,currentConceptionEntityValue.getConceptionEntityUID(),targetDateValue,
                            eventComment,globalEventData,timeScaleGrade,graphOperationExecutor);
                    if(attachResult){
                        successfulCount++;
                    }
                }
            }
            graphOperationExecutor.close();
            threadReturnDataMap.put(currentThreadName,successfulCount);
        }
    }

    private static boolean attachTimeScaleEventLogic(Map<String,String> timeScaleEntitiesMetaInfoMapping,String conceptionEntityUID,Object dateTime,String eventComment,
                                                 Map<String,Object> globalEventData,TimeFlow.TimeScaleGrade timeScaleGrade,
                                                  GraphOperationExecutor workingGraphOperationExecutor){
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
        propertiesMap.put(RealmConstant._TimeScaleEventReferTime,eventReferTime);
        propertiesMap.put(RealmConstant._TimeScaleEventComment,eventComment);
        propertiesMap.put(RealmConstant._TimeScaleEventScaleGrade,""+referTimeScaleGrade);
        propertiesMap.put(RealmConstant._TimeScaleEventTimeFlow,RealmConstant._defaultTimeFlowName);

        String createEventCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.TimeScaleEventClass}, propertiesMap);
        GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                new GetSingleConceptionEntityTransformer(RealmConstant.TimeScaleEventClass, workingGraphOperationExecutor);
        Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, createEventCql);
        if(newEntityRes != null) {
            String timeEventUID = ((ConceptionEntity)newEntityRes).getConceptionEntityUID();
            Map<String,Object> relationPropertiesMap = new HashMap<>();
            CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
            String createCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(conceptionEntityUID),Long.parseLong(timeEventUID), RealmConstant.TimeScale_AttachToRelationClass,relationPropertiesMap);
            GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                    (RealmConstant.TimeScale_AttachToRelationClass,workingGraphOperationExecutor);
            Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createCql);
            if(newRelationEntityRes != null){
                String timeScaleEntityUID = getTimeScaleEntityUID(timeScaleEntitiesMetaInfoMapping,eventReferTime,RealmConstant._defaultTimeFlowName, referTimeScaleGrade, workingGraphOperationExecutor);
                if(timeScaleEntityUID != null){
                    String linkToTimeScaleEntityCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(timeScaleEntityUID),Long.parseLong(timeEventUID),RealmConstant.TimeScale_TimeReferToRelationClass,relationPropertiesMap);
                    workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, linkToTimeScaleEntityCql);
                }
            }
        }
        return true;
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
            /*
            System.out.println("=========================================");
            System.out.println("GET MATCHED TimeScaleEntity");
            System.out.println("=========================================");
            */
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

    public static Map<String,Object> batchAttachNewRelations(List<RelationEntityValue> relationEntityValueList, String relationKindName, int degreeOfParallelism){
        int singlePartitionSize = (relationEntityValueList.size()/degreeOfParallelism)+1;
        List<List<RelationEntityValue>> rsList = Lists.partition(relationEntityValueList, singlePartitionSize);
        Map<String,Object> threadReturnDataMap = new Hashtable<>();
        threadReturnDataMap.put("StartTime", LocalDateTime.now());
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
                    for(RelationEntityValue currentRelationEntityValue:this.relationEntityValueList){
                        String sourceEntityUID = currentRelationEntityValue.getFromConceptionEntityUID();
                        String targetEntityUID = currentRelationEntityValue.getToConceptionEntityUID();
                        Map<String, Object> relationPropertiesValue = currentRelationEntityValue.getEntityAttributesValue();
                        String attachRelationCQL = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(sourceEntityUID),Long.parseLong(targetEntityUID),this.relationKindName,relationPropertiesValue);
                        Object returnObj = graphOperationExecutor.executeWrite(getSingleRelationEntityTransformer,attachRelationCQL);
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

    public static Map<String,Object> batchAttachNewRelationsWithSinglePropertyValueMatch(
            String fromConceptionKindName,QueryParameters fromExploreParameters,String fromAttributeName,
            String toConceptionKindName, QueryParameters toExploreParameters,String toAttributeName,
            String relationKindName,int degreeOfParallelism){
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
            Map<String,Object> batchLoadResultMap = BatchDataOperationUtil.batchAttachNewRelations(relationEntityValueList,relationKindName,degreeOfParallelism);
            batchLoadResultMap.put("StartTime", wholeStartDateTime);
            return batchLoadResultMap;
        }else{
            return null;
        }
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
}
