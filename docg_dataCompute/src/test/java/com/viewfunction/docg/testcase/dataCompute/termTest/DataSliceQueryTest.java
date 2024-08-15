package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlice;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                //dataSlicePropertyMap.put(RecordDateTime,DataSlicePropertyType.DATE);
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
                        //newEntityValueMap.put(RecordDateTime,format.parse(recordDateTime));
                        newEntityValueMap.put(RecordId,recordId);
                        newEntityValueMap.put(RoadSurfaceTemperature,Double.valueOf(roadSurfaceTemperature));
                        newEntityValueMap.put(AirTemperature,Double.valueOf(airTemperature));


                        System.out.println(format.parse(recordDateTime));


                        boolean addResul = targetDataSlice.addDataRecord(newEntityValueMap);
                        System.out.println(addResul);
                    }

                    lineCount++;
                }
                reader.close();



                System.out.println(targetDataSlice.getDataSliceMetaInfo().getTotalDataCount());



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
            dataService.eraseDataSlice(RoadWeatherInformationStationsRecordsDataSlice);
        }catch (ComputeGridException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
