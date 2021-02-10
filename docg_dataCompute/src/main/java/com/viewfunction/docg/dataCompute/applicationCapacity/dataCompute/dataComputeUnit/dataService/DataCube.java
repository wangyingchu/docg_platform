package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.Map;

public class DataCube {

    private Ignite invokerIgnite;
    private IgniteCache<?, ?> cache;

    public DataCube(Ignite invokerIgnite, IgniteCache igniteCache){
        this.invokerIgnite=invokerIgnite;
        this.cache = igniteCache;
    }

    public DataSlice createDataSlice(String dataSliceName,String primaryKey, Map<String, Object> sliceDataFiledMap){


        String createDataSliceSentence =  "CREATE TABLE "+dataSliceName+" (id LONG PRIMARY KEY, name VARCHAR) WITH \"template=replicated\"";


        //cache.query(new SqlFieldsQuery(
        //        "CREATE TABLE city (id LONG PRIMARY KEY, name VARCHAR) WITH \"template=replicated\"")).getAll();


        this.cache.query(new SqlFieldsQuery(createDataSliceSentence)).getAll();




        return null;
    }

    public DataSliceMetaInfo getDataCubeMetaInfo(){
        CacheConfiguration currentCacheConfig=this.cache.getConfiguration(CacheConfiguration.class);
        DataSliceMetaInfo dataSliceMetaInfo =new DataSliceMetaInfo();
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
}
