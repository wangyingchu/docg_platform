package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeGrid;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.UnitOperationResult;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;

public class ComputeGridOperator implements AutoCloseable{

    private Ignite operatorIgnite;

    private ComputeGridOperator(){}

    public void openOperatorSession() throws ComputeGridNotActiveException {
        Ignition.setClientMode(true);
        this.operatorIgnite = Ignition.start(DataComputeConfigurationHandler.getDataComputeIgniteConfigurationFilePath());
        UnitIgniteOperationUtil.checkGridActiveStatus(this.operatorIgnite);
    }

    public void closeOperatorSession(){
        if(this.operatorIgnite != null){
            this.operatorIgnite.close();
        }
    }

    public static ComputeGridOperator getComputeGridOperator() throws ComputeGridNotActiveException {
        ComputeGridOperator computeGridOperator = new ComputeGridOperator();
        computeGridOperator.openOperatorSession();
        return computeGridOperator;
    }

    @Override
    public void close() throws Exception {
        closeOperatorSession();
    }

    public ComputeGridRealtimeMetrics getComputeGridRealtimeMetrics(){
        UnitOperationResult unitOperationResult = UnitIgniteOperationUtil.isGridActive(this.operatorIgnite);
        if(!unitOperationResult.getResult()){
            System.out.println(unitOperationResult.getResultMessage());
            //return;
        }

        String unitScopeName= DataComputeConfigurationHandler.getConfigPropertyValue("unitScopeAttributeName");
        String unitScopeValue= DataComputeConfigurationHandler.getConfigPropertyValue("unitScopeAttributeValue");

        ClusterGroup clusterGroup = this.operatorIgnite.cluster().forAttribute(unitScopeName, unitScopeValue);
        ClusterNode oldestNode = clusterGroup.forOldest().node();
        ClusterNode youngestNode = clusterGroup.forYoungest().node();
        ClusterMetrics metrics = clusterGroup.metrics();

        ComputeGridRealtimeMetrics targetComputeGridRealtimeMetrics = new ComputeGridRealtimeMetrics();
        return targetComputeGridRealtimeMetrics;
    }
}
