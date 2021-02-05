package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.OperationResultVO;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.ResourceNodeIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;

import java.util.Collection;

public class LsunitCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public LsunitCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions){
        OperationResultVO operationResultVO= ResourceNodeIgniteOperationUtil.isGridActive(this.nodeIgnite);
        if(!operationResultVO.getResult()){
            System.out.println(operationResultVO.getResultMessage());
            return;
        }

        String connectomeScopeName= DataComputeConfigurationHandler.getConfigPropertyValue("unitScopeAttributeName");
        String connectomeScopeValue= DataComputeConfigurationHandler.getConfigPropertyValue("unitScopeAttributeValue");
        ClusterGroup nodes = nodeIgnite.cluster().forAttribute(connectomeScopeName, connectomeScopeValue);
        Collection<ClusterNode> appNodes = nodes.nodes();
        StringBuffer lsAppsMessageStringBuffer=new StringBuffer();
        lsAppsMessageStringBuffer.append("\n\r");
        lsAppsMessageStringBuffer.append("================================================================");
        lsAppsMessageStringBuffer.append("\n\r");
        lsAppsMessageStringBuffer.append("Total running unit number:        "+appNodes.size());
        lsAppsMessageStringBuffer.append("\n\r");
        lsAppsMessageStringBuffer.append("Connectome scope: " + DataComputeConfigurationHandler.getConfigPropertyValue("unitScopeAttributeValue"));
        lsAppsMessageStringBuffer.append("\n\r");
        for(ClusterNode clusterNode:appNodes){
            lsAppsMessageStringBuffer.append("-------------------------------------------------------------");
            lsAppsMessageStringBuffer.append("\n\r");
            lsAppsMessageStringBuffer.append("Unit Id:            " + clusterNode.id().toString());
            lsAppsMessageStringBuffer.append("\n\r");
            lsAppsMessageStringBuffer.append("Connectome type:    " + clusterNode.attribute(DataComputeConfigurationHandler.getConfigPropertyValue("unitRoleAttributeName")));
            lsAppsMessageStringBuffer.append("\n\r");
            lsAppsMessageStringBuffer.append("Host name:          " + clusterNode.hostNames());
            lsAppsMessageStringBuffer.append("\n\r");
            lsAppsMessageStringBuffer.append("IP address:         " + clusterNode.addresses());
            lsAppsMessageStringBuffer.append("\n\r");
            if(clusterNode.isClient()){
                lsAppsMessageStringBuffer.append("Client unit:        " + "YES");
            }else{
                lsAppsMessageStringBuffer.append("Client unit:        " + "NO");
            }
            lsAppsMessageStringBuffer.append("\n\r");
            if(clusterNode.isLocal()){
                lsAppsMessageStringBuffer.append("Local unit:         " + "YES");
            }else{
                lsAppsMessageStringBuffer.append("Local unit:         " + "NO");
            }
            lsAppsMessageStringBuffer.append("\n\r");
        }
        lsAppsMessageStringBuffer.append("-------------------------------------------------------------");
        lsAppsMessageStringBuffer.append("\n\r");
        lsAppsMessageStringBuffer.append("================================================================");
        System.out.println(lsAppsMessageStringBuffer.toString());
    }
}
