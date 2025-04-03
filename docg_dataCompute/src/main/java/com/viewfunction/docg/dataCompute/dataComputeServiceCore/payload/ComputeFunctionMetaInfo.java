package com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload;

public class ComputeFunctionMetaInfo {

    private String functionName;
    private String functionImplementation;
    private int maxFunctionPerUnit;
    private int totalRunningCount;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionImplementation() {
        return functionImplementation;
    }

    public void setFunctionImplementation(String functionImplementation) {
        this.functionImplementation = functionImplementation;
    }

    public int getMaxFunctionPerUnit() {
        return maxFunctionPerUnit;
    }

    public void setMaxFunctionPerUnit(int maxFunctionPerUnit) {
        this.maxFunctionPerUnit = maxFunctionPerUnit;
    }

    public int getTotalRunningCount() {
        return totalRunningCount;
    }

    public void setTotalRunningCount(int totalRunningCount) {
        this.totalRunningCount = totalRunningCount;
    }
}
