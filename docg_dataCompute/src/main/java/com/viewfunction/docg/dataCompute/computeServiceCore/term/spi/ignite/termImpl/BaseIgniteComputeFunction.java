package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.util.UnitIgniteFeatureHandler;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeFunction;

import org.apache.ignite.Ignite;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.ServiceContextResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;

public class BaseIgniteComputeFunction implements IgniteComputeFunction, Service {

    /** Serial version UID. */
    private static final long serialVersionUID = 0L;

    /** Ignite instance. */
    @IgniteInstanceResource
    private Ignite ignite;

    /** Service context. */
    @ServiceContextResource
    private ServiceContext ctx;

    private UnitIgniteFeatureHandler unitIgniteFeatureHandler;

    /** {@inheritDoc} */
    @Override public void cancel() {
        ignite.destroyCache(ctx.name());
        String consoleMessageStringBuffer = "-------------------------------------------------------------" +
                "\n\r" +
                "Service was cancelled: " + ctx.name() +
                "\n\r" +
                "-------------------------------------------------------------";
        System.out.println(consoleMessageStringBuffer);
    }

    /** {@inheritDoc} */
    @Override public void init() {
        String consoleMessageStringBuffer = "-------------------------------------------------------------" +
                "\n\r" +
                "Service was initialized: " + ctx.name() +
                "\n\r" +
                "-------------------------------------------------------------";
        System.out.println(consoleMessageStringBuffer);
    }

    /** {@inheritDoc} */
    @Override public void execute() throws Exception {
        //System.out.println("Executing distributed service: " + ctx.name());
    }

    protected Ignite getIgniteInstance(){
        return this.ignite;
    }

    protected UnitIgniteFeatureHandler getUnitIgniteFeatureHandler(){
        if(this.unitIgniteFeatureHandler == null){
            unitIgniteFeatureHandler=new UnitIgniteFeatureHandler(this.ignite);
        }
        return unitIgniteFeatureHandler;
    }
}
