package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.BaseIgniteComputeFunction;
import org.apache.ignite.IgniteQueue;

public class ComputeFunctionImplementationA extends BaseIgniteComputeFunction implements ComputeFunctionA {


    public String doSomeThing(String thing){
        //System.out.println("compute some thing -> "+thing);
        IgniteQueue igniteQueue = this.getUnitIgniteFeatureHandler().createUnlimitedQueue();
        System.out.println("====================");
        System.out.println(igniteQueue);
        System.out.println("====================");
        return "compute some thing -> "+thing;
    }


}
