package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.configuration.CacheConfiguration;

public class DataCube {

    private Ignite invokerIgnite;
    private IgniteCache<?, ?> cache;

    public DataCube(Ignite invokerIgnite, IgniteCache igniteCache){
        this.invokerIgnite=invokerIgnite;
        this.cache = igniteCache;
    }

    public DataSlice createDataSlice(String dataSliceName){
        return null;
    }

    public DataStorageMetaInfo getDataCubeMetaInfo(){
        CacheConfiguration currentCacheConfig=this.cache.getConfiguration(CacheConfiguration.class);
        DataStorageMetaInfo dataStorageMetaInfo =new DataStorageMetaInfo();
        dataStorageMetaInfo.setStorageName(currentCacheConfig.getName());
        dataStorageMetaInfo.setPrimaryDataCount(this.cache.size(CachePeekMode.PRIMARY));
        dataStorageMetaInfo.setBackupDataCount(this.cache.size(CachePeekMode.BACKUP));
        dataStorageMetaInfo.setTotalDataCount(this.cache.size(CachePeekMode.ALL));
        dataStorageMetaInfo.setStoreBackupNumber(currentCacheConfig.getBackups());
        CacheMode currentStoreCacheMode=currentCacheConfig.getCacheMode();
        String dataStoreMode="UNKNOWN";
        switch(currentStoreCacheMode){
            case PARTITIONED:dataStoreMode="Grid";break;
            case LOCAL:dataStoreMode="Unit Local";break;
            case REPLICATED:dataStoreMode="Grid PerUnit";break;
        }
        dataStorageMetaInfo.setDataStoreMode(dataStoreMode);
        dataStorageMetaInfo.setAtomicityMode(""+currentCacheConfig.getAtomicityMode());
        dataStorageMetaInfo.setSqlSchema(""+currentCacheConfig.getSqlSchema());
        dataStorageMetaInfo.setKeyClass(currentCacheConfig.getKeyType());
        dataStorageMetaInfo.setValueClass(currentCacheConfig.getValueType());
        return dataStorageMetaInfo;
    }
}
