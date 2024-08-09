package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class DataServiceTest {

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ComputeGridTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testDataServiceFunction() throws ComputeGridException {

    }
}
