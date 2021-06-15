package com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.result;

import java.util.Date;

public class DataSliceOperationResult {

    private long successItemsCount = 0;
    private long failItemsCount = 0;
    private Date startTime;
    private Date finishTime;
    private String operationSummary;

    public DataSliceOperationResult(){
        this.startTime = new Date();
    }

    public void increaseSuccessCount() {
        successItemsCount++;
    }

    public void increaseFailCount() {
        failItemsCount++;
    }

    public long getSuccessItemsCount() {
        return successItemsCount;
    }

    public long getFailItemsCount() {
        return failItemsCount;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void finishOperation() {
        this.finishTime = new Date();
    }

    public String getOperationSummary() {
        return operationSummary;
    }

    public void setOperationSummary(String operationSummary) {
        this.operationSummary = operationSummary;
    }

    public void setFailItemsCount(long failItemsCount) {
        this.failItemsCount = failItemsCount;
    }

    public void setSuccessItemsCount(long successItemsCount) {
        this.successItemsCount = successItemsCount;
    }
}
