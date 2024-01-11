package com.viewfunction.docg.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl.BaseIgniteComputeFunction;
import org.apache.ignite.IgniteAtomicReference;
import org.apache.ignite.IgniteQueue;

public class ComputeFunctionImplementationA extends BaseIgniteComputeFunction implements ComputeFunctionA {


    public String doSomeThing(String thing){
        //System.out.println("compute some thing -> "+thing);
        IgniteQueue<String> igniteQueue = this.getUnitIgniteFeatureHandler().createUnlimitedQueue();
        System.out.println("====================");
        System.out.println(igniteQueue);
        System.out.println("====================");
        IgniteAtomicReference<String> currentAomicReference = this.getUnitIgniteFeatureHandler().createAtomicReference(String.class);
        System.out.println("====================");
        System.out.println(currentAomicReference);
        System.out.println("====================");
        currentAomicReference.set("oossssddd");
        System.out.println(currentAomicReference.get());

        igniteQueue.add("igniteQueueValue1");
        igniteQueue.add("igniteQueueValue2");

        System.out.println(igniteQueue.peek());
        System.out.println(igniteQueue.poll());
        System.out.println(igniteQueue.take());
        System.out.println(igniteQueue.peek());
        return "compute some thing -> "+thing;
    }


}
