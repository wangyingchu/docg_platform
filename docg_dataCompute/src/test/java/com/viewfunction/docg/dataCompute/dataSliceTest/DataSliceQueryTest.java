package com.viewfunction.docg.dataCompute.dataSliceTest;


import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataServiceInvoker;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.QueryParameters;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.result.DataSliceQueryResult;

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
            queryParameters.setDefaultFilteringItem(new EqualFilteringItem("AIRTEMPERATURE",66.65));
            DataSliceQueryResult dataSliceQueryResult2 = targetDataSlice.queryDataRecords(queryParameters);
            System.out.println(dataSliceQueryResult2.getResultRecords().size());

        }
    }
}
