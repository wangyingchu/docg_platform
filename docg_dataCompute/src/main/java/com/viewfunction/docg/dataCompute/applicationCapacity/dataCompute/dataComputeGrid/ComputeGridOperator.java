package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeGrid;

import com.viewfunction.docg.dataCompute.computeServiceCore.util.config.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
        String unitScopeName= DataComputeConfigurationHandler.getConfigPropertyValue("unitScopeAttributeName");
        String unitScopeValue= DataComputeConfigurationHandler.getConfigPropertyValue("unitScopeAttributeValue");

        ClusterGroup clusterGroup = this.operatorIgnite.cluster().forAttribute(unitScopeName, unitScopeValue);
        ClusterNode oldestNode = clusterGroup.forOldest().node();
        ClusterNode youngestNode = clusterGroup.forYoungest().node();
        ClusterMetrics metrics = clusterGroup.metrics();

        ComputeGridRealtimeMetrics targetComputeGridRealtimeMetrics = new ComputeGridRealtimeMetrics();

        Instant instant = Instant.ofEpochMilli(metrics.getStartTime());
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        targetComputeGridRealtimeMetrics.setGridStartTime(localDateTime);
        targetComputeGridRealtimeMetrics.setGridUpTimeInMinute(metrics.getUpTime()/1000/60);
        targetComputeGridRealtimeMetrics.setGridIdleTimeInSecond(metrics.getCurrentIdleTime()/1000);
        targetComputeGridRealtimeMetrics.setGridTotalIdleTimeInSecond(metrics.getTotalIdleTime()/1000);
        //ComputeGridOperator 本身在当前时刻也是一个 DataComputeUnit,需要在总量中减去
        targetComputeGridRealtimeMetrics.setDataComputeUnitsAmount(metrics.getTotalNodes()-1);
        targetComputeGridRealtimeMetrics.setOldestUnitId(oldestNode.id().toString());
        targetComputeGridRealtimeMetrics.setYoungestUnitId(youngestNode.id().toString());
        targetComputeGridRealtimeMetrics.setUsedNonHeapMemoryInMB(metrics.getNonHeapMemoryUsed()/1024/1024);
        targetComputeGridRealtimeMetrics.setTotalNonHeapMemoryInMB(metrics.getNonHeapMemoryTotal()/1024/1024);
        targetComputeGridRealtimeMetrics.setUsedHeapMemoryInMB(metrics.getHeapMemoryUsed()/1024/1024);
        targetComputeGridRealtimeMetrics.setTotalHeapMemoryInMB(metrics.getHeapMemoryTotal()/1024/1024);
        targetComputeGridRealtimeMetrics.setAvailableCPUCores(metrics.getTotalCpus());
        targetComputeGridRealtimeMetrics.setCurrentCPULoadPercentage(metrics.getCurrentCpuLoad());
        targetComputeGridRealtimeMetrics.setAverageCPULoadPercentage(metrics.getAverageCpuLoad());
        targetComputeGridRealtimeMetrics.setTotalExecutedComputes(metrics.getTotalExecutedJobs());


        metrics.getBusyTimePercentage();
        metrics.getHeapMemoryCommitted();
        metrics.getHeapMemoryMaximum();
        metrics.getHeapMemoryInitialized();
        metrics.getNodeStartTime();
        metrics.getTotalCpus();
        metrics.getTotalNodes();


        return targetComputeGridRealtimeMetrics;
    }
}
