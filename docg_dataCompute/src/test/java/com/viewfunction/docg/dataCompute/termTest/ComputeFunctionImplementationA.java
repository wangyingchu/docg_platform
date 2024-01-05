package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeFunction;

public class ComputeFunctionImplementationA implements ComputeFunctionA, IgniteComputeFunction {


    public String doSomeThing(String thing){
        //System.out.println("compute some thing -> "+thing);
        return "compute some thing -> "+thing;
    }


}
