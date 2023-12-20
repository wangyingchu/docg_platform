package com.viewfunction.docg.dataCompute.computeServiceCore.payload;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataSlicePropertyType;

import java.util.Map;

public class DataSliceDetailInfo {


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

    private Map<String, DataSlicePropertyType> propertiesDefinition;











}
