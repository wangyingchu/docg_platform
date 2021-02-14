package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoreRealmOperationUtil {

    public final static String RealmGlobalUID = "RealmGlobalUID";

    public static void loadConceptionKindEntitiesToDataSlice(String conceptionKindName, List<String> attributeNamesList,QueryParameters queryParameters, String dataSliceName,boolean useConceptionEntityUIDAsPK,int degreeOfParallelism) {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind targetConceptionKind = coreRealm.getConceptionKind(conceptionKindName);
        try {
            ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  targetConceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);
            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();

            int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
            List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);

            if(useConceptionEntityUIDAsPK){
                attributeNamesList.add(RealmGlobalUID);
            }

            ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
            for(List<ConceptionEntityValue> currentConceptionEntityValueList:rsList){
                DataSliceInsertDataThread dataSliceInsertDataThread = new DataSliceInsertDataThread(dataSliceName,attributeNamesList,currentConceptionEntityValueList,useConceptionEntityUIDAsPK);
                executor.execute(dataSliceInsertDataThread);
            }
            executor.shutdown();

        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
    }

    private static class DataSliceInsertDataThread implements Runnable{
        private String dataSliceName;
        private List<String> sliceDataProperties;
        private List<ConceptionEntityValue> sliceDataRows;
        private boolean useConceptionEntityUIDAsPK;

        public DataSliceInsertDataThread(String dataSliceName, List<String> sliceDataProperties,List<ConceptionEntityValue> sliceDataRows,boolean useConceptionEntityUIDAsPK){
            this.dataSliceName = dataSliceName;
            this.sliceDataProperties = sliceDataProperties;
            this.sliceDataRows = sliceDataRows;
            this.useConceptionEntityUIDAsPK = useConceptionEntityUIDAsPK;
        }

        @Override
        public void run() {
            List<Map<String,Object>> sliceDataRowsDataList = new ArrayList<>();
            for(ConceptionEntityValue currentConceptionEntityValue :this.sliceDataRows){
                Map<String,Object> currentDataMap = currentConceptionEntityValue.getEntityAttributesValue();
                if(useConceptionEntityUIDAsPK){
                    currentDataMap.put(RealmGlobalUID,currentConceptionEntityValue.getConceptionEntityUID());
                }
                sliceDataRowsDataList.add(currentDataMap);
            }
            try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance()){
                DataSlice targetDataSlice = dataServiceInvoker.getDataSlice(this.dataSliceName);
                DataSliceOperationResult dataSliceOperationResult = targetDataSlice.addDataRecords(this.sliceDataProperties,sliceDataRowsDataList);
                System.out.println(dataSliceOperationResult.getOperationSummary());
                System.out.println(dataSliceOperationResult.getStartTime());
                System.out.println(dataSliceOperationResult.getFinishTime());
                System.out.println(dataSliceOperationResult.getSuccessItemsCount());
                System.out.println(dataSliceOperationResult.getFailItemsCount());
                System.out.println("--------------------------------------");
            } catch (ComputeGridNotActiveException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
