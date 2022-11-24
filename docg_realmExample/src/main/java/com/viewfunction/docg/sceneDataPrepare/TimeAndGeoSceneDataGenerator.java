package com.viewfunction.docg.sceneDataPrepare;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TimeAndGeoSceneDataGenerator {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        //load data
        //SeattleRealTimeFire911Calls_Realm_Generator.main(null);
        //RoadWeatherInformationStationsRecords_Realm_Generator.main(null);
        //generateFileViolationsData();
        //generateNoiseReportsData();
        generatePaidParkingTransactionData();
    }

    private static void generateFileViolationsData() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind _FireViolationConceptionKind = coreRealm.getConceptionKind("FireViolation");
        if(_FireViolationConceptionKind != null){
            coreRealm.removeConceptionKind(_FireViolationConceptionKind.getConceptionKindName(),true);
        }
        _FireViolationConceptionKind = coreRealm.getConceptionKind("FireViolation");
        if(_FireViolationConceptionKind == null){
            _FireViolationConceptionKind = coreRealm.createConceptionKind("FireViolation","消防违规");
        }
        importConceptionEntitiesFromExternalCSV("realmExampleData/time_and_geo_scene_data/Fire_Violations.csv",_FireViolationConceptionKind.getConceptionKindName(),null);
    }

    private static void generateNoiseReportsData() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind _NoiseReportConceptionKind = coreRealm.getConceptionKind("NoiseReport");
        if(_NoiseReportConceptionKind != null){
            coreRealm.removeConceptionKind(_NoiseReportConceptionKind.getConceptionKindName(),true);
        }
        _NoiseReportConceptionKind = coreRealm.getConceptionKind("NoiseReport");
        if(_NoiseReportConceptionKind == null){
            _NoiseReportConceptionKind = coreRealm.createConceptionKind("NoiseReport","噪声报告");
        }
        importConceptionEntitiesFromExternalCSV("realmExampleData/time_and_geo_scene_data/Noise_Reports.csv",_NoiseReportConceptionKind.getConceptionKindName(),null);
    }

    private static void generatePaidParkingTransactionData() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        ConceptionKind _PaidParkingTransactionConceptionKind = coreRealm.getConceptionKind("PaidParkingTransaction");
        if(_PaidParkingTransactionConceptionKind != null){
            coreRealm.removeConceptionKind(_PaidParkingTransactionConceptionKind.getConceptionKindName(),true);
        }
        _PaidParkingTransactionConceptionKind = coreRealm.getConceptionKind("PaidParkingTransaction");
        if(_PaidParkingTransactionConceptionKind == null){
            _PaidParkingTransactionConceptionKind = coreRealm.createConceptionKind("PaidParkingTransaction","停车缴费交易");
        }

        ConceptionEntityAttributesProcess conceptionEntityAttributesProcess = new ConceptionEntityAttributesProcess(){
            @Override
            public void doConceptionEntityAttributesProcess(Map<String, Object> entityValueMap) {

            }
        };
        importConceptionEntitiesFromExternalCSV("realmExampleData/time_and_geo_scene_data/Paid_Parking_Transaction_Data.csv",_PaidParkingTransactionConceptionKind.getConceptionKindName(),conceptionEntityAttributesProcess);
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