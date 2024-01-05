package com.viewfunction.docg.dataCompute.computeServiceCore.term;

public interface ComputeService extends AutoCloseable{

    public void deployGridComputeFunction(String functionName,ComputeFunction computeFunction);

    public void deployPerUnitComputeFunction(String functionName,ComputeFunction computeFunction);

    public void deployMultipleUnitComputeFunction(String functionName,ComputeFunction computeFunction,int functionCount);

    public Object getComputeFunction(String functionName,Class functionClass);
}
