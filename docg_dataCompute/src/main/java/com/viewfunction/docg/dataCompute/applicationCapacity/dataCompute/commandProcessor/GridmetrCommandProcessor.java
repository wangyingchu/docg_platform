package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.UnitOperationResult;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.DataRegionConfiguration;

import java.util.Date;

public class GridmetrCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public GridmetrCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        UnitOperationResult unitOperationResult = UnitIgniteOperationUtil.isGridActive(this.nodeIgnite);
        if(!unitOperationResult.getResult()){
            System.out.println(unitOperationResult.getResultMessage());
            return;
        }
        String unitScopeName= DataComputeConfigurationHandler.getConfigPropertyValue("unitScopeAttributeName");
        String unitScopeValue= DataComputeConfigurationHandler.getConfigPropertyValue("unitScopeAttributeValue");
        ClusterGroup clusterGroup = this.nodeIgnite.cluster().forAttribute(unitScopeName, unitScopeValue);
        this.nodeIgnite.configuration().getDataStorageConfiguration().getDefaultDataRegionConfiguration().getMaxSize();

        DataRegionConfiguration targetDataRegionConfiguration = this.nodeIgnite.configuration().getDataStorageConfiguration().getDefaultDataRegionConfiguration();

        this.nodeIgnite.cluster().forServers().metrics().getTotalNodes();

        //Collection<BaselineNode> baselineNodes = this.nodeIgnite.cluster().currentBaselineTopology();

        //this.nodeIgnite.snapshot().

        ClusterNode oldestNode=clusterGroup.forOldest().node();
        ClusterNode youngestNode=clusterGroup.forYoungest().node();
        ClusterMetrics metrics = clusterGroup.metrics();

        StringBuffer appInfoStringBuffer=new StringBuffer();
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Grid Start Time:              " + new Date(metrics.getNodeStartTime()).toString());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Grid Up Time:                 " + metrics.getUpTime()/1000/60+" Minute");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Grid Idle Time:               " + metrics.getCurrentIdleTime()/1000+" Second");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Grid Total Idle Time:         " + metrics.getTotalIdleTime()/1000+" Second");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Grid Units Amount:            " + metrics.getTotalNodes());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Oldest Unit Id:               " + oldestNode.id().toString());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Youngest Unit Id:             " + youngestNode.id().toString());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Used Non-Heap Memory:         " + metrics.getNonHeapMemoryUsed()/1024/1024+" MB");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Used Heap Memory:             " + metrics.getHeapMemoryUsed()/1024/1024+" MB");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Total Non-Heap Memory:         " + metrics.getNonHeapMemoryTotal()/1024/1024+" MB");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Total Heap Memory:             " + metrics.getHeapMemoryTotal()/1024/1024+" MB");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Available CPU cores:          " + metrics.getTotalCpus());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Global Cpu Load Percentage:   " + metrics.getCurrentCpuLoad()*100+"%");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Average Cpu Load Percentage:  " + metrics.getAverageCpuLoad()*100+"%");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Total Executed Computes:      " + metrics.getTotalExecutedJobs());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        System.out.println(appInfoStringBuffer.toString());


        System.out.println(metrics.getNonHeapMemoryTotal());
        System.out.println(metrics.getNonHeapMemoryMaximum());
        System.out.println(metrics.getNonHeapMemoryCommitted());
        System.out.println(metrics.getNonHeapMemoryInitialized());
        System.out.println(metrics.getNonHeapMemoryUsed());
        //System.out.println(clusterGroup.node().metrics().getNonHeapMemoryTotal());
    }
}
