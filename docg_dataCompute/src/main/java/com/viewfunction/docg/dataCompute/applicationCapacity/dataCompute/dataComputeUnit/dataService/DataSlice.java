package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSlicePropertiesStructureException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.*;

public class DataSlice {

    private Ignite invokerIgnite;
    private IgniteCache<?, ?> cache;

    public DataSlice(Ignite invokerIgnite, IgniteCache igniteCache){
        this.invokerIgnite=invokerIgnite;
        this.cache = igniteCache;
    }

    public void addDataRecord(Map<String,Object> dataPropertiesValue) throws DataSlicePropertiesStructureException {
        Map<String,String> slicePropertiesMap = getDataSlicePropertiesInfo();
        Set<String> propertiesSet = slicePropertiesMap.keySet();

        Set<String> dataPropertyNameSet = dataPropertiesValue.keySet();

        for(String currentPropertyName:dataPropertyNameSet){
            if(!propertiesSet.contains(currentPropertyName)){
                throw new DataSlicePropertiesStructureException();
            }
        }

        String[] arr = propertiesSet.stream().toArray(n -> new String[n]);









        for(String currentPropertyName:dataPropertyNameSet){

        }


        String sliceName = this.cache.getName();
        //SqlFieldsQuery qry = new SqlFieldsQuery("INSERT INTO city (id, name) VALUES (?, ?)");



    }

    public void addDataRecords(List<Map<String,Object>> dataPropertiesValueList){
        CacheConfiguration ccfg = cache.getConfiguration(CacheConfiguration.class);
        Collection<QueryEntity> entities = ccfg.getQueryEntities();

        if(entities != null && entities.size()>0){
            QueryEntity mainQueryEntity = entities.iterator().next();

            LinkedHashMap<String,String> propertiesMap = mainQueryEntity.getFields();

            System.out.println(propertiesMap);
            Set<String> keyField = mainQueryEntity.getKeyFields();
            System.out.println(keyField);
        }
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
