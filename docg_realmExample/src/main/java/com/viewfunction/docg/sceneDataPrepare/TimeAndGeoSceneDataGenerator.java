package com.viewfunction.docg.sceneDataPrepare;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TimeAndGeoSceneDataGenerator {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        //load data
        //SeattleRealTimeFire911Calls_Realm_Generator.main(null);
        //RoadWeatherInformationStationsRecords_Realm_Generator.main(null);
        //generateFileViolationsData();
        //generateNoiseReportsData();
        //generatePaidParkingTransactionData();

        generateSPDCrimeData();
    }

    private static void generateFileViolationsData() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        initConceptionKind("FireViolation","消防违规");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        BatchDataOperationUtil.ConceptionEntityAttributesProcess conceptionEntityAttributesProcess = new BatchDataOperationUtil.ConceptionEntityAttributesProcess(){
            @Override
            public void doConceptionEntityAttributesProcess(Map<String, Object> entityValueMap) {
                if(entityValueMap != null && entityValueMap.containsKey("Location")){
                    String locationPoint = entityValueMap.get("Location").toString();
                    entityValueMap.put(RealmConstant._GeospatialGLGeometryContent,locationPoint);
                    entityValueMap.put(RealmConstant._GeospatialGeometryType,"POINT");
                    entityValueMap.put(RealmConstant._GeospatialGlobalCRSAID,"EPSG:4326");
                }
                if(entityValueMap.containsKey("violation date") && !entityValueMap.get("violation date").toString().equals("")){
                    try {
                        Date date = sdf.parse(entityValueMap.get("violation date").toString());
                        entityValueMap.put("violationDate",date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(entityValueMap.containsKey("close date") && !entityValueMap.get("close date").toString().equals("")){
                    try {
                        Date date = sdf.parse(entityValueMap.get("close date").toString());
                        entityValueMap.put("closeDate",date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        BatchDataOperationUtil.importConceptionEntitiesFromExternalCSV("realmExampleData/time_and_geo_scene_data/Fire_Violations.csv","FireViolation",conceptionEntityAttributesProcess);
        linkDateAttribute("FireViolation","violationDate","Fire Violation occurred at",null,TimeFlow.TimeScaleGrade.DAY);
        linkDateAttribute("FireViolation","closeDate","Fire Violation closed at",null,TimeFlow.TimeScaleGrade.DAY);
    }

    private static void generateNoiseReportsData() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        initConceptionKind("NoiseReport","噪声报告");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");
        BatchDataOperationUtil.ConceptionEntityAttributesProcess conceptionEntityAttributesProcess = new BatchDataOperationUtil.ConceptionEntityAttributesProcess(){
            @Override
            public void doConceptionEntityAttributesProcess(Map<String, Object> entityValueMap){
                if(entityValueMap != null && entityValueMap.containsKey("Point")&& entityValueMap.containsKey("Source")){
                    String longitude = entityValueMap.get("Source").toString().replace(")\"","");
                    String latitude = entityValueMap.get("Point").toString().replace("\"(","");
                    String locationPoint = "POINT ("+longitude+" "+latitude+")";
                    if(!locationPoint.equals("POINT ( 0.0 0.0)")){
                        entityValueMap.put(RealmConstant._GeospatialGLGeometryContent, locationPoint);
                        entityValueMap.put(RealmConstant._GeospatialGeometryType, "POINT");
                        entityValueMap.put(RealmConstant._GeospatialGlobalCRSAID, "EPSG:4326");
                    }
                }
                if(entityValueMap.containsKey("Opened") && !entityValueMap.get("Opened").toString().equals("")){
                    Date date = null;
                    try {
                        date = sdf.parse(entityValueMap.get("Opened").toString());
                        entityValueMap.put("openedDate",date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(entityValueMap.containsKey("Closed") && !entityValueMap.get("Closed").toString().equals("")){
                    Date date = null;
                    try {
                        date = sdf.parse(entityValueMap.get("Closed").toString());
                        entityValueMap.put("closedDate",date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(entityValueMap.containsKey("Updated") && !entityValueMap.get("Updated").toString().equals("")){
                    Date date = null;
                    try {
                        date = sdf.parse(entityValueMap.get("Updated").toString());
                        entityValueMap.put("updatedDate",date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        BatchDataOperationUtil.importConceptionEntitiesFromExternalCSV("realmExampleData/time_and_geo_scene_data/Noise_Reports.csv","NoiseReport",conceptionEntityAttributesProcess);
        linkDateAttribute("NoiseReport","openedDate","Noise Report opened at",null,TimeFlow.TimeScaleGrade.MINUTE);
        linkDateAttribute("NoiseReport","closedDate","Noise Report closed at",null,TimeFlow.TimeScaleGrade.MINUTE);
        linkDateAttribute("NoiseReport","updatedDate","Noise Report updated at",null,TimeFlow.TimeScaleGrade.MINUTE);
    }

    private static void generatePaidParkingTransactionData() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        initConceptionKind("PaidParkingTransaction","停车缴费交易");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");
        BatchDataOperationUtil.ConceptionEntityAttributesProcess conceptionEntityAttributesProcess = new BatchDataOperationUtil.ConceptionEntityAttributesProcess(){
            @Override
            public void doConceptionEntityAttributesProcess(Map<String, Object> entityValueMap) {
                if(entityValueMap != null && entityValueMap.containsKey("Latitude") && entityValueMap.containsKey("Longitude")){
                    String locationPoint = "POINT ("+entityValueMap.get("Longitude")+" "+entityValueMap.get("Latitude")+")";
                    entityValueMap.put(RealmConstant._GeospatialGLGeometryContent,locationPoint);
                    entityValueMap.put(RealmConstant._GeospatialGeometryType,"POINT");
                    entityValueMap.put(RealmConstant._GeospatialGlobalCRSAID,"EPSG:4326");
                }
                if(entityValueMap.containsKey("Transaction DateTime") && !entityValueMap.get("Transaction DateTime").toString().equals("")){
                    Date date = null;
                    try {
                        date = sdf.parse(entityValueMap.get("Transaction DateTime").toString());
                        entityValueMap.put("transactionDate",date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        BatchDataOperationUtil.importConceptionEntitiesFromExternalCSV("realmExampleData/time_and_geo_scene_data/Paid_Parking_Transaction_Data.csv","PaidParkingTransaction",conceptionEntityAttributesProcess);
        linkDateAttribute("PaidParkingTransaction","transactionDate","Payment transaction occurred at",null,TimeFlow.TimeScaleGrade.MINUTE);
    }

    private static void generateSPDCrimeData() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        initConceptionKind("SPDCrimeReport","SPD犯罪记录");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa");
        BatchDataOperationUtil.ConceptionEntityAttributesProcess conceptionEntityAttributesProcess = new BatchDataOperationUtil.ConceptionEntityAttributesProcess(){
            @Override
            public void doConceptionEntityAttributesProcess(Map<String, Object> entityValueMap) {
                if(entityValueMap != null && entityValueMap.containsKey("Latitude") && entityValueMap.containsKey("Longitude")){
                    String locationPoint = "POINT ("+entityValueMap.get("Longitude")+" "+entityValueMap.get("Latitude")+")";
                    entityValueMap.put(RealmConstant._GeospatialGLGeometryContent,locationPoint);
                    entityValueMap.put(RealmConstant._GeospatialGeometryType,"POINT");
                    entityValueMap.put(RealmConstant._GeospatialGlobalCRSAID,"EPSG:4326");
                }
                if(entityValueMap.containsKey("Offense End DateTime") && !entityValueMap.get("Offense End DateTime").toString().equals("")){
                    Date date = null;
                    try {
                        date = sdf.parse(entityValueMap.get("Offense End DateTime").toString());
                        entityValueMap.put("offenseEndDate",date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(entityValueMap.containsKey("Offense Start DateTime") && !entityValueMap.get("Offense Start DateTime").toString().equals("")){
                    Date date = null;
                    try {
                        date = sdf.parse(entityValueMap.get("Offense Start DateTime").toString());
                        entityValueMap.put("offenseStartDate",date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(entityValueMap.containsKey("Report DateTime") && !entityValueMap.get("Report DateTime").toString().equals("")){
                    Date date = null;
                    try {
                        date = sdf.parse(entityValueMap.get("Report DateTime").toString());
                        entityValueMap.put("crimeReportDate",date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        BatchDataOperationUtil.importConceptionEntitiesFromExternalCSV("realmExampleData/spd_crime_data/SPD_Crime_Data__2008-Present.csv","SPDCrimeReport",conceptionEntityAttributesProcess);
        linkDateAttribute("SPDCrimeReport","offenseEndDate","Offense ended at",null,TimeFlow.TimeScaleGrade.MINUTE);
        linkDateAttribute("SPDCrimeReport","offenseStartDate","Offense started at",null,TimeFlow.TimeScaleGrade.MINUTE);
        linkDateAttribute("SPDCrimeReport","crimeReportDate","Crime report at",null,TimeFlow.TimeScaleGrade.MINUTE);
    }

    private static void initConceptionKind(String conceptionKindName,String conceptionKindDesc) throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind targetConceptionKind = coreRealm.getConceptionKind(conceptionKindName);
        if(targetConceptionKind != null){
            coreRealm.removeConceptionKind(targetConceptionKind.getConceptionKindName(),true);
        }
        targetConceptionKind = coreRealm.getConceptionKind(conceptionKindName);
        if(targetConceptionKind == null){
            targetConceptionKind = coreRealm.createConceptionKind(conceptionKindName,conceptionKindDesc);
        }
    }

    public static void linkDateAttribute(String conceptionKindName,String dateAttributeName,String eventComment,Map<String,Object> globalEventData,TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException{
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        ConceptionKind conceptionKind = coreRealm.getConceptionKind(conceptionKindName);
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add(dateAttributeName);
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);
        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        BatchDataOperationUtil.batchAttachTimeScaleEvents(conceptionEntityValueList,dateAttributeName,eventComment,globalEventData, timeScaleGrade, BatchDataOperationUtil.CPUUsageRate.High);
    }
}