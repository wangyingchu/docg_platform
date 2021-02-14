package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSliceDataException;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSlicePropertiesStructureException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.*;

public class DataSlice {

    private Ignite invokerIgnite;
    private IgniteCache<?, ?> cache;

    public DataSlice(Ignite invokerIgnite, IgniteCache igniteCache){
        this.invokerIgnite=invokerIgnite;
        this.cache = igniteCache;
    }

    public boolean addDataRecord(Map<String,Object> dataPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException {
        Map<String,String> slicePropertiesMap = getDataSlicePropertiesInfo();
        Set<String> slicePropertiesNameSet = slicePropertiesMap.keySet();

        Set<String> dataPropertyNameSet = dataPropertiesValue.keySet();
        for(String currentPropertyName:dataPropertyNameSet){
            if(!slicePropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new DataSlicePropertiesStructureException();
            }
        }

        String[] dataPropertiesNameArray = dataPropertyNameSet.stream().toArray(n -> new String[n]);
        StringBuffer propertiesNameSb = new StringBuffer();
        StringBuffer propertiesValuePlaceHolderSb = new StringBuffer();
        propertiesNameSb.append("(");
        propertiesValuePlaceHolderSb.append("(");

        Object[] propertiesValueArray = new Object[dataPropertyNameSet.size()];

        for(int i = 0; i< dataPropertiesNameArray.length; i++){
            String currentDataPropertyName = dataPropertiesNameArray[i];
            // get dataType for property value validate
            //String dataType = slicePropertiesMap.get(currentDataPropertyName.toUpperCase());
            propertiesNameSb.append(currentDataPropertyName);
            propertiesValuePlaceHolderSb.append("?");

            if(i < dataPropertiesNameArray.length-1){
                propertiesNameSb.append(",");
                propertiesValuePlaceHolderSb.append(",");
            }
            propertiesValueArray[i] = dataPropertiesValue.get(currentDataPropertyName);
        }
        propertiesNameSb.append(")");
        propertiesValuePlaceHolderSb.append(")");

        String sliceName = this.cache.getName();

        String sqlFieldsQuerySQL = "INSERT INTO "+sliceName+" "+propertiesNameSb.toString() +" VALUES "+propertiesValuePlaceHolderSb.toString();
        SqlFieldsQuery qry = new SqlFieldsQuery(sqlFieldsQuerySQL);

        try {
            List<List<?>> cursor = this.cache.query(qry.setArgs(propertiesValueArray)).getAll();
            for (List next : cursor) {
                for (Object item : next) {
                    if(item instanceof Long){
                        Long result = (Long) item;
                        if(result == 1){
                            return true;
                        }
                    }
                }
            }
        }catch (javax.cache.CacheException e){
            DataSliceDataException dataSliceDataException = new DataSliceDataException();
            dataSliceDataException.addSuppressed(e);
            throw dataSliceDataException;
        }
        return false;
    }

    public DataSliceOperationResult addDataRecords(List<String> propertiesNameList,List<Map<String,Object>> dataPropertiesValueList) throws DataSlicePropertiesStructureException{
        Map<String,String> slicePropertiesMap = getDataSlicePropertiesInfo();
        Set<String> slicePropertiesNameSet = slicePropertiesMap.keySet();

        for(String currentPropertyName:propertiesNameList){
            if(!slicePropertiesNameSet.contains(currentPropertyName.toUpperCase())){
                throw new DataSlicePropertiesStructureException();
            }
        }

        DataSliceOperationResult dataSliceOperationResult = new DataSliceOperationResult();

        StringBuffer propertiesNameSb = new StringBuffer();
        StringBuffer propertiesValuePlaceHolderSb = new StringBuffer();
        propertiesNameSb.append("(");
        propertiesValuePlaceHolderSb.append("(");

        for(int i = 0; i< propertiesNameList.size(); i++){
            String currentDataPropertyName = propertiesNameList.get(i);
            // get dataType for property value validate
            //String dataType = slicePropertiesMap.get(currentDataPropertyName.toUpperCase());
            propertiesNameSb.append(currentDataPropertyName);
            propertiesValuePlaceHolderSb.append("?");

            if(i < propertiesNameList.size() - 1){
                propertiesNameSb.append(",");
                propertiesValuePlaceHolderSb.append(",");
            }
        }
        propertiesNameSb.append(")");
        propertiesValuePlaceHolderSb.append(")");

        String sliceName = this.cache.getName();

        String sqlFieldsQuerySQL = "INSERT INTO "+sliceName+" "+propertiesNameSb.toString() +" VALUES "+propertiesValuePlaceHolderSb.toString();
        SqlFieldsQuery qry = new SqlFieldsQuery(sqlFieldsQuerySQL);

        for(Map<String,Object> currentDataValueRow :dataPropertiesValueList){
            Object[] propertiesValueArray = new Object[propertiesNameList.size()];
            for(int i =0;i<propertiesNameList.size();i++){
                String currentPropertyName = propertiesNameList.get(i);
                propertiesValueArray[i] = currentDataValueRow.get(currentPropertyName);
            }
            try {
                List<List<?>> cursor = this.cache.query(qry.setArgs(propertiesValueArray)).getAll();
                if(cursor.get(0).get(0) instanceof Long){
                    if((Long)cursor.get(0).get(0) == 1){
                        dataSliceOperationResult.increaseSuccessCount();
                    }
                }
            }catch (javax.cache.CacheException e){
                e.printStackTrace();
                dataSliceOperationResult.increaseFailCount();
            }
        }
        dataSliceOperationResult.setOperationSummary("DataSlice addDataRecords Operation");

        dataSliceOperationResult.finishOperation();
        return dataSliceOperationResult;
    }

    public DataSliceMetaInfo getDataSliceMetaInfo(){
        CacheConfiguration currentCacheConfig = this.cache.getConfiguration(CacheConfiguration.class);
        DataSliceMetaInfo dataSliceMetaInfo = new DataSliceMetaInfo();
        dataSliceMetaInfo.setDataSliceName(currentCacheConfig.getName());
        dataSliceMetaInfo.setPrimaryDataCount(this.cache.size(CachePeekMode.PRIMARY));
        dataSliceMetaInfo.setBackupDataCount(this.cache.size(CachePeekMode.BACKUP));
        dataSliceMetaInfo.setTotalDataCount(this.cache.size(CachePeekMode.ALL));
        dataSliceMetaInfo.setStoreBackupNumber(currentCacheConfig.getBackups());
        CacheMode currentStoreCacheMode=currentCacheConfig.getCacheMode();
        String dataStoreMode="UNKNOWN";
        switch(currentStoreCacheMode){
            case PARTITIONED:dataStoreMode="Grid";break;
            case LOCAL:dataStoreMode="Unit Local";break;
            case REPLICATED:dataStoreMode="Grid PerUnit";break;
        }
        dataSliceMetaInfo.setDataStoreMode(dataStoreMode);
        dataSliceMetaInfo.setAtomicityMode(""+currentCacheConfig.getAtomicityMode());
        dataSliceMetaInfo.setSliceGroupName(""+currentCacheConfig.getSqlSchema());
        dataSliceMetaInfo.setKeyClass(currentCacheConfig.getKeyType());
        dataSliceMetaInfo.setValueClass(currentCacheConfig.getValueType());
        return dataSliceMetaInfo;
    }

    private Set<String> getDataSlicePrimaryKeysInfo(){
        CacheConfiguration ccfg = cache.getConfiguration(CacheConfiguration.class);
        Collection<QueryEntity> entities = ccfg.getQueryEntities();
        if(entities != null && entities.size()>0){
            QueryEntity mainQueryEntity = entities.iterator().next();
            Set<String> keysField = mainQueryEntity.getKeyFields();
            return keysField;
        }
        return null;
    }

    private Map<String,String> getDataSlicePropertiesInfo(){
        CacheConfiguration ccfg = cache.getConfiguration(CacheConfiguration.class);
        Collection<QueryEntity> entities = ccfg.getQueryEntities();
        if(entities != null && entities.size()>0){
            QueryEntity mainQueryEntity = entities.iterator().next();
            LinkedHashMap<String,String> propertiesMap = mainQueryEntity.getFields();
            return propertiesMap;
        }
        return null;
    }
}
