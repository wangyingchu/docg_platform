package com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.common;

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
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSliceExistException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSlicePropertiesStructureException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlice;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CoreRealmOperationUtil {

    public final static String RealmGlobalUID = "DOCG_RealmGlobalUID";
    public final static String RelationFromEntityUID = "DOCG_RelationFromEntityUID";
    public final static String RelationToEntityUID = "DOCG_RelationToEntityUID";
    public final static String defaultSliceGroup = "DefaultSliceGroup";
    private final static int defaultResultNumber = 100000000;

    public static DataSliceOperationResult syncConceptionKindToDataSlice(String conceptionKindName,String dataSliceName,String dataSliceGroup,Map<String, DataSlicePropertyType> dataSlicePropertyMap,QueryParameters queryParameters){

        String dataSliceRealName = dataSliceName != null ? dataSliceName : conceptionKindName;
        String dataSliceRealGroup = dataSliceGroup != null ? dataSliceGroup : defaultSliceGroup;
        List<String> conceptionKindPropertiesList = new ArrayList<>();

        if(dataSlicePropertyMap != null && dataSlicePropertyMap.size() >0){

            Set<String> propertiesNameSet = dataSlicePropertyMap.keySet();
            conceptionKindPropertiesList.addAll(propertiesNameSet);
            dataSlicePropertyMap.put(CoreRealmOperationUtil.RealmGlobalUID,DataSlicePropertyType.STRING);

            // use this logic to avoid create already exist ignite nodes has same name
            IgniteConfiguration igniteConfiguration= new IgniteConfiguration();
            igniteConfiguration.setClientMode(true);
            igniteConfiguration.setIgniteInstanceName("DataSliceCreateThread_"+UUID.randomUUID());
            Ignite invokerIgnite =Ignition.start(igniteConfiguration);

            ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
            try(DataService dataService = targetComputeGrid.getDataService()){
                DataSlice targetDataSlice = dataService.getDataSlice(dataSliceRealName);
                if(targetDataSlice == null){
                    List<String> pkList = new ArrayList<>();
                    pkList.add(CoreRealmOperationUtil.RealmGlobalUID);
                    dataService.createGridDataSlice(dataSliceRealName,dataSliceRealGroup,dataSlicePropertyMap,pkList);
                }else{
                    return null;
                }
            } catch (ComputeGridException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            invokerIgnite.close();

            if(conceptionKindPropertiesList.size() > 0){
                QueryParameters executedQueryParameters;
                if(queryParameters != null){
                    executedQueryParameters = queryParameters;
                }else{
                    executedQueryParameters = new QueryParameters();
                    executedQueryParameters.setResultNumber(defaultResultNumber);
                }
                int processorNumber = Runtime.getRuntime().availableProcessors();
                int degreeOfParallelism = (processorNumber/2) >=4 ? (processorNumber/2) : 4;
                return loadConceptionKindEntitiesToDataSlice(conceptionKindName,conceptionKindPropertiesList,executedQueryParameters,dataSliceRealName,true,degreeOfParallelism);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    public static DataSliceOperationResult cleanAndSyncConceptionKindToDataSlice(String conceptionKindName,String dataSliceName,String dataSliceGroup,Map<String, DataSlicePropertyType> dataSlicePropertyMap,QueryParameters queryParameters){
        if(dataSlicePropertyMap == null ||dataSlicePropertyMap.size() == 0){
            return null;
        }

        String dataSliceRealName = dataSliceName != null ? dataSliceName : conceptionKindName;
        String dataSliceRealGroup = dataSliceGroup != null ? dataSliceGroup : defaultSliceGroup;
        Set<String> propertyNameSet = dataSlicePropertyMap.keySet();
        List<String> conceptionKindPropertiesList = new ArrayList<>();
        conceptionKindPropertiesList.addAll(propertyNameSet);

        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(DataService dataService = targetComputeGrid.getDataService()){
            DataSlice targetDataSlice = dataService.getDataSlice(dataSliceRealName);
            if(targetDataSlice != null){
                dataService.eraseDataSlice(dataSliceRealName);
            }
            dataSlicePropertyMap.put(CoreRealmOperationUtil.RealmGlobalUID,DataSlicePropertyType.STRING);
            List<String> pkList = new ArrayList<>();
            pkList.add(CoreRealmOperationUtil.RealmGlobalUID);
            dataService.createGridDataSlice(dataSliceRealName,dataSliceRealGroup,dataSlicePropertyMap,pkList);
        } catch (ComputeGridException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        QueryParameters executedQueryParameters;
        if(queryParameters != null){
            executedQueryParameters = queryParameters;
        }else{
            executedQueryParameters = new QueryParameters();
            executedQueryParameters.setResultNumber(defaultResultNumber);
        }

        int processorNumber = Runtime.getRuntime().availableProcessors();
        int degreeOfParallelism = (processorNumber/2) >=4 ? (processorNumber/2) : 4;
        return loadConceptionKindEntitiesToDataSlice(conceptionKindName,conceptionKindPropertiesList,executedQueryParameters,dataSliceRealName,true,degreeOfParallelism);
    }

    public static DataSliceOperationResult loadConceptionKindEntitiesToDataSlice(String conceptionKindName,String dataSliceName,Map<String,String> sliceAndKindAttributeMapping,QueryParameters queryParameters, String conceptionEntityUIDNameAlias) {
        if(sliceAndKindAttributeMapping == null ||sliceAndKindAttributeMapping.size() == 0){
            return null;
        }

        DataSliceOperationResult dataSliceOperationResult = new DataSliceOperationResult();
        Set<String> attributeNameSet = sliceAndKindAttributeMapping.keySet();
        List<String> attributeNamesList = new ArrayList<>();
        for(String currentAttributeName : attributeNameSet){
            String conceptionKindAttributeName = sliceAndKindAttributeMapping.get(currentAttributeName);
            if(!conceptionKindAttributeName.equals(conceptionEntityUIDNameAlias)){
                if(!attributeNamesList.contains(conceptionKindAttributeName)){
                    attributeNamesList.add(conceptionKindAttributeName);
                }
            }
        }

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind targetConceptionKind = coreRealm.getConceptionKind(conceptionKindName);
        int totalResultConceptionEntitiesCount = 0;

        try {
            ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult = targetConceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);
            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
            totalResultConceptionEntitiesCount = conceptionEntityValueList.size();

            int processorNumber = Runtime.getRuntime().availableProcessors();
            int degreeOfParallelism = (processorNumber/2) >=4 ? (processorNumber/2) : 4;
            int singlePartitionSize = (conceptionEntityValueList.size()/degreeOfParallelism)+1;
            List<List<ConceptionEntityValue>> rsList = Lists.partition(conceptionEntityValueList, singlePartitionSize);

            ExecutorService executor = Executors.newFixedThreadPool(rsList.size());

            List<DataSliceOperationResult> dataSliceOperationResultsList = new ArrayList<>();

            for(int i = 0;i < rsList.size(); i++){
                List<ConceptionEntityValue> currentConceptionEntityValueList = rsList.get(i);
                DataSliceInsertConceptionEntitiesThread dataSliceInsertConceptionEntitiesThread =
                        new DataSliceInsertConceptionEntitiesThread(i,dataSliceName,sliceAndKindAttributeMapping,conceptionEntityUIDNameAlias,currentConceptionEntityValueList,dataSliceOperationResultsList);
                executor.execute(dataSliceInsertConceptionEntitiesThread);
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE,TimeUnit.NANOSECONDS);

            long totalSuccessItemsCount = 0;
            for(DataSliceOperationResult currentDataSliceOperationResult: dataSliceOperationResultsList){
                totalSuccessItemsCount = totalSuccessItemsCount + currentDataSliceOperationResult.getSuccessItemsCount();
            }

            dataSliceOperationResult.setSuccessItemsCount(totalSuccessItemsCount);
            dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-totalSuccessItemsCount);
        } catch (CoreRealmServiceEntityExploreException | InterruptedException e) {
            e.printStackTrace();
        }

        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Load ConceptionKind Entities To DataSlice Operation");
        return dataSliceOperationResult;
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

        // use this logic to avoid create ready exist ignite nodes has same name
        IgniteConfiguration igniteConfiguration= new IgniteConfiguration();
        igniteConfiguration.setClientMode(true);
        igniteConfiguration.setIgniteInstanceName("DataSliceConfirmThread_"+UUID.randomUUID());
        Ignite invokerIgnite =Ignition.start(igniteConfiguration);
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(DataService dataService = targetComputeGrid.getDataService()){
            DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
            DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
            int successDataCount = dataSliceMetaInfo.getPrimaryDataCount() + dataSliceMetaInfo.getBackupDataCount();
            dataSliceOperationResult.setSuccessItemsCount(successDataCount);
            dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-successDataCount);
        } catch (ComputeGridException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Load ConceptionKind Entities To DataSlice Operation");
        invokerIgnite.close();
        return dataSliceOperationResult;
    }

    public static DataSliceOperationResult loadConceptionKindEntitiesToDataSlice(DataService dataService,String conceptionKindName, List<String> attributeNamesList,QueryParameters queryParameters, String dataSliceName,boolean useConceptionEntityUIDAsPK,int degreeOfParallelism) {
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

        DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
        DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
        int successDataCount = dataSliceMetaInfo.getPrimaryDataCount() + dataSliceMetaInfo.getBackupDataCount();
        dataSliceOperationResult.setSuccessItemsCount(successDataCount);
        dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-successDataCount);

        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Load ConceptionKind Entities To DataSlice Operation");
        return dataSliceOperationResult;
    }

    public static DataSliceOperationResult syncInnerDataKindEntitiesToDataSlice(DataService dataService,
                                                                                String innerDataKindName,String dataSliceGroup,
                                                                                List<AttributeKind> containsAttributesKinds,
                                                                                QueryParameters queryParameters, String dataSliceName,boolean useConceptionEntityUIDAsPK,int degreeOfParallelism) {
        DataSliceOperationResult dataSliceOperationResult = new DataSliceOperationResult();
        int totalResultConceptionEntitiesCount = 0;

        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put(CoreRealmOperationUtil.RealmGlobalUID,DataSlicePropertyType.STRING);
        List<String> attributeNamesList = new ArrayList<>();

        if(containsAttributesKinds != null && containsAttributesKinds.size() >0){
            for(AttributeKind currentAttributeKind :containsAttributesKinds){
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
                    case TIMESTAMP:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.TIMESTAMP);
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
                    case DATE:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.DATE);
                        break;
                    case TIME:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.TIME);
                        break;
                    case DATETIME:
                        dataSlicePropertyMap.put(currentAttributeKindName,DataSlicePropertyType.TIMESTAMP);
                        break;
                }
                attributeNamesList.add(currentAttributeKindName);
            }
        }

        // use this logic to avoid create ready exist ignite nodes has same name
        IgniteConfiguration igniteConfiguration= new IgniteConfiguration();
        igniteConfiguration.setClientMode(true);
        igniteConfiguration.setIgniteInstanceName("DataSliceCreateThread_"+UUID.randomUUID());
        Ignite invokerIgnite =Ignition.start(igniteConfiguration);
        try {
            DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
            if(targetDataSlice == null) {
                List<String> pkList = new ArrayList<>();
                pkList.add(CoreRealmOperationUtil.RealmGlobalUID);
                dataService.createGridDataSlice(dataSliceName,dataSliceGroup,dataSlicePropertyMap,pkList);
            }
        } catch (DataSliceExistException e) {
            throw new RuntimeException(e);
        } catch (DataSlicePropertiesStructureException e) {
            throw new RuntimeException(e);
        }
        invokerIgnite.close();

        try{
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

        DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
        DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
        int successDataCount = dataSliceMetaInfo.getPrimaryDataCount() + dataSliceMetaInfo.getBackupDataCount();
        dataSliceOperationResult.setSuccessItemsCount(successDataCount);
        dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-successDataCount);
        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Sync Inner DataKind Entities To DataSlice Operation");
        return dataSliceOperationResult;
    }

    public static DataSliceOperationResult loadInnerDataKindEntitiesToDataSlice(DataService dataService,String innerDataKindName, List<AttributeKind> containsAttributesKinds,QueryParameters queryParameters, String dataSliceName,boolean useConceptionEntityUIDAsPK,int degreeOfParallelism) {
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

        DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
        DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
        int successDataCount = dataSliceMetaInfo.getPrimaryDataCount() + dataSliceMetaInfo.getBackupDataCount();
        dataSliceOperationResult.setSuccessItemsCount(successDataCount);
        dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-successDataCount);

        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Load ConceptionKind Entities To DataSlice Operation");
        return dataSliceOperationResult;
    }

    public static DataSliceOperationResult loadRelationKindEntitiesToDataSlice(DataService dataService,String relationKindName, List<String> attributeNamesList,QueryParameters queryParameters, String dataSliceName,boolean useConceptionEntityUIDAsPK,int degreeOfParallelism) {
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

        DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
        DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
        int successDataCount = dataSliceMetaInfo.getPrimaryDataCount() + dataSliceMetaInfo.getBackupDataCount();
        dataSliceOperationResult.setSuccessItemsCount(successDataCount);
        dataSliceOperationResult.setFailItemsCount(totalResultConceptionEntitiesCount-successDataCount);

        dataSliceOperationResult.finishOperation();
        dataSliceOperationResult.setOperationSummary("Load RelationKind Entities To DataSlice Operation");
        return dataSliceOperationResult;
    }

    public static DataSliceOperationResult refreshDataSliceAndLoadDataFromConceptionKind(String dataSliceGroupName,String dataSliceName,Map<String, DataSlicePropertyType> dataSlicePropertyMap,String conceptionKindName,QueryParameters queryParameters,int degreeOfParallelism){
        if(dataSlicePropertyMap == null ||dataSlicePropertyMap.size() == 0){
            return null;
        }
        Set<String> propertyNameSet = dataSlicePropertyMap.keySet();
        List<String> conceptionKindPropertiesList = new ArrayList<>();
        conceptionKindPropertiesList.addAll(propertyNameSet);


        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(DataService dataService = targetComputeGrid.getDataService()){
            DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
            if(targetDataSlice != null){
                dataService.eraseDataSlice(dataSliceName);
            }
            dataSlicePropertyMap.put(CoreRealmOperationUtil.RealmGlobalUID,DataSlicePropertyType.STRING);
            if(targetDataSlice == null){
                List<String> pkList = new ArrayList<>();
                pkList.add(CoreRealmOperationUtil.RealmGlobalUID);
                dataService.createGridDataSlice(dataSliceName,dataSliceGroupName+"_CONCEPTION",dataSlicePropertyMap,pkList);
            }
        } catch (ComputeGridException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return loadConceptionKindEntitiesToDataSlice(conceptionKindName,conceptionKindPropertiesList,queryParameters,dataSliceName,true,degreeOfParallelism);
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

            ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
            try(DataService dataService = targetComputeGrid.getDataService()){
                DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
                DataSliceOperationResult dataSliceOperationResult = targetDataSlice.addDataRecords(this.sliceDataProperties,sliceDataRowsDataList);
                System.out.println("--------------------------------------");
                System.out.println("Execution result of : "+"DataSliceInsertDataThread_"+threadId);
                System.out.println(dataSliceOperationResult.getOperationSummary());
                System.out.println(dataSliceOperationResult.getStartTime());
                System.out.println(dataSliceOperationResult.getFinishTime());
                System.out.println(dataSliceOperationResult.getSuccessItemsCount());
                System.out.println(dataSliceOperationResult.getFailItemsCount());
                System.out.println("--------------------------------------");
            } catch (ComputeGridException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            invokerIgnite.close();
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
            Ignite invokerIgnite =Ignition.start(igniteConfiguration);

            ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
            try(DataService dataService = targetComputeGrid.getDataService()){
                DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
                DataSliceOperationResult dataSliceOperationResult = targetDataSlice.addDataRecords(this.sliceDataProperties,sliceDataRowsDataList);
                System.out.println("--------------------------------------");
                System.out.println("Execution result of : "+"DataSliceInsertRelationThread_"+threadId);
                System.out.println(dataSliceOperationResult.getOperationSummary());
                System.out.println(dataSliceOperationResult.getStartTime());
                System.out.println(dataSliceOperationResult.getFinishTime());
                System.out.println(dataSliceOperationResult.getSuccessItemsCount());
                System.out.println(dataSliceOperationResult.getFailItemsCount());
                System.out.println("--------------------------------------");
            } catch (ComputeGridException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            invokerIgnite.close();
        }
    }

    private static class DataSliceInsertConceptionEntitiesThread implements Runnable{
        private String dataSliceName;
        private List<String> sliceDataProperties;
        private List<ConceptionEntityValue> sliceDataRows;
        private int threadId;
        private Map<String,String> sliceAndKindAttributeMapping;
        private String conceptionEntityUIDNameAlias;
        private List<DataSliceOperationResult> dataSliceOperationResultsList;
        public DataSliceInsertConceptionEntitiesThread(int threadId,String dataSliceName, Map<String,String> sliceAndKindAttributeMapping,
                                                       String conceptionEntityUIDNameAlias,List<ConceptionEntityValue> sliceDataRows,
                                                       List<DataSliceOperationResult> dataSliceOperationResultsList){
            this.dataSliceName = dataSliceName;
            this.sliceAndKindAttributeMapping = sliceAndKindAttributeMapping;
            this.conceptionEntityUIDNameAlias = conceptionEntityUIDNameAlias;
            this.sliceDataProperties = new ArrayList<>(sliceAndKindAttributeMapping.keySet());
            this.sliceDataRows = sliceDataRows;
            this.dataSliceOperationResultsList = dataSliceOperationResultsList;
            this.threadId = threadId;
        }

        @Override
        public void run() {
            List<Map<String,Object>> sliceDataRowsDataList = new ArrayList<>();
            for(ConceptionEntityValue currentConceptionEntityValue :this.sliceDataRows){
                Map<String,Object> conceptionEntityDataMap = currentConceptionEntityValue.getEntityAttributesValue();
                Map<String,Object> currentDataMap = new HashMap<>();
                for(String currentSliceDataPropertyName : this.sliceDataProperties ){
                    String conceptionEntityAttributeName = sliceAndKindAttributeMapping.get(currentSliceDataPropertyName);
                    if(conceptionEntityAttributeName.equals(conceptionEntityUIDNameAlias)){
                        currentDataMap.put(currentSliceDataPropertyName,currentConceptionEntityValue.getConceptionEntityUID());
                    }else{
                        currentDataMap.put(currentSliceDataPropertyName,conceptionEntityDataMap.get(conceptionEntityAttributeName));
                    }
                }
                sliceDataRowsDataList.add(currentDataMap);
            }

            IgniteConfiguration igniteConfiguration= new IgniteConfiguration();
            igniteConfiguration.setClientMode(true);
            igniteConfiguration.setIgniteInstanceName("DataSliceInsertConceptionEntitiesThread_"+threadId);
            Ignite invokerIgnite =Ignition.start(igniteConfiguration);

            ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
            try(DataService dataService = targetComputeGrid.getDataService()){
                DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
                DataSliceOperationResult dataSliceOperationResult = targetDataSlice.addDataRecords(this.sliceDataProperties,sliceDataRowsDataList);
                this.dataSliceOperationResultsList.add(dataSliceOperationResult);
                System.out.println("--------------------------------------");
                System.out.println("Execution result of : "+"DataSliceInsertConceptionEntitiesThread_"+threadId);
                System.out.println(dataSliceOperationResult.getOperationSummary());
                System.out.println(dataSliceOperationResult.getStartTime());
                System.out.println(dataSliceOperationResult.getFinishTime());
                System.out.println(dataSliceOperationResult.getSuccessItemsCount());
                System.out.println(dataSliceOperationResult.getFailItemsCount());
                System.out.println("--------------------------------------");
            } catch (ComputeGridException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            invokerIgnite.close();
        }
    }
}
