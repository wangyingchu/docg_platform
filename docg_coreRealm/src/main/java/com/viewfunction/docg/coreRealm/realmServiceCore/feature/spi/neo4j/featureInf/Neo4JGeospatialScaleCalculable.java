package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;

import java.util.List;

public interface Neo4JGeospatialScaleCalculable extends GeospatialScaleCalculable {

    default public List<ConceptionEntity> getSpatialPredicateMatchedConceptionEntities(
            List<String> targetConceptionKinds, SpatialPredicateType spatialPredicateType,
            SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        return null;
    }

    default public List<ConceptionEntity> getSpatialBufferMatchedConceptionEntities(
            List<String> targetConceptionKinds, double bufferDistanceValue,
            SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        return null;
    }

    default public boolean isSpatialPredicateMatchedWith(
            String targetConceptionEntityUID, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException{
        return false;
    }
}
