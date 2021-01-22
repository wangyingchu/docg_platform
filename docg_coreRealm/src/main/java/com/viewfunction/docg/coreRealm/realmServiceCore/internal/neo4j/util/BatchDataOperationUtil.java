package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchDataOperationUtil {

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

    public static long batchAddNewEntities(String targetConceptionTypeName,List<ConceptionEntityValue> conceptionEntityValuesList){
        List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValuesList, 2000);
        boolean hasNextRound = true;
        int currentHandlingItemIdx = 0;

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<List<ConceptionEntityValue>> innerListRsList = new ArrayList<>();
        while(hasNextRound){
            System.out.println("ForLoop");
            innerListRsList.clear();
            for(int i = 0;i<5;i++){
                int currentListIdx = currentHandlingItemIdx + i;
                if(currentListIdx < rsList.size()) {
                    System.out.println("in "+currentHandlingItemIdx);
                    if (rsList.get(currentListIdx) != null) {
                        System.out.println("handleing.."+currentListIdx);
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

        System.out.println(rsList.size());

        return 0;
    }

    private static void batchInsertEntities(ExecutorService executor,String targetConceptionTypeName,List<List<ConceptionEntityValue>> innerListRsList){

        System.out.println("Do batch insert");
        System.out.println(innerListRsList.size());
        System.out.println(innerListRsList.get(0).size());
        System.out.println("==============================================");

        int threadNumber = innerListRsList.size();
        for(int i = 0;i< threadNumber;i++){
            List<ConceptionEntityValue> currentConceptionEntityValueList = innerListRsList.get(i);
            InsertRecordThread insertRecordThread = new InsertRecordThread(targetConceptionTypeName,currentConceptionEntityValueList);
            executor.execute(insertRecordThread);
        }
    }
}
