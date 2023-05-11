package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceObserver;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.vo.DataComputeUnitVO;

import java.util.Set;

public class DataServiceObserverTest {

    public static void main(String[] args){
        DataServiceObserver dataServiceObserver = DataServiceObserver.getObserverInstance();
        Set<DataComputeUnitVO> dataComputeUnitVOSet = dataServiceObserver.listDataComputeUnit();
        dataServiceObserver.closeObserveSession();
        for(DataComputeUnitVO currentDataComputeUnitVO:dataComputeUnitVOSet){
            System.out.println(currentDataComputeUnitVO.getUnitID());
            System.out.println(currentDataComputeUnitVO.getUnitType());
            System.out.println(currentDataComputeUnitVO.getUnitHostNames());
            System.out.println(currentDataComputeUnitVO.getUnitIPAddresses());
            System.out.println(currentDataComputeUnitVO.getIsClientUnit());
            System.out.println("==========================");
        }
    }
}
