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
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.result.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

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
            for(int i = 0;i < rsList.size(); i++){
                List<ConceptionEntityValue> currentConceptionEntityValueList = rsList.get(i);
                DataSliceInsertDataThread dataSliceInsertDataThread = new DataSliceInsertDataThread(i,dataSliceName,attributeNamesList,currentConceptionEntityValueList,useConceptionEntityUIDAsPK);
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
        private int threadId;

        public DataSliceInsertDataThread(int threadId,String dataSliceName, List<String> sliceDataProperties,List<ConceptionEntityValue> sliceDataRows,boolean useConceptionEntityUIDAsPK){
            this.dataSliceName = dataSliceName;
            this.sliceDataProperties = sliceDataProperties;
            this.sliceDataRows = sliceDataRows;
            this.useConceptionEntityUIDAsPK = useConceptionEntityUIDAsPK;
            this.threadId = threadId;
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

            IgniteConfiguration igniteConfiguration= new IgniteConfiguration();
            igniteConfiguration.setClientMode(true);
            igniteConfiguration.setIgniteInstanceName("DataSliceInsertDataThread_"+threadId);
            Ignite invokerIgnite =Ignition.start(igniteConfiguration);

            try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance(invokerIgnite)){
                DataSlice targetDataSlice = dataServiceInvoker.getDataSlice(this.dataSliceName);
                DataSliceOperationResult dataSliceOperationResult = targetDataSlice.addDataRecords(this.sliceDataProperties,sliceDataRowsDataList);
                System.out.println("--------------------------------------");
                System.out.println("Execution result of : "+"DataSliceInsertDataThread_"+threadId);
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
