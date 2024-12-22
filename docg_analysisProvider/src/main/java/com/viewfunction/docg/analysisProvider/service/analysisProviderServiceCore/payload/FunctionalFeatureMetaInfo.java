package com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload;

public class FunctionalFeatureMetaInfo {

    private String functionalFeatureName;
    private String functionalFeatureDescription;

    public FunctionalFeatureMetaInfo(String functionalFeatureName, String functionalFeatureDescription) {
        this.setFunctionalFeatureName(functionalFeatureName);
        this.setFunctionalFeatureDescription(functionalFeatureDescription);
    }

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
