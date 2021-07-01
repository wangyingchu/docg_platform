package providerClientTest;

import com.viewfunction.docg.analysisProvider.client.AnalysisProviderClient;
import com.viewfunction.docg.analysisProvider.client.exception.AnalysisEngineRuntimeException;
import com.viewfunction.docg.analysisProvider.client.exception.ProviderClientInitException;
import com.viewfunction.docg.analysisProvider.feature.communication.AnalyseResponseCallback;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyzeTreesCrownAreaInSection;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.SpatialPropertiesAggregateStatisticRequest;
import org.apache.spark.sql.Row;

import java.util.Date;

public class SpatialPropertiesStatisticTest01 {

    public static void main(String[] args){
        AnalysisProviderClient analysisProviderClient = new AnalysisProviderClient("127.0.0.1",9999);
        analysisProviderClient.openSession();

        SpatialPropertiesAggregateStatisticRequest spatialPropertiesAggregateStatisticRequest = new SpatialPropertiesAggregateStatisticRequest();
        spatialPropertiesAggregateStatisticRequest.setSubjectConception("CommunityReportingArea");
        spatialPropertiesAggregateStatisticRequest.setObjectConception("TreeCanopy");
        spatialPropertiesAggregateStatisticRequest.setPredicateType(SpatialPropertiesAggregateStatisticRequest.PredicateType.Contains);

        spatialPropertiesAggregateStatisticRequest.setSubjectIdentityProperty("OBJECTID");
        spatialPropertiesAggregateStatisticRequest.setSubjectCalculationProperty("SHAPE_AREA");
        spatialPropertiesAggregateStatisticRequest.setObjectCalculationProperty("SHAPE_AREA");
        spatialPropertiesAggregateStatisticRequest.setObjectAggregationType(SpatialPropertiesAggregateStatisticRequest.ObjectAggregationType.MAX);

        spatialPropertiesAggregateStatisticRequest.setSubjectReturnProperties(new String[]{"GEN_ALIAS","NEIGHDIST","DETL_NAMES"});
        spatialPropertiesAggregateStatisticRequest.setCalculationOperator(SpatialPropertiesAggregateStatisticRequest.CalculationOperator.Divide);
        spatialPropertiesAggregateStatisticRequest.setStatisticResultProperty("CalculationResult");

        try {
            System.out.println(new Date());
            AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                @Override
                public void onResponseReceived(Object analyseResponseObject) {
                    System.out.println(analyseResponseObject);
                    System.out.println(new Date());

                    try {
                        analysisProviderClient.closeSession();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccessResponseReceived(AnalyseResponse analyseResponse) {
                    System.out.println(analyseResponse);
                    System.out.println(analyseResponse.getResponseUUID());
                    System.out.println(analyseResponse.getResponseDateTime());
                    System.out.println(analyseResponse.getRequestUUID());
                    System.out.println(analyseResponse.getResponseData());

                    Row[] resultRow = (Row[])analyseResponse.getResponseData();
                    String[] fieldNames = resultRow[0].schema().fieldNames();
                    System.out.println(resultRow.length);

                    for(Row currentRow:resultRow){
                        for(String currentField:fieldNames){
                            //System.out.print(currentField+" - "+currentRow.get(currentRow.fieldIndex(currentField)).getClass()+" ");
                            System.out.print(currentField+" - "+currentRow.get(currentRow.fieldIndex(currentField))+" ");
                        }
                        System.out.println("");
                        //System.out.println(currentRow.json());
                    }
                    System.out.println(new Date());
                    try {
                        analysisProviderClient.closeSession();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailureResponseReceived(Throwable throwable) {
                    System.out.println(throwable);
                    System.out.println(new Date());
                    try {
                        analysisProviderClient.closeSession();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }
            };

            analysisProviderClient.sendAnalyseRequest(spatialPropertiesAggregateStatisticRequest,analyseResponseCallback,600);

        } catch (AnalysisEngineRuntimeException | ProviderClientInitException e) {
            e.printStackTrace();
        }

        /*
        try {
            Thread.sleep(10000);
            analysisProviderClient.closeSession();
        } catch (ProviderClientInitException | InterruptedException e) {
            e.printStackTrace();
        }
        */
    }
}
