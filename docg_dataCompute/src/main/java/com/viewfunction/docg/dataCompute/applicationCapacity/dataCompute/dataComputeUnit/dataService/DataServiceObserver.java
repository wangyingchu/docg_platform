package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.DataComputeConfigurationHandler;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.ClientCacheConfiguration;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.ClientConfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DataServiceObserver implements AutoCloseable{

    private String observedUnitIPsStr= DataComputeConfigurationHandler.getConfigPropertyValue("observedUnitIPs");
    private IgniteClient igniteClient;

    private DataServiceObserver(){}

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

    public static DataServiceObserver getObserverInstance(){
        DataServiceObserver dataServiceObserver = new DataServiceObserver();
        dataServiceObserver.openObserveSession();
        return dataServiceObserver;
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
}
