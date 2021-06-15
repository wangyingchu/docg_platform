package com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable;

public class MemoryTableMetaInfo {

    private int primaryDataCount;
    private int backupDataCount;
    private int totalDataCount;
    private int storeBackupNumber;
    private String memoryTableMode;
    private String atomicityMode;
    private String memoryTableGroupName;
    private Class keyClass;
    private Class valueClass;
    private String memoryTableName;

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

    public String getMemoryTableMode() {
        return memoryTableMode;
    }

    public void setMemoryTableMode(String memoryTableMode) {
        this.memoryTableMode = memoryTableMode;
    }

    public String getAtomicityMode() {
        return atomicityMode;
    }

    public void setAtomicityMode(String atomicityMode) {
        this.atomicityMode = atomicityMode;
    }

    public String getMemoryTableGroupName() {
        return memoryTableGroupName;
    }

    public void setMemoryTableGroupName(String memoryTableGroupName) {
        this.memoryTableGroupName = memoryTableGroupName;
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

    public String getMemoryTableName() {
        return memoryTableName;
    }

    public void setMemoryTableName(String memoryTableName) {
        this.memoryTableName = memoryTableName;
    }
}
