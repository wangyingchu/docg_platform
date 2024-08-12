package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceDetailInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceQueryResult;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlice;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

public class DataSliceTest {
    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for DataSliceTest");
        System.out.println("--------------------------------------------------");
    }

    @Test
    public void testDataSliceFunction() throws ComputeGridException {
        String testDataSliceName = "testDataSliceFunctionSlice";
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(DataService dataService = targetComputeGrid.getDataService()){
            List<String> dataSliceNameList = dataService.listDataSliceNames();
            if(dataSliceNameList.contains(testDataSliceName)){
                dataService.eraseDataSlice(testDataSliceName);
            }

            Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
            dataSlicePropertyMap.put("property1",DataSlicePropertyType.STRING);
            dataSlicePropertyMap.put("property2",DataSlicePropertyType.INT);
            dataSlicePropertyMap.put("property3",DataSlicePropertyType.DOUBLE);
            dataSlicePropertyMap.put("property4",DataSlicePropertyType.STRING);
            List<String> pkList = new ArrayList<>();
            pkList.add("property1");
            pkList.add("property2");
            DataSlice targetDataSlice = dataService.createPerUnitDataSlice(testDataSliceName,testDataSliceName+"_Group",dataSlicePropertyMap,pkList);
            Assert.assertNotNull(targetDataSlice);

            List<Map<String,Object>> dataList = new ArrayList<>();
            for(int i=0;i<100000;i++) {
                Map<String, Object> dataPropertiesValue = new HashMap<>();
                dataPropertiesValue.put("property1", "DataProperty1Value" + new Date().getTime());
                dataPropertiesValue.put("property2", 1000+i);
                dataPropertiesValue.put("property3", 1238.999d);
                dataPropertiesValue.put("property4", "PROP$VALUE+"+i);
                dataList.add(dataPropertiesValue);
            }

            List<String> propertyList = new ArrayList<>();
            propertyList.add("property1");
            propertyList.add("property2");
            propertyList.add("property3");
            propertyList.add("property4");

            DataSliceOperationResult addDataResult1 = targetDataSlice.addDataRecords(propertyList,dataList);
            Assert.assertNotNull(addDataResult1.getOperationSummary());
            Assert.assertNotNull(addDataResult1.getStartTime());
            Assert.assertNotNull(addDataResult1.getFinishTime());
            Assert.assertEquals(addDataResult1.getSuccessItemsCount(),100000);
            Assert.assertEquals(addDataResult1.getFailItemsCount(),0);

            DataSliceMetaInfo dataSliceMetaInfo1 = targetDataSlice.getDataSliceMetaInfo();
            Assert.assertEquals(dataSliceMetaInfo1.getPrimaryDataCount(),100000);
            Assert.assertEquals(dataSliceMetaInfo1.getDataSliceName(),testDataSliceName);

            DataSliceDetailInfo dataSliceDetailInfo1 =targetComputeGrid.getDataSliceDetail(testDataSliceName);
            Assert.assertEquals(dataSliceDetailInfo1.getPrimaryDataCount(),100000);
            Assert.assertEquals(dataSliceDetailInfo1.getPrimaryKeyPropertiesNames().size(),2);
            Assert.assertTrue(dataSliceDetailInfo1.getPrimaryKeyPropertiesNames().contains("property1".toUpperCase()));
            Assert.assertTrue(dataSliceDetailInfo1.getPrimaryKeyPropertiesNames().contains("property2".toUpperCase()));

            Map<String, Object> dataPropertiesValue0 = new HashMap<>();
            String dateTimestampStr1 = ""+new Date().getTime();
            dataPropertiesValue0.put("property1", "DataProperty1Value" + dateTimestampStr1);
            dataPropertiesValue0.put("property2", 1);
            dataPropertiesValue0.put("property3", 1238.999d);
            dataPropertiesValue0.put("property4", "PROP$VALUE+"+100001);
            boolean addDataResult = targetDataSlice.addDataRecord(dataPropertiesValue0);
            Assert.assertTrue(addDataResult);

            Map<String, Object> dataPropertiesValue1 = new HashMap<>();
            String dateTimestampStr2 = ""+new Date().getTime();
            dataPropertiesValue1.put("property1", "DataProperty1Value" + dateTimestampStr2);
            dataPropertiesValue1.put("property2", 2);
            dataPropertiesValue1.put("property3", 1238.999d);
            dataPropertiesValue1.put("property4", "PROP$VALUE+"+100001);
            addDataResult = targetDataSlice.addDataRecord(dataPropertiesValue1);
            Assert.assertTrue(addDataResult);

            dataSliceDetailInfo1 =targetComputeGrid.getDataSliceDetail(testDataSliceName);
            Assert.assertEquals(dataSliceDetailInfo1.getPrimaryDataCount(),100002);

            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setDefaultFilteringItem(new EqualFilteringItem("property1", "DataProperty1Value" + dateTimestampStr1));
            DataSliceQueryResult dataSliceQueryResult = targetDataSlice.queryDataRecords(queryParameters);
            Assert.assertNotNull(dataSliceQueryResult);
            Assert.assertEquals(dataSliceQueryResult.getResultRecords().size(),1);
            Assert.assertNotNull(dataSliceQueryResult.getStartTime());
            Assert.assertNotNull(dataSliceQueryResult.getFinishTime());
            Assert.assertNotNull(dataSliceQueryResult.getOperationSummary());
            Assert.assertNotNull(dataSliceQueryResult.getQueryLogic());

            Map<String,Object> dataPropertiesValue2 = new HashMap<>();
            dataPropertiesValue2.put("property1", "DataProperty1Value" + dateTimestampStr2);
            dataPropertiesValue2.put("property2", 2);
            Map<String,Object> resultData = targetDataSlice.getDataRecordByPrimaryKeys(dataPropertiesValue2);
            Assert.assertNotNull(resultData);
            Assert.assertEquals(resultData.size(),4);
            Assert.assertEquals(resultData.get("property1".toUpperCase()),"DataProperty1Value" + dateTimestampStr2);
            Assert.assertEquals(resultData.get("property2".toUpperCase()),2);
            Assert.assertEquals(resultData.get("property3".toUpperCase()),1238.999d);
            Assert.assertEquals(resultData.get("property4".toUpperCase()),"PROP$VALUE+"+100001);






            dataService.eraseDataSlice(testDataSliceName);
            targetDataSlice = dataService.getDataSlice(testDataSliceName);
            Assert.assertNull(targetDataSlice);
        }catch (ComputeGridException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
