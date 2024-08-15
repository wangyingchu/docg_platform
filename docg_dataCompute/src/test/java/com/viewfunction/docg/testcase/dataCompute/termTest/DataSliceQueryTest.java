package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.analysis.query.filteringItem.*;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSliceDataException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceQueryResult;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlice;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataSliceQueryTest {

    private static final String RoadWeatherInformationStationsRecordsDataSlice = "RoadWeatherRecords";
    private static final String StationName = "stationName";
    private static final String StationLocation = "stationLocation";
    private static final String RecordDateTime = "dateTime";
    private static final String RecordId = "recordId";
    private static final String RoadSurfaceTemperature = "roadSurfaceTemperature";
    private static final String AirTemperature = "airTemperature";

    @BeforeTest
    public void initData(){
        System.out.println("--------------------------------------------------");
        System.out.println("Init unit test data for DataSliceQueryTest");
        System.out.println("--------------------------------------------------");

        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(DataService dataService = targetComputeGrid.getDataService()){
            DataSlice targetDataSlice = dataService.getDataSlice(RoadWeatherInformationStationsRecordsDataSlice);
            if(targetDataSlice == null){
                Map<String, DataSlicePropertyType> dataSlicePropertyMap = new HashMap<>();
                dataSlicePropertyMap.put(StationName,DataSlicePropertyType.STRING);
                dataSlicePropertyMap.put(StationLocation,DataSlicePropertyType.STRING);
                dataSlicePropertyMap.put(RecordDateTime,DataSlicePropertyType.TIMESTAMP);
                dataSlicePropertyMap.put(RecordId,DataSlicePropertyType.STRING);
                dataSlicePropertyMap.put(RoadSurfaceTemperature,DataSlicePropertyType.DOUBLE);
                dataSlicePropertyMap.put(AirTemperature,DataSlicePropertyType.DOUBLE);

                List<String> pkList = new ArrayList<>();
                pkList.add(RecordId);
                targetDataSlice = dataService.createGridDataSlice(RoadWeatherInformationStationsRecordsDataSlice,"defaultSliceGroup",dataSlicePropertyMap,pkList);

                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
                File file = new File("src/test/Road_Weather_Information_Stations.csv");

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String tempStr;
                int lineCount = 0;

                while ((tempStr = reader.readLine()) != null) {
                    if(lineCount != 0){
                        Map<String,Object> newEntityValueMap = new HashMap<>();
                        String[] dataItems = tempStr.split(",");

                        String stationName = dataItems[0];
                        String stationLocation = dataItems[1];
                        String recordDateTime = dataItems[2];
                        String recordId = dataItems[3];
                        String roadSurfaceTemperature = dataItems[4];
                        String airTemperature = dataItems[5];

                        newEntityValueMap.put(StationName,stationName);
                        newEntityValueMap.put(StationLocation,stationLocation);
                        newEntityValueMap.put(RecordDateTime,format.parse(recordDateTime));
                        newEntityValueMap.put(RecordId,recordId+ UUID.randomUUID());
                        newEntityValueMap.put(RoadSurfaceTemperature,Double.valueOf(roadSurfaceTemperature));
                        newEntityValueMap.put(AirTemperature,Double.valueOf(airTemperature));
                        targetDataSlice.addDataRecord(newEntityValueMap);
                    }
                    lineCount++;
                }
                reader.close();
                Assert.assertEquals(targetDataSlice.getDataSliceMetaInfo().getTotalDataCount(),20319);
            }
        } catch (ComputeGridException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDataSliceQueryFunction() throws ComputeGridException {
        ComputeGrid targetComputeGrid = ComputeGridTermFactory.getComputeGrid();
        try(DataService dataService = targetComputeGrid.getDataService()){
            DataSlice targetDataSlice = dataService.getDataSlice(RoadWeatherInformationStationsRecordsDataSlice);

            DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
            Assert.assertEquals(dataSliceMetaInfo.getPrimaryDataCount(),20319);

            DataSliceQueryResult dataSliceQueryResult = targetDataSlice.queryDataRecords("SELECT * FROM RoadWeatherRecords where airTEMPERATURE > 70.0 and airTEMPERATURE <74 LIMIT 100");
            Assert.assertNotNull(dataSliceQueryResult.getQueryLogic());
            Assert.assertNotNull(dataSliceQueryResult.getStartTime());
            Assert.assertNotNull(dataSliceQueryResult.getFinishTime());
            Assert.assertNotNull(dataSliceQueryResult.getOperationSummary());
            Assert.assertNotNull(dataSliceQueryResult.getResultRecords());
            Assert.assertEquals(dataSliceQueryResult.getResultRecords().size(),100);

            dataSliceQueryResult = targetDataSlice.queryDataRecords("SELECT * FROM RoadWeatherRecords where airTEMPERATURE > 70.0 and airTEMPERATURE <79 LIMIT 100");
            Assert.assertEquals(dataSliceQueryResult.getResultRecords().size(),100);

            dataSliceQueryResult = targetDataSlice.queryDataRecords("SELECT * FROM RoadWeatherRecords where airTEMPERATURE > 70.0 and airTEMPERATURE <74");
            Assert.assertEquals(dataSliceQueryResult.getResultRecords().size(),2659);

            dataSliceQueryResult = targetDataSlice.queryDataRecords("SELECT * FROM RoadWeatherRecords where airTEMPERATURE > 70.0 and airTEMPERATURE <79 LIMIT 50");
            Assert.assertEquals(dataSliceQueryResult.getResultRecords().size(),50);
            List<Map<String, Object>> recordResult = dataSliceQueryResult.getResultRecords();
            for (Map<String, Object> currentRecord : recordResult) {
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                Assert.assertTrue(airTEMPERATUREValue>70.0);
                Assert.assertTrue(airTEMPERATUREValue<79.0);
            }

            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setResultNumber(50000);

            FilteringItem filteringItem = new EqualFilteringItem("AirTemperature", 66.65);
            queryParameters.setDefaultFilteringItem(filteringItem);
            DataSliceQueryResult dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            List<Map<String, Object>> resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),12);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                Assert.assertTrue(airTEMPERATUREValue==66.65);
            }

            filteringItem = new BetweenFilteringItem("AirTemperature",Double.valueOf("65.66"),Double.valueOf("65.77"));
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),91);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                Assert.assertTrue(airTEMPERATUREValue>=65.65);
                Assert.assertTrue(airTEMPERATUREValue<=65.77);
            }

            filteringItem = new GreaterThanFilteringItem("AirTemperature",66.65);
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),7109);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                //System.out.println(airTEMPERATUREValue);
                Assert.assertTrue(airTEMPERATUREValue>66.65);
            }

            filteringItem = new GreaterThanEqualFilteringItem("AirTemperature",66.65);
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),7121);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                //System.out.println(airTEMPERATUREValue);
                Assert.assertTrue(airTEMPERATUREValue>=66.65);
            }

            List<Object> valueList = new ArrayList<>();
            valueList.add(Double.valueOf("66.65"));
            valueList.add(Double.valueOf("65.77"));
            filteringItem = new InValueFilteringItem("AirTemperature",valueList);
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),18);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                //System.out.println(airTEMPERATUREValue);
                Assert.assertTrue((airTEMPERATUREValue==66.65 | airTEMPERATUREValue==65.77));
            }

            filteringItem = new LessThanEqualFilteringItem("AirTemperature",66.65);
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),13210);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                //System.out.println(airTEMPERATUREValue);
                Assert.assertTrue(airTEMPERATUREValue<=66.65);
            }

            filteringItem = new LessThanFilteringItem("AirTemperature",66.65);
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),13198);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                //System.out.println(airTEMPERATUREValue);
                Assert.assertTrue(airTEMPERATUREValue<66.65);
            }

            filteringItem = new NotEqualFilteringItem("AirTemperature",66.65);
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),20307);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                //System.out.println(airTEMPERATUREValue);
                Assert.assertTrue(airTEMPERATUREValue!=66.65);
            }

            filteringItem = new NullValueFilteringItem("AirTemperature");
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),0);

            boolean exceptionThrown = false;
            filteringItem = new NullValueFilteringItem("AirTemperatureNOTExist");
            queryParameters.setDefaultFilteringItem(filteringItem);
            try {
                dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            }catch (DataSliceDataException e){
                exceptionThrown = true;
            }
            Assert.assertTrue(exceptionThrown);

            filteringItem = new SimilarFilteringItem("STATIONNAME","Bridge", SimilarFilteringItem.MatchingType.EndWith);
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),6750);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                String sTATIONNAME = currentRecord.get("STATIONNAME".toUpperCase()).toString();
                //System.out.println(sTATIONNAME);
                Assert.assertTrue(sTATIONNAME.endsWith("Bridge"));
            }

            filteringItem = new SimilarFilteringItem("STATIONNAME","Magno", SimilarFilteringItem.MatchingType.BeginWith);
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),3393);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                String sTATIONNAME = currentRecord.get("STATIONNAME".toUpperCase()).toString();
                //System.out.println(sTATIONNAME);
                Assert.assertTrue(sTATIONNAME.startsWith("Magno"));
            }

            filteringItem = new SimilarFilteringItem("STATIONNAME","SWMy", SimilarFilteringItem.MatchingType.Contain);
            queryParameters.setDefaultFilteringItem(filteringItem);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),3389);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                String sTATIONNAME = currentRecord.get("STATIONNAME".toUpperCase()).toString();
                //System.out.println(sTATIONNAME);
                Assert.assertTrue(sTATIONNAME.contains("SWMy"));
                Assert.assertTrue(!sTATIONNAME.startsWith("SWMy"));
                Assert.assertTrue(!sTATIONNAME.endsWith("SWMy"));
            }

            filteringItem = new EqualFilteringItem("AIRTEMPERATURE", 66.65);
            queryParameters.setDefaultFilteringItem(filteringItem);

            FilteringItem andFilteringItem1 = new GreaterThanFilteringItem("ROADSURFACETEMPERATURE", 80.0);
            queryParameters.addFilteringItem(andFilteringItem1, QueryParameters.FilteringLogic.AND);

            List<Object> valueList2 = new ArrayList<>();
            valueList2.add("AuroraBridge");
            valueList2.add("MagnoliaBridge");
            FilteringItem andFilteringItem2 = new InValueFilteringItem("STATIONNAME", valueList2);
            queryParameters.addFilteringItem(andFilteringItem2, QueryParameters.FilteringLogic.AND);

            FilteringItem orFilteringItem1 = new LessThanFilteringItem("AirTemperature", 66.65);
            queryParameters.addFilteringItem(orFilteringItem1, QueryParameters.FilteringLogic.OR);

            queryParameters.setDistinctMode(true);
            queryParameters.addSortingAttribute("AirTemperature", QueryParameters.SortingLogic.ASC);

            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            //select distinct * from RoadWeatherRecords where ((AIRTEMPERATURE = 66.65 and ROADSURFACETEMPERATURE > 80.0 and STATIONNAME in ('AuroraBridge', 'MagnoliaBridge')) or AirTemperature < 66.65) order by AirTemperature asc limit 50000 offset 0
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),13198);
            for (Map<String, Object> currentRecord : resultRecordsList) {
                String AirTemperature = currentRecord.get("STATIONNAME".toUpperCase()).toString();
                double airTEMPERATUREValue = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                //System.out.println(airTEMPERATUREValue);
                //Assert.assertTrue(airTEMPERATUREValue<=66.65);
                if(airTEMPERATUREValue==66.65){
                    double _ROADSURFACETEMPERATUREValue = Double.parseDouble(currentRecord.get("ROADSURFACETEMPERATURE".toUpperCase()).toString());
                    String _STATIONNAME = currentRecord.get("STATIONNAME".toUpperCase()).toString();
                    //System.out.println(_STATIONNAME);
                    //System.out.println(airTEMPERATUREValue);
                    Assert.assertTrue((_STATIONNAME.equals("AuroraBridge") | _STATIONNAME.equals("MagnoliaBridge")));
                    Assert.assertTrue(_ROADSURFACETEMPERATUREValue >80.0);
                }else{
                    Assert.assertTrue(airTEMPERATUREValue <66.65);
                }
            }

            for(int i=0;i<resultRecordsList.size()-1;i++){
                Map<String, Object> currentRecord = resultRecordsList.get(i);
                Map<String, Object> nextRecord = resultRecordsList.get(i+1);
                double airTEMPERATUREValue_current = Double.parseDouble(currentRecord.get("airTEMPERATURE".toUpperCase()).toString());
                double airTEMPERATUREValue_next = Double.parseDouble(nextRecord.get("airTEMPERATURE".toUpperCase()).toString());
                Assert.assertTrue(airTEMPERATUREValue_current <=airTEMPERATUREValue_next);
            }

            queryParameters.setResultNumber(350);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),350);

            queryParameters.setResultNumber(50000);
            queryParameters.setStartPage(40);
            queryParameters.setEndPage(60);
            queryParameters.setPageSize(10);
            dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            resultRecordsList = dataSliceQueryResult2.getResultRecords();
            Assert.assertEquals(resultRecordsList.size(),200);

            dataService.eraseDataSlice(RoadWeatherInformationStationsRecordsDataSlice);
        }catch (ComputeGridException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
