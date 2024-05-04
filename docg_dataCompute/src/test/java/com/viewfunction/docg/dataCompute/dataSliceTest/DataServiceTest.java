package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlicePropertyType;

import java.util.*;

public class DataServiceTest {

    public static void main(String args[]) throws Exception {

        try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance()){

            //add record
            Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
            dataSlicePropertyMap.put("property1",DataSlicePropertyType.STRING);
            dataSlicePropertyMap.put("property2",DataSlicePropertyType.INT);
            dataSlicePropertyMap.put("property3",DataSlicePropertyType.DOUBLE);
            dataSlicePropertyMap.put("property4",DataSlicePropertyType.STRING);
            List<String> pkList = new ArrayList<>();
            pkList.add("property1");
            pkList.add("property2");
            DataSlice targetDataSlice = dataServiceInvoker.createPerUnitDataSlice("gridDataSlice1","sliceGroup1",dataSlicePropertyMap,pkList);
            System.out.println(targetDataSlice);
            System.out.println(targetDataSlice.getDataSliceMetaInfo().getDataSliceName());
            System.out.println(targetDataSlice.getDataSliceMetaInfo().getStoreBackupNumber());
            System.out.println(targetDataSlice.getDataSliceMetaInfo().getSliceGroupName());
            System.out.println(dataServiceInvoker.listDataSlices());






            //DataSlice targetDataSlice0 = dataServiceInvoker.getDataSlice("gridDataSlice1");

            for(int i=0;i<10;i++) {
                Map<String, Object> dataPropertiesValue = new HashMap<>();
                dataPropertiesValue.put("property1", "DataProperty1Value" + new Date().getTime());
                dataPropertiesValue.put("property2", 1000+i);
                dataPropertiesValue.put("property3", 1238.999d);
                dataPropertiesValue.put("property4", "PROP$VALUE+"+i);
                boolean addResult = targetDataSlice.addDataRecord(dataPropertiesValue);
                System.out.println(addResult);
            }


            DataSlice targetDataSlice2 = dataServiceInvoker.getDataSlice("gridDataSlice1");
            Map<String, Object> dataPropertiesValue = new HashMap<>();
            /*

            dataPropertiesValue.put("property1", "DataProperty1Value1613699647346");
            dataPropertiesValue.put("property3", 3001.234d);
            dataPropertiesValue.put("property4", "HAHAHawAHA=======");
            //dataPropertiesValue.put("property2", 1000);
            boolean updateResult = targetDataSlice2.updateDataRecord(dataPropertiesValue);
            System.out.println(updateResult);

            dataPropertiesValue = new HashMap<>();
            dataPropertiesValue.put("property1", "DataProperty1Value1613636927853===122");
            dataPropertiesValue.put("property3", 800000.456d);
            dataPropertiesValue.put("property4", "1111==");
            dataPropertiesValue.put("property2", 5000);

            boolean addOrUpdateResult = targetDataSlice2.addOrUpdateDataRecord(dataPropertiesValue);
            System.out.println(addOrUpdateResult);
 */
            dataPropertiesValue = new HashMap<>();
            dataPropertiesValue.put("property1", "DataProperty1Value1613719808845");
            dataPropertiesValue.put("property2", 1000);
            Map<String,Object> resultData = targetDataSlice2.getDataRecordByPrimaryKeys(dataPropertiesValue);
            System.out.println(resultData);


            //dataPropertiesValue = new HashMap<>();
            //dataPropertiesValue.put("property2", 5000);
            //resultData = targetDataSlice2.getDataRecordByPrimaryKeys(dataPropertiesValue);
            //System.out.println(resultData);


            dataPropertiesValue = new HashMap<>();
            dataPropertiesValue.put("property2", 1003);
            dataPropertiesValue.put("property1", "DataProperty1Value1613699647532");

            boolean deleteRecordResult = targetDataSlice2.deleteDataRecord(dataPropertiesValue);
            System.out.println(deleteRecordResult);


            List<Map<String,Object>> pkValueList = new ArrayList<>();

            pkValueList.add(dataPropertiesValue);


            Map<String,Object> dataPropertiesValue2 = new HashMap<>();
            dataPropertiesValue2.put("property2", 1003);
            dataPropertiesValue2.put("property1", "DataProperty1Value1613710961612");
            pkValueList.add(dataPropertiesValue2);


            DataSliceOperationResult dataSliceOperationResult = targetDataSlice2.deleteDataRecords(pkValueList);

            System.out.println(dataSliceOperationResult.getOperationSummary());
            System.out.println(dataSliceOperationResult.getStartTime());
            System.out.println(dataSliceOperationResult.getFinishTime());
            System.out.println(dataSliceOperationResult.getSuccessItemsCount());
            System.out.println(dataSliceOperationResult.getFailItemsCount());


/*
            Map<String,DataSlicePropertyType> dataSlicePropertyMap2 = new HashMap<>();
            dataSlicePropertyMap2.put("property1",DataSlicePropertyType.DOUBLE);
            dataSlicePropertyMap2.put("property2",DataSlicePropertyType.LONG);
            pkList = new ArrayList<>();
            pkList.add("property1");

            DataSlice targetDataSlice2 = dataServiceInvoker.createPerUnitDataSlice("gridDataSlice2","sliceGroup1",dataSlicePropertyMap2,pkList);
            System.out.println(targetDataSlice2);
            System.out.println(targetDataSlice2.getDataSliceMetaInfo().getDataSliceName());
            System.out.println(targetDataSlice2.getDataSliceMetaInfo().getStoreBackupNumber());
            System.out.println(targetDataSlice2.getDataSliceMetaInfo().getSliceGroupName());
            System.out.println(dataServiceInvoker.listDataSlices());
*/







            /*

            for(int i=0;i<10;i++) {
                Map<String, Object> dataPropertiesValue = new HashMap<>();
                dataPropertiesValue.put("property1", "DataProperty1Value" + new Date().getTime());
                dataPropertiesValue.put("property2", 1000+i);
                dataPropertiesValue.put("property3", 1238.999d);
                targetDataSlice.addDataRecord(dataPropertiesValue);
            }
            */

/*
            DataSlice targetDataSlice = dataServiceInvoker.getDataSlice("gridDataSlice1");


            List<Map<String,Object>> dataRowList = new ArrayList<>();
            for(int i=0;i<1000000;i++) {
                Map<String, Object> dataPropertiesValue = new HashMap<>();
                dataPropertiesValue.put("property1", "DataProperty1Value" + new Date().getTime());
                dataPropertiesValue.put("property2", 1000+i);
                dataPropertiesValue.put("property3", 1238.999d);
                dataRowList.add(dataPropertiesValue);
            }

            List<String> propertyList = new ArrayList<>();
            propertyList.add("property1");
            propertyList.add("property2");
            propertyList.add("property3");




            Map<String, Object> dataPropertiesValue = new HashMap<>();
            dataPropertiesValue.put("property1", "DataProperty1Value" + new Date().getTime());
            dataPropertiesValue.put("property2", "67890aaa");
            dataPropertiesValue.put("property3", 1238.999d);

            dataRowList.add(dataPropertiesValue);



            DataSliceOperationResult dataSliceOperationResult = targetDataSlice.addDataRecords(propertyList,dataRowList);



            System.out.println(dataSliceOperationResult.getOperationSummary());
            System.out.println(dataSliceOperationResult.getFailItemsCount());
            System.out.println(dataSliceOperationResult.getStartTime());
            System.out.println(dataSliceOperationResult.getFinishTime());
            System.out.println(dataSliceOperationResult.getSuccessItemsCount());


*/

            //System.out.println(dataSliceOperationResult);
            //System.out.println(dataSliceOperationResult);


            /*
            Map<String, Object> dataPropertiesValue = new HashMap<>();
            dataPropertiesValue.put("property1", "DataProperty1Value" + new Date().getTime());
            dataPropertiesValue.put("property2", 67890);
            dataPropertiesValue.put("property3", 1238.999d);
            boolean addResult = targetDataSlice.addDataRecord(dataPropertiesValue);
            System.out.println(addResult);
            */





            /*
            targetDataSlice.addDataRecords(null);
            targetDataSlice = dataServiceInvoker.getDataSlice("gridDataSlice2");
            targetDataSlice.addDataRecords(null);
            targetDataSlice = dataServiceInvoker.getDataSlice("gridDataSlice2");
            */




        }
    }
}
