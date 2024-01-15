package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.BaseIgniteValueReturnComputeLogic;

public class ValueReturnComputeLogicA extends BaseIgniteValueReturnComputeLogic<Integer> {
    @Override
    public Integer execute() {
        return Integer.valueOf(10086);
    }
}
