package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEvent;

public class GeospatialScaleDataPair {

    private GeospatialScaleEvent geospatialScaleEvent;
    private GeospatialScaleEntity geospatialScaleEntity;

    public GeospatialScaleDataPair(GeospatialScaleEvent geospatialScaleEvent,GeospatialScaleEntity geospatialScaleEntity){
        this.geospatialScaleEvent = geospatialScaleEvent;
        this.geospatialScaleEntity = geospatialScaleEntity;
    }

    public GeospatialScaleEvent getGeospatialScaleEvent() {
        return geospatialScaleEvent;
    }

    public GeospatialScaleEntity getGeospatialScaleEntity() {
        return geospatialScaleEntity;
    }
}
