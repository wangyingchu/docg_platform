package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.viewfunction.docg.dataCompute.computeServiceCore.util.config.DataComputeConfigurationHandler;
import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.util.UnitOperationResult;
import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.consoleApplication.feature.BaseCommandProcessor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.services.ServiceDescriptor;

import java.util.*;

public class LssvcCommandProcessor implements BaseCommandProcessor {

    private Ignite nodeIgnite;

    public LssvcCommandProcessor(Ignite nodeIgnite){
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
        IgniteServices svcs = this.nodeIgnite.services(this.nodeIgnite.cluster().forServers().forAttribute(unitScopeName,unitScopeValue));
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
                lsServicesMessageStringBuffer.append("Max service per unit:        " + currentServiceDesc.maxPerNodeCount());
            }else{
                lsServicesMessageStringBuffer.append("Max service per unit:        " + "unlimited");
            }
            lsServicesMessageStringBuffer.append("\n\r");
            lsServicesMessageStringBuffer.append("Service total count:         " + currentServiceDesc.totalCount());
            lsServicesMessageStringBuffer.append("\n\r");
            lsServicesMessageStringBuffer.append("Running on units:");
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
