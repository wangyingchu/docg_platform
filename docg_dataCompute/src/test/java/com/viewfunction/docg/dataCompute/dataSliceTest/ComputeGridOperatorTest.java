package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeGrid.ComputeGridOperator;

public class ComputeGridOperatorTest {

    public static void main(String[] args) {

        try (ComputeGridOperator computeGridOperator = ComputeGridOperator.getComputeGridOperator()) {
            computeGridOperator.getComputeGridRealtimeMetrics();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}