package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.admin;

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;

public class AnalysisProviderRegisterFunctionalFeatureRequest extends AnalyseRequest {

    private String functionalFeatureName;
    private String functionalFeatureDescription;

    public String getFunctionalFeatureName() {
        return functionalFeatureName;
    }

    public void setFunctionalFeatureName(String functionalFeatureName) {
        this.functionalFeatureName = functionalFeatureName;
    }

    public String getFunctionalFeatureDescription() {
        return functionalFeatureDescription;
    }

    public void setFunctionalFeatureDescription(String functionalFeatureDescription) {
        this.functionalFeatureDescription = functionalFeatureDescription;
    }
}
