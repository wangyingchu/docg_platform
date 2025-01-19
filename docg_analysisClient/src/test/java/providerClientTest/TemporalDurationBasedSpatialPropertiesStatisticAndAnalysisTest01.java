package providerClientTest;

import com.viewfunction.docg.analysisProvider.client.AnalysisProviderClient;
import com.viewfunction.docg.analysisProvider.client.exception.AnalysisEngineRuntimeException;
import com.viewfunction.docg.analysisProvider.client.exception.ProviderClientInitException;
import com.viewfunction.docg.analysisProvider.feature.communication.AnalyseResponseCallback;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.ResponseDataset;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.SpatialCommonConfig;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis.TemporalDurationBasedSpatialPropertiesStatisticRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TemporalDurationBasedSpatialPropertiesStatisticAndAnalysisTest01 {

    public static void main(String[] args){
        TemporalDurationBasedSpatialPropertiesStatisticRequest temporalDurationBasedSpatialPropertiesStatisticRequest =
                new TemporalDurationBasedSpatialPropertiesStatisticRequest();

        temporalDurationBasedSpatialPropertiesStatisticRequest.setSubjectConception("MeshHexagon");
        temporalDurationBasedSpatialPropertiesStatisticRequest.setSubjectGroup("DEFAULTSLICEGROUP");
        temporalDurationBasedSpatialPropertiesStatisticRequest.setObjectConception("LandingPAXTrafficFlow119");
        temporalDurationBasedSpatialPropertiesStatisticRequest.setObjectGroup("DEFAULTSLICEGROUP");

        temporalDurationBasedSpatialPropertiesStatisticRequest.setPredicateType(SpatialCommonConfig.PredicateType.Contains);
        temporalDurationBasedSpatialPropertiesStatisticRequest.setGeospatialScaleLevel(SpatialCommonConfig.GeospatialScaleLevel.GlobalLevel);
        temporalDurationBasedSpatialPropertiesStatisticRequest.setObjectAggregationType(TemporalDurationBasedSpatialPropertiesStatisticRequest.ObjectAggregationType.COUNT);

        temporalDurationBasedSpatialPropertiesStatisticRequest.setSubjectIdentityProperty("BIAD_ALG_CREATEMESH_ATTR_MESH_ID");
        temporalDurationBasedSpatialPropertiesStatisticRequest.setSubjectReturnProperties(new String[]{"BIAD_ALG_CREATEMESH_ATTR_MESH_ID"});

        temporalDurationBasedSpatialPropertiesStatisticRequest.setObjectStatisticProperty("BIAD_ALG_LANDINGPAX_ATTR_POINTID");
        temporalDurationBasedSpatialPropertiesStatisticRequest.setObjectTemporalProperty("BIAD_ALG_LANDINGPAX_ATTR_DATETIME");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startLocalDateTime = getLocalDateTime(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 00, 00, 00);
        LocalDateTime endLocalDateTime = getLocalDateTime(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 00, 10, 00);

        ZonedDateTime starZonedDateTime = startLocalDateTime.atZone(ZoneId.systemDefault());
        Instant starInstant = starZonedDateTime.toInstant();
        ZonedDateTime endZonedDateTime = endLocalDateTime.atZone(ZoneId.systemDefault());
        Instant endInstant = endZonedDateTime.toInstant();

        temporalDurationBasedSpatialPropertiesStatisticRequest.setStatisticStartTime(starInstant.toEpochMilli());
        temporalDurationBasedSpatialPropertiesStatisticRequest.setStatisticEndTime(endInstant.toEpochMilli());
        temporalDurationBasedSpatialPropertiesStatisticRequest.setTemporalDurationType(ChronoUnit.SECONDS);
        temporalDurationBasedSpatialPropertiesStatisticRequest.setDurationCount(10);
        temporalDurationBasedSpatialPropertiesStatisticRequest.setStatisticResultTemporalProperty("timeWindowProperty");

        temporalDurationBasedSpatialPropertiesStatisticRequest.setResponseDataForm(AnalyseRequest.ResponseDataForm.STREAM_BACK);

        AnalysisProviderClient analysisProviderClient = new AnalysisProviderClient("127.0.0.1",9999);
        analysisProviderClient.openSession();

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

            analysisProviderClient.sendAnalyseRequest(temporalDurationBasedSpatialPropertiesStatisticRequest,analyseResponseCallback,600);
        } catch (AnalysisEngineRuntimeException | ProviderClientInitException e) {
            e.printStackTrace();
        }
    }

    private static LocalDateTime getLocalDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDateTime specificDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        return specificDateTime;
    }
}
