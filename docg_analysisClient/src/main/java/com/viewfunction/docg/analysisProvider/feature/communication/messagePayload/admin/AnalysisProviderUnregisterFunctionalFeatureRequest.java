package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.admin;

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;

public class AnalysisProviderUnregisterFunctionalFeatureRequest extends AnalyseRequest {

    private String functionalFeatureName;

    public String getFunctionalFeatureName() {
        return functionalFeatureName;
    }

    public void setFunctionalFeatureName(String functionalFeatureName) {
        this.functionalFeatureName = functionalFeatureName;
    }
}
