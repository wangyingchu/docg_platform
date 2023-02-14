package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;

public class SystemStatusSnapshotInfo {

    private long snapshotTookTime;
    private Date systemStartupTime;
    private Date systemCreateTime;
    private long totalAcceptedRequestCount;
    private long currentAcceptedRequestCount;
    private long peakRequestCount;
    private long totalDiskSpaceSize;
    private long freeDiskSpaceSize;
    private long usableDiskSpaceSize;
    private double freeDiskPercent;
    private String systemImplementationTech;
    private String systemImplementationVersion;
    private String systemImplementationEdition;

    public SystemStatusSnapshotInfo(){
        this.snapshotTookTime = new Date().getTime();
    }

    public long getSnapshotTookTime() {
        return snapshotTookTime;
    }

    public Date getSystemStartupTime() {
        return systemStartupTime;
    }

    public void setSystemStartupTime(Date systemStartupTime) {
        this.systemStartupTime = systemStartupTime;
    }

    public Date getSystemCreateTime() {
        return systemCreateTime;
    }

    public void setSystemCreateTime(Date systemCreateTime) {
        this.systemCreateTime = systemCreateTime;
    }

    public long getTotalAcceptedRequestCount() {
        return totalAcceptedRequestCount;
    }

    public void setTotalAcceptedRequestCount(long totalAcceptedRequestCount) {
        this.totalAcceptedRequestCount = totalAcceptedRequestCount;
    }

    public long getCurrentAcceptedRequestCount() {
        return currentAcceptedRequestCount;
    }

    public void setCurrentAcceptedRequestCount(long currentAcceptedRequestCount) {
        this.currentAcceptedRequestCount = currentAcceptedRequestCount;
    }

    public long getPeakRequestCount() {
        return peakRequestCount;
    }

    public void setPeakRequestCount(long peakRequestCount) {
        this.peakRequestCount = peakRequestCount;
    }

    public long getTotalDiskSpaceSize() {
        return totalDiskSpaceSize;
    }

    public void setTotalDiskSpaceSize(long totalDiskSpaceSize) {
        this.totalDiskSpaceSize = totalDiskSpaceSize;
    }

    public long getFreeDiskSpaceSize() {
        return freeDiskSpaceSize;
    }

    public void setFreeDiskSpaceSize(long freeDiskSpaceSize) {
        this.freeDiskSpaceSize = freeDiskSpaceSize;
    }

    public long getUsableDiskSpaceSize() {
        return usableDiskSpaceSize;
    }

    public void setUsableDiskSpaceSize(long usableDiskSpaceSize) {
        this.usableDiskSpaceSize = usableDiskSpaceSize;
    }

    public double getFreeDiskPercent() {
        return freeDiskPercent;
    }

    public void setFreeDiskPercent(double freeDiskPercent) {
        this.freeDiskPercent = freeDiskPercent;
    }

    public String getSystemImplementationTech() {
        return systemImplementationTech;
    }

    public void setSystemImplementationTech(String systemImplementationTech) {
        this.systemImplementationTech = systemImplementationTech;
    }

    public String getSystemImplementationVersion() {
        return systemImplementationVersion;
    }

    public void setSystemImplementationVersion(String systemImplementationVersion) {
        this.systemImplementationVersion = systemImplementationVersion;
    }

    public String getSystemImplementationEdition() {
        return systemImplementationEdition;
    }

    public void setSystemImplementationEdition(String systemImplementationEdition) {
        this.systemImplementationEdition = systemImplementationEdition;
    }
}
