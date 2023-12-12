package com.viewfunction.docg.dataCompute.computeServiceCore.payload;

import java.time.LocalDateTime;

public class ComputeUnitRealtimeStatisticsInfo {
    private String unitID;
    private LocalDateTime unitStartTime;
    private long unitUpTimeInMinute;
    private long maxAvailableMemoryInMB;
    private long usedMemoryInMB;
    private long assignedMemoryInMB;
    private int availableCPUCores;
    private double currentCPULoadPercentage;
    private double averageCPULoadPercentage;
    private long currentIdleTimeInSecond;
    private long totalIdleTimeInSecond;
    private long totalBusyTimeInSecond;
}
