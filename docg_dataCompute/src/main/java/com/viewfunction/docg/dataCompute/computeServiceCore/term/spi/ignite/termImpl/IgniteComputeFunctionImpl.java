package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunction;
import org.apache.ignite.services.Service;

public class IgniteComputeFunctionImpl implements ComputeFunction, Service {

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void execute() {

    }


    /** {@inheritDoc} */
    @Override public void cancel() {
        //ignite.destroyCache(ctx.name());

        //System.out.println("Service was cancelled: " + ctx.name());
    }


}
