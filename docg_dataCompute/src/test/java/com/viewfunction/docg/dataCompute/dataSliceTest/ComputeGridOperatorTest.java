package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.ComputeGridOperator;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.ComputeGridRealtimeStatisticsInfo;

public class ComputeGridOperatorTest {

    public static void main(String[] args) {

        try (ComputeGridOperator computeGridOperator = ComputeGridOperator.getComputeGridOperator()) {
            ComputeGridRealtimeStatisticsInfo targetComputeGridRealtimeStatisticsInfo = computeGridOperator.getComputeGridRealtimeMetrics();
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getGridStartTime());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getGridUpTimeInMinute());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getGridTotalIdleTimeInSecond());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getDataComputeUnitsAmount());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getOldestUnitId());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getYoungestUnitId());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getMaxAvailableMemoryInMB());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getAssignedMemoryInMB());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getUsedMemoryInMB());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getAvailableCPUCores());
            System.out.println(targetComputeGridRealtimeStatisticsInfo.getTotalExecutedComputes());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}