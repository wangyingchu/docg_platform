package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchDataOperationUtil {

    public static void batchAddNewEntities(String targetConceptionTypeName,List<ConceptionEntityValue> conceptionEntityValuesList){
        int degreeOfParallelism = 5;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValuesList, 2000);
        boolean hasNextRound = true;
        int currentHandlingItemIdx = 0;

        ExecutorService executor = Executors.newFixedThreadPool(degreeOfParallelism);
        List<List<ConceptionEntityValue>> innerListRsList = new ArrayList<>();
        while(hasNextRound){
            innerListRsList.clear();
            for(int i = 0;i<degreeOfParallelism;i++){
                int currentListIdx = currentHandlingItemIdx + i;
                if(currentListIdx < rsList.size()) {
                    if (rsList.get(currentListIdx) != null) {
                        innerListRsList.add(rsList.get(currentListIdx));
                    }
                }else{
                    hasNextRound = false;
                }
            }
            currentHandlingItemIdx = currentHandlingItemIdx + 5;
            batchInsertEntities(executor,targetConceptionTypeName,innerListRsList);
        }
        executor.shutdown();
    }

    private static void batchInsertEntities(ExecutorService executor,String targetConceptionTypeName,List<List<ConceptionEntityValue>> innerListRsList){
        int threadNumber = innerListRsList.size();
        for(int i = 0;i< threadNumber;i++){
            List<ConceptionEntityValue> currentConceptionEntityValueList = innerListRsList.get(i);
            InsertRecordThread insertRecordThread = new InsertRecordThread(targetConceptionTypeName,currentConceptionEntityValueList);
            executor.execute(insertRecordThread);
        }
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
        int degreeOfParallelism = 15;
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, 2000);

        boolean hasNextRound = true;
        int currentHandlingItemIdx = 0;

        ExecutorService executor = Executors.newFixedThreadPool(degreeOfParallelism);
        List<List<ConceptionEntityValue>> innerListRsList = new ArrayList<>();
        while(hasNextRound){
            innerListRsList.clear();
            for(int i = 0;i<degreeOfParallelism;i++){
                int currentListIdx = currentHandlingItemIdx + i;
                if(currentListIdx < rsList.size()) {
                    if (rsList.get(currentListIdx) != null) {
                        innerListRsList.add(rsList.get(currentListIdx));
                    }
                }else{
                    hasNextRound = false;
                }
            }
            currentHandlingItemIdx = currentHandlingItemIdx + degreeOfParallelism;
            batchLinkTimeScaleEvents(executor,innerListRsList,targetConceptionTypeName,timeEventAttributeName,relationKindName,relationDirection,globalEventData,timeScaleGrade);
        }
        executor.shutdown();
    }

    private static void batchLinkTimeScaleEvents(ExecutorService executor,List<List<ConceptionEntityValue>> innerListRsList,
                                                 String targetConceptionTypeName, String timeEventAttributeName,
                                                 String relationKindName, RelationDirection relationDirection,
                                                 Map<String,Object> globalEventData, TimeFlow.TimeScaleGrade timeScaleGrade){
        int threadNumber = innerListRsList.size();
        for(int i = 0;i< threadNumber;i++){
            List<ConceptionEntityValue> currentConceptionEntityValueList = innerListRsList.get(i);
            LinkTimeScaleEventThread linkTimeScaleEventThread = new LinkTimeScaleEventThread(targetConceptionTypeName,
                    timeEventAttributeName,relationKindName,relationDirection,globalEventData,timeScaleGrade,currentConceptionEntityValueList);
            executor.execute(linkTimeScaleEventThread);
        }
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
                    Neo4JConceptionEntityImpl _Neo4JConceptionEntityImpl = new Neo4JConceptionEntityImpl(conceptionKindName,currentConceptionEntityValue.getConceptionEntityUID());
                    _Neo4JConceptionEntityImpl.setGlobalGraphOperationExecutor(graphOperationExecutor);
                    try {
                        _Neo4JConceptionEntityImpl.attachTimeScaleEvent(targetDateValue.getTime(),relationKindName, relationDirection,globalEventData,timeScaleGrade);
                    } catch (CoreRealmServiceRuntimeException e) {
                        e.printStackTrace();
                    }

                }
            }
            graphOperationExecutor.close();
        }
    }
}
