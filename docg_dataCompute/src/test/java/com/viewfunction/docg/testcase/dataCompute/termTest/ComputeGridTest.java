package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.ComputeGridRealtimeStatisticsInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.ComputeUnitRealtimeStatisticsInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataComputeUnitMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.ComputeGridImplTech;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Set;

public class ComputeGridTest {

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ComputeGridTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testComputeGridFunction() throws ComputeGridException {
        ComputeGrid computeGrid = ComputeGridTermFactory.getComputeGrid();
        Assert.assertNotNull(computeGrid);
        Assert.assertEquals(computeGrid.getGridImplTech(), ComputeGridImplTech.IGNITE);

        ComputeService computeService = computeGrid.getComputeService();
        Assert.assertNotNull(computeService);

        DataService dataService = computeGrid.getDataService();
        Assert.assertNotNull(dataService);

        ComputeGridRealtimeStatisticsInfo targetComputeGridRealtimeStatisticsInfo = computeGrid.getGridRealtimeStatisticsInfo();
        Assert.assertNotNull(targetComputeGridRealtimeStatisticsInfo);
        Assert.assertNotNull(targetComputeGridRealtimeStatisticsInfo.getOldestUnitId());
        Assert.assertNotNull(targetComputeGridRealtimeStatisticsInfo.getYoungestUnitId());
        Assert.assertTrue(targetComputeGridRealtimeStatisticsInfo.getAssignedMemoryInMB()>=0);

        Assert.assertTrue(targetComputeGridRealtimeStatisticsInfo.getUsedMemoryInMB()>=0);
        Assert.assertTrue(targetComputeGridRealtimeStatisticsInfo.getMaxAvailableMemoryInMB()>0);
        Assert.assertNotNull(targetComputeGridRealtimeStatisticsInfo.getGridStartTime());
        Assert.assertTrue(targetComputeGridRealtimeStatisticsInfo.getGridUpTimeInMinute()>=0);
        Assert.assertTrue(targetComputeGridRealtimeStatisticsInfo.getTotalIdleTimeInSecond()>=0);
        Assert.assertEquals(targetComputeGridRealtimeStatisticsInfo.getDataComputeUnitsAmount(),1);
        Assert.assertTrue(targetComputeGridRealtimeStatisticsInfo.getTotalAvailableCPUCores()>1);

        Set<ComputeUnitRealtimeStatisticsInfo> computeUnitRealtimeStatisticsInfoSet = computeGrid.getComputeUnitsRealtimeStatisticsInfo();
        Assert.assertNotNull(computeUnitRealtimeStatisticsInfoSet);

        for(ComputeUnitRealtimeStatisticsInfo currentComputeUnitRealtimeStatisticsInfo:computeUnitRealtimeStatisticsInfoSet){
            Assert.assertNotNull(currentComputeUnitRealtimeStatisticsInfo.getUnitStartTime());
            Assert.assertNotNull(currentComputeUnitRealtimeStatisticsInfo.getUnitID());
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getAssignedMemoryInMB()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getMaxAvailableMemoryInMB()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getUsedMemoryInMB()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getAvailableCPUCores()>0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getUnitUpTimeInMinute()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getTotalIdleTimeInSecond()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getTotalBusyTimeInSecond()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getAverageCPULoadPercentage()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getCurrentCPULoadPercentage()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getIdleTimePercentage()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getBusyTimePercentage()>=0);
            Assert.assertTrue(currentComputeUnitRealtimeStatisticsInfo.getCurrentIdleTimeInSecond()>=0);
        }

        Set<DataComputeUnitMetaInfo> dataComputeUnitMetaInfoSet = computeGrid.listDataComputeUnit();
        Assert.assertNotNull(dataComputeUnitMetaInfoSet);

        Set<DataSliceMetaInfo> dataSliceMetaInfoSet = computeGrid.listDataSlice();
        Assert.assertNotNull(dataSliceMetaInfoSet);
        Assert.assertEquals(dataSliceMetaInfoSet.size(),0);
    }
}
