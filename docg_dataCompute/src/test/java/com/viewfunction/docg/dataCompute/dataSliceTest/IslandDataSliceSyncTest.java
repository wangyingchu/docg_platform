package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil;

import java.util.HashMap;
import java.util.Map;

public class IslandDataSliceSyncTest {
    public static void main(String[] args){
        //refreshDataSliceAndLoadGYD_BuildingTest();
        //refreshDataSliceAndLoadGYD_BusStopTest();
        //refreshDataSliceAndLoadGYD_FrutexTest();
        //refreshDataSliceAndLoadGYD_FunctionalZoneTest();
        //refreshDataSliceAndLoadGYD_IndividualTreeTest();
        //refreshDataSliceAndLoadGYD_LandMarkTest();
        //refreshDataSliceAndLoadGYD_PhysicEquipmentTest();
        //refreshDataSliceAndLoadGYD_RegionNameTest();
    }

    public static void refreshDataSliceAndLoadGYD_BuildingTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("OBJECTID",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("Shape_Area",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("Shape_Leng",DataSlicePropertyType.DOUBLE);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "GYD_Building",dataSlicePropertyMap,"GYD_Building",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadGYD_BusStopTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("OBJECTID",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("name",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "GYD_BusStop",dataSlicePropertyMap,"GYD_BusStop",queryParameters,10);
    }
    public static void refreshDataSliceAndLoadGYD_FrutexTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("CrownArea",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("CrownDiame",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("CrownVolum",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("DBH",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("QKID",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("TreeID",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("TreeHeight",DataSlicePropertyType.DOUBLE);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "GYD_Frutex",dataSlicePropertyMap,"GYD_Frutex",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadGYD_FunctionalZoneTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("GNQHID",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("GNQMC",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("Shape_Leng",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("Shape_Area",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("OBJECTID",DataSlicePropertyType.INT);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "GYD_FunctionalZone",dataSlicePropertyMap,"GYD_FunctionalZone",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadGYD_IndividualTreeTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("GNQHID",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("Ymax",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("DMID",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("OBJECTID_1",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("OBJECTID",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("Xmin",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("SG",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("XJDMJ",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("SGMJ",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("GF",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("SZ",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("BH1",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("SGTJ",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("XJ",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "GYD_IndividualTree",dataSlicePropertyMap,"GYD_IndividualTree",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadGYD_LandMarkTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("type",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("OBJECTID",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("name",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "GYD_LandMark",dataSlicePropertyMap,"GYD_LandMark",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadGYD_PhysicEquipmentTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("registryTi",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("alarmStatu",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("responsibl",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("useDepartm",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("qrCode",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("ID",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("maintenanc",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("productId",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("serviceLif",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("alarmName",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("competentD",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("NAME",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("size",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("deriveMeta",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("constructi",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("objectCode",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("latitude",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("creatorId",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("manufactur",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("deviceBran",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "GYD_PhysicEquipment",dataSlicePropertyMap,"GYD_PhysicEquipment",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadGYD_RegionNameTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("uid",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("OBJECTID",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("name",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "GYD_RegionName",dataSlicePropertyMap,"GYD_RegionName",queryParameters,10);
    }



}
