package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteValueReturnComputeLogic;
import org.apache.ignite.lang.IgniteCallable;

public abstract class BaseIgniteValueReturnComputeLogic<V> extends BaseIgniteComputeLogic implements IgniteValueReturnComputeLogic<V>, IgniteCallable<V> {


    @Override
    public V call() throws Exception {
        return execute();
    }
}
