package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

public class DataSlice {

    private Ignite invokerIgnite;
    private IgniteCache<?, ?> cache;

    public DataSlice(Ignite invokerIgnite, IgniteCache igniteCache){
        this.invokerIgnite=invokerIgnite;
        this.cache = igniteCache;
    }

    public void addData(){



        CacheConfiguration ccfg = cache.getConfiguration(CacheConfiguration.class);
        Collection<QueryEntity> entities = ccfg.getQueryEntities();

        if(entities != null && entities.size()>0){
            QueryEntity mainQueryEntity = entities.iterator().next();

            LinkedHashMap<String,String> propertiesMap = mainQueryEntity.getFields();

            System.out.println(propertiesMap);
            Set<String> keyField = mainQueryEntity.getKeyFields();
            System.out.println(keyField);

        }









        //this.cache.metrics().getTxKeyCollisions();


        //this.cache.metrics().getValueType();
        //this.cache.metrics().


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
