package com.viewfunction.docg.analysisProvider.service.analysisProviderServiceCore.payload;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ProviderRunningInfo {

    private Timestamp providerStartTime;
    private Timestamp providerStopTime;
    private String providerRunningUUID;

    public ProviderRunningInfo(Timestamp providerStartTime, Timestamp providerStopTime, String providerRunningUUID) {
        this.providerStartTime = providerStartTime;
        this.providerStopTime = providerStopTime;
        this.providerRunningUUID = providerRunningUUID;
    }

    public LocalDateTime getProviderStartTime() {
        return providerStartTime != null ? providerStartTime.toLocalDateTime() : null;
    }

    public LocalDateTime getProviderStopTime() {
        return providerStopTime != null ? providerStopTime.toLocalDateTime() : null;
    }

    public String getProviderRunningUUID() {
        return providerRunningUUID;
    }
}