package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;

import java.util.List;

public interface GeospatialScaleCalculable {

    public enum SpatialPredicateType {Contains,Intersects,Within,Equals,Crosses,Touches,Overlaps,Disjoint,Cover,CoveredBy}

    public enum SpatialScaleLevel {Global,Country,Local}

    public List<ConceptionEntity> getSpatialPredicateMatchedConceptionEntities(List<String> targetConceptionKinds,
                                                                               SpatialPredicateType spatialPredicateType,
                                                                               SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public List<ConceptionEntity> getSpatialBufferMatchedConceptionEntities(List<String> targetConceptionKinds,
                                                                            double bufferDistanceValue,
                                                                            SpatialPredicateType spatialPredicateType,
                                                                            SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public boolean isSpatialPredicateMatchedWith(SpatialPredicateType spatialPredicateType,String targetConceptionEntityUID,
                                                 SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public GeospatialScaleFeatureSupportable.WKTGeometryType getEntityGeometryType(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public double getEntitiesSpatialDistance(String targetConceptionEntityUID, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;
}
