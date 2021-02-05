package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.UnitOperationResult;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;

import java.util.Date;

public class UnitmetrCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public UnitmetrCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        UnitOperationResult unitOperationResult = UnitIgniteOperationUtil.isGridActive(this.nodeIgnite);
        if(!unitOperationResult.getResult()){
            System.out.println(unitOperationResult.getResultMessage());
            return;
        }
        ClusterNode clusterNode=this.nodeIgnite.cluster().localNode();
        ClusterMetrics metrics = clusterNode.metrics();
        StringBuffer appInfoStringBuffer=new StringBuffer();
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Unit Id:                      " + clusterNode.id().toString());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Unit Start Time:              " + new Date(metrics.getNodeStartTime()).toString());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Unit Up Time:                 " + metrics.getUpTime()/1000/60+" Minute");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Current Idle Time:            " + metrics.getCurrentIdleTime()/1000+" Second");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Busy Time Percentage:         " + metrics.getBusyTimePercentage()*100+"%");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Used Non-Heap Memory:         " + metrics.getNonHeapMemoryUsed()/1024/1024+" MB");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Used Heap Memory:             " + metrics.getHeapMemoryUsed()/1024/1024+" MB");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Available CPU cores:          " + metrics.getTotalCpus());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Current Cpu Load Percentage:  " + metrics.getCurrentCpuLoad()*100+"%");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Average Cpu Load Percentage:  " + metrics.getAverageCpuLoad()*100+"%");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Current Active Computes:      " + metrics.getCurrentActiveJobs());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Total Executed Computes:      " + metrics.getTotalExecutedJobs());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Max Active Computes:          " + metrics.getMaximumActiveJobs());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        System.out.println(appInfoStringBuffer.toString());
    }
}
