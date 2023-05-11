package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.*;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSliceExistException;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSlicePropertiesStructureException;

import java.util.*;

public class DataServiceObserverTest {

    public static void main(String[] args){
        //initDataSlice();

        DataServiceObserver dataServiceObserver = DataServiceObserver.getObserverInstance();
        Set<DataComputeUnitMetaInfo> dataComputeUnitMetaInfoSet = dataServiceObserver.listDataComputeUnit();
        Set<DataSliceMetaInfo> dataSliceMetaInfoSet = dataServiceObserver.listDataSlice();
        dataServiceObserver.closeObserveSession();
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
    }

    private static void initDataSlice(){
        try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance()){
            Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
            dataSlicePropertyMap.put("property1",DataSlicePropertyType.STRING);
            dataSlicePropertyMap.put("property2",DataSlicePropertyType.INT);
            dataSlicePropertyMap.put("property3",DataSlicePropertyType.DOUBLE);
            dataSlicePropertyMap.put("property4",DataSlicePropertyType.STRING);
            List<String> pkList = new ArrayList<>();
            pkList.add("property1");
            pkList.add("property2");
            dataServiceInvoker.createPerUnitDataSlice("gridDataSlice1","sliceGroup1",dataSlicePropertyMap,pkList);
            dataServiceInvoker.createGridDataSlice("gridDataSlice2","sliceGroup1",dataSlicePropertyMap,pkList);
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
