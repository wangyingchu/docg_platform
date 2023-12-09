package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil;

import java.util.HashMap;
import java.util.Map;

public class RefreshDataSliceAndLoadDataFromConceptionKindTest {

    public static void main(String[] args){
        //refreshDataSliceAndLoadCadastralControlLinesTest();
        //refreshDataSliceAndLoadCitywideGreenStormInfrastructureTest();
        refreshDataSliceAndLoadSeattleParksAndRecreationTest();
    }

    public static void refreshDataSliceAndLoadCadastralControlLinesTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("CL_TYPE",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("CL_EDT_DAT",DataSlicePropertyType.DATE);
        dataSlicePropertyMap.put("CL_LNGTH",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("CL_SRC_DOC",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("CL_ID",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("SHAPE_Leng",DataSlicePropertyType.DOUBLE);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "CadastralControlLines",dataSlicePropertyMap,"CadastralControlLines",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadCitywideGreenStormInfrastructureTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("GB_CSO_BSN",DataSlicePropertyType.INT);
        dataSlicePropertyMap.put("GB_DPRTMNT",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("GB_SRC_TYP",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("GB_KEY",DataSlicePropertyType.INT);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "CitywideGreenStormInfrastructure",dataSlicePropertyMap,"CitywideGreenStormInfrastructure",queryParameters,10);
    }

    public static void refreshDataSliceAndLoadSeattleParksAndRecreationTest(){
        Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
        dataSlicePropertyMap.put("DOCG_GS_GLGeometryContent",DataSlicePropertyType.STRING);
        dataSlicePropertyMap.put("pma",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("parksbnd_a",DataSlicePropertyType.DOUBLE);
        dataSlicePropertyMap.put("name",DataSlicePropertyType.STRING);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.refreshDataSliceAndLoadDataFromConceptionKind("defaultSliceGroup",
                "SeattleParksAndRecreation",dataSlicePropertyMap,"SeattleParksAndRecreation",queryParameters,10);
    }
}
