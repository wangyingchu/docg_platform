package engineClientTest;

import com.viewfunction.docg.analysisMotor.client.AnalysisEngineClient;
import com.viewfunction.docg.analysisMotor.client.exception.AnalysisEngineRuntimeException;
import com.viewfunction.docg.analysisMotor.client.exception.EngineClientInitException;
import com.viewfunction.docg.analysisMotor.feature.communication.AnalyseResponseCallback;
import com.viewfunction.docg.analysisMotor.feature.communication.messagePayload.AnalyseResponse;
import com.viewfunction.docg.analysisMotor.feature.communication.messagePayload.AnalyzeTreesCrownAreaInSection;

import java.util.Date;

public class EngineClientTestCase01 {

    public static void main(String[] args) throws EngineClientInitException, AnalysisEngineRuntimeException {
        AnalysisEngineClient analysisEngineClient = new AnalysisEngineClient("127.0.0.1",9999);
        analysisEngineClient.openSession();
        /*
        for(int i=0;i<1000;i++){
            AnalyzeTreesCrownAreaInSection analyzeTreesCrownAreaInSection = new AnalyzeTreesCrownAreaInSection("treeType002",i);
            analysisEngineClient.sendAnalyseRequest(analyzeTreesCrownAreaInSection);
        }
        */
        AnalyzeTreesCrownAreaInSection analyzeTreesCrownAreaInSection = new AnalyzeTreesCrownAreaInSection("treeType002",1941);
        analysisEngineClient.sendAnalyseRequest(analyzeTreesCrownAreaInSection);

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
                    System.out.println(new Date());
                }

                @Override
                public void onFailureResponseReceived(Throwable throwable) {
                    System.out.println(throwable);
                    System.out.println(new Date());
                }
            };
            analysisEngineClient.sendAnalyseRequest(analyzeTreesCrownAreaInSection,analyseResponseCallback,100);
        } catch (AnalysisEngineRuntimeException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        analysisEngineClient.closeSession();
    }
}
