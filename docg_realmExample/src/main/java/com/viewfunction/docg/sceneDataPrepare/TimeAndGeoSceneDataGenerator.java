package com.viewfunction.docg.sceneDataPrepare;

import com.google.common.collect.Lists;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

public interface TimeAndGeoSceneDataGenerator {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        //load data
        //SeattleRealTimeFire911Calls_Realm_Generator.main(null);
        //RoadWeatherInformationStationsRecords_Realm_Generator.main(null);
        generateFileViolationsData();
        //generateNoiseReportsData();
        //generatePaidParkingTransactionData();
    }

    private static void generateFileViolationsData() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind _FireViolationConceptionKind = coreRealm.getConceptionKind("FireViolation");
        if(_FireViolationConceptionKind != null){
            coreRealm.removeConceptionKind(_FireViolationConceptionKind.getConceptionKindName(),true);
        }
        _FireViolationConceptionKind = coreRealm.getConceptionKind("FireViolation");
        if(_FireViolationConceptionKind == null){
            _FireViolationConceptionKind = coreRealm.createConceptionKind("FireViolation","消防违规");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        ConceptionEntityAttributesProcess conceptionEntityAttributesProcess = new ConceptionEntityAttributesProcess(){
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
        importConceptionEntitiesFromExternalCSV("realmExampleData/time_and_geo_scene_data/Fire_Violations.csv",_FireViolationConceptionKind.getConceptionKindName(),conceptionEntityAttributesProcess);
        linkDateAttribute("FireViolation","violationDate","Fire Violation occurred at",null,TimeFlow.TimeScaleGrade.DAY);
        linkDateAttribute("FireViolation","closeDate","Fire Violation closed at",null,TimeFlow.TimeScaleGrade.DAY);
    }

    private static void generateNoiseReportsData() throws CoreRealmServiceRuntimeException {
        initConceptionKind("NoiseReport","噪声报告");
        ConceptionEntityAttributesProcess conceptionEntityAttributesProcess = new ConceptionEntityAttributesProcess(){
            @Override
            public void doConceptionEntityAttributesProcess(Map<String, Object> entityValueMap) {
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
            }
        };
        importConceptionEntitiesFromExternalCSV("realmExampleData/time_and_geo_scene_data/Noise_Reports.csv","NoiseReport",conceptionEntityAttributesProcess);
    }

    private static void generatePaidParkingTransactionData() throws CoreRealmServiceRuntimeException {
        initConceptionKind("PaidParkingTransaction","停车缴费交易");
        ConceptionEntityAttributesProcess conceptionEntityAttributesProcess = new ConceptionEntityAttributesProcess(){
            @Override
            public void doConceptionEntityAttributesProcess(Map<String, Object> entityValueMap) {
                if(entityValueMap != null && entityValueMap.containsKey("Latitude") && entityValueMap.containsKey("Longitude")){
                    String locationPoint = "POINT ("+entityValueMap.get("Longitude")+" "+entityValueMap.get("Latitude")+")";
                    entityValueMap.put(RealmConstant._GeospatialGLGeometryContent,locationPoint);
                    entityValueMap.put(RealmConstant._GeospatialGeometryType,"POINT");
                    entityValueMap.put(RealmConstant._GeospatialGlobalCRSAID,"EPSG:4326");
                }
            }
        };
        importConceptionEntitiesFromExternalCSV("realmExampleData/time_and_geo_scene_data/Paid_Parking_Transaction_Data.csv","PaidParkingTransaction",conceptionEntityAttributesProcess);
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
        //Part 2 link to time
        ConceptionKind conceptionKind = coreRealm.getConceptionKind(conceptionKindName);
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add(dateAttributeName);
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);
        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        BatchDataOperationUtil.batchAttachTimeScaleEvents(conceptionEntityValueList,dateAttributeName,eventComment,globalEventData, timeScaleGrade, BatchDataOperationUtil.CPUUsageRate.High);
    }



    public interface ConceptionEntityAttributesProcess {
        void doConceptionEntityAttributesProcess(Map<String,Object> entityValueMap);
    }

    public static boolean importConceptionEntitiesFromExternalCSV(String csvLocation, String conceptionKind, ConceptionEntityAttributesProcess conceptionEntityAttributesProcess){
        if(csvLocation == null || conceptionKind == null){
            return false;
        }else{
            try{
                List<ConceptionEntityValue> _conceptionEntityValueList = Lists.newArrayList();
                BufferedReader reader = new BufferedReader(new FileReader(csvLocation));
                String header = reader.readLine();
                List<String> attributeNameList = new ArrayList<>();
                String[] attributesArray = header.split(",");
                for(String currentStr : attributesArray){
                    attributeNameList.add(currentStr.replaceAll("\"",""));
                }
                reader.close();
                File file = new File(csvLocation);
                reader = new BufferedReader(new FileReader(file));
                String tempStr;
                int lineCount = 0;

                while ((tempStr = reader.readLine()) != null) {
                    if(lineCount > 0){
                        Map<String,Object> newEntityValueMap = new HashMap<>();
                        String[] dataItems = tempStr.split(",");
                        if(dataItems.length == attributeNameList.size()) {
                            for (int i = 0; i < dataItems.length; i++) {
                                String attributeName = attributeNameList.get(i);
                                String attributeOriginalValue = dataItems[i];
                                newEntityValueMap.put(attributeName, attributeOriginalValue);
                            }
                            if(conceptionEntityAttributesProcess != null){
                                conceptionEntityAttributesProcess.doConceptionEntityAttributesProcess(newEntityValueMap);
                            }
                            ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                            conceptionEntityValue.setEntityAttributesValue(newEntityValueMap);
                            _conceptionEntityValueList.add(conceptionEntityValue);
                        }
                    }
                    lineCount ++;
                }
                reader.close();

                BatchDataOperationUtil.batchAddNewEntities(conceptionKind,_conceptionEntityValueList, BatchDataOperationUtil.CPUUsageRate.High);
            } catch (IOException e) {
                    throw new RuntimeException(e);
            }
            return true;
        }
    }
}