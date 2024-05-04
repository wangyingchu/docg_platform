package com.viewfunction.docg.dataCompute.dataComputeServiceCore.term;

public interface FixInputTypeComputeLogic<E,R> extends ComputeLogic{

    public R execute(E input);
}
