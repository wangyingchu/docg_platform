package com.viewfunction.docg.testcase.dataCompute.termTest;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.ComputeGrid;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlice;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.term.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.common.CoreRealmOperationUtil;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.factory.ComputeGridTermFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

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
                dataSlicePropertyMap.put(RecordDateTime,DataSlicePropertyType.DATE);
                dataSlicePropertyMap.put(RecordId,DataSlicePropertyType.STRING);
                dataSlicePropertyMap.put(RoadSurfaceTemperature,DataSlicePropertyType.DOUBLE);
                dataSlicePropertyMap.put(AirTemperature,DataSlicePropertyType.DOUBLE);
                dataSlicePropertyMap.put(CoreRealmOperationUtil.RealmGlobalUID,DataSlicePropertyType.STRING);

                List<String> pkList = new ArrayList<>();
                pkList.add(CoreRealmOperationUtil.RealmGlobalUID);

                dataService.createGridDataSlice(RoadWeatherInformationStationsRecordsDataSlice,"defaultSliceGroup",dataSlicePropertyMap,pkList);
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
