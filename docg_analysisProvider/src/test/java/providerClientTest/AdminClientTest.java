package providerClientTest;

import com.viewfunction.docg.analysisProvider.client.AnalysisProviderAdminClient;
import com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload.FunctionalFeatureInfo;

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
            Thread.sleep(5000);
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
    }
}
