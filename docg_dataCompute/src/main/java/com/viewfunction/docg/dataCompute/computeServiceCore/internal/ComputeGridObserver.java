package com.viewfunction.docg.dataCompute.computeServiceCore.internal;

import com.viewfunction.docg.dataCompute.computeServiceCore.payload.ComputeGridRealtimeStatisticsInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataComputeUnitMetaInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.config.DataComputeConfigurationHandler;

import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.ClientCacheConfiguration;
import org.apache.ignite.client.ClientClusterGroup;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.ClientConfiguration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class ComputeGridObserver implements AutoCloseable{

    private String observedUnitIPsStr= DataComputeConfigurationHandler.getConfigPropertyValue("observedUnitIPs");
    private IgniteClient igniteClient;

    private ComputeGridObserver(){}

    public void openObserveSession(){
        String[] unitIPArray = observedUnitIPsStr.split(",");
        ClientConfiguration cfg = new ClientConfiguration().setAddresses(unitIPArray).setPartitionAwarenessEnabled(true);
        this.igniteClient = Ignition.startClient(cfg);
    }

    public void closeObserveSession(){
        if(this.igniteClient != null){
            this.igniteClient.close();
        }
    }

    public static ComputeGridObserver getObserverInstance(){
        ComputeGridObserver computeGridObserver = new ComputeGridObserver();
        computeGridObserver.openObserveSession();
        return computeGridObserver;
    }

    @Override
    public void close() throws Exception {
        closeObserveSession();
    }

    public Set<DataComputeUnitMetaInfo> listDataComputeUnit(){
        Set<DataComputeUnitMetaInfo> dataComputeUnitMetaInfoSet = new HashSet<>();
        Collection<ClusterNode> nodesCollection = this.igniteClient.cluster().forServers().nodes();
        if(nodesCollection != null){
            for(ClusterNode currentClusterNode:nodesCollection){
                String _UnitId = currentClusterNode.id().toString();
                String _UnitType = currentClusterNode.attribute(DataComputeConfigurationHandler.getConfigPropertyValue("unitRoleAttributeName"));
                Collection<String> _HostName = currentClusterNode.hostNames();
                Collection<String> _IPAddress = currentClusterNode.addresses();
                boolean isClientUnit = currentClusterNode.isClient();
                DataComputeUnitMetaInfo currentDataComputeUnitMetaInfo = new DataComputeUnitMetaInfo(_UnitId,_UnitType,_HostName,_IPAddress,isClientUnit);
                dataComputeUnitMetaInfoSet.add(currentDataComputeUnitMetaInfo);
            }
        }
        return dataComputeUnitMetaInfoSet;
    }

    public Set<DataSliceMetaInfo> listDataSlice(){
        Set<DataSliceMetaInfo> dataSliceMetaInfoSet = new HashSet<>();
        Collection<String> cacheNameCollection = this.igniteClient.cacheNames();
        if(cacheNameCollection != null){
            for(String currentCacheName : cacheNameCollection){
                ClientCache currentCache = this.igniteClient.cache(currentCacheName);
                ClientCacheConfiguration clientCacheConfiguration = currentCache.getConfiguration();
                DataSliceMetaInfo dataSliceMetaInfo = new DataSliceMetaInfo();
                dataSliceMetaInfo.setDataSliceName(clientCacheConfiguration.getName());
                dataSliceMetaInfo.setPrimaryDataCount(currentCache.size(CachePeekMode.PRIMARY));
                dataSliceMetaInfo.setBackupDataCount(currentCache.size(CachePeekMode.BACKUP));
                dataSliceMetaInfo.setTotalDataCount(currentCache.size(CachePeekMode.ALL));
                dataSliceMetaInfo.setStoreBackupNumber(clientCacheConfiguration.getBackups());
                CacheMode currentStoreCacheMode=clientCacheConfiguration.getCacheMode();
                String dataStoreMode="UNKNOWN";
                switch(currentStoreCacheMode){
                    case PARTITIONED:dataStoreMode="Grid";break;
                    case REPLICATED:dataStoreMode="Grid PerUnit";break;
                }
                dataSliceMetaInfo.setDataStoreMode(dataStoreMode);
                dataSliceMetaInfo.setAtomicityMode(""+clientCacheConfiguration.getAtomicityMode());
                dataSliceMetaInfo.setSliceGroupName(""+clientCacheConfiguration.getSqlSchema());
                dataSliceMetaInfoSet.add(dataSliceMetaInfo);
            }
        }
        return dataSliceMetaInfoSet;
    }

    public ComputeGridRealtimeStatisticsInfo getGridRealtimeStatisticsInfo(){
        Map<String,Object> currentIgniteMetricsValueMap = getCurrentIgniteMetricsValueMap();
        long maxAvailableMem = Long.parseLong(currentIgniteMetricsValueMap.get("io.dataregion.Default_DataStore_Region.MaxSize").toString());
        long usedMem = Long.parseLong(currentIgniteMetricsValueMap.get("io.dataregion.Default_DataStore_Region.TotalUsedSize").toString());
        long assignedMem = Long.parseLong(currentIgniteMetricsValueMap.get("io.dataregion.Default_DataStore_Region.OffHeapSize").toString());
        int upTime = Integer.parseInt(currentIgniteMetricsValueMap.get("ignite.uptime").toString());
        long startTime = Long.parseLong(currentIgniteMetricsValueMap.get("ignite.startTimestamp").toString());

        int executorService = Integer.parseInt(currentIgniteMetricsValueMap.get("ignite.executorServiceFormatted").toString());


        ClientClusterGroup computeUnitCluster = this.igniteClient.cluster().forServers();

        ComputeGridRealtimeStatisticsInfo computeGridRealtimeStatisticsInfo = new ComputeGridRealtimeStatisticsInfo();
        computeGridRealtimeStatisticsInfo.setAssignedMemoryInMB(assignedMem/1024/1024);
        computeGridRealtimeStatisticsInfo.setMaxAvailableMemoryInMB(maxAvailableMem/1024/1024);
        computeGridRealtimeStatisticsInfo.setUsedMemoryInMB(usedMem/1024/1024);
        computeGridRealtimeStatisticsInfo.setYoungestUnitId(computeUnitCluster.forYoungest().node().id().toString());
        computeGridRealtimeStatisticsInfo.setOldestUnitId(computeUnitCluster.forOldest().node().id().toString());
        computeGridRealtimeStatisticsInfo.setGridUpTimeInMinute(upTime/1000/60);
        Instant instant = Instant.ofEpochMilli(startTime);
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        computeGridRealtimeStatisticsInfo.setGridStartTime(localDateTime);
        computeGridRealtimeStatisticsInfo.setDataComputeUnitsAmount(computeUnitCluster.nodes().size());
        computeGridRealtimeStatisticsInfo.setAvailableCPUCores(executorService);



        /*
        targetComputeGridRealtimeStatisticsInfo.setCurrentCPULoadPercentage(metrics.getCurrentCpuLoad());
        targetComputeGridRealtimeStatisticsInfo.setAverageCPULoadPercentage(metrics.getAverageCpuLoad());
        targetComputeGridRealtimeStatisticsInfo.setTotalExecutedComputes(metrics.getTotalExecutedJobs());
        targetComputeGridRealtimeStatisticsInfo.setGridIdleTimeInSecond(metrics.getCurrentIdleTime()/1000);
        targetComputeGridRealtimeStatisticsInfo.setGridTotalIdleTimeInSecond(metrics.getTotalIdleTime()/1000);
        */




        return computeGridRealtimeStatisticsInfo;
    }


    private Map<String,Object> getCurrentIgniteMetricsValueMap(){
        Map<String,Object> currentIgniteMetricsValueMap = new HashMap<>();
        List<List<?>> listValue = this.igniteClient.query(new SqlFieldsQuery("select name, value from SYS.METRICS").setSchema("SYS")).getAll();
        for(List<?> currentList :listValue){
            String metricName = currentList.get(0).toString();
            Object metricNameValue = currentList.get(1);
            currentIgniteMetricsValueMap.put(metricName,metricNameValue);
        }
        return currentIgniteMetricsValueMap;
    }
}
