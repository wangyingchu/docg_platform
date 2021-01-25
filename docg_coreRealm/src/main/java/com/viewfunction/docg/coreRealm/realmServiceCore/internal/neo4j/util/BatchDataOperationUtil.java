package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.google.common.collect.Lists;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleTimeScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JTimeScaleEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchDataOperationUtil {

    private static Logger logger = LoggerFactory.getLogger(BatchDataOperationUtil.class);
    private static Map<String,String> TimeScaleEntitiesMetaInfoMapping_Minute = new HashMap<>();

    public static void batchAddNewEntities(String targetConceptionTypeName,List<ConceptionEntityValue> conceptionEntityValuesList){
        int degreeOfParallelism = 10;
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

    public static void batchAttachTimeScaleEvents(List<ConceptionEntityValue> conceptionEntityValueList,String targetConceptionTypeName, String timeEventAttributeName,
                                                  String relationKindName, RelationDirection relationDirection,
                                                  Map<String,Object> globalEventData, TimeFlow.TimeScaleGrade timeScaleGrade){
        int degreeOfParallelism = 5;
        int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);

        ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
        for(List<ConceptionEntityValue> currentConceptionEntityValueList:rsList){
            LinkTimeScaleEventThread linkTimeScaleEventThread = new LinkTimeScaleEventThread(targetConceptionTypeName,
                    timeEventAttributeName,relationKindName,relationDirection,globalEventData,timeScaleGrade,currentConceptionEntityValueList);
            executor.execute(linkTimeScaleEventThread);
        }
        executor.shutdown();
    }

    private static class LinkTimeScaleEventThread implements Runnable{
        private String conceptionKindName;
        private String timeEventAttributeName;
        private String relationKindName;
        private RelationDirection relationDirection;
        private Map<String,Object> globalEventData;
        private TimeFlow.TimeScaleGrade timeScaleGrade;
        private List<ConceptionEntityValue> conceptionEntityValueList;

        public LinkTimeScaleEventThread(String conceptionKindName, String timeEventAttributeName,String relationKindName,
                                        RelationDirection relationDirection,Map<String,Object> globalEventData,
                                        TimeFlow.TimeScaleGrade timeScaleGrade,List<ConceptionEntityValue> conceptionEntityValueList
                                         ){
            this.conceptionKindName = conceptionKindName;
            this.timeEventAttributeName = timeEventAttributeName;
            this.relationKindName = relationKindName;
            this.relationDirection = relationDirection;
            this.globalEventData = globalEventData;
            this.timeScaleGrade = timeScaleGrade;
            this.conceptionEntityValueList = conceptionEntityValueList;
        }

        @Override
        public void run() {
            GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
            for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValueList){
                if(currentConceptionEntityValue.getEntityAttributesValue().get(timeEventAttributeName) != null){
                    Date targetDateValue = (Date)currentConceptionEntityValue.getEntityAttributesValue().get(timeEventAttributeName);
                    attachTimeScaleEventLogic(currentConceptionEntityValue.getConceptionEntityUID(),targetDateValue.getTime(),
                            relationKindName, relationDirection,globalEventData,timeScaleGrade,graphOperationExecutor);
                    /*
                    Neo4JConceptionEntityImpl _Neo4JConceptionEntityImpl = new Neo4JConceptionEntityImpl(conceptionKindName,currentConceptionEntityValue.getConceptionEntityUID());
                    _Neo4JConceptionEntityImpl.setGlobalGraphOperationExecutor(graphOperationExecutor);
                    try {
                        _Neo4JConceptionEntityImpl.attachTimeScaleEvent(targetDateValue.getTime(),relationKindName, relationDirection,globalEventData,timeScaleGrade);
                    } catch (CoreRealmServiceRuntimeException e) {
                        e.printStackTrace();
                    }
                    */
                }
            }
            graphOperationExecutor.close();
        }
    }

    private static void attachTimeScaleEventLogic(String conceptionEntityUID,long dateTime,String relationKindName,
                                                  RelationDirection relationDirection,Map<String,Object> globalEventData,
                                                  TimeFlow.TimeScaleGrade timeScaleGrade,GraphOperationExecutor workingGraphOperationExecutor){
        Map<String, Object> propertiesMap = globalEventData != null ? globalEventData : new HashMap<>();
        CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
        String createEventCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.TimeScaleEventClass}, propertiesMap);
        GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                new GetSingleConceptionEntityTransformer(RealmConstant.TimeScaleEventClass, workingGraphOperationExecutor);
        Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, createEventCql);
        if(newEntityRes != null) {
            String timeEventUID = ((ConceptionEntity)newEntityRes).getConceptionEntityUID();
            String createCql = null;
            Map<String,Object> relationPropertiesMap = new HashMap<>();
            CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
            switch (relationDirection) {
                case FROM:
                    createCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(conceptionEntityUID),Long.parseLong(timeEventUID),relationKindName,relationPropertiesMap);
                    break;
                case TO:
                    createCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(timeEventUID),Long.parseLong(conceptionEntityUID),relationKindName,relationPropertiesMap);
                    break;
                case TWO_WAY:
            }

            GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                    (relationKindName,workingGraphOperationExecutor);
            Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createCql);
            if(newRelationEntityRes != null){
                String timeScaleEntityUID = getTimeScaleEntityUID(dateTime,RealmConstant._defaultTimeFlowName, timeScaleGrade, workingGraphOperationExecutor);

                String linkToTimeScaleEntityCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(timeScaleEntityUID),Long.parseLong(timeEventUID),RealmConstant.TimeScale_TimeReferToRelationClass,relationPropertiesMap);
                workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, linkToTimeScaleEntityCql);
            }
        }
    }

    private static String getTimeScaleEntityUID(long dateTime, String timeFlowName, TimeFlow.TimeScaleGrade timeScaleGrade,GraphOperationExecutor workingGraphOperationExecutor){
        Calendar eventCalendar=Calendar.getInstance();
        eventCalendar.setTimeInMillis(dateTime);

        int year = eventCalendar.get(Calendar.YEAR) ;
        int month = eventCalendar.get(Calendar.MONTH)+1;
        int day = eventCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = eventCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = eventCalendar.get(Calendar.MINUTE);
        //int second = eventCalendar.get(Calendar.SECOND);

        String TimeScaleEntityKey = timeFlowName+":"+year+"_"+month+"_"+day+"_"+hour+"_"+minute;

        if(TimeScaleEntitiesMetaInfoMapping_Minute.containsKey(TimeScaleEntityKey)){
            //System.out.println("=========================================");
            //System.out.println("MATCHED MATCHED MATCHED");
            //System.out.println("=========================================");
            return TimeScaleEntitiesMetaInfoMapping_Minute.get(TimeScaleEntityKey);
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
                String  timeScaleEntityUID = ((Neo4JTimeScaleEntityImpl)queryRes).getTimeScaleEntityUID();
                TimeScaleEntitiesMetaInfoMapping_Minute.put(TimeScaleEntityKey,timeScaleEntityUID);
                return timeScaleEntityUID;
            }
        }
        return null;
    }
}
