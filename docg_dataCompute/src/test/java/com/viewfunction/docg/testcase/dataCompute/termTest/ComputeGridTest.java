package com.viewfunction.docg.testcase.dataCompute.termTest;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ComputeGridTest {

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for CoreRealmTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testComputeGridFunction(){
        Assert.assertTrue(true);
    }
}
