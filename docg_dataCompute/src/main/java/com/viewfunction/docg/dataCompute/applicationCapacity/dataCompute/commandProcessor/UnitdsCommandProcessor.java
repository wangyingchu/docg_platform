package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.OperationResultVO;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.ResourceNodeIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.Collection;
import java.util.Iterator;

public class UnitdsCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public UnitdsCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        OperationResultVO operationResultVO= ResourceNodeIgniteOperationUtil.isGridActive(this.nodeIgnite);
        if(!operationResultVO.getResult()){
            System.out.println(operationResultVO.getResultMessage());
            return;
        }

        Collection<String> cacheNameCollection=this.nodeIgnite.cacheNames();

        StringBuffer lsDataStoreMessageStringBuffer=new StringBuffer();
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("Grid total data store number: "+cacheNameCollection.size());
        lsDataStoreMessageStringBuffer.append("\n\r");

        Iterator<String> cacheNameIterator= cacheNameCollection.iterator();
        while(cacheNameIterator.hasNext()){
            String currentCacheName=cacheNameIterator.next();
            IgniteCache currentCache=this.nodeIgnite.cache(currentCacheName);
            CacheMetrics currentCacheMetrics=currentCache.metrics();
            CacheConfiguration currentCacheConfig=(CacheConfiguration)currentCache.getConfiguration(CacheConfiguration.class);
            CacheMode currentStoreCacheMode=currentCacheConfig.getCacheMode();
            String dataStoreMode="UNKNOWN";
            switch(currentStoreCacheMode){
                case PARTITIONED:dataStoreMode="Grid Singleton";break;
                case LOCAL:dataStoreMode="Unit Local";break;
                case REPLICATED:dataStoreMode="Grid PerNode";break;
            }
            lsDataStoreMessageStringBuffer.append("-------------------------------------------------------------");
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Data Store Name:          " + currentCacheMetrics.name());
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Data Key Type:            " + currentCacheConfig.getKeyType().getName());
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Data Value Type:          " + currentCacheConfig.getValueType().getName());
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Local Data Primary Count: " + currentCache.localSize(CachePeekMode.PRIMARY));
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Local Data Backup Count:  " + currentCache.localSize(CachePeekMode.BACKUP));
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Local Data Total Count:   " + currentCache.localSize(CachePeekMode.ALL));
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Store Data Primary Count: " + currentCache.size(CachePeekMode.PRIMARY));
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Store Data Backup Count:  " + currentCache.size(CachePeekMode.BACKUP));
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Store Data Total Count:   " + currentCache.size(CachePeekMode.ALL));
            lsDataStoreMessageStringBuffer.append("\n\r");
            /*
            lsDataStoreMessageStringBuffer.append(currentCache.size(CachePeekMode.NEAR)+"\n\r");
            lsDataStoreMessageStringBuffer.append(currentCache.size(CachePeekMode.OFFHEAP)+"\n\r");
            lsDataStoreMessageStringBuffer.append(currentCache.size(CachePeekMode.ONHEAP)+"\n\r");
            lsDataStoreMessageStringBuffer.append(currentCache.size(CachePeekMode.SWAP)+"\n\r");
            */
            lsDataStoreMessageStringBuffer.append("Data Store Mode:          " + dataStoreMode);
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("Atomicity Mode:           " + currentCacheConfig.getAtomicityMode());
            lsDataStoreMessageStringBuffer.append("\n\r");
            if(currentStoreCacheMode.equals(CacheMode.PARTITIONED)) {
                lsDataStoreMessageStringBuffer.append("Backups Number:           " + currentCacheConfig.getBackups());
                lsDataStoreMessageStringBuffer.append("\n\r");
            }
            lsDataStoreMessageStringBuffer.append("Data Region:              " +  currentCacheConfig.getDataRegionName());
            lsDataStoreMessageStringBuffer.append("\n\r");
            lsDataStoreMessageStringBuffer.append("SQL Schema:               " +  currentCacheConfig.getSqlSchema());
            lsDataStoreMessageStringBuffer.append("\n\r");
        }
        lsDataStoreMessageStringBuffer.append("-------------------------------------------------------------");
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        System.out.println(lsDataStoreMessageStringBuffer.toString());
    }
}