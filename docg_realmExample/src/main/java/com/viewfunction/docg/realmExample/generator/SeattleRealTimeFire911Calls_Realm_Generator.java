package com.viewfunction.docg.realmExample.generator;

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
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SeattleRealTimeFire911Calls_Realm_Generator {

    private static final String Fire911CallConceptionType = "Fire911Call";
    private static final String Address = "address";
    private static final String Type = "type";
    private static final String Datetime = "datetime";
    private static final String Latitude = "latitude";
    private static final String Longitude = "longitude";
    private static final String Location = "location";
    private static final String IncidentNumber = "incidentNumber";

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        //createData();
        linkData();
    }

    public static void createData() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        //Part 1
        ConceptionKind _Fire911CallConceptionKind = coreRealm.getConceptionKind(Fire911CallConceptionType);
        if(_Fire911CallConceptionKind != null){
            coreRealm.removeConceptionKind(Fire911CallConceptionType,true);
        }
        _Fire911CallConceptionKind = coreRealm.getConceptionKind(Fire911CallConceptionType);
        if(_Fire911CallConceptionKind == null){
            _Fire911CallConceptionKind = coreRealm.createConceptionKind(Fire911CallConceptionType,"911报警记录");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");

        List<ConceptionEntityValue> _Fire911CallEntityValueList = Lists.newArrayList();
        //Please unzip Seattle_Real_Time_Fire_911_Calls_huge.csv.zip before execute
        File file = new File("realmExampleData/seattle_fire_911_calls/Seattle_Real_Time_Fire_911_Calls_huge.csv");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                String currentLine = !tempStr.startsWith("Address,")? tempStr : null;
                if(currentLine != null){
                    String address = null;
                    String type = null;
                    String datetime = null;
                    String latitude = null;
                    String longitude = null;
                    String reportLocation = null;
                    String incidentNumber = null;

                    String[] dataItems =  currentLine.split(",");

                    if(dataItems.length == 7){
                        address = dataItems[0].trim();
                        type = dataItems[1].trim();
                        datetime = dataItems[2].trim();
                        latitude = dataItems[3].trim();
                        longitude = dataItems[4].trim();
                        reportLocation = dataItems[5].trim();
                        incidentNumber = dataItems[6].trim();
                    }

                    if(dataItems.length == 8){
                        address = dataItems[0].trim();
                        type = (dataItems[1].trim()+dataItems[2].trim()).replaceAll("\"","");
                        datetime = dataItems[3].trim();
                        latitude = dataItems[4].trim();
                        longitude = dataItems[5].trim();
                        reportLocation = dataItems[6].trim();
                        incidentNumber = dataItems[7].trim();
                    }

                    Map<String,Object> newEntityValueMap = new HashMap<>();

                    newEntityValueMap.put(Address,address);
                    newEntityValueMap.put(Type,type);
                    Date date = sdf.parse(datetime);
                    newEntityValueMap.put(Datetime,date);
                    if(!latitude.equals("")){
                        newEntityValueMap.put(Latitude,Double.parseDouble(latitude));
                    }
                    if(!longitude.equals("")){
                        newEntityValueMap.put(Longitude,Double.parseDouble(longitude));
                    }
                    newEntityValueMap.put(Location,reportLocation);
                    newEntityValueMap.put(IncidentNumber,incidentNumber);

                    newEntityValueMap.put("DOCG_GS_GLGeometryContent",reportLocation);
                    newEntityValueMap.put("DOCG_GS_GeometryType","POINT");
                    newEntityValueMap.put("DOCG_GS_GlobalCRSAID","EPSG:4326");

                    ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                    _Fire911CallEntityValueList.add(conceptionEntityValue);
                }
            }
            reader.close();
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

        BatchDataOperationUtil.batchAddNewEntities(Fire911CallConceptionType,_Fire911CallEntityValueList,10);
    }

    public static void linkData() throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException{
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        //Part 2 link to time
        ConceptionKind conceptionKind = coreRealm.getConceptionKind(Fire911CallConceptionType);
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add(Datetime);
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);
        conceptionEntitiesAttributeResult.getConceptionEntityValues();

        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        BatchDataOperationUtil.batchAttachTimeScaleEvents(conceptionEntityValueList,Datetime,"occurredAt",null, TimeFlow.TimeScaleGrade.MINUTE,10);
    }
}
