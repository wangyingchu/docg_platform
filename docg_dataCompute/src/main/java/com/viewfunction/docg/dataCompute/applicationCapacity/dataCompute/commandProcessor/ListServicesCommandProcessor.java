package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.OperationResultVO;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataCube.ResourceNodeIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.services.ServiceDescriptor;

import java.util.*;

public class ListServicesCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public ListServicesCommandProcessor(Ignite nodeIgnite){
        this.nodeIgnite=nodeIgnite;
    }

    @Override
    public void processCommand(String command, String[] commandOptions) {
        OperationResultVO operationResultVO= ResourceNodeIgniteOperationUtil.isGridActive(this.nodeIgnite);
        if(!operationResultVO.getResult()){
            System.out.println(operationResultVO.getResultMessage());
            return;
        }

        String connectomeScopeName= DataComputeConfigurationHandler.getConfigPropertyValue("connectomeScopeAttributeName");
        String connectomeScopeValue= DataComputeConfigurationHandler.getConfigPropertyValue("connectomeScopeAttributeValue");
        IgniteServices svcs = this.nodeIgnite.services(this.nodeIgnite.cluster().forServers().forAttribute(connectomeScopeName,connectomeScopeValue));
        Collection<ServiceDescriptor> serviceDescCollection=svcs.serviceDescriptors();
        StringBuffer lsServicesMessageStringBuffer=new StringBuffer();
        lsServicesMessageStringBuffer.append("\n\r");
        lsServicesMessageStringBuffer.append("================================================================");
        lsServicesMessageStringBuffer.append("\n\r");
        lsServicesMessageStringBuffer.append("Total running service number: "+serviceDescCollection.size());
        lsServicesMessageStringBuffer.append("\n\r");
        Iterator<ServiceDescriptor> serviceDescriptorIterator= serviceDescCollection.iterator();
        while(serviceDescriptorIterator.hasNext()){
            ServiceDescriptor currentServiceDesc=serviceDescriptorIterator.next();
            lsServicesMessageStringBuffer.append("-------------------------------------------------------------");
            lsServicesMessageStringBuffer.append("\n\r");
            lsServicesMessageStringBuffer.append("Service Name:                " + currentServiceDesc.name());
            lsServicesMessageStringBuffer.append("\n\r");
            lsServicesMessageStringBuffer.append("Service class:               " + currentServiceDesc.serviceClass());
            lsServicesMessageStringBuffer.append("\n\r");
            if(currentServiceDesc.maxPerNodeCount()!=0){
                lsServicesMessageStringBuffer.append("Max service per application: " + currentServiceDesc.maxPerNodeCount());
            }else{
                lsServicesMessageStringBuffer.append("Max service per application: " + "unlimited");
            }
            lsServicesMessageStringBuffer.append("\n\r");
            lsServicesMessageStringBuffer.append("Service total count:         " + currentServiceDesc.totalCount());
            lsServicesMessageStringBuffer.append("\n\r");
            lsServicesMessageStringBuffer.append("Running on applications:");
            lsServicesMessageStringBuffer.append("\n\r");

            Map<UUID,Integer> topologyMap=currentServiceDesc.topologySnapshot();
            Set<UUID> applicationsIdSet= topologyMap.keySet();
            Iterator<UUID> appIdSetIterator=applicationsIdSet.iterator();
            while(appIdSetIterator.hasNext()){
                UUID currentAppId=appIdSetIterator.next();
                if(topologyMap.get(currentAppId)==1){
                    lsServicesMessageStringBuffer.append("    "+currentAppId);
                    lsServicesMessageStringBuffer.append("\n\r");
                }
            }
        }
        lsServicesMessageStringBuffer.append("-------------------------------------------------------------");
        lsServicesMessageStringBuffer.append("\n\r");
        lsServicesMessageStringBuffer.append("================================================================");
        System.out.println(lsServicesMessageStringBuffer.toString());
    }
}
