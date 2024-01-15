package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.BaseIgniteFixInputTypeComputeLogic;


public class FixInputTypeComputeLogicA extends BaseIgniteFixInputTypeComputeLogic<Double,String> {

    @Override
    public String execute(Double input) {
        return (Math.random()*1000)+"__ResultStr__"+input;
    }
}
