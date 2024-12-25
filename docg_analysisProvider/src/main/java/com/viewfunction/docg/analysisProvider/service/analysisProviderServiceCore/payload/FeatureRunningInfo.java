package com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload;

import java.time.LocalDateTime;

public class FeatureRunningInfo {

    private String featureRunningStatus;
    private String requestUUID;
    private LocalDateTime requestTime;
    private String responseUUID;
    private String featureName;
    private String responseDataForm;
    private LocalDateTime runningStartTime;
    private LocalDateTime runningFinishTime;

    public FeatureRunningInfo(String featureRunningStatus, String requestUUID, LocalDateTime requestTime,
                              String responseUUID, String featureName, String responseDataForm,
                              LocalDateTime runningStartTime, LocalDateTime runningFinishTime) {
        this.featureRunningStatus = featureRunningStatus;
        this.requestUUID = requestUUID;
        this.requestTime = requestTime;
        this.responseUUID = responseUUID;
        this.featureName = featureName;
        this.responseDataForm = responseDataForm;
        this.runningStartTime = runningStartTime;
        this.runningFinishTime = runningFinishTime;
    }

    public FeatureRunningInfo(){}

    public String getFeatureRunningStatus() {
        return featureRunningStatus;
    }

    public void setFeatureRunningStatus(String featureRunningStatus) {
        this.featureRunningStatus = featureRunningStatus;
    }

    public String getRequestUUID() {
        return requestUUID;
    }

    public void setRequestUUID(String requestUUID) {
        this.requestUUID = requestUUID;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public String getResponseUUID() {
        return responseUUID;
    }

    public void setResponseUUID(String responseUUID) {
        this.responseUUID = responseUUID;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getResponseDataForm() {
        return responseDataForm;
    }

    public void setResponseDataForm(String responseDataForm) {
        this.responseDataForm = responseDataForm;
    }

    public LocalDateTime getRunningStartTime() {
        return runningStartTime;
    }

    public void setRunningStartTime(LocalDateTime runningStartTime) {
        this.runningStartTime = runningStartTime;
    }

    public LocalDateTime getRunningFinishTime() {
        return runningFinishTime;
    }

    public void setRunningFinishTime(LocalDateTime runningFinishTime) {
        this.runningFinishTime = runningFinishTime;
    }
}
