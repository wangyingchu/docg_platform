package providerClientTest;

import com.viewfunction.docg.analysisProvider.client.AnalysisProviderAdminClient;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.FeatureRunningInfo;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.FunctionalFeatureInfo;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.ProviderRunningInfo;

import java.util.List;

public class AdminClientTest {

    public static void main(String[] args) {
        AnalysisProviderAdminClient analysisProviderAdminClient = new AnalysisProviderAdminClient("127.0.0.1",9999);

        AnalysisProviderAdminClient.PingAnalysisProviderCallback pingAnalysisProviderCallback = new AnalysisProviderAdminClient.PingAnalysisProviderCallback() {
            @Override
            public void onPingSuccess() {
                System.out.println("Ping Success");
            }

            @Override
            public void onPingFail() {
                System.out.println("Ping Fail");
            }
        };
        analysisProviderAdminClient.pingAnalysisProvider(pingAnalysisProviderCallback,3);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AnalysisProviderAdminClient.ListFunctionalFeaturesCallback listFunctionalFeaturesCallback = new AnalysisProviderAdminClient.ListFunctionalFeaturesCallback() {
            @Override
            public void onExecutionSuccess(List<FunctionalFeatureInfo> functionalFeatureInfoList) {
                if(functionalFeatureInfoList != null){
                    for(FunctionalFeatureInfo functionalFeatureInfo:functionalFeatureInfoList){
                        System.out.println(functionalFeatureInfo.getFunctionalFeatureName());
                        System.out.println(functionalFeatureInfo.getFunctionalFeatureDescription());
                        System.out.println("-------------------------");
                    }
                }
            }

            @Override
            public void onExecutionFail() {

            }
        };
        analysisProviderAdminClient.listFunctionalFeatures(listFunctionalFeaturesCallback,10);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AnalysisProviderAdminClient.ListFeatureRunningStatusCallback listFeatureRunningStatusCallback = new AnalysisProviderAdminClient.ListFeatureRunningStatusCallback() {
            @Override
            public void onExecutionSuccess(List<FeatureRunningInfo> featureRunningInfo) {
                if(featureRunningInfo != null){
                    for(FeatureRunningInfo featureRunningInfoItem:featureRunningInfo){
                        System.out.println(featureRunningInfoItem.getFeatureName());
                        System.out.println(featureRunningInfoItem.getFeatureRunningStatus());
                        System.out.println(featureRunningInfoItem.getRequestUUID());
                        System.out.println(featureRunningInfoItem.getResponseUUID());
                        System.out.println(featureRunningInfoItem.getResponseDataForm());
                        System.out.println(featureRunningInfoItem.getRequestTime());
                        System.out.println(featureRunningInfoItem.getRunningStartTime());
                        System.out.println(featureRunningInfoItem.getRunningFinishTime());
                        System.out.println("-------------------------");
                    }
                }
            }

            @Override
            public void onExecutionFail() {

            }
        };
        analysisProviderAdminClient.listFeatureRunningStatus(listFeatureRunningStatusCallback,3);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AnalysisProviderAdminClient.ListProviderRunningStatusCallback listProviderRunningStatusCallback = new AnalysisProviderAdminClient.ListProviderRunningStatusCallback() {
            @Override
            public void onExecutionSuccess(List<ProviderRunningInfo> providerRunningInfoList) {
                if(providerRunningInfoList != null){
                    for(ProviderRunningInfo providerRunningInfoItem:providerRunningInfoList){
                        System.out.println(providerRunningInfoItem.getProviderRunningUUID());
                        System.out.println(providerRunningInfoItem.getProviderStartTime());
                        System.out.println(providerRunningInfoItem.getProviderStopTime());
                        System.out.println("-------------------------");
                    }

                }
            }

            @Override
            public void onExecutionFail() {

            }
        };
        analysisProviderAdminClient.listProviderRunningStatus(listProviderRunningStatusCallback,10);
    }
}
