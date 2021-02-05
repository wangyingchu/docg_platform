package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

import org.apache.ignite.binary.BinaryObject;

public interface BinaryDataMatchCondition {

    public boolean match(BinaryObject keyBinaryObject, BinaryObject valueBinaryObject);
}
