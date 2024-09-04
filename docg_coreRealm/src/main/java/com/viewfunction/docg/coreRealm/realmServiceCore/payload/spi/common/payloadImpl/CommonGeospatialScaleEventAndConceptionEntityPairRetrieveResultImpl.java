package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesRetrieveStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleEventAndConceptionEntityPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleEventAndConceptionEntityPairRetrieveResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl implements GeospatialScaleEventAndConceptionEntityPairRetrieveResult {

    private List<GeospatialScaleEventAndConceptionEntityPair> geospatialScaleEventAndConceptionEntityPairList;
    private EntitiesRetrieveStatistics entitiesRetrieveStatistics;

    public CommonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl(){
        this.geospatialScaleEventAndConceptionEntityPairList = new ArrayList<>();
        this.entitiesRetrieveStatistics = new EntitiesRetrieveStatistics();
        this.entitiesRetrieveStatistics.setStartTime(new Date());
    }

    public void finishEntitiesRetrieving(){
        this.entitiesRetrieveStatistics.setFinishTime(new Date());
    }

    public void addGeospatialScaleEventAndConceptionEntityPairs(List<GeospatialScaleEventAndConceptionEntityPair> timeScaleEventAndConceptionEntityPairList){
        this.geospatialScaleEventAndConceptionEntityPairList.addAll(timeScaleEventAndConceptionEntityPairList);
    }

    @Override
    public List<GeospatialScaleEventAndConceptionEntityPair> getGeospatialScaleEventAndConceptionEntityPairs() {
        return List.of();
    }

    @Override
    public EntitiesRetrieveStatistics getOperationStatistics() {
        return null;
    }
}
