package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.util.UnitIgniteFeatureHandler;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeLogic;
import org.apache.ignite.Ignite;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.ServiceContextResource;
import org.apache.ignite.services.ServiceContext;

public class BaseIgniteComputeLogic implements IgniteComputeLogic {

    /** Serial version UID. */
    private static final long serialVersionUID = 0L;

    /** Ignite instance. */
    @IgniteInstanceResource
    private Ignite ignite;

    /** Service context. */
    @ServiceContextResource
    private ServiceContext ctx;

    private UnitIgniteFeatureHandler unitIgniteFeatureHandler;

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
