package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSliceExistException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSlicePropertiesStructureException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.ComputeGridObserver;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataComputeUnitMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceDetailInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlicePropertyType;

import java.util.*;

public class DataServiceObserverTest {

    public static void main(String[] args){
        //initDataSlice();

        ComputeGridObserver computeGridObserver = ComputeGridObserver.getObserverInstance();
        Set<DataComputeUnitMetaInfo> dataComputeUnitMetaInfoSet = computeGridObserver.listDataComputeUnit();
        Set<DataSliceMetaInfo> dataSliceMetaInfoSet = computeGridObserver.listDataSlice();
        DataSliceDetailInfo dataSliceDetailInfo = computeGridObserver.getDataSliceDetail("gridDataSlice1");
        computeGridObserver.closeObserveSession();
        for(DataSliceMetaInfo currentDataSliceMetaInfo : dataSliceMetaInfoSet){
            System.out.println(currentDataSliceMetaInfo.getDataSliceName());
            System.out.println(currentDataSliceMetaInfo.getAtomicityMode());
            System.out.println(currentDataSliceMetaInfo.getBackupDataCount());
            System.out.println(currentDataSliceMetaInfo.getDataStoreMode());
            System.out.println(currentDataSliceMetaInfo.getPrimaryDataCount());
            System.out.println(currentDataSliceMetaInfo.getSliceGroupName());
            System.out.println(currentDataSliceMetaInfo.getStoreBackupNumber());
            System.out.println(currentDataSliceMetaInfo.getTotalDataCount());
            System.out.println("**************************");
        }
        for(DataComputeUnitMetaInfo currentDataComputeUnitMetaInfo : dataComputeUnitMetaInfoSet){
            System.out.println(currentDataComputeUnitMetaInfo.getUnitID());
            System.out.println(currentDataComputeUnitMetaInfo.getUnitType());
            System.out.println(currentDataComputeUnitMetaInfo.getUnitHostNames());
            System.out.println(currentDataComputeUnitMetaInfo.getUnitIPAddresses());
            System.out.println(currentDataComputeUnitMetaInfo.getIsClientUnit());
            System.out.println("==========================");
        }

        System.out.println(dataSliceDetailInfo.getDataSliceName());
        System.out.println(dataSliceDetailInfo.getSliceGroupName());
        System.out.println(dataSliceDetailInfo.getStoreBackupNumber());
        System.out.println(dataSliceDetailInfo.getPrimaryDataCount());
        System.out.println(dataSliceDetailInfo.getBackupDataCount());
        System.out.println(dataSliceDetailInfo.getTotalDataCount());
        System.out.println(dataSliceDetailInfo.getAtomicityMode());
        System.out.println(dataSliceDetailInfo.getDataStoreMode());
        System.out.println(dataSliceDetailInfo.getPropertiesDefinition());
    }

    private static void initDataSlice(){
        try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance()){
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
            dataServiceInvoker.createPerUnitDataSlice("gridDataSliceA","sliceGroup1",dataSlicePropertyMap,pkList);
            dataServiceInvoker.createGridDataSlice("gridDataSliceB","sliceGroup1",dataSlicePropertyMap,pkList);
        } catch (DataSliceExistException e) {
            throw new RuntimeException(e);
        } catch (DataSlicePropertiesStructureException e) {
            throw new RuntimeException(e);
        } catch (ComputeGridNotActiveException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
