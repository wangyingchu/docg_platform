package com.viewfunction.docg.dataCompute.dataSliceTest;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.QueryParameters;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.filteringItem.*;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.result.DataSliceQueryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataSliceQueryTest {

    public static void main(String args[]) throws Exception {

        try(DataServiceInvoker dataServiceInvoker = DataServiceInvoker.getInvokerInstance()){
            DataSlice targetDataSlice = dataServiceInvoker.getDataSlice("RoadWeatherRecords");
            DataSliceMetaInfo dataSliceMetaInfo = targetDataSlice.getDataSliceMetaInfo();
            System.out.println(dataSliceMetaInfo.getPrimaryDataCount());

            DataSliceQueryResult dataSliceQueryResult = targetDataSlice.queryDataRecords("SELECT * FROM RoadWeatherRecords where airTEMPERATURE > 70.0 and airTEMPERATURE <74 LIMIT 100");
            System.out.println(dataSliceQueryResult.getQueryLogic());
            System.out.println(dataSliceQueryResult.getStartTime());
            System.out.println(dataSliceQueryResult.getFinishTime());
            System.out.println(dataSliceQueryResult.getOperationSummary());
            List<Map<String,Object>> recordResult = dataSliceQueryResult.getResultRecords();
            System.out.println(recordResult.size());
            for(Map<String,Object> currentRecord: recordResult){
                System.out.println(currentRecord);
            }

            QueryParameters queryParameters = new QueryParameters();

            List<Object> valueList = new ArrayList<>();
            valueList.add(Double.valueOf("66.65"));
            valueList.add(Double.valueOf("65.77"));

            FilteringItem filteringItem = new EqualFilteringItem("AIRTEMPERATURE",66.65);
            //FilteringItem filteringItem = new BetweenFilteringItem("AirTemperature",Double.valueOf("65.66"),Double.valueOf("65.77"));
            //FilteringItem filteringItem = new GreaterThanFilteringItem("AirTemperature",66.65);
            //FilteringItem filteringItem = new GreaterThanEqualFilteringItem("AirTemperature",66.65);
            //FilteringItem filteringItem = new InValueFilteringItem("AirTemperature",valueList);
            //FilteringItem filteringItem = new LessThanEqualFilteringItem("AirTemperature",66.65);
            //FilteringItem filteringItem = new LessThanFilteringItem("AirTemperature",66.65);
            //FilteringItem filteringItem = new NotEqualFilteringItem("AIRTEMPERATURE",66.65);
            //FilteringItem filteringItem = new NullValueFilteringItem("AirTemperature");

            //FilteringItem filteringItem = new SimilarFilteringItem("STATIONNAME","Bridge", SimilarFilteringItem.MatchingType.EndWith);
            //FilteringItem filteringItem = new SimilarFilteringItem("STATIONNAME","Magno", SimilarFilteringItem.MatchingType.BeginWith);
            //FilteringItem filteringItem = new SimilarFilteringItem("STATIONNAME","SWMy", SimilarFilteringItem.MatchingType.Contain);

            //filteringItem.reverseCondition();
            queryParameters.setDefaultFilteringItem(filteringItem);

            FilteringItem andFilteringItem1 = new GreaterThanFilteringItem("ROADSURFACETEMPERATURE",80.0);
            queryParameters.addFilteringItem(andFilteringItem1, QueryParameters.FilteringLogic.AND);

            List<Object> valueList2 = new ArrayList<>();
            valueList2.add("AuroraBridge");
            valueList2.add("MagnoliaBridge");
            FilteringItem andFilteringItem2 = new InValueFilteringItem("STATIONNAME",valueList2);
            queryParameters.addFilteringItem(andFilteringItem2, QueryParameters.FilteringLogic.AND);

            FilteringItem orFilteringItem1 = new EqualFilteringItem("REALMGLOBALUID","8150050");
            queryParameters.addFilteringItem(orFilteringItem1, QueryParameters.FilteringLogic.OR);

            FilteringItem orFilteringItem2 = new LessThanEqualFilteringItem("AirTemperature",66.65);
            queryParameters.addFilteringItem(orFilteringItem2, QueryParameters.FilteringLogic.OR);

            //queryParameters.setResultNumber(50000);
            queryParameters.setStartPage(40);
            queryParameters.setEndPage(60);
            queryParameters.setPageSize(10);
            queryParameters.setDistinctMode(true);

            queryParameters.addSortingAttribute("AirTemperature", QueryParameters.SortingLogic.ASC);
            queryParameters.addSortingAttribute("REALMGLOBALUID", QueryParameters.SortingLogic.DESC);

            DataSliceQueryResult dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            System.out.println(dataSliceQueryResult2.getResultRecords().size());

        }
    }
}
