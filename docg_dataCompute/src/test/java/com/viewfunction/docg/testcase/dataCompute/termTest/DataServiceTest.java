package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.*;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataServiceTest {

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for DataServiceTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testDataServiceFunction() throws ComputeGridException {
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(DataService dataService = targetComputeGrid.getDataService()){
            List<String> dataSliceNameList = dataService.listDataSliceNames();
            Assert.assertEquals(dataSliceNameList.size(),0);

            Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
            dataSlicePropertyMap.put("property1",DataSlicePropertyType.STRING);
            dataSlicePropertyMap.put("property2",DataSlicePropertyType.INT);
            dataSlicePropertyMap.put("property3",DataSlicePropertyType.DOUBLE);
            dataSlicePropertyMap.put("property4",DataSlicePropertyType.STRING);
            List<String> pkList = new ArrayList<>();
            pkList.add("property1");
            pkList.add("property2");
            DataSlice perUnitDataSlice1 = dataService.createPerUnitDataSlice("PerUnitDataSlice1","sliceGroup1",dataSlicePropertyMap,pkList);
            Assert.assertNotNull(perUnitDataSlice1);

            DataSliceMetaInfo dataSliceMetaInfo1 = perUnitDataSlice1.getDataSliceMetaInfo();
            Assert.assertNotNull(dataSliceMetaInfo1);
            Assert.assertEquals(dataSliceMetaInfo1.getDataStoreMode(), DataSliceStoreMode.PerUnit);
            Assert.assertEquals(dataSliceMetaInfo1.getSliceGroupName(),   "\""+"sliceGroup1".toUpperCase()+"\"");
            Assert.assertEquals(dataSliceMetaInfo1.getDataSliceName(),"PerUnitDataSlice1");

            perUnitDataSlice1 =  dataService.getDataSlice("PerUnitDataSlice1");
            Assert.assertNotNull(perUnitDataSlice1);
            Assert.assertEquals(perUnitDataSlice1.getDataSliceMetaInfo().getDataSliceName(),"PerUnitDataSlice1");

            dataSliceNameList = dataService.listDataSliceNames();
            Assert.assertEquals(dataSliceNameList.size(),1);
            Assert.assertEquals(dataSliceNameList.get(0),"PerUnitDataSlice1");

            dataService.eraseDataSlice("PerUnitDataSlice1");

            dataSliceNameList = dataService.listDataSliceNames();
            Assert.assertEquals(dataSliceNameList.size(),0);

            perUnitDataSlice1 =  dataService.getDataSlice("PerUnitDataSlice1");
            Assert.assertNull(perUnitDataSlice1);

            DataSlice gridDataSlice1 = dataService.createGridDataSlice("GridDataSlice1","sliceGroup2",dataSlicePropertyMap,pkList);
            Assert.assertNotNull(gridDataSlice1);

            DataSliceMetaInfo dataSliceMetaInfo2 = gridDataSlice1.getDataSliceMetaInfo();
            Assert.assertNotNull(dataSliceMetaInfo2);
            Assert.assertEquals(dataSliceMetaInfo2.getDataStoreMode(), DataSliceStoreMode.Grid);
            Assert.assertEquals(dataSliceMetaInfo2.getSliceGroupName(),   "\""+"sliceGroup2".toUpperCase()+"\"");
            Assert.assertEquals(dataSliceMetaInfo2.getDataSliceName(),"GridDataSlice1");

            dataSliceNameList = dataService.listDataSliceNames();
            Assert.assertEquals(dataSliceNameList.size(),1);
            Assert.assertEquals(dataSliceNameList.get(0),"GridDataSlice1");

            dataService.eraseDataSlice("GridDataSlice1");

            dataSliceNameList = dataService.listDataSliceNames();
            Assert.assertEquals(dataSliceNameList.size(),0);

            perUnitDataSlice1 =  dataService.getDataSlice("GridDataSlice1");
            Assert.assertNull(perUnitDataSlice1);

        } catch (ComputeGridException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
