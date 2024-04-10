package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataSliceDetailInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.factory.ComputeGridTermFactory;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientException;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;

import java.util.*;

public class DataSliceTest {

    public static void main(String[] args){
        massDataInsertTest();
        //checkMetr();
        //checkDataSlice();
    }

    private static void checkDataSlice(){
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try {
            DataSliceDetailInfo dataSliceDetailInfo =targetComputeGrid.getDataSliceDetail("gridDataSlice1");
            System.out.println(dataSliceDetailInfo.getPrimaryKeyPropertiesNames());

        } catch (ComputeGridException e) {
            throw new RuntimeException(e);
        }
    }

    private static void massDataInsertTest(){
        try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance()){
            Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
            dataSlicePropertyMap.put("property1",DataSlicePropertyType.STRING);
            dataSlicePropertyMap.put("property2",DataSlicePropertyType.INT);
            dataSlicePropertyMap.put("property3",DataSlicePropertyType.DOUBLE);
            dataSlicePropertyMap.put("property4",DataSlicePropertyType.STRING);
            List<String> pkList = new ArrayList<>();
            pkList.add("property1");
            pkList.add("property2");

            DataSlice targetDataSlice = dataServiceInvoker.createGridDataSlice("gridDataSlice1","sliceGroup1",dataSlicePropertyMap,pkList);

            System.out.println(targetDataSlice);
            System.out.println(targetDataSlice.getDataSliceMetaInfo().getDataSliceName());
            System.out.println(targetDataSlice.getDataSliceMetaInfo().getStoreBackupNumber());
            System.out.println(targetDataSlice.getDataSliceMetaInfo().getSliceGroupName());
            System.out.println(dataServiceInvoker.listDataSlices());


            List<Map<String,Object>> dataList = new ArrayList<>();
            for(int i=0;i<10000000;i++) {
                Map<String, Object> dataPropertiesValue = new HashMap<>();
                dataPropertiesValue.put("property1", "DataProperty1Value" + new Date().getTime());
                dataPropertiesValue.put("property2", 1000+i);
                dataPropertiesValue.put("property3", 1238.999d);
                dataPropertiesValue.put("property4", "PROP$VALUE+"+i);
                dataList.add(dataPropertiesValue);
            }

            List<String> propertyList = new ArrayList<>();
            propertyList.add("property1");propertyList.add("property2");propertyList.add("property3");propertyList.add("property4");


            DataSliceOperationResult addResult = targetDataSlice.addDataRecords(propertyList,dataList);
            System.out.println(addResult.getSuccessItemsCount());



            System.out.println("========================");
            System.out.println("========================");
            System.out.println("========================");






        } catch (ComputeGridNotActiveException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkMetr(){
        ClientConfiguration cfg = new ClientConfiguration().setAddresses("127.0.0.1:10800");

        try (IgniteClient igniteClient = Ignition.startClient(cfg)) {

            // getting the id of the first node
            UUID nodeId = (UUID) igniteClient.query(new SqlFieldsQuery("SELECT * from NODES").setSchema("SYS"))
                    .getAll().iterator().next().get(0);


            System.out.println(nodeId);
            System.out.println(nodeId); System.out.println(nodeId); System.out.println(nodeId);

            Object result = igniteClient.query(new SqlFieldsQuery("select NONHEAP_MEMORY_COMMITED from NODE_METRICS ")
                    .setSchema("SYS")).getAll().iterator().next().get(0);
System.out.println(result.getClass());
System.out.println(result);

            result = igniteClient.query(new SqlFieldsQuery("select name, value from SYS.METRICS")
                    .setSchema("SYS")).getAll().iterator().next().get(0);


            result = igniteClient.query(new SqlFieldsQuery("select name, value from SYS.METRICS")
                    .setSchema("SYS")).getAll().iterator().next().get(0);

            System.out.println(result.getClass());
            System.out.println(result);



            List<List<?>> listValue = igniteClient.query(new SqlFieldsQuery("select name, value from SYS.METRICS")
                    .setSchema("SYS")).getAll();

            for(List<?> currentList :listValue){


                //System.out.println(currentList);

                //System.out.println(currentList.size());

                String metricName = currentList.get(0).toString();
                Object metricNameValue = currentList.get(1);


                System.out.println(metricName);
                System.out.println(metricNameValue);
            }



            double cpu_load = (Double) igniteClient
                    .query(new SqlFieldsQuery("select CUR_CPU_LOAD * 100 from NODE_METRICS where NODE_ID = ? ")
                            .setSchema("SYS").setArgs(nodeId.toString()))
                    .getAll().iterator().next().get(0);

            System.out.println("node's cpu load = " + cpu_load);



        } catch (ClientException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.format("Unexpected failure: %s\n", e);
        }
    }
}
