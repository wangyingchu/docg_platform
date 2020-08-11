package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;

public class EntitiesOperationStatistics {

    private long successItemsCount = 0;
    private long failItemsCount = 0;
    private Date startTime;
    private Date finishTime;
    private String operationSummary;

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

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
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
}
