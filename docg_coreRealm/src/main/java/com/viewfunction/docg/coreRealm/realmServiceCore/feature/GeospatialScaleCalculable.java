package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;

import java.util.List;
import java.util.Set;

public interface GeospatialScaleCalculable {

    /*
        相等(Equals)：几何形状拓扑上相等。
        脱节(Disjoint)：几何形状没有共有的点。
        相交(Intersects)：几何形状至少有一个共有点（区别于脱节）
        接触(Touches)：几何形状有至少一个公共的边界点，但是没有内部点。
        交叉(Crosses)：几何形状共享一些但不是所有的内部点。
        内含(Within)：几何形状A的线都在几何形状B内部。
        包含(Contains)：几何形状B的线都在几何形状A内部（区别于内含）
        重叠(Overlaps)：几何形状共享一部分但不是所有的公共点，而且相交处有他们自己相同的区域。
    */
    public enum SpatialPredicateType {Contains,Intersects,Within,Equals,Crosses,Touches,Overlaps,Disjoint,Cover,CoveredBy}

    public enum SpatialScaleLevel {Global,Country,Local}

    public List<ConceptionEntity> getSpatialPredicateMatchedConceptionEntities(String targetConceptionKind, AttributesParameters attributesParameters,
                                                                               SpatialPredicateType spatialPredicateType,
                                                                               SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException;

    public List<ConceptionEntity> getSpatialBufferMatchedConceptionEntities(String targetConceptionKind, AttributesParameters attributesParameters,
                                                                            double bufferDistanceValue,
                                                                            SpatialPredicateType spatialPredicateType,
                                                                            SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException;

    public boolean isSpatialPredicateMatchedWith(SpatialPredicateType spatialPredicateType,String targetConceptionEntityUID,
                                                 SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public boolean isSpatialPredicateMatchedWith(SpatialPredicateType spatialPredicateType, Set<String> targetConceptionEntityUIDsSet,
                                                 SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public GeospatialScaleFeatureSupportable.WKTGeometryType getEntityGeometryType(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public double getEntitiesSpatialDistance(String targetConceptionEntityUID, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public boolean isSpatialDistanceWithinEntity(String targetConceptionEntityUID, double distanceValue, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public boolean isSpatialDistanceWithinEntities(Set<String> targetConceptionEntityUIDsSet, double distanceValue, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public String getEntitySpatialBufferWKTGeometryContent(double bufferDistanceValue,SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public String getEntitySpatialEnvelopeWKTGeometryContent(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public String getEntitySpatialCentroidPointWKTGeometryContent(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    public String getEntitySpatialInteriorPointWKTGeometryContent(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;
}
