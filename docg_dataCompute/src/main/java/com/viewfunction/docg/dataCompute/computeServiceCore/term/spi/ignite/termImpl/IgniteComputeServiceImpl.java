package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.util.UnitIgniteOperationUtil;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.*;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeFunction;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeService;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteVoidReturnComputeLogic;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.config.DataComputeConfigurationHandler;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.Ignition;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.lang.IgniteFuture;

import java.util.ArrayList;
import java.util.Collection;

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

    @Override
    public void executePerUnitVoidReturnComputeLogic(VoidReturnComputeLogic voidReturnComputeLogic) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Broadcasts to all nodes in the cluster group.
        compute.broadcast((BaseIgniteVoidReturnComputeLogic)voidReturnComputeLogic);
    }

    @Override
    public <V> Collection<V> executePerUnitValueReturnComputeLogic(ValueReturnComputeLogic<V> valueReturnComputeLogic) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Broadcasts to all nodes in the cluster group.
        return compute.broadcast((BaseIgniteValueReturnComputeLogic<V>)valueReturnComputeLogic);
    }

    @Override
    public <E, R> Collection<R> executePerUnitFixInputTypeComputeLogic(FixInputTypeComputeLogic<E, R> fixInputTypeComputeLogic, E valeOfInputType) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Broadcasts to all nodes in the cluster group.
        return compute.broadcast((BaseIgniteFixInputTypeComputeLogic<E,R>)fixInputTypeComputeLogic,valeOfInputType);
    }

    @Override
    public void executeGridSingletonVoidReturnComputeLogic(VoidReturnComputeLogic voidReturnComputeLogic) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Run on a node within the underlying cluster group.
        compute.run((BaseIgniteVoidReturnComputeLogic)voidReturnComputeLogic);
    }

    @Override
    public <V> V executeGridSingletonValueReturnComputeLogic(ValueReturnComputeLogic<V> valueReturnComputeLogic) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Run on a node within the underlying cluster group.
        return compute.call((BaseIgniteValueReturnComputeLogic<V>)valueReturnComputeLogic);
    }

    @Override
    public <E, R> R executeGridSingletonFixInputTypeComputeLogic(FixInputTypeComputeLogic<E, R> fixInputTypeComputeLogic, E valeOfInputType) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Run on a node within the underlying cluster group.
        return compute.apply((BaseIgniteFixInputTypeComputeLogic<E,R>)fixInputTypeComputeLogic,valeOfInputType);
    }

    @Override
    public void executeGridMultipleVoidReturnComputeLogic(Collection<VoidReturnComputeLogic> voidReturnComputeLogics) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Run on a node within the underlying cluster group.
        Collection<BaseIgniteVoidReturnComputeLogic> logicCollection = new ArrayList<>();
        for(VoidReturnComputeLogic currentLogic:voidReturnComputeLogics){
            logicCollection.add((BaseIgniteVoidReturnComputeLogic)currentLogic);
        }
        compute.run(logicCollection);
    }

    @Override
    public <V> Collection<V> executeGridMultipleValueReturnComputeLogic(Collection<ValueReturnComputeLogic<V>> valueReturnComputeLogics) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Run on a node within the underlying cluster group.
        Collection<BaseIgniteValueReturnComputeLogic<V>> logicCollection = new ArrayList<>();
        for(ValueReturnComputeLogic<V> currentLogic:valueReturnComputeLogics){
            logicCollection.add((BaseIgniteValueReturnComputeLogic<V>)currentLogic);
        }
        return compute.call(logicCollection);
    }

    @Override
    public <E, R> Collection<R> executeGridMultipleFixInputTypeComputeLogic(FixInputTypeComputeLogic<E, R> fixInputTypeComputeLogic, Collection<E> valeOfInputType) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Run on a node within the underlying cluster group.
        return compute.apply((BaseIgniteFixInputTypeComputeLogic<E,R>)fixInputTypeComputeLogic,valeOfInputType);
    }

    @Override
    public AsyncExecutionResultHandler asyncExecutePerUnitVoidReturnComputeLogic(VoidReturnComputeLogic voidReturnComputeLogic) {
        IgniteCompute compute = this.invokerIgnite.compute(this.invokerIgnite.cluster().forServers());
        //Broadcasts to all nodes in the cluster group.
        IgniteFuture<Void> igniteFuture = compute.broadcastAsync((BaseIgniteVoidReturnComputeLogic)voidReturnComputeLogic);

        return null;
    }
}
