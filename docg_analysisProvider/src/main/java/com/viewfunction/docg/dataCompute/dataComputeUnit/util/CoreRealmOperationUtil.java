package com.viewfunction.docg.dataCompute.dataComputeUnit.util;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionEntityValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonConceptionEntitiesAttributesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.DataSliceServiceInvoker;
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.result.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.exception.ComputeGridNotActiveException;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CoreRealmOperationUtil {

    public final static String RealmGlobalUID = "RealmGlobalUID";
    public final static String RelationFromEntityUID = "RelationFromEntityUID";
    public final static String RelationToEntityUID = "RelationToEntityUID";
    public final static String defaultSliceGroup = "DefaultSliceGroup";
    private final static int defaultResultNumber = 100000000;

    public static DataSliceOperationResult syncConceptionKindToDataSlice(String conceptionKindName, String dataSliceName, String dataSliceGroup){

        String dataSliceRealName = dataSliceName != null ? dataSliceName : conceptionKindName;
        String dataSliceRealGroup = dataSliceGroup != null ? dataSliceGroup : defaultSliceGroup;

        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put(CoreRealmOperationUtil.RealmGlobalUID,DataSlicePropertyType.STRING);
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind targetConceptionKind = coreRealm.getConceptionKind(conceptionKindName);
        List<AttributeKind> attributeKindList = targetConceptionKind.getContainsSingleValueAttributeKinds();
        if(attributeKindList.size() == 0){
            return null;
        }

        List<String> conceptionKindPropertiesList = new ArrayList<>();
        if(attributeKindList != null && attributeKindList.size() >0){
            for(AttributeKind currentAttributeKind :attributeKindList){
                String currentAttributeKindName = currentAttributeKind.getAttributeKindName();
                AttributeDataType currentAttributeDataType = currentAttributeKind.getAttributeDataType();
                switch (currentAttributeDataType){
                    case DECIMAL:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.DECIMAL);
                        break;
                    case BOOLEAN:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.BOOLEAN);
                        break;
                    case STRING:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.STRING);
                        break;
                    case DOUBLE:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.DOUBLE);
                        break;
                    case BINARY:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.BINARY);
                        break;
                    case SHORT:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.SHORT);
                        break;
                    case FLOAT:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.FLOAT);
                        break;
                    case DATE:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.DATE);
                        break;
                    case LONG:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.LONG);
                        break;
                    case BYTE:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.BYTE);
                        break;
                    case INT:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.INT);
                        break;
                }
                conceptionKindPropertiesList.add(currentAttributeKindName);
            }
        }

        try(DataSliceServiceInvoker dataSliceServiceInvoker = DataSliceServiceInvoker.getInvokerInstance()){
            DataSlice targetDataSlice = dataSliceServiceInvoker.getDataSlice(dataSliceRealName);
            if(targetDataSlice == null){
                List<String> pkList = new ArrayList<>();
                pkList.add(CoreRealmOperationUtil.RealmGlobalUID);
                dataSliceServiceInvoker.createGridDataSlice(dataSliceRealName,dataSliceRealGroup,dataSlicePropertyMap,pkList);
            }
        } catch (ComputeGridNotActiveException e) {
                e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(conceptionKindPropertiesList.size() > 0){
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setResultNumber(defaultResultNumber);
            int processorNumber = Runtime.getRuntime().availableProcessors();
            int degreeOfParallelism = (processorNumber/2) >=4 ? (processorNumber/2) : 4;
            return loadConceptionKindEntitiesToDataSlice(conceptionKindName,conceptionKindPropertiesList,queryParameters,dataSliceRealName,true,degreeOfParallelism);
        }else{
            return null;
        }
    }

    public static DataSliceOperationResult syncConceptionKindToDataSlice(String conceptionKindName,String dataSliceName,String dataSliceGroup,Map<String, DataSlicePropertyType> dataSlicePropertyMap){

        String dataSliceRealName = dataSliceName != null ? dataSliceName : conceptionKindName;
        String dataSliceRealGroup = dataSliceGroup != null ? dataSliceGroup : defaultSliceGroup;
        List<String> conceptionKindPropertiesList = new ArrayList<>();

        if(dataSlicePropertyMap != null && dataSlicePropertyMap.size() >0){

            Set<String> propertiesNameSet = dataSlicePropertyMap.keySet();
            conceptionKindPropertiesList.addAll(propertiesNameSet);
            dataSlicePropertyMap.put(CoreRealmOperationUtil.RealmGlobalUID,DataSlicePropertyType.STRING);

            try(DataSliceServiceInvoker dataSliceServiceInvoker = DataSliceServiceInvoker.getInvokerInstance()){
                DataSlice targetDataSlice = dataSliceServiceInvoker.getDataSlice(dataSliceRealName);
                if(targetDataSlice == null){
                    List<String> pkList = new ArrayList<>();
                    pkList.add(CoreRealmOperationUtil.RealmGlobalUID);
                    dataSliceServiceInvoker.createGridDataSlice(dataSliceRealName,dataSliceRealGroup,dataSlicePropertyMap,pkList);
                }
            } catch (ComputeGridNotActiveException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(conceptionKindPropertiesList.size() > 0){
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(defaultResultNumber);
                int processorNumber = Runtime.getRuntime().availableProcessors();
                int degreeOfParallelism = (processorNumber/2) >=4 ? (processorNumber/2) : 4;
                return loadConceptionKindEntitiesToDataSlice(conceptionKindName,conceptionKindPropertiesList,queryParameters,dataSliceRealName,true,degreeOfParallelism);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    public static DataSliceOperationResult refreshDataSliceAndLoadDataFromConceptionKind(String dataSliceGroupName,String dataSliceName,Map<String, DataSlicePropertyType> dataSlicePropertyMap,String conceptionKindName,QueryParameters queryParameters,int degreeOfParallelism){
        if(dataSlicePropertyMap == null ||dataSlicePropertyMap.size() == 0){
            return null;
        }
        Set<String> propertyNameSet = dataSlicePropertyMap.keySet();
        List<String> conceptionKindPropertiesList = new ArrayList<>();
        conceptionKindPropertiesList.addAll(propertyNameSet);

        try(DataSliceServiceInvoker dataSliceServiceInvoker = DataSliceServiceInvoker.getInvokerInstance()){
            DataSlice targetDataSlice = dataSliceServiceInvoker.getDataSlice(dataSliceName);
            if(targetDataSlice != null){
                dataSliceServiceInvoker.eraseDataSlice(dataSliceName);
            }
            dataSlicePropertyMap.put(CoreRealmOperationUtil.RealmGlobalUID,DataSlicePropertyType.STRING);
            if(targetDataSlice == null){
                List<String> pkList = new ArrayList<>();
                pkList.add(CoreRealmOperationUtil.RealmGlobalUID);
                dataSliceServiceInvoker.createGridDataSlice(dataSliceName,dataSliceGroupName+"_CONCEPTION",dataSlicePropertyMap,pkList);
            }
        } catch (ComputeGridNotActiveException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadConceptionKindEntitiesToDataSlice(conceptionKindName,conceptionKindPropertiesList,queryParameters,dataSliceName,true,degreeOfParallelism);
    }

    public static DataSliceOperationResult loadConceptionKindEntitiesToDataSlice(String conceptionKindName, List<String> attributeNamesList,QueryParameters queryParameters, String dataSliceName,boolean useConceptionEntityUIDAsPK,int degreeOfParallelism) {
        DataSliceOperationResult dataSliceOperationResult = new DataSliceOperationResult();

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind targetConceptionKind = coreRealm.getConceptionKind(conceptionKindName);
        int totalResultConceptionEntitiesCount = 0;

        try {
            ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  targetConceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);
            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
            totalResultConceptionEntitiesCount = conceptionEntityValueList.size();

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
            executor.awaitTermination(Long.MAX_VALUE,TimeUnit.NANOSECONDS);
        } catch (CoreRealmServiceEntityExploreException | InterruptedException e) {
            e.printStackTrace();
        }

        try(DataSliceServiceInvoker dataSliceServiceInvoker = DataSliceServiceInvoker.getInvokerInstance()){
            DataSlice targetDataSlice = dataSliceServiceInvoker.getDataSlice(dataSliceName);
            DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
            int successDataCount = dataSliceMetaInfo.getPrimaryDataCount() + dataSliceMetaInfo.getBackupDataCount();
            dataSliceOperationResult.setSuccessItemsCount(successDataCount);
            dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-successDataCount);
        } catch (ComputeGridNotActiveException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Load ConceptionKind Entities To DataSlice Operation");
        return dataSliceOperationResult;
    }

    public static DataSliceOperationResult loadConceptionKindEntitiesToDataSlice(DataSliceServiceInvoker dataSliceServiceInvoker, String conceptionKindName, List<String> attributeNamesList, QueryParameters queryParameters, String dataSliceName, boolean useConceptionEntityUIDAsPK, int degreeOfParallelism) {
        DataSliceOperationResult dataSliceOperationResult = new DataSliceOperationResult();

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind targetConceptionKind = coreRealm.getConceptionKind(conceptionKindName);
        if(targetConceptionKind == null){
            return null;
        }
        int totalResultConceptionEntitiesCount = 0;

        try {
            ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  targetConceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);
            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
            totalResultConceptionEntitiesCount = conceptionEntityValueList.size();

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
            executor.awaitTermination(Long.MAX_VALUE,TimeUnit.NANOSECONDS);
        } catch (CoreRealmServiceEntityExploreException | InterruptedException e) {
            e.printStackTrace();
        }

        DataSlice targetDataSlice = dataSliceServiceInvoker.getDataSlice(dataSliceName);
        DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
        int successDataCount = dataSliceMetaInfo.getPrimaryDataCount() + dataSliceMetaInfo.getBackupDataCount();
        dataSliceOperationResult.setSuccessItemsCount(successDataCount);
        dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-successDataCount);

        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Load ConceptionKind Entities To DataSlice Operation");
        return dataSliceOperationResult;
    }

    public static DataSliceOperationResult loadInnerDataKindEntitiesToDataSlice(DataSliceServiceInvoker dataSliceServiceInvoker, String innerDataKindName, List<AttributeKind> containsAttributesKinds, QueryParameters queryParameters, String dataSliceName, boolean useConceptionEntityUIDAsPK, int degreeOfParallelism) {
        DataSliceOperationResult dataSliceOperationResult = new DataSliceOperationResult();
        int totalResultConceptionEntitiesCount = 0;
        try {
            List<String> attributeNamesList = new ArrayList<>();
            for(AttributeKind currentAttributeKind : containsAttributesKinds){
                attributeNamesList.add(currentAttributeKind.getAttributeKindName());
            }

            CommonConceptionEntitiesAttributesRetrieveResultImpl commonConceptionEntitiesAttributesRetrieveResultImpl
                    = new CommonConceptionEntitiesAttributesRetrieveResultImpl();
            commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            GraphOperationExecutor workingGraphOperationExecutor = new GraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchAttributesWithQueryParameters(innerDataKindName,queryParameters,attributeNamesList);
                GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer =
                        new GetListConceptionEntityValueTransformer(attributeNamesList,containsAttributesKinds);
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer, queryCql);
                if(resEntityRes != null){
                    List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
                    commonConceptionEntitiesAttributesRetrieveResultImpl.addConceptionEntitiesAttributes(resultEntitiesValues);
                    commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultEntitiesValues.size());
                }
            }finally {
                workingGraphOperationExecutor.close();
            }
            commonConceptionEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
            ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult = commonConceptionEntitiesAttributesRetrieveResultImpl;
            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
            totalResultConceptionEntitiesCount = conceptionEntityValueList.size();

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
            executor.awaitTermination(Long.MAX_VALUE,TimeUnit.NANOSECONDS);
        } catch (CoreRealmServiceEntityExploreException | InterruptedException e) {
            e.printStackTrace();
        }

        DataSlice targetDataSlice = dataSliceServiceInvoker.getDataSlice(dataSliceName);
        DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
        int successDataCount = dataSliceMetaInfo.getPrimaryDataCount() + dataSliceMetaInfo.getBackupDataCount();
        dataSliceOperationResult.setSuccessItemsCount(successDataCount);
        dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-successDataCount);

        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Load ConceptionKind Entities To DataSlice Operation");
        return dataSliceOperationResult;
    }

    public static DataSliceOperationResult loadRelationKindEntitiesToDataSlice(DataSliceServiceInvoker dataSliceServiceInvoker, String relationKindName, List<String> attributeNamesList, QueryParameters queryParameters, String dataSliceName, boolean useConceptionEntityUIDAsPK, int degreeOfParallelism) {
        DataSliceOperationResult dataSliceOperationResult = new DataSliceOperationResult();

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        RelationKind targetRelationKind = coreRealm.getRelationKind(relationKindName);
        if(targetRelationKind == null){
            return null;
        }
        int totalResultConceptionEntitiesCount = 0;
        try {
            RelationEntitiesAttributesRetrieveResult relationEntitiesAttributesRetrieveResult = targetRelationKind.getEntityAttributesByAttributeNames(attributeNamesList,queryParameters);
            List<RelationEntityValue> conceptionEntityValueList = relationEntitiesAttributesRetrieveResult.getRelationEntityValues();
            totalResultConceptionEntitiesCount = conceptionEntityValueList.size();

            int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
            List<List<RelationEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);

            if(useConceptionEntityUIDAsPK){
                attributeNamesList.add(RealmGlobalUID);
            }

            ExecutorService executor = Executors.newFixedThreadPool(rsList.size());
            for(int i = 0;i < rsList.size(); i++){
                List<RelationEntityValue> currentConceptionEntityValueList = rsList.get(i);
                DataSliceInsertRelationThread dataSliceInsertRelationThread = new DataSliceInsertRelationThread(i,dataSliceName,attributeNamesList,currentConceptionEntityValueList,useConceptionEntityUIDAsPK);
                executor.execute(dataSliceInsertRelationThread);
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE,TimeUnit.NANOSECONDS);
        } catch (CoreRealmServiceEntityExploreException | InterruptedException e) {
            e.printStackTrace();
        }

        DataSlice targetDataSlice = dataSliceServiceInvoker.getDataSlice(dataSliceName);
        DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
        int successDataCount = dataSliceMetaInfo.getPrimaryDataCount() + dataSliceMetaInfo.getBackupDataCount();
        dataSliceOperationResult.setSuccessItemsCount(successDataCount);
        dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-successDataCount);

        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Load RelationKind Entities To DataSlice Operation");
        return dataSliceOperationResult;
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

            try(DataSliceServiceInvoker dataSliceServiceInvoker = DataSliceServiceInvoker.getInvokerInstance(invokerIgnite)){
                DataSlice targetDataSlice = dataSliceServiceInvoker.getDataSlice(this.dataSliceName);
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

    private static class DataSliceInsertRelationThread implements Runnable{
        private String dataSliceName;
        private List<String> sliceDataProperties;
        private List<RelationEntityValue> sliceDataRows;
        private boolean useRelationEntityUIDAsPK;
        private int threadId;

        public DataSliceInsertRelationThread(int threadId,String dataSliceName, List<String> sliceDataProperties,List<RelationEntityValue> sliceDataRows,boolean useRelationEntityUIDAsPK){
            this.dataSliceName = dataSliceName;
            this.sliceDataProperties = sliceDataProperties;
            this.sliceDataRows = sliceDataRows;
            this.useRelationEntityUIDAsPK = useRelationEntityUIDAsPK;
            this.threadId = threadId;
        }

        @Override
        public void run() {
            List<Map<String,Object>> sliceDataRowsDataList = new ArrayList<>();
            for(RelationEntityValue currentRelationEntityValue :this.sliceDataRows){
                Map<String,Object> currentDataMap = currentRelationEntityValue.getEntityAttributesValue();
                if(useRelationEntityUIDAsPK){
                    currentDataMap.put(RealmGlobalUID,currentRelationEntityValue.getRelationEntityUID());
                }
                currentDataMap.put(RelationFromEntityUID,currentRelationEntityValue.getFromConceptionEntityUID());
                currentDataMap.put(RelationToEntityUID,currentRelationEntityValue.getToConceptionEntityUID());
                sliceDataRowsDataList.add(currentDataMap);
            }

            IgniteConfiguration igniteConfiguration= new IgniteConfiguration();
            igniteConfiguration.setClientMode(true);
            igniteConfiguration.setIgniteInstanceName("DataSliceInsertRelationThread_"+threadId);
            Ignite invokerIgnite = Ignition.start(igniteConfiguration);

            try(DataSliceServiceInvoker dataSliceServiceInvoker = DataSliceServiceInvoker.getInvokerInstance(invokerIgnite)){
                DataSlice targetDataSlice = dataSliceServiceInvoker.getDataSlice(this.dataSliceName);
                DataSliceOperationResult dataSliceOperationResult = targetDataSlice.addDataRecords(this.sliceDataProperties,sliceDataRowsDataList);
                System.out.println("--------------------------------------");
                System.out.println("Execution result of : "+"DataSliceInsertRelationThread_"+threadId);
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
