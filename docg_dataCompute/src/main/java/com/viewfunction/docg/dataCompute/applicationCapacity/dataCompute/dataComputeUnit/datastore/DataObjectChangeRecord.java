package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

public class DataObjectChangeRecord {
    private Object key;
    private Object value;
    private Object oldValue;
    private String dataStoreName;
    private String dataChangeType;

    public DataObjectChangeRecord(Object key, Object value,Object oldValue,String dataStoreName,String dataChangeType){
        this.key=key;
        this.value=value;
        this.oldValue=oldValue;
        this.dataStoreName=dataStoreName;
        this.dataChangeType=dataChangeType;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public String getDataStoreName() {
        return dataStoreName;
    }

    public String getDataChangeType() {
        return dataChangeType;
    }
}
