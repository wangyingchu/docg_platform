package com.viewfunction.docg.dataCompute.computeServiceCore.term;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.DataSlice;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.DataSliceExistException;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.DataSlicePropertiesStructureException;

import java.util.List;
import java.util.Map;

public interface DataService extends AutoCloseable{

    public DataSlice createGridDataSlice(String dataSliceName, String dataSliceGroup,
                                         Map<String, DataSlicePropertyType> propertiesDefinitionMap,
                                         List<String> primaryKeysList) throws DataSliceExistException, DataSlicePropertiesStructureException;

    public DataSlice createPerUnitDataSlice(String dataSliceName, String dataSliceGroup,
                                            Map<String, DataSlicePropertyType> propertiesDefinitionMap,
                                            List<String> primaryKeysList) throws DataSliceExistException,DataSlicePropertiesStructureException;

    public void eraseDataSlice(String dataSliceName);

    public DataSlice getDataSlice(String dataSliceName);

    public List<String> listDataSlices();

}
