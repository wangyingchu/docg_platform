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

public class RoadWeatherInformationStationsRecords_Realm_Generator {

    private static final String RoadWeatherInformationStationsRecordsConceptionType = "RoadWeatherRecords";
    private static final String StationName = "stationName";
    private static final String StationLocation = "stationLocation";
    private static final String RecordDateTime = "dateTime";
    private static final String RecordId = "recordId";
    private static final String RoadSurfaceTemperature = "roadSurfaceTemperature";
    private static final String AirTemperature = "airTemperature";

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        //createData();
        linkData();
    }

    private static void createData() throws CoreRealmServiceRuntimeException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();

        //Part 1
        ConceptionKind _WeatherInformationStationsRecordConceptionKind = coreRealm.getConceptionKind(RoadWeatherInformationStationsRecordsConceptionType);
        if(_WeatherInformationStationsRecordConceptionKind != null){
            coreRealm.removeConceptionKind(RoadWeatherInformationStationsRecordsConceptionType,true);
        }
        _WeatherInformationStationsRecordConceptionKind = coreRealm.getConceptionKind(RoadWeatherInformationStationsRecordsConceptionType);
        if(_WeatherInformationStationsRecordConceptionKind == null){
            _WeatherInformationStationsRecordConceptionKind = coreRealm.createConceptionKind(RoadWeatherInformationStationsRecordsConceptionType,"道路天气信息记录");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        List<ConceptionEntityValue> _WeatherInformationStationsRecordEntityValueList = Lists.newArrayList();
        //Please unzip Road_Weather_Information_Stations_huge.csv.zip before execute
        File file = new File("realmExampleData/road_weather_information_stations_records/Road_Weather_Information_Stations_huge.csv");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                String currentLine = !tempStr.startsWith("StationName,StationLocation,DateTime,")? tempStr : null;
                if(currentLine != null){
                    String stationName = null;
                    String stationLocation = null;
                    String dateTime = null;
                    String recordId = null;
                    String roadSurfaceTemperature = null;
                    String airTemperature = null;

                    String[] dataItems =  currentLine.split(",");
                    stationName = dataItems[0].trim();
                    stationLocation = dataItems[1].trim();
                    dateTime = dataItems[2].trim();
                    recordId = dataItems[3].trim();
                    roadSurfaceTemperature = dataItems[4].trim();
                    airTemperature = dataItems[5].trim();

                    Map<String,Object> newEntityValueMap = new HashMap<>();
                    newEntityValueMap.put(StationName,stationName);
                    newEntityValueMap.put(StationLocation,stationLocation);
                    Date date = sdf.parse(dateTime);
                    newEntityValueMap.put(RecordDateTime,date);
                    newEntityValueMap.put(RecordId,recordId);
                    newEntityValueMap.put(RoadSurfaceTemperature,Double.valueOf(roadSurfaceTemperature));
                    newEntityValueMap.put(AirTemperature,Double.valueOf(airTemperature));

                    newEntityValueMap.put("DOCG_GS_GLGeometryContent",stationLocation);
                    newEntityValueMap.put("DOCG_GS_GeometryType","POINT");
                    newEntityValueMap.put("DOCG_GS_GlobalCRSAID","EPSG:4326");

                    ConceptionEntityValue conceptionEntityValue = new ConceptionEntityValue(newEntityValueMap);
                    _WeatherInformationStationsRecordEntityValueList.add(conceptionEntityValue);
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
        BatchDataOperationUtil.batchAddNewEntities(RoadWeatherInformationStationsRecordsConceptionType,_WeatherInformationStationsRecordEntityValueList,10);
    }

    private static void linkData() throws CoreRealmServiceEntityExploreException {
        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        //Part 2 link to time
        ConceptionKind conceptionKind = coreRealm.getConceptionKind(RoadWeatherInformationStationsRecordsConceptionType);
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(10000000);
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add(RecordDateTime);
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  conceptionKind.getSingleValueEntityAttributesByAttributeNames(attributeNamesList,queryParameters);

        List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
        BatchDataOperationUtil.batchAttachTimeScaleEvents(conceptionEntityValueList,RecordDateTime,"recordedAt",null, TimeFlow.TimeScaleGrade.MINUTE,10);
    }
}
