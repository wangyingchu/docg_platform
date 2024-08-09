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

        ComputeGridRealtimeStatisticsInfo computeGridRealtimeStatisticsInfo = computeGrid.getGridRealtimeStatisticsInfo();
        Assert.assertNotNull(computeGridRealtimeStatisticsInfo);

        Set<ComputeUnitRealtimeStatisticsInfo> computeUnitRealtimeStatisticsInfoSet = computeGrid.getComputeUnitsRealtimeStatisticsInfo();
        Assert.assertNotNull(computeUnitRealtimeStatisticsInfoSet);

        Set<DataComputeUnitMetaInfo> dataComputeUnitMetaInfoSet = computeGrid.listDataComputeUnit();
        Assert.assertNotNull(dataComputeUnitMetaInfoSet);

        Set<DataSliceMetaInfo> dataSliceMetaInfoSet = computeGrid.listDataSlice();
        Assert.assertNotNull(dataSliceMetaInfoSet);
        Assert.assertEquals(dataSliceMetaInfoSet.size(),0);
    }
}
