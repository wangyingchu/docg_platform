package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.vo.DataComputeUnitVO;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.DataComputeConfigurationHandler;
import org.apache.ignite.Ignition;
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

    public Set<DataComputeUnitVO> listDataComputeUnit(){
        Set<DataComputeUnitVO> dataComputeUnitVOSet = new HashSet<>();
        Collection<ClusterNode> nodesCollection =this.igniteClient.cluster().forServers().nodes();
        if(nodesCollection != null){
            for(ClusterNode currentClusterNode:nodesCollection){
                String _UnitId = currentClusterNode.id().toString();
                String _UnitType = currentClusterNode.attribute(DataComputeConfigurationHandler.getConfigPropertyValue("unitRoleAttributeName"));
                Collection<String> _HostName = currentClusterNode.hostNames();
                Collection<String> _IPAddress = currentClusterNode.addresses();
                boolean isClientUnit = currentClusterNode.isClient();
                DataComputeUnitVO currentDataComputeUnitVO = new DataComputeUnitVO(_UnitId,_UnitType,_HostName,_IPAddress,isClientUnit);
                dataComputeUnitVOSet.add(currentDataComputeUnitVO);
            }
        }
        return dataComputeUnitVOSet;
    }
}
