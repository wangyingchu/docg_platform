package com.viewfunction.docg.dataCompute.dataComputeServiceCore.term;

import com.viewfunction.docg.dataCompute.dataComputeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.payload.*;
import com.viewfunction.docg.dataCompute.dataComputeServiceCore.util.ComputeGridImplTech;

import java.util.Set;

public interface ComputeGrid {

    public ComputeGridImplTech getGridImplTech();

    public DataService getDataService() throws ComputeGridException;

    public ComputeGridRealtimeStatisticsInfo getGridRealtimeStatisticsInfo() throws ComputeGridException;

    public Set<ComputeUnitRealtimeStatisticsInfo> getComputeUnitsRealtimeStatisticsInfo() throws ComputeGridException;

    public Set<DataComputeUnitMetaInfo> listDataComputeUnit() throws ComputeGridException;

    public Set<DataSliceMetaInfo> listDataSlice() throws ComputeGridException;

    public DataSliceDetailInfo getDataSliceDetail(String dataSliceName) throws ComputeGridException;

    public ComputeService getComputeService() throws ComputeGridException;

    public Set<ComputeFunctionMetaInfo> listComputeFunction() throws ComputeGridException;
}
