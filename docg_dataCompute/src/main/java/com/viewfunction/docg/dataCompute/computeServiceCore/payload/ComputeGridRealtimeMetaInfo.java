package com.viewfunction.docg.dataCompute.computeServiceCore.payload;

import java.time.LocalDateTime;

public class ComputeGridRealtimeMetaInfo {
    private LocalDateTime gridStartTime;
    private long gridUpTimeInMinute;
    private long gridIdleTimeInSecond;
    private long gridTotalIdleTimeInSecond;
    private int dataComputeUnitsAmount;
    private String oldestUnitId;
    private String youngestUnitId;
    private long usedNonHeapMemoryInMB;
    private long usedHeapMemoryInMB;
    private long totalNonHeapMemoryInMB;
    private long totalHeapMemoryInMB;
    private int availableCPUCores;
    private double currentCPULoadPercentage;
    private double averageCPULoadPercentage;
    private int totalExecutedComputes;

    public LocalDateTime getGridStartTime() {
        return gridStartTime;
    }

    public void setGridStartTime(LocalDateTime gridStartTime) {
        this.gridStartTime = gridStartTime;
    }

    public long getGridUpTimeInMinute() {
        return gridUpTimeInMinute;
    }

    public void setGridUpTimeInMinute(long gridUpTimeInMinute) {
        this.gridUpTimeInMinute = gridUpTimeInMinute;
    }

    public long getGridIdleTimeInSecond() {
        return gridIdleTimeInSecond;
    }

    public void setGridIdleTimeInSecond(long gridIdleTimeInSecond) {
        this.gridIdleTimeInSecond = gridIdleTimeInSecond;
    }

    public long getGridTotalIdleTimeInSecond() {
        return gridTotalIdleTimeInSecond;
    }

    public void setGridTotalIdleTimeInSecond(long gridTotalIdleTimeInSecond) {
        this.gridTotalIdleTimeInSecond = gridTotalIdleTimeInSecond;
    }

    public int getDataComputeUnitsAmount() {
        return dataComputeUnitsAmount;
    }

    public void setDataComputeUnitsAmount(int dataComputeUnitsAmount) {
        this.dataComputeUnitsAmount = dataComputeUnitsAmount;
    }

    public String getOldestUnitId() {
        return oldestUnitId;
    }

    public void setOldestUnitId(String oldestUnitId) {
        this.oldestUnitId = oldestUnitId;
    }

    public String getYoungestUnitId() {
        return youngestUnitId;
    }

    public void setYoungestUnitId(String youngestUnitId) {
        this.youngestUnitId = youngestUnitId;
    }

    public long getUsedNonHeapMemoryInMB() {
        return usedNonHeapMemoryInMB;
    }

    public void setUsedNonHeapMemoryInMB(long usedNonHeapMemoryInMB) {
        this.usedNonHeapMemoryInMB = usedNonHeapMemoryInMB;
    }

    public long getUsedHeapMemoryInMB() {
        return usedHeapMemoryInMB;
    }

    public void setUsedHeapMemoryInMB(long usedHeapMemoryInMB) {
        this.usedHeapMemoryInMB = usedHeapMemoryInMB;
    }

    public long getTotalNonHeapMemoryInMB() {
        return totalNonHeapMemoryInMB;
    }

    public void setTotalNonHeapMemoryInMB(long totalNonHeapMemoryInMB) {
        this.totalNonHeapMemoryInMB = totalNonHeapMemoryInMB;
    }

    public long getTotalHeapMemoryInMB() {
        return totalHeapMemoryInMB;
    }

    public void setTotalHeapMemoryInMB(long totalHeapMemoryInMB) {
        this.totalHeapMemoryInMB = totalHeapMemoryInMB;
    }

    public int getAvailableCPUCores() {
        return availableCPUCores;
    }

    public void setAvailableCPUCores(int availableCPUCores) {
        this.availableCPUCores = availableCPUCores;
    }

    public double getCurrentCPULoadPercentage() {
        return currentCPULoadPercentage;
    }

    public void setCurrentCPULoadPercentage(double currentCPULoadPercentage) {
        this.currentCPULoadPercentage = currentCPULoadPercentage;
    }

    public double getAverageCPULoadPercentage() {
        return averageCPULoadPercentage;
    }

    public void setAverageCPULoadPercentage(double averageCPULoadPercentage) {
        this.averageCPULoadPercentage = averageCPULoadPercentage;
    }

    public int getTotalExecutedComputes() {
        return totalExecutedComputes;
    }

    public void setTotalExecutedComputes(int totalExecutedComputes) {
        this.totalExecutedComputes = totalExecutedComputes;
    }
}
