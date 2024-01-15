package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteFixInputTypeComputeLogic;
import org.apache.ignite.lang.IgniteClosure;

public abstract class BaseIgniteFixInputTypeComputeLogic<E,R> extends BaseIgniteComputeLogic implements IgniteFixInputTypeComputeLogic<E,R>, IgniteClosure<E,R> {

    @Override
    public R apply(E e) {
        return execute(e);
    }
}
