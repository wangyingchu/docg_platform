package providerClientTest;

import com.viewfunction.docg.analysisProvider.client.AnalysisProviderClient;
import com.viewfunction.docg.analysisProvider.client.exception.AnalysisEngineRuntimeException;
import com.viewfunction.docg.analysisProvider.client.exception.ProviderClientInitException;
import com.viewfunction.docg.analysisProvider.feature.communication.AnalyseResponseCallback;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyzeTreesCrownAreaInSection;

import java.util.Date;

import org.apache.spark.sql.Row;

public class AnalysisFeatureTest01 {

    public static void main(String[] args){
        AnalysisProviderClient analysisProviderClient = new AnalysisProviderClient("127.0.0.1",9999);
        analysisProviderClient.openSession();

        AnalyzeTreesCrownAreaInSection analyzeTreesCrownAreaInSection = new AnalyzeTreesCrownAreaInSection("treeType002",1941);
        try {
            System.out.println(new Date());
            AnalyseResponseCallback analyseResponseCallback = new AnalyseResponseCallback() {
                @Override
                public void onResponseReceived(Object analyseResponseObject) {
                    System.out.println(analyseResponseObject);
                    System.out.println(new Date());
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
                            System.out.print(currentField+" - "+currentRow.get(currentRow.fieldIndex(currentField)).getClass()+" ");
                        }
                        System.out.println("");
                        //System.out.println(currentRow.json());
                    }
                    System.out.println(new Date());
                }

                @Override
                public void onFailureResponseReceived(Throwable throwable) {
                    System.out.println(throwable);
                    System.out.println(new Date());
                }
            };

            analysisProviderClient.sendAnalyseRequest(analyzeTreesCrownAreaInSection,analyseResponseCallback,600);

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
