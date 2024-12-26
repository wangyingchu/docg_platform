package com.viewfunction.docg.analysisProvider.feature.communication;

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseResponse;

public interface AnalyseResponseCallback {

    public void onResponseReceived(Object analyseResponseObject);

    public void onSuccessResponseReceived(AnalyseResponse analyseResponse);

    public void onFailureResponseReceived(Throwable throwable);
}
