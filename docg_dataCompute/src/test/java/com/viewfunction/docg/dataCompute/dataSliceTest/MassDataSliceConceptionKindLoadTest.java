package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil;

import java.util.HashMap;
import java.util.Map;

public class MassDataSliceConceptionKindLoadTest {

    public static void main(String[] args){
        //refreshDataSliceAndLoadChinaFirmTest();
        //refreshDataSliceAndLoadDOCG_GS_CountryRegionTest();
        //refreshDataSliceAndLoadDOCG_GS_ProvinceTest();
        //refreshDataSliceAndLoadDOCG_GS_PrefectureTest();
        //refreshDataSliceAndLoadDOCG_GS_CountyTest();
    }

    public static void refreshDataSliceAndLoadChinaFirmTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("address",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("city",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("companyType",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("province",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("lng_wgs",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("lat_wgs",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("approvedTime",DataSlicePropertyType.DATE);
        dataSlicePropertyMap.put("name",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("category",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("startDate",DataSlicePropertyType.DATE);
        dataSlicePropertyMap.put("status",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "ChinaFirm",dataSlicePropertyMap,"ChinaFirm",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadDOCG_GS_CountryRegionTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("DOCG_GeospatialChineseName",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "DOCG_GS_CountryRegion",dataSlicePropertyMap,"DOCG_GS_CountryRegion",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadDOCG_GS_ProvinceTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("DOCG_GeospatialChineseName",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("DOCG_GeospatialCode",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "DOCG_GS_Province",dataSlicePropertyMap,"DOCG_GS_Province",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadDOCG_GS_PrefectureTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("ChinaProvinceName",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("DOCG_GeospatialChineseName",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("ChinaDivisionCode",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("DOCG_GeospatialCode",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "DOCG_GS_Prefecture",dataSlicePropertyMap,"DOCG_GS_Prefecture",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadDOCG_GS_CountyTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("ChinaProvinceName",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("ChinaPrefectureName",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("DOCG_GeospatialChineseName",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("ChinaDivisionCode",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("ChinaParentDivisionCode",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("DOCG_GeospatialCode",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "DOCG_GS_County",dataSlicePropertyMap,"DOCG_GS_County",queryParameters,10);
    }


}
