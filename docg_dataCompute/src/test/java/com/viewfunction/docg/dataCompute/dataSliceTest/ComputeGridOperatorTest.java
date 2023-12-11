package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeGrid.ComputeGridOperator;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.ComputeGridRealtimeMetaInfo;

public class ComputeGridOperatorTest {

    public static void main(String[] args) {

        try (ComputeGridOperator computeGridOperator = ComputeGridOperator.getComputeGridOperator()) {
            ComputeGridRealtimeMetaInfo targetComputeGridRealtimeMetaInfo = computeGridOperator.getComputeGridRealtimeMetrics();

            System.out.println(targetComputeGridRealtimeMetaInfo.getGridStartTime());
            System.out.println(targetComputeGridRealtimeMetaInfo.getGridUpTimeInMinute());
            System.out.println(targetComputeGridRealtimeMetaInfo.getGridIdleTimeInSecond());
            System.out.println(targetComputeGridRealtimeMetaInfo.getGridTotalIdleTimeInSecond());
            System.out.println(targetComputeGridRealtimeMetaInfo.getDataComputeUnitsAmount());
            System.out.println(targetComputeGridRealtimeMetaInfo.getOldestUnitId());
            System.out.println(targetComputeGridRealtimeMetaInfo.getYoungestUnitId());
            System.out.println(targetComputeGridRealtimeMetaInfo.getUsedNonHeapMemoryInMB());
            System.out.println(targetComputeGridRealtimeMetaInfo.getTotalNonHeapMemoryInMB());
            System.out.println(targetComputeGridRealtimeMetaInfo.getUsedHeapMemoryInMB());
            System.out.println(targetComputeGridRealtimeMetaInfo.getTotalHeapMemoryInMB());
            System.out.println(targetComputeGridRealtimeMetaInfo.getAvailableCPUCores());
            System.out.println(targetComputeGridRealtimeMetaInfo.getCurrentCPULoadPercentage());
            System.out.println(targetComputeGridRealtimeMetaInfo.getAverageCPULoadPercentage());
            System.out.println(targetComputeGridRealtimeMetaInfo.getTotalExecutedComputes());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}