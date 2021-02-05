package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

public class DataObject {

    private Object key;
    private Object value;

    public DataObject(Object key, Object value){
        this.key=key;
        this.value=value;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
