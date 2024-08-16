package com.viewfunction.docg.testcase.dataCompute.termTest.computeServiceLogic;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.spi.ignite.termImpl.BaseIgniteFixInputTypeComputeLogic;


public class FixInputTypeComputeLogicA extends BaseIgniteFixInputTypeComputeLogic<Double,String> {

    @Override
    public String execute(Double input) {
        return (Math.random()*1000)+"__ResultStr__"+input;
    }
}
