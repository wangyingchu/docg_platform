package com.viewfunction.docg.dataCompute.computeServiceCore.term;

import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.ComputeGridRealtimeStatisticsInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.ComputeGridImplTech;

public interface ComputeGrid {

    public ComputeGridImplTech getGridImplTech();

    public DataService getDataService() throws ComputeGridException;

    public ComputeGridRealtimeStatisticsInfo getGridRealtimeStatisticsInfo() throws ComputeGridException;

}
