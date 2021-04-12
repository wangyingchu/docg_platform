package com.viewfunction.docg.knowledgeManage.applicationCapacity.dataSlicesSynchronization.dataSlicesSync;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSliceExistException;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.DataSlicePropertiesStructureException;
import com.viewfunction.docg.knowledgeManage.consoleApplication.util.ApplicationLauncherUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataSliceSyncUtil {

    public static void syncGeospatialRegionData(DataServiceInvoker dataServiceInvoker){
        //DataSlice targetDataSlice = dataServiceInvoker.getDataSlice(RealmConstant.GeospatialScaleContinentEntityClass);
        DataSlice targetContinentDataSlice = dataServiceInvoker.getDataSlice(RealmConstant.GeospatialScaleContinentEntityClass);
        DataSlice targetCountryRegionDataSlice = dataServiceInvoker.getDataSlice(RealmConstant.GeospatialScaleCountryRegionEntityClass);
        DataSlice targetProvinceDataSlice = dataServiceInvoker.getDataSlice(RealmConstant.GeospatialScaleProvinceEntityClass);
        DataSlice targetPrefectureDataSlice = dataServiceInvoker.getDataSlice(RealmConstant.GeospatialScalePrefectureEntityClass);
        DataSlice targetCountyDataSlice = dataServiceInvoker.getDataSlice(RealmConstant.GeospatialScaleCountyEntityClass);
        DataSlice targetTownshipDataSlice = dataServiceInvoker.getDataSlice(RealmConstant.GeospatialScaleTownshipEntityClass);
        DataSlice targetVillageDataSlice = dataServiceInvoker.getDataSlice(RealmConstant.GeospatialScaleVillageEntityClass);

    }

    public static void batchSyncPerDefinedDataSlices(DataServiceInvoker dataServiceInvoker) {
        String dataSliceGroupName = ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.dataSliceGroup");
        String dataSyncPerLoadResultNumber = ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.dataSyncPerLoadResultNumber");
        String degreeOfParallelismNumber = ApplicationLauncherUtil.getApplicationInfoPropertyValue("DataSlicesSynchronization.degreeOfParallelism");
        int dataSyncPerLoadResultNum = dataSyncPerLoadResultNumber != null ? Integer.parseInt(dataSyncPerLoadResultNumber) : 100000000;
        int degreeOfParallelismNum = degreeOfParallelismNumber != null ? Integer.parseInt(degreeOfParallelismNumber) : 5;

        Map<String,List<DataPropertyInfo>> conceptionKindDataPropertiesMap = new HashMap<>();
        Map<String,List<DataPropertyInfo>> relationKindDataPropertiesMap = new HashMap<>();
        String lastConceptionKindName = null;
        String lastRelationKindName = null;
        String currentHandleType = "ConceptionKind";

        File file = new File("DataSlicesSyncKindList");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                String currentLine = tempStr.trim();
                if(currentLine.startsWith("ConceptionKind.")){
                    //handle ConceptionKind define
                    currentHandleType = "ConceptionKind";
                    String currentConceptionKindName = currentLine.replace("ConceptionKind.","");
                    lastConceptionKindName = currentConceptionKindName;
                }else if(currentLine.startsWith("RelationKind.")){
                    //handle ConceptionKind define
                    currentHandleType = "RelationKind";
                    String currentRelationKindName = currentLine.replace("RelationKind.","");
                    lastRelationKindName = currentRelationKindName;
                }else{
                    String[] propertyDefineArray = currentLine.split("    ");
                    String propertyName = propertyDefineArray[0];
                    String propertyType = propertyDefineArray[1];
                    if(currentHandleType.equals("ConceptionKind")){
                        initKindPropertyDefine(conceptionKindDataPropertiesMap,lastConceptionKindName,propertyName,propertyType);
                    }
                    if(currentHandleType.equals("RelationKind")){
                        initKindPropertyDefine(relationKindDataPropertiesMap,lastRelationKindName,propertyName,propertyType);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        //handle conceptionKinds data
        Set<String> conceptionKindsSet = conceptionKindDataPropertiesMap.keySet();
        try {
            for(String currentConceptionKind : conceptionKindsSet){
                DataSlice targetDataSlice = dataServiceInvoker.getDataSlice(currentConceptionKind);
                if (targetDataSlice == null) {
                    List<DataPropertyInfo> kindDataPropertyInfoList = conceptionKindDataPropertiesMap.get(currentConceptionKind);
                    Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
                    if(kindDataPropertyInfoList != null) {
                        for (DataPropertyInfo currentDataPropertyInfo : kindDataPropertyInfoList) {
                            dataSlicePropertyMap.put(currentDataPropertyInfo.getPropertyName(), currentDataPropertyInfo.getPropertyType());
                        }
                    }
                    dataSlicePropertyMap.put(CoreRealmOperationUtil.RealmGlobalUID, DataSlicePropertyType.STRING);
                    List<String> pkList = new ArrayList<>();
                    pkList.add(CoreRealmOperationUtil.RealmGlobalUID);
                    dataServiceInvoker.createGridDataSlice(currentConceptionKind, dataSliceGroupName, dataSlicePropertyMap, pkList);
                }
            }

            for(String currentConceptionKind : conceptionKindsSet){
                List<DataPropertyInfo> kindDataPropertyInfoList = conceptionKindDataPropertiesMap.get(currentConceptionKind);
                List<String> conceptionKindPropertiesList = new ArrayList<>();
                if(kindDataPropertyInfoList != null){
                    for(DataPropertyInfo currentDataPropertyInfo : kindDataPropertyInfoList){
                        conceptionKindPropertiesList.add(currentDataPropertyInfo.getPropertyName());
                    }
                }
                QueryParameters queryParameters = new QueryParameters();
                queryParameters.setResultNumber(dataSyncPerLoadResultNum);
                CoreRealmOperationUtil.loadConceptionKindEntitiesToDataSlice(dataServiceInvoker,currentConceptionKind, conceptionKindPropertiesList,
                        queryParameters, currentConceptionKind, true, degreeOfParallelismNum);
            }
        } catch (DataSliceExistException e) {
            e.printStackTrace();
        } catch (DataSlicePropertiesStructureException e) {
            e.printStackTrace();
        }
        //handle relationKinds data
    }

    private static void initKindPropertyDefine(Map<String,List<DataPropertyInfo>> kindDataPropertiesMap,String KindName,String propertyName,String propertyType){
        if(propertyName.startsWith("Attribute.")){
            String propertyRealName = propertyName.replace("Attribute.","");
            DataPropertyInfo currentDataPropertyInfo = null;
            switch(propertyType){
                case "BOOLEAN" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.BOOLEAN);
                    break;
                case "INT" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.INT);
                    break;
                case "SHORT" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.SHORT);
                    break;
                case "LONG" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.LONG);
                    break;
                case "FLOAT" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.FLOAT);
                    break;
                case "DOUBLE" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.DOUBLE);
                    break;
                case "DATE" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.DATE);
                    break;
                case "STRING" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.STRING);
                    break;
                case "BYTE" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.BYTE);
                    break;
                case "DECIMAL" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.DECIMAL);
                    break;
                case "BINARY" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.BINARY);
                    break;
                case "GEOMETRY" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.GEOMETRY);
                    break;
                case "UUID" :
                    currentDataPropertyInfo = new DataPropertyInfo(propertyRealName,DataSlicePropertyType.UUID);
                    break;
            }
            if(currentDataPropertyInfo != null){
                if(!kindDataPropertiesMap.containsKey(KindName)){
                    kindDataPropertiesMap.put(KindName,new ArrayList<>());
                }
                kindDataPropertiesMap.get(KindName).add(currentDataPropertyInfo);
            }
        }
    }
}