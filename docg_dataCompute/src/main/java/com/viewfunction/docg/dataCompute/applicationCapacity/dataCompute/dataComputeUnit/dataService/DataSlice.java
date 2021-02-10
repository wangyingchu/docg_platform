package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.configuration.CacheConfiguration;

public class DataSlice {

    private Ignite invokerIgnite;
    private IgniteCache<?, ?> cache;

    public DataSlice(Ignite invokerIgnite, IgniteCache igniteCache){
        this.invokerIgnite=invokerIgnite;
        this.cache = igniteCache;
    }

    public DataSliceMetaInfo getDataSliceMetaInfo(){
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
