package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEvent;

public class GeospatialScaleEventAndConceptionEntityPair {

    private GeospatialScaleEvent geospatialScaleEvent;
    private ConceptionEntity conceptionEntity;

    public GeospatialScaleEventAndConceptionEntityPair(GeospatialScaleEvent geospatialScaleEvent, ConceptionEntity conceptionEntity){
        this.geospatialScaleEvent = geospatialScaleEvent;
        this.conceptionEntity = conceptionEntity;
    }

    public ConceptionEntity getConceptionEntity() {
        return conceptionEntity;
    }

    public GeospatialScaleEvent getGeospatialScaleEvent() {
        return geospatialScaleEvent;
    }
}
