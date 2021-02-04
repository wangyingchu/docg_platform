package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.OperationResultVO;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.ResourceNodeIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;

import java.util.Date;

public class CubemetrCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public CubemetrCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        OperationResultVO operationResultVO= ResourceNodeIgniteOperationUtil.isGridActive(this.nodeIgnite);
        if(!operationResultVO.getResult()){
            System.out.println(operationResultVO.getResultMessage());
            return;
        }
        ClusterNode clusterNode=this.nodeIgnite.cluster().localNode();
        ClusterMetrics metrics = clusterNode.metrics();
        StringBuffer appInfoStringBuffer=new StringBuffer();
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Application Id:               " + clusterNode.id().toString());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Application Start Time:       " + new Date(metrics.getNodeStartTime()).toString());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Application Up Time:          " + metrics.getUpTime()/1000/60+" Minute");
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
