package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.beust.jcommander.JCommander;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.util.UnitOperationResult;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.configuration.CacheConfiguration;

import java.util.Collection;
import java.util.Iterator;

public class LsdsCommandProcessor  implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public LsdsCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        UnitOperationResult unitOperationResult = UnitIgniteOperationUtil.isGridActive(this.nodeIgnite);
        if(!unitOperationResult.getResult()){
            System.out.println(unitOperationResult.getResultMessage());
            return;
        }

        UnitdsCommandOptions unitdsCommandOptions = new UnitdsCommandOptions();
        JCommander.newBuilder().addObject(unitdsCommandOptions).build().parse(commandOptions);
        String startSliceNameFilter = unitdsCommandOptions.getStartSliceName();

        Collection<String> cacheNameCollection=this.nodeIgnite.cacheNames();

        StringBuffer lsDataStoreMessageStringBuffer=new StringBuffer();
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("Grid total data store number: "+cacheNameCollection.size());
        lsDataStoreMessageStringBuffer.append("\n\r");

        Iterator<String> cacheNameIterator= cacheNameCollection.iterator();
        int currentItem = 1;
        while(cacheNameIterator.hasNext()){
            String currentCacheName=cacheNameIterator.next();
            boolean showCurrentSlice = false;
            if(startSliceNameFilter == null){
                showCurrentSlice = true;
            }else if(startSliceNameFilter != null && currentCacheName.toUpperCase().startsWith(startSliceNameFilter.toUpperCase())){
                showCurrentSlice = true;
            }

            if(showCurrentSlice){
                IgniteCache currentCache=this.nodeIgnite.cache(currentCacheName);
                CacheMetrics currentCacheMetrics=currentCache.metrics();
                CacheConfiguration currentCacheConfig=(CacheConfiguration)currentCache.getConfiguration(CacheConfiguration.class);

                lsDataStoreMessageStringBuffer.append("-------------------------------------------------------------");
                lsDataStoreMessageStringBuffer.append("\n\r");
                lsDataStoreMessageStringBuffer.append(" "+currentItem+". " + currentCacheMetrics.name()+" - "+currentCacheConfig.getSqlSchema());
                lsDataStoreMessageStringBuffer.append("\n\r");
                lsDataStoreMessageStringBuffer.append(" Local Data:  " + currentCache.localSize(CachePeekMode.PRIMARY)+"(P) | "+currentCache.localSize(CachePeekMode.BACKUP)+"(B) | "+currentCache.localSize(CachePeekMode.ALL)+"(T)");
                lsDataStoreMessageStringBuffer.append("\n\r");
                lsDataStoreMessageStringBuffer.append(" Slice Data:  " + currentCache.size(CachePeekMode.PRIMARY)+"(P) | "+currentCache.size(CachePeekMode.BACKUP)+"(B) | "+currentCache.size(CachePeekMode.ALL)+"(T)");
                lsDataStoreMessageStringBuffer.append("\n\r");
                currentItem ++;
            }
        }

        lsDataStoreMessageStringBuffer.append("-------------------------------------------------------------");
        lsDataStoreMessageStringBuffer.append("\n\r");
        lsDataStoreMessageStringBuffer.append("================================================================");
        System.out.println(lsDataStoreMessageStringBuffer);
    }
}
