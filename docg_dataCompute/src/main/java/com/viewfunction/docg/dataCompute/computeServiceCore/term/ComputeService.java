package com.viewfunction.docg.dataCompute.computeServiceCore.term;

import java.util.Collection;

public interface ComputeService extends AutoCloseable{

    public void deployGridComputeFunction(String functionName,ComputeFunction computeFunction);

    public void deployPerUnitComputeFunction(String functionName,ComputeFunction computeFunction);

    public void deployMultipleUnitComputeFunction(String functionName,ComputeFunction computeFunction,int functionCount);

    <T> T getComputeFunction(String functionName,Class<? super T> functionClass);

    public void executePerUnitVoidReturnComputeLogic(VoidReturnComputeLogic voidReturnComputeLogic);

    <V> Collection<V> executePerUnitValueReturnComputeLogic(ValueReturnComputeLogic<V> valueReturnComputeLogic);

    <E,R> Collection<R> executePerUnitFixInputTypeComputeLogic(FixInputTypeComputeLogic<E,R> fixInputTypeComputeLogic, E valeOfInputType);

    public void executeGridSingletonVoidReturnComputeLogic(VoidReturnComputeLogic voidReturnComputeLogic);

    <V> V executeGridSingletonValueReturnComputeLogic(ValueReturnComputeLogic<V> valueReturnComputeLogic);

    <E,R> R executeGridSingletonFixInputTypeComputeLogic(FixInputTypeComputeLogic<E,R> fixInputTypeComputeLogic, E valeOfInputType);

    public void executeGridMultipleVoidReturnComputeLogic(Collection<VoidReturnComputeLogic> voidReturnComputeLogic);

    <V> Collection<V> executeGridMultipleValueReturnComputeLogic(Collection<ValueReturnComputeLogic<V>> valueReturnComputeLogics);

    <E,R> Collection<R> executeGridMultipleFixInputTypeComputeLogic(FixInputTypeComputeLogic<E,R> fixInputTypeComputeLogics, Collection<E> valeOfInputType);

    AsyncExecutionResultHandler asyncExecutePerUnitVoidReturnComputeLogic(VoidReturnComputeLogic voidReturnComputeLogic);
}
