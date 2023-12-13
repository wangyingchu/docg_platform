package com.viewfunction.docg.dataCompute.computeServiceCore.payload;

import java.time.LocalDateTime;

public class ComputeUnitRealtimeStatisticsInfo {
    private String unitID;
    private LocalDateTime unitStartTime;
    private long unitUpTimeInMinute;
    private int availableCPUCores;
    private double currentCPULoadPercentage;
    private double averageCPULoadPercentage;
    private long currentIdleTimeInSecond;
    private long totalIdleTimeInSecond;
    private long totalBusyTimeInSecond;
    private double busyTimePercentage;
    private double idleTimePercentage;
    private long maxAvailableMemoryInMB;
    private long usedMemoryInMB;
    private long assignedMemoryInMB;

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public LocalDateTime getUnitStartTime() {
        return unitStartTime;
    }

    public void setUnitStartTime(LocalDateTime unitStartTime) {
        this.unitStartTime = unitStartTime;
    }

    public long getUnitUpTimeInMinute() {
        return unitUpTimeInMinute;
    }

    public void setUnitUpTimeInMinute(long unitUpTimeInMinute) {
        this.unitUpTimeInMinute = unitUpTimeInMinute;
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

    public long getCurrentIdleTimeInSecond() {
        return currentIdleTimeInSecond;
    }

    public void setCurrentIdleTimeInSecond(long currentIdleTimeInSecond) {
        this.currentIdleTimeInSecond = currentIdleTimeInSecond;
    }

    public long getTotalIdleTimeInSecond() {
        return totalIdleTimeInSecond;
    }

    public void setTotalIdleTimeInSecond(long totalIdleTimeInSecond) {
        this.totalIdleTimeInSecond = totalIdleTimeInSecond;
    }

    public long getTotalBusyTimeInSecond() {
        return totalBusyTimeInSecond;
    }

    public void setTotalBusyTimeInSecond(long totalBusyTimeInSecond) {
        this.totalBusyTimeInSecond = totalBusyTimeInSecond;
    }

    public double getBusyTimePercentage() {
        return busyTimePercentage;
    }

    public void setBusyTimePercentage(double busyTimePercentage) {
        this.busyTimePercentage = busyTimePercentage;
    }

    public double getIdleTimePercentage() {
        return idleTimePercentage;
    }

    public void setIdleTimePercentage(double idleTimePercentage) {
        this.idleTimePercentage = idleTimePercentage;
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
}
