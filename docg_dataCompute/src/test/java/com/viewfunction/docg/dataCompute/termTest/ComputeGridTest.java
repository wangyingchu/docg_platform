package com.viewfunction.docg.dataCompute.termTest;


import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.DataSliceExistException;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.DataSlicePropertiesStructureException;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSlice;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;

import java.util.*;

public class ComputeGridTest {
    private static final String _unitTestDataSliceGroupName = "Testing_SliceGroup";

    public static void main(String[] args)  {

        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        System.out.println(targetComputeGrid.getGridImplTech());

        try(DataService dataService = targetComputeGrid.getDataService()){
            List<String> dataSliceList = dataService.listDataSlices();
            System.out.println(dataSliceList);

            //createDataSlicesTest(dataService,"gridDataSlice1");
            //insertDataTest(dataService,"gridDataSlice1");
            emptyDataTest(dataService,"gridDataSlice1");

        } catch (ComputeGridException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void createDataSlicesTest(DataService dataService,String dataSliceName) throws DataSliceExistException, DataSlicePropertiesStructureException {
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("property1",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("property2",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("property3",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("property4",DataSlicePropertyType.STRING);
        List<String> pkList = new ArrayList<>();
        pkList.add("property1");
        pkList.add("property2");

        DataSlice targetDataSlice = dataService.createGridDataSlice(dataSliceName, _unitTestDataSliceGroupName,dataSlicePropertyMap,pkList);
        System.out.println(targetDataSlice);
        System.out.println(targetDataSlice.getDataSliceMetaInfo().getDataSliceName());
        System.out.println(targetDataSlice.getDataSliceMetaInfo().getStoreBackupNumber());
        System.out.println(targetDataSlice.getDataSliceMetaInfo().getSliceGroupName());
        System.out.println(dataService.listDataSlices());
    }

    private static void insertDataTest(DataService dataService,String dataSliceName) throws DataSlicePropertiesStructureException {
        DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);

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

        DataSliceOperationResult addResult = targetDataSlice.addDataRecords(propertyList,dataList);
        System.out.println(addResult.getSuccessItemsCount());
    }

    private static void emptyDataTest(DataService dataService,String dataSliceName) throws DataSlicePropertiesStructureException{
        DataSlice targetDataSlice = dataService.getDataSlice(dataSliceName);
        targetDataSlice.emptyDataSlice();


    }
}
