package com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload;

import java.time.LocalDateTime;

public class ProviderRunningInfo {

    private LocalDateTime providerStartTime;
    private LocalDateTime providerStopTime;
    private String providerRunningUUID;

    public ProviderRunningInfo(LocalDateTime providerStartTime, LocalDateTime providerStopTime, String providerRunningUUID) {
        this.providerStartTime = providerStartTime;
        this.providerStopTime = providerStopTime;
        this.providerRunningUUID = providerRunningUUID;
    }

    public LocalDateTime getProviderStartTime() {
        return providerStartTime;
    }

    public void setProviderStartTime(LocalDateTime providerStartTime) {
        this.providerStartTime = providerStartTime;
    }

    public LocalDateTime getProviderStopTime() {
        return providerStopTime;
    }

    public void setProviderStopTime(LocalDateTime providerStopTime) {
        this.providerStopTime = providerStopTime;
    }

    public String getProviderRunningUUID() {
        return providerRunningUUID;
    }

    public void setProviderRunningUUID(String providerRunningUUID) {
        this.providerRunningUUID = providerRunningUUID;
    }
}