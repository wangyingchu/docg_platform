package providerClientTest;

import com.viewfunction.docg.analysisProvider.client.AnalysisProviderClient;
import com.viewfunction.docg.analysisProvider.client.exception.AnalyseRequestFormatException;
import com.viewfunction.docg.analysisProvider.client.exception.AnalysisEngineRuntimeException;
import com.viewfunction.docg.analysisProvider.client.exception.ProviderClientInitException;
import com.viewfunction.docg.analysisProvider.feature.communication.AnalyseResponseCallback;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.AdministrativeDivisionSpatialCalculateRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.SpatialCommonConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdministrativeDivisionSpatialAnalysisTest01 {

    public static void main(String[] args) throws AnalyseRequestFormatException {
        AnalysisProviderClient analysisProviderClient = new AnalysisProviderClient("127.0.0.1",9999);
        analysisProviderClient.openSession();

        AdministrativeDivisionSpatialCalculateRequest administrativeDivisionSpatialCalculateRequest = new AdministrativeDivisionSpatialCalculateRequest();

        administrativeDivisionSpatialCalculateRequest.setSubjectConception("firmData");
        //administrativeDivisionSpatialCalculateRequest.setSampleValue(0.13);
        administrativeDivisionSpatialCalculateRequest.setSampleValue(0.0013);

        String[] subjectReturnProperties = new String[1];
        subjectReturnProperties[0] = "name";
        administrativeDivisionSpatialCalculateRequest.setSubjectReturnProperties(subjectReturnProperties);

        String[] administrativeDivisionReturnProperties = new String[2];
        administrativeDivisionReturnProperties[0] = "DOCG_GEOSPATIALCODE";
        administrativeDivisionReturnProperties[1] = "DOCG_GEOSPATIALCHINESENAME";
        administrativeDivisionSpatialCalculateRequest.setAdministrativeDivisionReturnProperties(administrativeDivisionReturnProperties);

        administrativeDivisionSpatialCalculateRequest.setPredicateType(SpatialCommonConfig.PredicateType.Within);
        administrativeDivisionSpatialCalculateRequest.setGeospatialScaleGrade(SpatialCommonConfig.GeospatialScaleGrade.County);
        administrativeDivisionSpatialCalculateRequest.setGeospatialScaleLevel(SpatialCommonConfig.GeospatialScaleLevel.CountryLevel);

        try {
            System.out.println(new Date());
            AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                @Override
                public void onResponseReceived(Object analyseResponseObject) {

                    System.out.println(new Date());
                    System.out.println(analyseResponseObject);
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
                    System.out.println(analyseResponse.getResponseData());
                    System.out.println("===================");
                    System.out.println(analyseResponse.getRequestUUID());
                    System.out.println(analyseResponse.getResponseDataForm());

                    ResponseDataset responseDataset = (ResponseDataset)analyseResponse.getResponseData();
                    Map<String,String> propertiesInfoMap =  responseDataset.getPropertiesInfo();
                    ArrayList<HashMap<String,Object>> datalist = responseDataset.getDataList();

                    System.out.println(datalist.size());

                    System.out.println(datalist.get(1000));
                    for(HashMap<String,Object> currentDataRow : datalist){
                        //System.out.println(currentDataRow);
                    }
                    System.out.println();
                    System.out.println(propertiesInfoMap);

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

            analysisProviderClient.sendAnalyseRequest(administrativeDivisionSpatialCalculateRequest,analyseResponseCallback,800);

        } catch (AnalysisEngineRuntimeException | ProviderClientInitException e) {
            e.printStackTrace();
        }
    }
}
