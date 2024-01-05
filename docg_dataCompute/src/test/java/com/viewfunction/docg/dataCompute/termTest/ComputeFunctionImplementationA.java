package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunction;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.ComputeFunctionImpl;
import org.apache.ignite.services.Service;

public class ComputeFunctionImplementationA implements ComputeFunctionA,ComputeFunction {


    public String doSomeThing(String thing){
        //System.out.println("compute some thing -> "+thing);
        return "compute some thing -> "+thing;
    }


}
