package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataClassTypeNotMatchedException;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSliceExistException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResourceNodeDataStoreInvokerTest {

    public static void main(String[] args) throws DataClassTypeNotMatchedException, ComputeGridNotActiveException {
        /*
        ConnectomeDataStoreInvoker connectomeDataStoreInvoker=new ConnectomeDataStoreInvoker();
        try {
            connectomeDataStoreInvoker.openDataAccessSession();
            testCreateDataStores(connectomeDataStoreInvoker);
            testGetDataStoreMetaInfo(connectomeDataStoreInvoker);
            testAddData(connectomeDataStoreInvoker);
            testUpdateData(connectomeDataStoreInvoker);
            testDeleteData(connectomeDataStoreInvoker);
            testEmptyData(connectomeDataStoreInvoker);
            testEraseDataStores(connectomeDataStoreInvoker);
            testCreateDataStoreWithType(connectomeDataStoreInvoker);
            testDataStoreWithTypeOperation(connectomeDataStoreInvoker);
            testEraseDataStoresWithType(connectomeDataStoreInvoker);
        }finally {
            connectomeDataStoreInvoker.closeDataAccessSession();
        }
        */
        try(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker = ResourceNodeDataStoreInvoker.getInvokerInstance()){
            testCreateDataStores(resourceNodeDataStoreInvoker);
            testAddData(resourceNodeDataStoreInvoker);
            testUpdateData(resourceNodeDataStoreInvoker);


            /*
            //testDeleteData(resourceNodeDataStoreInvoker);
            testEmptyData(resourceNodeDataStoreInvoker);
            //testEraseDataStores(resourceNodeDataStoreInvoker);
            testCreateDataStoreWithType(resourceNodeDataStoreInvoker);
            testDataStoreWithTypeOperation(resourceNodeDataStoreInvoker);
            testEraseDataStoresWithType(resourceNodeDataStoreInvoker);
            */


        }
    }

    public static void testCreateDataStores(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker){
        try {
            resourceNodeDataStoreInvoker.createConnectomeLocalDataStore("ConnectomeLocalDataStore");
            ResourceNodeDataStore<String,Integer> store2= resourceNodeDataStoreInvoker.createGridPerNodeDataStore("GridPerNodeDataStore",String.class,Integer.class);
            ResourceNodeDataStore<String, Float> store3= resourceNodeDataStoreInvoker.createGridSingletonDataStore("GridSingletonDataStore",String.class,Float.class);
        } catch (DataSliceExistException e) {
            e.printStackTrace();
        }
        System.out.println(resourceNodeDataStoreInvoker.listGridDataStores());
    }

    public static void testAddData(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker)  throws DataClassTypeNotMatchedException {
        ResourceNodeDataStore<String, Float> targetStore1= resourceNodeDataStoreInvoker.getGridDataStore("GridSingletonDataStore");
        System.out.println(targetStore1.getData("key1"));
        targetStore1.addData("key1",new Float(100));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.getData("key1"));
        targetStore1.addData("key1",new Float(200));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.getData("key1"));

        System.out.println(targetStore1.addDataIfNotExist("key1", new Float(300)));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.addDataIfNotExist("key2", new Float(300)));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());

        Map<String, Float> dataMap=new HashMap<>();
        dataMap.put("key3",new Float(300));
        dataMap.put("key4",new Float(400));
        dataMap.put("key5",new Float(500));
        targetStore1.addData(dataMap);

        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        dataMap.clear();
        dataMap.put("key1",new Float(1000));
        targetStore1.addData(dataMap);

        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.getData("key1"));

        ResourceNodeDataStore<String, Integer> targetStore2= resourceNodeDataStoreInvoker.getGridDataStore("GridPerNodeDataStore");
        System.out.println(targetStore2.getData("GridPerNodeDataStore_key1"));
        targetStore2.addData("GridPerNodeDataStore_key1",new Integer(45690));
        System.out.println(targetStore2.getData("GridPerNodeDataStore_key1"));

        DataSliceMetaInfo metaInfo = targetStore2.getDataStoreMetaInfo();
        System.out.println(metaInfo.getPrimaryDataCount());
        System.out.println(metaInfo.getBackupDataCount());
        System.out.println(metaInfo.getTotalDataCount());
        System.out.println(metaInfo.getStoreBackupNumber());
    }

    public static void testUpdateData(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker)  throws DataClassTypeNotMatchedException {
        ResourceNodeDataStore<String, Float> targetStore1= resourceNodeDataStoreInvoker.getGridDataStore("GridSingletonDataStore");
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.getData("key1"));
        targetStore1.updateData("key1",new Float(5000));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.getData("key1"));
        System.out.println(targetStore1.updateDataIfOldValueMatched("key1", new Float(3000), new Float(8000)));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.getData("key1"));
        System.out.println(targetStore1.updateDataIfOldValueMatched("key1", new Float(5000), new Float(8000)));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.getData("key1"));
    }

    public static void testDeleteData(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker)  throws DataClassTypeNotMatchedException {
        ResourceNodeDataStore<String, Float> targetStore1= resourceNodeDataStoreInvoker.getGridDataStore("GridSingletonDataStore");
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.deleteData("NotExistDataKey"));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.deleteData("key2"));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());

        System.out.println(targetStore1.deleteDataIfValueMatched("key1",new Float(5000)));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        System.out.println(targetStore1.deleteDataIfValueMatched("key1",new Float(8000)));
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());

        Set<String> keySet=new HashSet<>();
        keySet.add("key3");
        keySet.add("key4");
        targetStore1.deleteData(keySet);
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());

        keySet.add("NotExistDataKey");
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
    }

    public static void testEmptyData(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker){
        ResourceNodeDataStore<String, Float> targetStore1= resourceNodeDataStoreInvoker.getGridDataStore("GridSingletonDataStore");
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
        targetStore1.emptyDataStore();
        System.out.println(targetStore1.getDataStoreMetaInfo().getPrimaryDataCount());
    }

    public static void testEraseDataStores(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker){
        resourceNodeDataStoreInvoker.eraseDataStore("ConnectomeLocalDataStore");
        resourceNodeDataStoreInvoker.eraseDataStore("GridPerNodeDataStore");
        resourceNodeDataStoreInvoker.eraseDataStore("GridSingletonDataStore");
        System.out.println(resourceNodeDataStoreInvoker.listGridDataStores());
    }

    public static void testCreateDataStoreWithType(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker) throws DataClassTypeNotMatchedException {
        try {
            ResourceNodeDataStore store1= resourceNodeDataStoreInvoker.createGridSingletonDataStore("GridSingletonDataStoreWithType", String.class, Integer.class);
            ResourceNodeDataStore store2= resourceNodeDataStoreInvoker.createGridSingletonDataStore("GridPerNodeDataStoreWithType", Integer.class, Float.class);
            ResourceNodeDataStore store3= resourceNodeDataStoreInvoker.createGridSingletonDataStore("ConnectomeLocalDataStoreWithType", Float.class, Long.class);
        } catch (DataSliceExistException e) {
            e.printStackTrace();
        }
        System.out.println(resourceNodeDataStoreInvoker.listGridDataStores());
    }

    public static void testDataStoreWithTypeOperation(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker) throws DataClassTypeNotMatchedException {
        ResourceNodeDataStore store= resourceNodeDataStoreInvoker.getGridDataStore("GridSingletonDataStoreWithType");

        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());
        store.addData("newValue1", new Integer(200));
        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());

        try {
            store.addData(new Integer(200), new Integer(200));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());

        Map dataMap=new HashMap<>();
        dataMap.put("newValue1",new Integer(300));
        dataMap.put("newValue2",new Integer(400));
        dataMap.put("newValue3",new Integer(500));

        store.addData(dataMap);
        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());

        dataMap.clear();
        dataMap.put("newValue4",new Integer(300));
        dataMap.put(new Integer(500),new Integer(400));
        try {
            store.addData(dataMap);
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());

        dataMap.clear();
        dataMap.put("newValue4",new Integer(300));
        dataMap.put("newValue5",new Float(400));
        try {
            store.addData(dataMap);
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());

        try {
            System.out.println(store.addDataIfNotExist("newValue4", new Float(300)));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }

        try {
            System.out.println(store.addDataIfNotExist(new Float(400), new Integer(300)));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }

        System.out.println(store.addDataIfNotExist("newValue4", new Integer(900)));
        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());

        try {
            store.updateData("newValue4", new Float(5000));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        try {
            store.updateData(new Float(400), new Integer(5000));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        System.out.println(store.updateData("newValue4", new Integer(10000)));
        System.out.println(store.getData("newValue4"));

        try {
            store.updateDataIfOldValueMatched( new Integer(5000), new Integer(5000), new Integer(5000));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        try {
            store.updateDataIfOldValueMatched("newValue4", new Float(5000), new Integer(5000));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        try {
            store.updateDataIfOldValueMatched("newValue4", new Integer(5000), new Long(5000));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        System.out.println(store.updateDataIfOldValueMatched("newValue4", new Integer(10000), new Integer(110000)));
        System.out.println(store.getData("newValue4"));

        try {
            store.deleteData(new Integer(5000));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        store.deleteData("newValue4");
        System.out.println(store.getData("newValue4"));
        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());

        try {
            store.deleteDataIfValueMatched(new Integer(5000), new Integer(500));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        try {
            store.deleteDataIfValueMatched("newValue3",new Float(500));
        }catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        System.out.println( store.deleteDataIfValueMatched("newValue3", new Integer(500)));
        System.out.println(store.getData("newValue3"));
        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());


        Set keySet=new HashSet<>();
        keySet.add("newValue1");
        keySet.add("newValue2");
        keySet.add(new Integer(500));
        try {
            store.deleteData(keySet);
        } catch (DataClassTypeNotMatchedException e){
            System.out.println("NeuronGridDataClassTypeNotMatchedException should throws" );
        }
        keySet.clear();
        keySet.add("newValue1");
        keySet.add("newValue2");
        store.deleteData(keySet);
        System.out.println(store.getDataStoreMetaInfo().getPrimaryDataCount());
    }

    public static void testEraseDataStoresWithType(ResourceNodeDataStoreInvoker resourceNodeDataStoreInvoker){
        resourceNodeDataStoreInvoker.eraseDataStore("GridSingletonDataStoreWithType");
        resourceNodeDataStoreInvoker.eraseDataStore("GridPerNodeDataStoreWithType");
        resourceNodeDataStoreInvoker.eraseDataStore("ConnectomeLocalDataStoreWithType");
        System.out.println(resourceNodeDataStoreInvoker.listGridDataStores());
    }
}
