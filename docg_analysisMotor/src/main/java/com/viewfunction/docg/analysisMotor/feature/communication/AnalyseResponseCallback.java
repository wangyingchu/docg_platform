package com.viewfunction.docg.analysisMotor.feature.communication;

import com.viewfunction.docg.analysisMotor.feature.communication.messagePayload.AnalyseResponse;

public interface AnalyseResponseCallback {

    public void onResponseReceived(Object analyseResponseObject);

    public void onSuccessResponseReceived(AnalyseResponse analyseResponse);

    public void onFailureResponseReceived(Throwable throwable);
}
