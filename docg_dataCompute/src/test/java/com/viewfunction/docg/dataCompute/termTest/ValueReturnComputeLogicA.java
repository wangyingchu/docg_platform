package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.BaseIgniteValueReturnComputeLogic;

public class ValueReturnComputeLogicA extends BaseIgniteValueReturnComputeLogic<Double> {
    @Override
    public Double execute() {
        return Double.valueOf(Math.random());
    }
}