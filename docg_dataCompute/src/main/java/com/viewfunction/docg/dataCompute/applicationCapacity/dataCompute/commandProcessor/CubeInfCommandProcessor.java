package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;

import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;

public class CubeInfCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public CubeInfCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        ClusterNode clusterNode=this.nodeIgnite.cluster().localNode();
        StringBuffer appInfoStringBuffer=new StringBuffer();
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Cube Id:               " + clusterNode.id().toString());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Connectome type:       " + clusterNode.attribute(DataComputeConfigurationHandler.getConfigPropertyValue("connectomeRoleAttributeName")));
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Connectome scope:      " + clusterNode.attribute(DataComputeConfigurationHandler.getConfigPropertyValue("connectomeScopeAttributeName")));
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Host name:             " + clusterNode.hostNames());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("IP address:            " + clusterNode.addresses());
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("Max available Memory:  " + this.nodeIgnite.configuration().getDataStorageConfiguration().getDefaultDataRegionConfiguration().getMaxSize()/1024/1024/1024+" GB");
        appInfoStringBuffer.append("\n\r");
        if(clusterNode.isClient()){
            appInfoStringBuffer.append("Client cube:           " + "YES");
        }else{
            appInfoStringBuffer.append("Client cube:           " + "NO");
        }
        appInfoStringBuffer.append("\n\r");
        appInfoStringBuffer.append("================================================================");
        System.out.println(appInfoStringBuffer.toString());
    }
}
