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

    public DataStoreMetaInfo getDataCubeMetaInfo(){
        CacheConfiguration currentCacheConfig=this.cache.getConfiguration(CacheConfiguration.class);
        DataStoreMetaInfo dataStoreMetaInfo=new DataStoreMetaInfo();
        dataStoreMetaInfo.setPrimaryDataCount(this.cache.size(CachePeekMode.PRIMARY));
        dataStoreMetaInfo.setBackupDataCount(this.cache.size(CachePeekMode.BACKUP));
        dataStoreMetaInfo.setTotalDataCount(this.cache.size(CachePeekMode.ALL));
        dataStoreMetaInfo.setStoreBackupNumber(currentCacheConfig.getBackups());
        CacheMode currentStoreCacheMode=currentCacheConfig.getCacheMode();
        String dataStoreMode="UNKNOWN";
        switch(currentStoreCacheMode){
            case PARTITIONED:dataStoreMode="Grid";break;
            case LOCAL:dataStoreMode="Unit Local";break;
            case REPLICATED:dataStoreMode="Grid PerUnit";break;
        }
        dataStoreMetaInfo.setDataStoreMode(dataStoreMode);
        dataStoreMetaInfo.setAtomicityMode(""+currentCacheConfig.getAtomicityMode());
        dataStoreMetaInfo.setSqlSchema(""+currentCacheConfig.getSqlSchema());
        dataStoreMetaInfo.setKeyClass(currentCacheConfig.getKeyType());
        dataStoreMetaInfo.setValueClass(currentCacheConfig.getValueType());
        return dataStoreMetaInfo;
    }

}
