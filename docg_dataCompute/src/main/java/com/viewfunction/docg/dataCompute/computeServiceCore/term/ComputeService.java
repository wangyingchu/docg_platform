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

    public void executeGridSingletonComputeLogic(VoidReturnComputeLogic voidReturnComputeLogic);

    <V> V executeGridSingletonComputeLogic(ValueReturnComputeLogic<V> valueReturnComputeLogic);

    <E,R> R executeGridSingletonComputeLogic(FixInputTypeComputeLogic<E,R> fixInputTypeComputeLogic,E valeOfInputType);

    public void executeGridMultipleVoidReturnComputeLogic(Collection<VoidReturnComputeLogic> voidReturnComputeLogic);

    <V> Collection<V> executeGridMultipleComputeLogic(Collection<ValueReturnComputeLogic<V>> valueReturnComputeLogics);

    <E,R> Collection<R> executeGridMultipleComputeLogic(FixInputTypeComputeLogic<E,R> fixInputTypeComputeLogics,Collection<E> valeOfInputType);
}
