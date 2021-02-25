package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.result.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil;

public class CoreRealmOperationUtilTest {

    public static void main(String[] args){

        DataSliceOperationResult dataSliceOperationResult = CoreRealmOperationUtil.syncConceptionKindToDataSlice("Fire911Call",null,null);

        System.out.println(dataSliceOperationResult.getStartTime());
        System.out.println(dataSliceOperationResult.getFinishTime());
        System.out.println(dataSliceOperationResult.getSuccessItemsCount());
        System.out.println(dataSliceOperationResult.getFailItemsCount());
        System.out.println(dataSliceOperationResult.getOperationSummary());
    }
}
