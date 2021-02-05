package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.datastore;

import java.util.List;

public interface DataChangeActions {

    public void onDataChangeReceived(List<DataObjectChangeRecord> changingDataObjectList);

    public boolean isNeededDataChange(DataObjectChangeRecord dataObjectChangeRecord);

}
