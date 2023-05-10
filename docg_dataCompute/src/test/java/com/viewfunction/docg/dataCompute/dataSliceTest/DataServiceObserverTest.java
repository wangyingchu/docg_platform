package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceObserver;

public class DataServiceObserverTest {

    public static void main(String[] args){


        DataServiceObserver dataServiceObserver = DataServiceObserver.getObserverInstance();
        dataServiceObserver.closeObserveSession();

    }



}
