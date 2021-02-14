package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlicePropertyType;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.CoreRealmOperationUtil;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreRealmConceptionLoadTest {

    private static final String RoadWeatherInformationStationsRecordsConceptionType = "RoadWeatherRecords";
    private static final String StationName = "stationName";
    private static final String StationLocation = "stationLocation";
    private static final String RecordDateTime = "dateTime";
    private static final String RecordId = "recordId";
    private static final String RoadSurfaceTemperature = "roadSurfaceTemperature";
    private static final String AirTemperature = "airTemperature";

    public static void main(String[] args){
        try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance()){
            DataSlice targetDataSlice = dataServiceInvoker.getDataSlice(RoadWeatherInformationStationsRecordsConceptionType);

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

                dataServiceInvoker.createPerUnitDataSlice(RoadWeatherInformationStationsRecordsConceptionType,"defaultSliceGroup",dataSlicePropertyMap,pkList);
            }
        } catch (ComputeGridNotActiveException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> conceptionKindPropertiesList = new ArrayList<>();
        conceptionKindPropertiesList.add(StationName);
        conceptionKindPropertiesList.add(StationLocation);
        conceptionKindPropertiesList.add(RecordDateTime);
        conceptionKindPropertiesList.add(RecordId);
        conceptionKindPropertiesList.add(RoadSurfaceTemperature);
        conceptionKindPropertiesList.add(AirTemperature);

        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(100000000);

        CoreRealmOperationUtil.loadConceptionKindEntitiesToDataSlice(RoadWeatherInformationStationsRecordsConceptionType,conceptionKindPropertiesList,queryParameters,RoadWeatherInformationStationsRecordsConceptionType,true,10);
    }
}
