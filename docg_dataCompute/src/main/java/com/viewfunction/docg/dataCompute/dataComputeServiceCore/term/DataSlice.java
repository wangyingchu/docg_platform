package com.viewfunction.docg.dataCompute.dataComputeServiceCore.term;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceMetaInfo;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceOperationResult;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.DataSliceQueryResult;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSlicePropertiesStructureException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSliceDataException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.DataSliceQueryStructureException;

import java.util.List;
import java.util.Map;

public interface DataSlice {

    public boolean addDataRecord(Map<String,Object> dataPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException;

    public DataSliceOperationResult addDataRecords(List<String> propertiesNameList, List<Map<String,Object>> dataPropertiesValueList) throws DataSlicePropertiesStructureException;

    public boolean updateDataRecord(Map<String,Object> dataPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException;

    public boolean addOrUpdateDataRecord(Map<String,Object> dataPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException;

    public Map<String,Object> getDataRecordByPrimaryKeys(Map<String,Object> dataPKPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException;

    public boolean deleteDataRecord(Map<String,Object> dataPKPropertiesValue) throws DataSlicePropertiesStructureException, DataSliceDataException;

    public DataSliceOperationResult deleteDataRecords(List<Map<String,Object>> dataPKPropertiesValueList) throws DataSlicePropertiesStructureException, DataSliceDataException;

    public DataSliceQueryResult queryDataRecords(String queryLogic) throws DataSliceDataException;

    public DataSliceQueryResult queryDataRecords(QueryParameters queryParameters) throws DataSliceDataException, DataSliceQueryStructureException;

    public void emptyDataSlice();

    public DataSliceMetaInfo getDataSliceMetaInfo();
}
