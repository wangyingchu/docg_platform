package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception.ComputeGridNotActiveException;
import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.ComputeGridOperator;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.ComputeGridRealtimeStatisticsInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.ComputeGridImplTech;

public class IgniteComputeGridImpl implements IgniteComputeGrid {

    @Override
    public ComputeGridImplTech getGridImplTech() {
        return ComputeGridImplTech.IGNITE;
    }

    @Override
    public DataService getDataService() throws ComputeGridException {
        DataService dataService = IgniteDataServiceImpl.getServiceInstance();
        return dataService;
    }

    @Override
    public ComputeGridRealtimeStatisticsInfo getGridRealtimeStatisticsInfo() throws ComputeGridException {
        try (ComputeGridOperator computeGridOperator = ComputeGridOperator.getComputeGridOperator()) {
            ComputeGridRealtimeStatisticsInfo targetComputeGridRealtimeStatisticsInfo = computeGridOperator.getComputeGridRealtimeMetrics();
            return targetComputeGridRealtimeStatisticsInfo;
        } catch (ComputeGridNotActiveException e) {
            throw new ComputeGridException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
