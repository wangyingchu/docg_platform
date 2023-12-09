package com.viewfunction.docg.dataCompute.computeServiceCore.payload;

public class DataSliceMetaInfo {

    private int primaryDataCount;
    private int backupDataCount;
    private int totalDataCount;
    private int storeBackupNumber;
    private String dataStoreMode;
    private String atomicityMode;
    private String sliceGroupName;
    private Class keyClass;
    private Class valueClass;
    private String dataSliceName;

    public int getPrimaryDataCount() {
        return primaryDataCount;
    }

    public void setPrimaryDataCount(int primaryDataCount) {
        this.primaryDataCount = primaryDataCount;
    }

    public int getBackupDataCount() {
        return backupDataCount;
    }

    public void setBackupDataCount(int backupDataCount) {
        this.backupDataCount = backupDataCount;
    }

    public int getTotalDataCount() {
        return totalDataCount;
    }

    public void setTotalDataCount(int totalDataCount) {
        this.totalDataCount = totalDataCount;
    }

    public int getStoreBackupNumber() {
        return storeBackupNumber;
    }

    public void setStoreBackupNumber(int storeBackupNumber) {
        this.storeBackupNumber = storeBackupNumber;
    }

    public String getDataStoreMode() {
        return dataStoreMode;
    }

    public void setDataStoreMode(String dataStoreMode) {
        this.dataStoreMode = dataStoreMode;
    }

    public String getAtomicityMode() {
        return atomicityMode;
    }

    public void setAtomicityMode(String atomicityMode) {
        this.atomicityMode = atomicityMode;
    }

    public String getSliceGroupName() {
        return sliceGroupName;
    }

    public void setSliceGroupName(String sliceGroupName) {
        this.sliceGroupName = sliceGroupName;
    }

    public Class getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(Class keyClass) {
        this.keyClass = keyClass;
    }

    public Class getValueClass() {
        return valueClass;
    }

    public void setValueClass(Class valueClass) {
        this.valueClass = valueClass;
    }

    public String getDataSliceName() {
        return dataSliceName;
    }

    public void setDataSliceName(String dataSliceName) {
        this.dataSliceName = dataSliceName;
    }
}
