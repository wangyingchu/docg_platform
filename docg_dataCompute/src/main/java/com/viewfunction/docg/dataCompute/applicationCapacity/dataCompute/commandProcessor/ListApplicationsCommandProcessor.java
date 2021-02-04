package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;


import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.OperationResultVO;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.ResourceNodeIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;

import java.util.Collection;

public class ListApplicationsCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public ListApplicationsCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions){
        OperationResultVO operationResultVO= ResourceNodeIgniteOperationUtil.isGridActive(this.nodeIgnite);
        if(!operationResultVO.getResult()){
            System.out.println(operationResultVO.getResultMessage());
            return;
        }

        String connectomeScopeName= DataComputeConfigurationHandler.getConfigPropertyValue("connectomeScopeAttributeName");
        String connectomeScopeValue= DataComputeConfigurationHandler.getConfigPropertyValue("connectomeScopeAttributeValue");
        ClusterGroup nodes = nodeIgnite.cluster().forAttribute(connectomeScopeName, connectomeScopeValue);
        Collection<ClusterNode> appNodes = nodes.nodes();
        StringBuffer lsAppsMessageStringBuffer=new StringBuffer();
        lsAppsMessageStringBuffer.append("\n\r");
        lsAppsMessageStringBuffer.append("================================================================");
        lsAppsMessageStringBuffer.append("\n\r");
        lsAppsMessageStringBuffer.append("Total running application number: "+appNodes.size());
        lsAppsMessageStringBuffer.append("\n\r");
        lsAppsMessageStringBuffer.append("Connectome scope: " + DataComputeConfigurationHandler.getConfigPropertyValue("connectomeScopeAttributeValue"));
        lsAppsMessageStringBuffer.append("\n\r");
        for(ClusterNode clusterNode:appNodes){
            lsAppsMessageStringBuffer.append("-------------------------------------------------------------");
            lsAppsMessageStringBuffer.append("\n\r");
            lsAppsMessageStringBuffer.append("Application Id:     " + clusterNode.id().toString());
            lsAppsMessageStringBuffer.append("\n\r");
            lsAppsMessageStringBuffer.append("Connectome type:    " + clusterNode.attribute(DataComputeConfigurationHandler.getConfigPropertyValue("connectomeRoleAttributeName")));
            lsAppsMessageStringBuffer.append("\n\r");
            lsAppsMessageStringBuffer.append("Host name:          " + clusterNode.hostNames());
            lsAppsMessageStringBuffer.append("\n\r");
            lsAppsMessageStringBuffer.append("IP address:         " + clusterNode.addresses());
            lsAppsMessageStringBuffer.append("\n\r");
            if(clusterNode.isClient()){
                lsAppsMessageStringBuffer.append("Client application: " + "YES");
            }else{
                lsAppsMessageStringBuffer.append("Client application: " + "NO");
            }
            lsAppsMessageStringBuffer.append("\n\r");
            if(clusterNode.isLocal()){
                lsAppsMessageStringBuffer.append("Local application:  " + "YES");
            }else{
                lsAppsMessageStringBuffer.append("Local application:  " + "NO");
            }
            lsAppsMessageStringBuffer.append("\n\r");
        }
        lsAppsMessageStringBuffer.append("-------------------------------------------------------------");
        lsAppsMessageStringBuffer.append("\n\r");
        lsAppsMessageStringBuffer.append("================================================================");
        System.out.println(lsAppsMessageStringBuffer.toString());
    }
}
