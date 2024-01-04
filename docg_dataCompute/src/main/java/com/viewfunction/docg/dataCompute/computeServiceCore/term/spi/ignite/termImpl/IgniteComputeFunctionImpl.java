package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunction;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunctionCallback;
import org.apache.ignite.services.Service;

public class IgniteComputeFunctionImpl implements ComputeFunction, Service {

    ComputeFunctionCallback computeFunctionCallback;

    @Override
    public void init() {
        if(this.computeFunctionCallback != null){
            this.computeFunctionCallback.onPrepare();
        }
    }

    @Override
    public void execute() {
        if(this.computeFunctionCallback != null){
            this.computeFunctionCallback.onRun();
        }
    }

    @Override public void cancel() {
        if(this.computeFunctionCallback != null){
            this.computeFunctionCallback.onFinish();
        }
    }

    @Override
    public void setComputeFunctionCallback(ComputeFunctionCallback computeFunctionCallback) {
        this.computeFunctionCallback = computeFunctionCallback;
    }
}
