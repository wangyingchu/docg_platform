package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

public class DataServiceTest {

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for DataServiceTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testDataServiceFunction() throws ComputeGridException {
        ComputeGrid computeGrid = ComputeGridTermFactory.getComputeGrid();
        DataService dataService = computeGrid.getDataService();
        List<String> dataSliceNameList = dataService.listDataSliceNames();
        Assert.assertEquals(dataSliceNameList.size(),0);
        for (String dataSliceName : dataSliceNameList) {}

    }
}
