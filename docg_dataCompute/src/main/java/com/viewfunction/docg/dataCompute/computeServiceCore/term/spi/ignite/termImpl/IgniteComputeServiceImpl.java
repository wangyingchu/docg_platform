package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunction;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeFunction;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeService;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.config.DataComputeConfigurationHandler;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.Ignition;

public class IgniteComputeServiceImpl implements IgniteComputeService {

    private Ignite invokerIgnite;

    private IgniteComputeServiceImpl(){}

    public void openServiceSession() throws ComputeGridNotActiveException {
        Ignition.setClientMode(true);
        this.invokerIgnite = Ignition.start(DataComputeConfigurationHandler.getDataComputeIgniteConfigurationFilePath());
        UnitIgniteOperationUtil.checkGridActiveStatus(this.invokerIgnite);
    }

    public void closeServiceSession(){
        if(this.invokerIgnite !=null){
            this.invokerIgnite.close();
        }
    }

    public static IgniteComputeServiceImpl getServiceInstance() throws ComputeGridNotActiveException {
        IgniteComputeServiceImpl igniteComputeServiceImpl= new IgniteComputeServiceImpl();
        try{
            igniteComputeServiceImpl.openServiceSession();
        }catch (ComputeGridNotActiveException e){
            igniteComputeServiceImpl.closeServiceSession();
            throw e;
        }
        return igniteComputeServiceImpl;
    }

    @Override
    public void close() throws Exception {
        closeServiceSession();
    }

    @Override
    public void deployGridComputeFunction(String functionName, ComputeFunction computeFunction) {
        IgniteServices svcs = this.invokerIgnite.services(this.invokerIgnite.cluster().forServers());
        svcs.deployClusterSingleton(functionName, (IgniteComputeFunction)computeFunction);
    }

    @Override
    public void deployPerUnitComputeFunction(String functionName, ComputeFunction computeFunction) {
        IgniteServices svcs = this.invokerIgnite.services(this.invokerIgnite.cluster().forServers());
        svcs.deployNodeSingleton(functionName, (IgniteComputeFunction)computeFunction);
    }

    @Override
    public void deployMultipleUnitComputeFunction(String functionName, ComputeFunction computeFunction,int functionCount) {
        IgniteServices svcs = this.invokerIgnite.services(this.invokerIgnite.cluster().forServers());
        svcs.deployMultiple(functionName, (IgniteComputeFunction)computeFunction,functionCount,0);
    }

    @Override
    public <T> T getComputeFunction(String functionName, Class<? super T> functionClass) {
        T computeFunction = this.invokerIgnite.services().serviceProxy(functionName,functionClass, false);
        return computeFunction;
    }
}
