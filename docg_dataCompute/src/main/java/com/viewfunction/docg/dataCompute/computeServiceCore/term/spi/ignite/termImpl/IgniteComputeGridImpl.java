package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.exception.ComputeGridException;
import com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.ComputeGridObserver;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.ComputeGridRealtimeStatisticsInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.ComputeUnitRealtimeStatisticsInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.payload.DataComputeUnitMetaInfo;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.DataService;
import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteComputeGrid;
import com.viewfunction.docg.dataCompute.computeServiceCore.util.ComputeGridImplTech;

import java.util.Set;

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
        try(ComputeGridObserver computeGridObserver = ComputeGridObserver.getObserverInstance()){
            ComputeGridRealtimeStatisticsInfo computeGridRealtimeStatisticsInfo = computeGridObserver.getGridRealtimeStatisticsInfo();
            return computeGridRealtimeStatisticsInfo;
        } catch (Exception e) {
            throw new ComputeGridException(e);
        }
    }

    @Override
    public Set<ComputeUnitRealtimeStatisticsInfo> getComputeUnitsRealtimeStatisticsInfo() throws ComputeGridException {
        try(ComputeGridObserver computeGridObserver = ComputeGridObserver.getObserverInstance()){
            Set<ComputeUnitRealtimeStatisticsInfo> computeUnitRealtimeStatisticsInfoSet = computeGridObserver.getComputeUnitsRealtimeStatisticsInfo();
            return computeUnitRealtimeStatisticsInfoSet;
        } catch (Exception e) {
            throw new ComputeGridException(e);
        }
    }

    @Override
    public Set<DataComputeUnitMetaInfo> listDataComputeUnit() throws ComputeGridException {
        try(ComputeGridObserver computeGridObserver = ComputeGridObserver.getObserverInstance()){
            Set<DataComputeUnitMetaInfo> dataComputeUnitMetaInfoSet = computeGridObserver.listDataComputeUnit();
            return dataComputeUnitMetaInfoSet;
        } catch (Exception e) {
            throw new ComputeGridException(e);
        }
    }
}
