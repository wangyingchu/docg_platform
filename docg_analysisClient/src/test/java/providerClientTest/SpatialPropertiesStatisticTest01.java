package providerClientTest;

import com.viewfunction.docg.analysisProvider.client.AnalysisProviderClient;
import com.viewfunction.docg.analysisProvider.client.exception.AnalysisEngineRuntimeException;
import com.viewfunction.docg.analysisProvider.client.exception.ProviderClientInitException;
import com.viewfunction.docg.analysisProvider.feature.communication.AnalyseResponseCallback;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.SpatialCommonConfig;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.SpatialPropertiesAggregateStatisticRequest;
import com.viewfunction.docg.analysisProvider.fundamental.coreRealm.ConceptionEntitiesOperationConfig;
import com.viewfunction.docg.analysisProvider.fundamental.coreRealm.CoreRealmOperationConstant;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SpatialPropertiesStatisticTest01 {

    public static void main(String[] args){
        AnalysisProviderClient analysisProviderClient = new AnalysisProviderClient("127.0.0.1",9999);
        analysisProviderClient.openSession();

        SpatialPropertiesAggregateStatisticRequest spatialPropertiesAggregateStatisticRequest = new SpatialPropertiesAggregateStatisticRequest();
        spatialPropertiesAggregateStatisticRequest.setGeospatialScaleLevel(SpatialCommonConfig.GeospatialScaleLevel.GlobalLevel);
        spatialPropertiesAggregateStatisticRequest.setSubjectConception("GreenLand");
        spatialPropertiesAggregateStatisticRequest.setObjectConception("AddressAndPlaceName");
        spatialPropertiesAggregateStatisticRequest.setPredicateType(SpatialCommonConfig.PredicateType.Contains);

        spatialPropertiesAggregateStatisticRequest.setSubjectIdentityProperty("DOCG_REALMGLOBALUID");
        //spatialPropertiesAggregateStatisticRequest.setSubjectCalculationProperty("NAME");
        spatialPropertiesAggregateStatisticRequest.setObjectCalculationProperty("ID");
        spatialPropertiesAggregateStatisticRequest.setObjectAggregationType(SpatialPropertiesAggregateStatisticRequest.ObjectAggregationType.COUNT);

        spatialPropertiesAggregateStatisticRequest.setSubjectReturnProperties(new String[]{"NAME","CODE"});
        spatialPropertiesAggregateStatisticRequest.setCalculationOperator(SpatialPropertiesAggregateStatisticRequest.CalculationOperator.Add);
        spatialPropertiesAggregateStatisticRequest.setStatisticResultProperty("CalculationResult");

        spatialPropertiesAggregateStatisticRequest.setResponseDataForm(AnalyseRequest.ResponseDataForm.STREAM_BACK);
        //spatialPropertiesAggregateStatisticRequest.setResponseDataForm(AnalyseRequest.ResponseDataForm.DATA_SLICE);
        //spatialPropertiesAggregateStatisticRequest.setResponseDataForm(AnalyseRequest.ResponseDataForm.CONCEPTION_KIND);
        HashMap<String,Object> map = new HashMap<>();
        //map.put(CoreRealmOperationConstant.ConceptionKindName,"TestConceptionKindName");
        //map.put(CoreRealmOperationConstant.ConceptionEntitiesInsertMode(),ConceptionEntitiesInsertMode.CLEAN_INSERT());
        map.put(CoreRealmOperationConstant.ConceptionEntitiesInsertMode, ConceptionEntitiesOperationConfig.ConceptionEntitiesInsertMode.APPEND);
        //map.put(CoreRealmOperationConstant.ConceptionEntitiesInsertMode(), ConceptionEntitiesInsertMode.OVERWRITE());
        //map.put(CoreRealmOperationConstant.ConceptionEntityPKAttributeName,"DOCG_RealmGlobalUID");

        spatialPropertiesAggregateStatisticRequest.setRequestParameters(map);
        try {
            System.out.println(new Date());
            AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                @Override
                public void onResponseReceived(Object analyseResponseObject) {}

                @Override
                public void onSuccessResponseReceived(AnalyseResponse analyseResponse) {
                    System.out.println(analyseResponse);
                    System.out.println("ResponseDataForm: "+analyseResponse.getResponseDataForm());
                    System.out.println("RequestUUID: "+analyseResponse.getRequestUUID());
                    System.out.println("ResponseUUID: "+analyseResponse.getResponseUUID());
                    System.out.println(analyseResponse.getResponseDateTime());
                    System.out.println(analyseResponse.getResponseCode());
                    System.out.println(analyseResponse.getResponseSummary());

                    System.out.println(analyseResponse.getResponseData());
                    ResponseDataset responseDataset = (ResponseDataset)analyseResponse.getResponseData();
                    ArrayList<HashMap<String,Object>> datalist = responseDataset.getDataList();

                    for(HashMap<String,Object> currentDataRow : datalist){
                        System.out.println(currentDataRow);
                    }

                    System.out.println(datalist.size());
                    System.out.println( responseDataset.getPropertiesInfo());

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
