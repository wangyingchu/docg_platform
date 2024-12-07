package providerClientTest;

import com.viewfunction.docg.analysisProvider.client.AnalysisProviderClient;
import com.viewfunction.docg.analysisProvider.client.exception.AnalyseRequestFormatException;
import com.viewfunction.docg.analysisProvider.client.exception.AnalysisEngineRuntimeException;
import com.viewfunction.docg.analysisProvider.client.exception.ProviderClientInitException;
import com.viewfunction.docg.analysisProvider.feature.communication.AnalyseResponseCallback;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.AdministrativeDivisionSpatialCalculateRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.SpatialCommonConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AdministrativeDivisionSpatialAnalysisTest01 {

    public static void main(String[] args) throws AnalyseRequestFormatException {
        AnalysisProviderClient analysisProviderClient = new AnalysisProviderClient("127.0.0.1",9900);
        analysisProviderClient.openSession();

        AdministrativeDivisionSpatialCalculateRequest administrativeDivisionSpatialCalculateRequest = new AdministrativeDivisionSpatialCalculateRequest();

        administrativeDivisionSpatialCalculateRequest.setSubjectConception("Firm");
        administrativeDivisionSpatialCalculateRequest.setSampleValue(0.00011);

        String[] subjectReturnProperties = new String[1];
        subjectReturnProperties[0] = "name";
        administrativeDivisionSpatialCalculateRequest.setSubjectReturnProperties(subjectReturnProperties);

        String[] administrativeDivisionReturnProperties = new String[2];
        administrativeDivisionReturnProperties[0] = "DOCG_GEOSPATIALCODE";
        administrativeDivisionReturnProperties[1] = "DOCG_GEOSPATIALCHINESENAME";
        administrativeDivisionSpatialCalculateRequest.setAdministrativeDivisionReturnProperties(administrativeDivisionReturnProperties);

        administrativeDivisionSpatialCalculateRequest.setPredicateType(SpatialCommonConfig.PredicateType.Within);
        administrativeDivisionSpatialCalculateRequest.setGeospatialScaleGrade(SpatialCommonConfig.GeospatialScaleGrade.Township);
        //administrativeDivisionSpatialCalculateRequest.setGeospatialScaleGrade(SpatialCommonConfig.GeospatialScaleGrade.Province);
        administrativeDivisionSpatialCalculateRequest.setGeospatialScaleLevel(SpatialCommonConfig.GeospatialScaleLevel.GlobalLevel);

        administrativeDivisionSpatialCalculateRequest.setResponseDataForm(AnalyseRequest.ResponseDataForm.DATA_SLICE);
        //administrativeDivisionSpatialCalculateRequest.setResponseDataForm(AnalyseRequest.ResponseDataForm.STREAM_BACK);

        try {
            System.out.println(new Date());
            AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                @Override
                public void onResponseReceived(Object analyseResponseObject) {
                    System.out.println("onResponseReceived");
                }

                @Override
                public void onSuccessResponseReceived(AnalyseResponse analyseResponse) {
                    System.out.println(analyseResponse);
                    System.out.println("ResponseDataForm: "+analyseResponse.getResponseDataForm());
                    System.out.println("RequestUUID: "+analyseResponse.getRequestUUID());
                    System.out.println("ResponseUUID: "+analyseResponse.getResponseUUID());
                    System.out.println(analyseResponse.getResponseDateTime());

                    System.out.println(analyseResponse.getResponseData());
                    ResponseDataset responseDataset = (ResponseDataset)analyseResponse.getResponseData();
                    ArrayList<HashMap<String,Object>> datalist = responseDataset.getDataList();

                    for(HashMap<String,Object> currentDataRow : datalist){
                        System.out.println(currentDataRow);
                    }

                    System.out.println(datalist.size());
                    System.out.println(responseDataset.getPropertiesInfo());

                    try {
                        analysisProviderClient.closeSession();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailureResponseReceived(Throwable throwable) {
                    System.out.println("onFailureResponseReceived");

                    //System.out.println(throwable);
                    //System.out.println(new Date());
                    try {
                        analysisProviderClient.closeSession();
                    } catch (ProviderClientInitException e) {
                        e.printStackTrace();
                    }
                }
            };
            System.out.println("BEFORE SEND MESSAGE");
            analysisProviderClient.sendAnalyseRequest(administrativeDivisionSpatialCalculateRequest,analyseResponseCallback,2000);
            System.out.println("AFTER SEND MESSAGE");
            //analysisProviderClient.closeSession();
        } catch (AnalysisEngineRuntimeException | ProviderClientInitException e) {
            e.printStackTrace();
        }
    }
}
