package com.viewfunction.docg.dataCompute.computeServiceCore.term;

public interface FixInputTypeComputeLogic<E,R> extends ComputeLogic{

    public R execute(E input);
}
