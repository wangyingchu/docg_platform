package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeService;

public interface IgniteComputeService<T> extends ComputeService {
    T getComputeFunction(String functionName, Class functionClass);
}
