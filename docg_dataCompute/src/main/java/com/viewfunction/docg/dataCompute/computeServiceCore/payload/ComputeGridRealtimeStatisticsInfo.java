package com.viewfunction.docg.dataCompute.computeServiceCore.payload;

import java.time.LocalDateTime;

public class ComputeGridRealtimeStatisticsInfo {
    private LocalDateTime gridStartTime;
    private long gridUpTimeInMinute;
    private int dataComputeUnitsAmount;
    private String oldestUnitId;
    private String youngestUnitId;
    private long maxAvailableMemoryInMB;
    private long usedMemoryInMB;
    private long assignedMemoryInMB;



    private int availableCPUCores;

    private int totalExecutedComputes;

    private long gridTotalIdleTimeInSecond;

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

    public long getMaxAvailableMemoryInMB() {
        return maxAvailableMemoryInMB;
    }

    public void setMaxAvailableMemoryInMB(long maxAvailableMemoryInMB) {
        this.maxAvailableMemoryInMB = maxAvailableMemoryInMB;
    }

    public long getUsedMemoryInMB() {
        return usedMemoryInMB;
    }

    public void setUsedMemoryInMB(long usedMemoryInMB) {
        this.usedMemoryInMB = usedMemoryInMB;
    }

    public long getAssignedMemoryInMB() {
        return assignedMemoryInMB;
    }

    public void setAssignedMemoryInMB(long assignedMemoryInMB) {
        this.assignedMemoryInMB = assignedMemoryInMB;
    }

    public int getAvailableCPUCores() {
        return availableCPUCores;
    }

    public void setAvailableCPUCores(int availableCPUCores) {
        this.availableCPUCores = availableCPUCores;
    }

    public int getTotalExecutedComputes() {
        return totalExecutedComputes;
    }

    public void setTotalExecutedComputes(int totalExecutedComputes) {
        this.totalExecutedComputes = totalExecutedComputes;
    }
}
