package providerClientTest;

import com.viewfunction.docg.analysisProvider.client.AnalysisProviderAdminClient;

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


    }
}
