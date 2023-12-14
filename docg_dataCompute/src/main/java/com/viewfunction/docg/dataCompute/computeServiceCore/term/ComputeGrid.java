package com.viewfunction.docg.dataCompute.computeServiceCore.term;

import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.ComputeGridRealtimeStatisticsInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.ComputeUnitRealtimeStatisticsInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataComputeUnitMetaInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.ComputeGridImplTech;

import java.util.Set;

public interface ComputeGrid {

    public ComputeGridImplTech getGridImplTech();

    public DataService getDataService() throws ComputeGridException;

    public ComputeGridRealtimeStatisticsInfo getGridRealtimeStatisticsInfo() throws ComputeGridException;

    public Set<ComputeUnitRealtimeStatisticsInfo> getComputeUnitsRealtimeStatisticsInfo() throws ComputeGridException;

    public Set<DataComputeUnitMetaInfo> listDataComputeUnit() throws ComputeGridException;
}
