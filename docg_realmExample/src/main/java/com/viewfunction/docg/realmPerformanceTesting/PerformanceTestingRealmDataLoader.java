package com.viewfunction.docg.realmPerformanceTesting;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PerformanceTestingRealmDataLoader {

    private static final String FirmConceptionType = "Firm";
    private static final String  Name = "name";
    private static final String  CompanyType = "companyType";
    private static final String  Address = "address";
    private static final String  Status = "status";
    private static final String  StartDate = "startDate";
    private static final String  ApprovedDate = "approvedDate";
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
        //linkDate(ApprovedDate,"firmApprovedAt");
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
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd");
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
                            Date start_dateObj = sdf.parse(start_date);
                            newEntityValueMap.put(StartDate,start_dateObj);
                        }
                        if(!approved_time.equals("")){
                            Date approved_timeObj = sdf.parse(approved_time);
                            newEntityValueMap.put(ApprovedDate,approved_timeObj);
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
        } catch (IOException | ParseException e) {
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
        BatchDataOperationUtil.batchAddNewEntities(FirmConceptionType,_FirmEntityValueList,20);
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
        BatchDataOperationUtil.batchAttachTimeScaleEvents(conceptionEntityValueList,datePropertyName,attachEventName,null, TimeFlow.TimeScaleGrade.DAY,30);
    }
}
