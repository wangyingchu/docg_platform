package com.viewfunction.docg.testcase.dataCompute.termTest.computeServiceLogic;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.spi.ignite.termImpl.BaseIgniteVoidReturnComputeLogic;
import org.apache.ignite.IgniteAtomicLong;

public class VoidReturnComputeLogicA extends BaseIgniteVoidReturnComputeLogic {
    @Override
    public void execute() {
        IgniteAtomicLong currentIgniteAtomicLong = this.getUnitIgniteFeatureHandler().createAtomicLong(10000);
        System.out.println(currentIgniteAtomicLong.addAndGet(500));
        System.out.println(currentIgniteAtomicLong.get());

        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("AAASSSSDDDDD");
        System.out.println("++++++++++++++++++++++++++++++++++");
        System.out.println("++++++++++++++++++++++++++++++++++");

    }
}