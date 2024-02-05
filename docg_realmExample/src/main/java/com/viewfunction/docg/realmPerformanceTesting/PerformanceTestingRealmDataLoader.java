package com.viewfunction.docg.realmPerformanceTesting;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataSliceQueryResult;
import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.exception.ComputeGridNotActiveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceTestingRealmDataLoader {

    private static final String FirmConceptionType = "Firm";
    private static final String  Name = "name";
    private static final String  CompanyType = "companyType";
    private static final String  Address = "address";
    private static final String  Status = "status";
    private static final String  StartDate = "registrationDate";
    private static final String  RegistrationChangeDate = "lastRegistrationChangeDate";
    private static final String  CancelDate = "cancelDate";
    private static final String  Category = "category";
    private static final String  City = "city";
    private static final String  Province = "province";
    private static final String  Latitude = "latitude";
    private static final String  Longitude = "longitude";

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        //Step 1: init Geo Data
        //initDefaultGeoData();
        //Step 2: init Time Data
        //initDefaultTimeData();
        //Step 3: load firm data
        //loadFirmData("/media/wangychu/Data/Data/A_gridded_establishment_dataset_as_a_proxy_for_economic_activity_in_China/firm_2005.csv");
        //loadFirmData("/media/wangychu/Data/Data/A_gridded_establishment_dataset_as_a_proxy_for_economic_activity_in_China/firm_2010.csv");
        //loadFirmData("/media/wangychu/Data/Data/A_gridded_establishment_dataset_as_a_proxy_for_economic_activity_in_China/firm_2015.csv");
        //Step 4: Link Date
        //linkDate(StartDate,"firmStartedAt");
        //linkDate(RegistrationChangeDate,"firmRegistrationChangedAt");
        //linkDate(CancelDate,"firmCanceledAt");
        //linkCountyGeoData("R924dce5b06ea4f8e920fce8e8f1d9277");
        //linkTownshipGeoData("R2dcccb8ceeb648d8a99dbc9b342d4209");
    }

    private static void initDefaultGeoData(){
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        GeospatialRegion defaultGeospatialRegion = coreRealm.getOrCreateGeospatialRegion();
        defaultGeospatialRegion.createGeospatialScaleEntities();
    }

    private static void initDefaultTimeData() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        TimeFlow defaultTimeFlow = coreRealm.getOrCreateTimeFlow();
        defaultTimeFlow.createTimeSpanEntities(1980,2025,false);
    }

    private static void loadFirmData(String filePath) throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind _FirmConceptionKind = coreRealm.getConceptionKind(FirmConceptionType);
        _FirmConceptionKind = coreRealm.getConceptionKind(FirmConceptionType);
        if(_FirmConceptionKind == null){
            _FirmConceptionKind = coreRealm.createConceptionKind(FirmConceptionType,"中国工商企业");
        }

        List<ConceptionEntityValue> _FirmEntityValueList = Lists.newArrayList();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;

            int notCorrectCount = 0;
            while ((tempStr = reader.readLine()) != null) {
                String currentLine = !tempStr.startsWith("name")? tempStr : null;
                if(currentLine != null){
                    String name = null;
                    String company_type = null;
                    String address = null;
                    String status = null;
                    String start_date = null;
                    String approved_time = null;
                    String category = null;
                    String city = null;
                    String province = null;
                    String lat_wgs = null;
                    String lng_wgs = null;

                    String[] dataItems =  currentLine.split("\t");
                    if(dataItems.length != 11){
                        notCorrectCount++;
                    }else{
                        name = dataItems[0].trim();
                        company_type = dataItems[1].trim();
                        address = dataItems[2].trim();
                        status = dataItems[3].trim();
                        start_date = dataItems[4].trim();
                        approved_time = dataItems[5].trim();
                        category = dataItems[6].trim();
                        city = dataItems[7].trim();
                        province = dataItems[8].trim();
                        lat_wgs = dataItems[9].trim();
                        lng_wgs = dataItems[10].trim();

                        Map<String,Object> newEntityValueMap = new HashMap<>();

                        if(!name.equals("")){
                            newEntityValueMap.put(Name,name);
                        }
                        if(!company_type.equals("")){
                            newEntityValueMap.put(CompanyType,company_type);
                        }
                        if(!address.equals("")){
                            newEntityValueMap.put(Address,address);
                        }
                        if(!status.equals("")){
                            newEntityValueMap.put(Status,status);
                        }
                        if(!start_date.equals("")){
                            String startDateString = start_date.trim().replace(" 00:00:00","");
                            LocalDate startDate = LocalDate.parse(startDateString);
                            newEntityValueMap.put(StartDate,startDate);
                        }
                        if(!approved_time.equals("")){
                            //Date approved_timeObj = sdf.parse(approved_time.trim()+" 00:00:00");
                            String approvedDateString = approved_time.trim().replace(" 00:00:00","");
                            LocalDate approvedDate = LocalDate.parse(approvedDateString);
                            if(status != null & status.equals("注销")){
                                newEntityValueMap.put(CancelDate,approvedDate);
                            }else{
                                newEntityValueMap.put(RegistrationChangeDate,approvedDate);
                            }
                        }
                        if(!category.equals("")){
                            newEntityValueMap.put(Category,category);
                        }
                        if(!city.equals("")){
                            newEntityValueMap.put(City,city);
                        }
                        if(!province.equals("")){
                            newEntityValueMap.put(Province,province);
                        }
                        if(!lat_wgs.equals("")){
                            Double latitudeObject =Double.parseDouble(lat_wgs);
                            newEntityValueMap.put(Latitude,latitudeObject);
                        }
                        if(!lng_wgs.equals("")){
                            Double longitudeObject =Double.parseDouble(lng_wgs);
                            newEntityValueMap.put(Longitude,longitudeObject);
                        }
                        if(!lat_wgs.equals("") & !lng_wgs.equals("")){
                            newEntityValueMap.put(RealmConstant._GeospatialGeometryType,"POINT");
                            String locationWKT = "POINT ("+lng_wgs+" "+lat_wgs+")";
                            newEntityValueMap.put(RealmConstant._GeospatialGLGeometryContent,locationWKT);
                            newEntityValueMap.put(RealmConstant._GeospatialCLGeometryContent,locationWKT);
                            newEntityValueMap.put(RealmConstant._GeospatialGlobalCRSAID,"EPSG:4326"); // CRS EPSG:4326 - WGS 84 - Geographic
                            newEntityValueMap.put(RealmConstant._GeospatialCountryCRSAID,"EPSG:4490"); // CRS EPSG:4490 - CGCS2000 - Geographic
                        }
                        ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                        _FirmEntityValueList.add(conceptionEntityValue);
                    }
                }
            }
            reader.close();
            System.out.println(notCorrectCount);
            System.out.println(_FirmEntityValueList.size());
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
        BatchDataOperationUtil.batchAddNewEntities(FirmConceptionType,_FirmEntityValueList,30);
    }

    private static void linkDate(String datePropertyName,String attachEventName) throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        //Part 2 link to time
        ConceptionKind conceptionKind = coreRealm.getConceptionKind(FirmConceptionType);
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add(datePropertyName);
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);

        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        BatchDataOperationUtil.batchAttachTimeScaleEvents(conceptionEntityValueList,datePropertyName,null,attachEventName,null, TimeFlow.TimeScaleGrade.DAY,30);
    }

    private static void  linkCountyGeoData(String dataSliceName){
        List<RelationEntityValue> relationEntityValueList = new ArrayList<>();
        try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance()){
            DataSlice targetDataSlice = dataServiceInvoker.getDataSlice(dataSliceName);
            DataSliceQueryResult dataSliceQueryResult = targetDataSlice.queryDataRecords((com.viewfunction.docg.dataCompute.computeServiceCore.analysis.query.QueryParameters) null);
            for(Map<String,Object> currentData:dataSliceQueryResult.getResultRecords()){
                RelationEntityValue currentRelationEntityValue = new RelationEntityValue();
                currentRelationEntityValue.setFromConceptionEntityUID(currentData.get("FIRMDATA__REALMGLOBALUID").toString());
                currentRelationEntityValue.setToConceptionEntityUID(currentData.get("COUNTY__REALMGLOBALUID").toString());
                Map<String,Object> geospatialCodePropertyDataMap = new HashMap<>();
                geospatialCodePropertyDataMap.put(RealmConstant.GeospatialCodeProperty,currentData.get("COUNTY__DOCG_GEOSPATIALCODE").toString());
                currentRelationEntityValue.setEntityAttributesValue(geospatialCodePropertyDataMap);
                relationEntityValueList.add(currentRelationEntityValue);
            }
        } catch (ComputeGridNotActiveException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String,Object> attachGeoResult =
                BatchDataOperationUtil.batchAttachGeospatialScaleEvents(relationEntityValueList,null,"firmLocatedAtCounty",null, GeospatialRegion.GeospatialScaleGrade.COUNTY,16);
        System.out.println(attachGeoResult);
    }

    private static void  linkTownshipGeoData(String dataSliceName){
        List<RelationEntityValue> relationEntityValueList = new ArrayList<>();
        try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance()){
            DataSlice targetDataSlice = dataServiceInvoker.getDataSlice(dataSliceName);
            DataSliceQueryResult dataSliceQueryResult = targetDataSlice.queryDataRecords((com.viewfunction.docg.dataCompute.computeServiceCore.analysis.query.QueryParameters) null);
            for(Map<String,Object> currentData:dataSliceQueryResult.getResultRecords()){
                RelationEntityValue currentRelationEntityValue = new RelationEntityValue();
                currentRelationEntityValue.setFromConceptionEntityUID(currentData.get("FIRMDATA__REALMGLOBALUID").toString());
                currentRelationEntityValue.setToConceptionEntityUID(currentData.get("TOWNSHIP__REALMGLOBALUID").toString());
                Map<String,Object> geospatialCodePropertyDataMap = new HashMap<>();
                geospatialCodePropertyDataMap.put(RealmConstant.GeospatialCodeProperty,currentData.get("TOWNSHIP__DOCG_GEOSPATIALCODE").toString());
                currentRelationEntityValue.setEntityAttributesValue(geospatialCodePropertyDataMap);
                relationEntityValueList.add(currentRelationEntityValue);
            }
        } catch (ComputeGridNotActiveException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String,Object> attachGeoResult =
                BatchDataOperationUtil.batchAttachGeospatialScaleEvents(relationEntityValueList,null,"firmLocatedAtTownship",null, GeospatialRegion.GeospatialScaleGrade.TOWNSHIP,16);
        System.out.println(attachGeoResult);
    }
}
