package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeGrid.ComputeGridOperator;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeGrid.ComputeGridRealtimeMetrics;

public class ComputeGridOperatorTest {

    public static void main(String[] args) {

        try (ComputeGridOperator computeGridOperator = ComputeGridOperator.getComputeGridOperator()) {
            ComputeGridRealtimeMetrics targetComputeGridRealtimeMetrics = computeGridOperator.getComputeGridRealtimeMetrics();

            System.out.println(targetComputeGridRealtimeMetrics.getGridStartTime());
            System.out.println(targetComputeGridRealtimeMetrics.getGridUpTimeInMinute());
            System.out.println(targetComputeGridRealtimeMetrics.getGridIdleTimeInSecond());
            System.out.println(targetComputeGridRealtimeMetrics.getGridTotalIdleTimeInSecond());
            System.out.println(targetComputeGridRealtimeMetrics.getDataComputeUnitsAmount());
            System.out.println(targetComputeGridRealtimeMetrics.getOldestUnitId());
            System.out.println(targetComputeGridRealtimeMetrics.getYoungestUnitId());
            System.out.println(targetComputeGridRealtimeMetrics.getUsedNonHeapMemoryInMB());
            System.out.println(targetComputeGridRealtimeMetrics.getTotalNonHeapMemoryInMB());
            System.out.println(targetComputeGridRealtimeMetrics.getUsedHeapMemoryInMB());
            System.out.println(targetComputeGridRealtimeMetrics.getTotalHeapMemoryInMB());
            System.out.println(targetComputeGridRealtimeMetrics.getAvailableCPUCores());
            System.out.println(targetComputeGridRealtimeMetrics.getCurrentCPULoadPercentage());
            System.out.println(targetComputeGridRealtimeMetrics.getAverageCPULoadPercentage());
            System.out.println(targetComputeGridRealtimeMetrics.getTotalExecutedComputes());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}