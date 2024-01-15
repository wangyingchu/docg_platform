package com.viewfunction.docg.dataCompute.computeServiceCore.term;

import java.util.Collection;

public interface ComputeService extends AutoCloseable{

    public void deployGridComputeFunction(String functionName,ComputeFunction computeFunction);

    public void deployPerUnitComputeFunction(String functionName,ComputeFunction computeFunction);

    public void deployMultipleUnitComputeFunction(String functionName,ComputeFunction computeFunction,int functionCount);

    <T> T getComputeFunction(String functionName,Class<? super T> functionClass);

    public void executePerUnitComputeLogic(VoidReturnComputeLogic voidReturnComputeLogic);

    <V> Collection<V> executePerUnitComputeLogic(ValueReturnComputeLogic<V> valueReturnComputeLogic);

    <E,R> Collection<R> executePerUnitComputeLogic(FixInputTypeComputeLogic<E,R> fixInputTypeComputeLogic,E valeOfInputType);
}
