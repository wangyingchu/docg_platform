package com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class FeatureRunningInfo {

    private String featureRunningStatus;
    private String requestUUID;
    private Timestamp requestTime;
    private String responseUUID;
    private String featureName;
    private String responseDataForm;
    private Timestamp runningStartTime;
    private Timestamp runningFinishTime;

    public FeatureRunningInfo(String featureRunningStatus, String requestUUID, Timestamp requestTime,
                              String responseUUID, String featureName, String responseDataForm,
                              Timestamp runningStartTime, Timestamp runningFinishTime) {
        this.featureRunningStatus = featureRunningStatus;
        this.requestUUID = requestUUID;
        this.requestTime = requestTime;
        this.responseUUID = responseUUID;
        this.featureName = featureName;
        this.responseDataForm = responseDataForm;
        this.runningStartTime = runningStartTime;
        this.runningFinishTime = runningFinishTime;
    }

    public String getFeatureRunningStatus() {
        return featureRunningStatus;
    }

    public String getRequestUUID() {
        return requestUUID;
    }

    public LocalDateTime getRequestTime() {
        return requestTime != null ? requestTime.toLocalDateTime() : null;
    }

    public String getResponseUUID() {
        return responseUUID;
    }

    public String getFeatureName() {
        return featureName;
    }

    public String getResponseDataForm() {
        return responseDataForm;
    }

    public LocalDateTime getRunningStartTime() {
        return runningStartTime != null ? runningStartTime.toLocalDateTime() : null;
    }

    public LocalDateTime getRunningFinishTime() {
        return runningFinishTime != null ? runningFinishTime.toLocalDateTime() : null;
    }
}
