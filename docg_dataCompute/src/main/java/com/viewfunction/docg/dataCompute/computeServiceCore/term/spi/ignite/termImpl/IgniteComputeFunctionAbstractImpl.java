package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeFunctionCallback;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeFunction;
import org.apache.ignite.services.Service;

import java.io.Serializable;

public abstract class IgniteComputeFunctionAbstractImpl implements IgniteComputeFunction, Service, Serializable,ComputeFunctionCallback {

    public abstract void onPrepare();

    public abstract void onRun();

    public abstract void onFinish();

    @Override
    public void init() {
        onPrepare();
    }

    @Override
    public void execute() {
        onRun();
    }

    @Override public void cancel() {
        onFinish();
    }
}
