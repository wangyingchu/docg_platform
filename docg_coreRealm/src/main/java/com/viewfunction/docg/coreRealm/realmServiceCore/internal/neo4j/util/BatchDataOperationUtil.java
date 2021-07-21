package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.google.common.collect.Lists;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleTimeScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchDataOperationUtil {

    private static Logger logger = LoggerFactory.getLogger(BatchDataOperationUtil.class);

    public static void batchAddNewEntities(String targetConceptionTypeName,List<ConceptionEntityValue> conceptionEntityValuesList,int degreeOfParallelism){
        int singlePartitionSize = (conceptionEntityValuesList.size()/degreeOfParallelism)+1;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValuesList, singlePartitionSize);

        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<ConceptionEntityValue> currentConceptionEntityValueList:rsList){
            InsertRecordThread insertRecordThread = new InsertRecordThread(targetConceptionTypeName,currentConceptionEntityValueList);
            executor.execute(insertRecordThread);
        }
        executor.shutdown();
    }

    private static class InsertRecordThread implements Runnable{
        private List<ConceptionEntityValue> conceptionEntityValueList;
        private String conceptionKindName;

        public InsertRecordThread(String conceptionKindName,List<ConceptionEntityValue> conceptionEntityValueList){
            this.conceptionEntityValueList = conceptionEntityValueList;
            this.conceptionKindName = conceptionKindName;
        }

        @Override
        public void run(){
            CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
            coreRealm.openGlobalSession();
            ConceptionKind conceptionKind = coreRealm.getConceptionKind(conceptionKindName);
            for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
                conceptionKind.newEntity(currentConceptionEntityValue,false);
            }
            coreRealm.closeGlobalSession();
        }
    }

    public static void batchAttachTimeScaleEvents(List<ConceptionEntityValue> conceptionEntityValueList, String timeEventAttributeName,String eventComment,
                                                  Map<String,Object> globalEventData, TimeFlow.TimeScaleGrade timeScaleGrade,int degreeOfParallelism){
        int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);

        Map<String,String> timeScaleEntitiesMetaInfoMapping = new HashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<ConceptionEntityValue> currentConceptionEntityValueList:rsList){
            LinkTimeScaleEventThread linkTimeScaleEventThread = new LinkTimeScaleEventThread(timeEventAttributeName,
                    eventComment,globalEventData,timeScaleGrade,currentConceptionEntityValueList,timeScaleEntitiesMetaInfoMapping);
            executor.execute(linkTimeScaleEventThread);
        }
        executor.shutdown();
    }

    private static class LinkTimeScaleEventThread implements Runnable{
        private String timeEventAttributeName;
        private String eventComment;
        private Map<String,Object> globalEventData;
        private TimeFlow.TimeScaleGrade timeScaleGrade;
        private List<ConceptionEntityValue> conceptionEntityValueList;
        private Map<String,String> timeScaleEntitiesMetaInfoMapping;

        public LinkTimeScaleEventThread(String timeEventAttributeName,String eventComment,Map<String,Object> globalEventData,
                                        TimeFlow.TimeScaleGrade timeScaleGrade,List<ConceptionEntityValue> conceptionEntityValueList,
                                        Map<String,String> timeScaleEntitiesMetaInfoMapping
                                         ){
            this.timeEventAttributeName = timeEventAttributeName;
            this.eventComment = eventComment;
            this.globalEventData = globalEventData;
            this.timeScaleGrade = timeScaleGrade;
            this.conceptionEntityValueList = conceptionEntityValueList;
            this.timeScaleEntitiesMetaInfoMapping = timeScaleEntitiesMetaInfoMapping;
        }

        @Override
        public void run() {
            GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
            for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
                if(currentConceptionEntityValue.getEntityAttributesValue().get(timeEventAttributeName) != null){
                    Date targetDateValue = (Date)currentConceptionEntityValue.getEntityAttributesValue().get(timeEventAttributeName);
                    attachTimeScaleEventLogic(timeScaleEntitiesMetaInfoMapping,currentConceptionEntityValue.getConceptionEntityUID(),targetDateValue.getTime(),
                            eventComment,globalEventData,timeScaleGrade,graphOperationExecutor);
                }
            }
            graphOperationExecutor.close();
        }
    }

    private static void attachTimeScaleEventLogic(Map<String,String> timeScaleEntitiesMetaInfoMapping,String conceptionEntityUID,long dateTime,String eventComment,
                                                 Map<String,Object> globalEventData,TimeFlow.TimeScaleGrade timeScaleGrade,
                                                  GraphOperationExecutor workingGraphOperationExecutor){
        Map<String, Object> propertiesMap = new HashMap<>();
        if(globalEventData != null){
            propertiesMap.putAll(globalEventData);
        }
        CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
        propertiesMap.put(RealmConstant._TimeScaleEventReferTime,dateTime);
        propertiesMap.put(RealmConstant._TimeScaleEventComment,eventComment);
        propertiesMap.put(RealmConstant._TimeScaleEventScaleGrade,""+timeScaleGrade);
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
                String timeScaleEntityUID = getTimeScaleEntityUID(timeScaleEntitiesMetaInfoMapping,dateTime,RealmConstant._defaultTimeFlowName, timeScaleGrade, workingGraphOperationExecutor);
                if(timeScaleEntityUID != null){
                    String linkToTimeScaleEntityCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(timeScaleEntityUID),Long.parseLong(timeEventUID),RealmConstant.TimeScale_TimeReferToRelationClass,relationPropertiesMap);
                    workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, linkToTimeScaleEntityCql);
                }
            }
        }
    }

    private static String getTimeScaleEntityUID(Map<String,String> timeScaleEntitiesMetaInfoMapping,long dateTime, String timeFlowName, TimeFlow.TimeScaleGrade timeScaleGrade,GraphOperationExecutor workingGraphOperationExecutor){
        Calendar eventCalendar=Calendar.getInstance();
        eventCalendar.setTimeInMillis(dateTime);

        int year = eventCalendar.get(Calendar.YEAR) ;
        int month = eventCalendar.get(Calendar.MONTH)+1;
        int day = eventCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = eventCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = eventCalendar.get(Calendar.MINUTE);
        //int second = eventCalendar.get(Calendar.SECOND);

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

    public static void batchAttachNewRelations(List<RelationEntityValue> relationEntityValueList, String relationKindName, int degreeOfParallelism){
        int singlePartitionSize = (relationEntityValueList.size()/degreeOfParallelism)+1;
        List<List<RelationEntityValue>> rsList = Lists.partition(relationEntityValueList, singlePartitionSize);

        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<RelationEntityValue> currentRelationEntityValueList:rsList){
            AttachRelationThread attachRelationThread = new AttachRelationThread(currentRelationEntityValueList,relationKindName);
            executor.execute(attachRelationThread);
        }
        executor.shutdown();
    }

    private static class AttachRelationThread implements Runnable{
        private List<RelationEntityValue> relationEntityValueList;
        private String relationKindName;

        public AttachRelationThread(List<RelationEntityValue> relationEntityValueList,String relationKindName){
            this.relationEntityValueList = relationEntityValueList;
            this.relationKindName = relationKindName;
        }

        @Override
        public void run(){
            if(this.relationEntityValueList != null && this.relationEntityValueList.size() >0){
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
                        graphOperationExecutor.executeWrite(getSingleRelationEntityTransformer,attachRelationCQL);
                    }
                    graphOperationExecutor.close();
                }
            }
        }
    }
}
