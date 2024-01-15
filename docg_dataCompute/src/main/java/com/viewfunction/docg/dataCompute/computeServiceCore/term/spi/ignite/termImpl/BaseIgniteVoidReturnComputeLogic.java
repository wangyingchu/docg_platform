package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteVoidReturnComputeLogic;
import org.apache.ignite.lang.IgniteRunnable;

public abstract class BaseIgniteVoidReturnComputeLogic extends BaseIgniteComputeLogic implements IgniteVoidReturnComputeLogic,IgniteRunnable {

    public void run(){
        execute();
    }
}
