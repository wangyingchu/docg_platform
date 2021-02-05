package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

public interface GeneralDataMatchCondition<K,V> {

    public boolean match(K keyObject, V valueObject);
}
