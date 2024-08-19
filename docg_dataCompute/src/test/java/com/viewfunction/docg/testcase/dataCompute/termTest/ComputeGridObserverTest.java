package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.ComputeGridObserver;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.*;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.*;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;

public class ComputeGridObserverTest {

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for ComputeGridObserverTest");
        System.out.println("--------------------------------------------------");

        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(DataService dataService = targetComputeGrid.getDataService()){
            Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();

            dataSlicePropertyMap.put("property1",DataSlicePropertyType.BOOLEAN);
            dataSlicePropertyMap.put("property2",DataSlicePropertyType.INT);
            dataSlicePropertyMap.put("property3",DataSlicePropertyType.SHORT);
            dataSlicePropertyMap.put("property4",DataSlicePropertyType.LONG);
            dataSlicePropertyMap.put("property5",DataSlicePropertyType.FLOAT);
            dataSlicePropertyMap.put("property6",DataSlicePropertyType.DOUBLE);
            dataSlicePropertyMap.put("property7",DataSlicePropertyType.DATE);
            dataSlicePropertyMap.put("property8",DataSlicePropertyType.TIME);
            dataSlicePropertyMap.put("property9",DataSlicePropertyType.TIMESTAMP);
            dataSlicePropertyMap.put("property10",DataSlicePropertyType.STRING);
            dataSlicePropertyMap.put("property11",DataSlicePropertyType.BYTE);
            dataSlicePropertyMap.put("property12",DataSlicePropertyType.DECIMAL);
            dataSlicePropertyMap.put("property13",DataSlicePropertyType.BINARY);
            dataSlicePropertyMap.put("property14",DataSlicePropertyType.GEOMETRY);
            dataSlicePropertyMap.put("property15",DataSlicePropertyType.UUID);

            List<String> pkList = new ArrayList<>();
            pkList.add("property1");
            pkList.add("property2");
            dataService.createPerUnitDataSlice("gridDataSliceA","sliceGroup1",dataSlicePropertyMap,pkList);
            dataService.createGridDataSlice("gridDataSliceB","sliceGroup1",dataSlicePropertyMap,pkList);
        } catch (ComputeGridException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testComputeGridObserverFunction() throws ComputeGridException {
        ComputeGridObserver computeGridObserver = ComputeGridObserver.getObserverInstance();
        Set<DataComputeUnitMetaInfo> dataComputeUnitMetaInfoSet = computeGridObserver.listDataComputeUnit();
        Set<DataSliceMetaInfo> dataSliceMetaInfoSet = computeGridObserver.listDataSlice();
        DataSliceDetailInfo dataSliceDetailInfo = computeGridObserver.getDataSliceDetail("gridDataSliceA");
        ComputeGridRealtimeStatisticsInfo computeGridRealtimeStatisticsInfo = computeGridObserver.getGridRealtimeStatisticsInfo();
        Set<ComputeUnitRealtimeStatisticsInfo> computeUnitRealtimeStatisticsInfoSet = computeGridObserver.getComputeUnitsRealtimeStatisticsInfo();
        computeGridObserver.closeObserveSession();
        for(DataSliceMetaInfo currentDataSliceMetaInfo : dataSliceMetaInfoSet){
            Assert.assertNotNull(currentDataSliceMetaInfo.getDataSliceName());
            Assert.assertNotNull(currentDataSliceMetaInfo.getDataStoreMode());
            Assert.assertEquals(currentDataSliceMetaInfo.getAtomicityMode(), DataSliceAtomicityMode.ATOMIC);
            Assert.assertEquals(currentDataSliceMetaInfo.getBackupDataCount(),0);
            if(currentDataSliceMetaInfo.getDataSliceName().equals("gridDataSliceA")){
                Assert.assertEquals(currentDataSliceMetaInfo.getDataStoreMode(), DataSliceStoreMode.PerUnit);
            }
            if(currentDataSliceMetaInfo.getDataSliceName().equals("gridDataSliceB")){
                Assert.assertEquals(currentDataSliceMetaInfo.getDataStoreMode(), DataSliceStoreMode.Grid);
            }
            Assert.assertEquals(currentDataSliceMetaInfo.getPrimaryDataCount(),0);
            Assert.assertEquals(currentDataSliceMetaInfo.getSliceGroupName(),"\"SLICEGROUP1\"");
            Assert.assertEquals(currentDataSliceMetaInfo.getTotalDataCount(),0);
        }
        for(DataComputeUnitMetaInfo currentDataComputeUnitMetaInfo : dataComputeUnitMetaInfoSet){
            Assert.assertNotNull(currentDataComputeUnitMetaInfo.getUnitID());
            Assert.assertEquals(currentDataComputeUnitMetaInfo.getUnitType(),"SERVICE_UNIT");
            Assert.assertNotNull(currentDataComputeUnitMetaInfo.getUnitHostNames());
            Assert.assertFalse(currentDataComputeUnitMetaInfo.getUnitHostNames().isEmpty());
            Assert.assertNotNull(currentDataComputeUnitMetaInfo.getUnitIPAddresses());
            Assert.assertFalse(currentDataComputeUnitMetaInfo.getUnitIPAddresses().isEmpty());
            Assert.assertFalse(currentDataComputeUnitMetaInfo.getIsClientUnit());
        }

        Assert.assertEquals(dataSliceDetailInfo.getDataSliceName(),"gridDataSliceA");
        Assert.assertEquals(dataSliceDetailInfo.getDataStoreMode(),DataSliceStoreMode.PerUnit);
        Assert.assertEquals(dataSliceDetailInfo.getAtomicityMode(), DataSliceAtomicityMode.ATOMIC);
        Assert.assertEquals(dataSliceDetailInfo.getPrimaryDataCount(),0);
        Assert.assertEquals(dataSliceDetailInfo.getTotalDataCount(),0);
        Assert.assertNotNull(dataSliceDetailInfo.getPropertiesDefinition());
        Assert.assertEquals(dataSliceDetailInfo.getPropertiesDefinition().size(),15);

        List<String> propertyList = new ArrayList<>();
        propertyList.add("property1".toUpperCase());
        propertyList.add("property2".toUpperCase());
        propertyList.add("property3".toUpperCase());
        propertyList.add("property4".toUpperCase());
        propertyList.add("property5".toUpperCase());
        propertyList.add("property6".toUpperCase());
        propertyList.add("property7".toUpperCase());
        propertyList.add("property8".toUpperCase());
        propertyList.add("property9".toUpperCase());
        propertyList.add("property10".toUpperCase());
        propertyList.add("property11".toUpperCase());
        propertyList.add("property12".toUpperCase());
        propertyList.add("property13".toUpperCase());
        propertyList.add("property14".toUpperCase());
        propertyList.add("property15".toUpperCase());
        for(String currentDataSlicePropertyName : dataSliceDetailInfo.getPropertiesDefinition().keySet()){
            Assert.assertTrue(propertyList.contains(currentDataSlicePropertyName));
        }

        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(DataService dataService = targetComputeGrid.getDataService()){
            dataService.eraseDataSlice("gridDataSliceA");
            dataService.eraseDataSlice("gridDataSliceB");
        }catch (ComputeGridException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(computeGridRealtimeStatisticsInfo);
        Assert.assertNotNull(computeGridRealtimeStatisticsInfo.getGridStartTime());
        Assert.assertTrue(computeGridRealtimeStatisticsInfo.getDataComputeUnitsAmount()>=1);
        Assert.assertNotNull(computeGridRealtimeStatisticsInfo.getYoungestUnitId());
        Assert.assertNotNull(computeGridRealtimeStatisticsInfo.getOldestUnitId());
        Assert.assertTrue(computeGridRealtimeStatisticsInfo.getTotalAvailableCPUCores()>0);

        Assert.assertNotNull(computeUnitRealtimeStatisticsInfoSet);
        Assert.assertTrue(computeUnitRealtimeStatisticsInfoSet.size() >= 1);
        ComputeUnitRealtimeStatisticsInfo firstComputeUnitRealtimeStatisticsInfo = computeUnitRealtimeStatisticsInfoSet.iterator().next();
        Assert.assertNotNull(firstComputeUnitRealtimeStatisticsInfo);

        Assert.assertTrue(firstComputeUnitRealtimeStatisticsInfo.getAvailableCPUCores()>0);
        Assert.assertTrue(firstComputeUnitRealtimeStatisticsInfo.getAverageCPULoadPercentage()>0);
        Assert.assertTrue(firstComputeUnitRealtimeStatisticsInfo.getBusyTimePercentage()>0);
        Assert.assertNotNull(firstComputeUnitRealtimeStatisticsInfo.getUnitID());
        Assert.assertNotNull(firstComputeUnitRealtimeStatisticsInfo.getUnitStartTime());
        Assert.assertTrue(firstComputeUnitRealtimeStatisticsInfo.getMaxAvailableMemoryInMB()>0);
    }
}
