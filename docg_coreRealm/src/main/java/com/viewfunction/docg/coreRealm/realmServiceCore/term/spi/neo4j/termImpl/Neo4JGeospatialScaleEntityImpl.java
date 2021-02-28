package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleEventsRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JGeospatialScaleEntity;

import java.util.List;

public class Neo4JGeospatialScaleEntityImpl implements Neo4JGeospatialScaleEntity {

    @Override
    public GeospatialRegion.GeospatialScaleGrade getGeospatialScaleGrade() {
        return null;
    }

    @Override
    public GeospatialScaleEntity getParentEntity() {
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> getFellowEntities() {
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> getChildEntities() {
        return null;
    }

    @Override
    public InheritanceTree<GeospatialScaleEntity> getOffspringEntities() {
        return null;
    }

    @Override
    public Long countAttachedGeospatialScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode, GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }

    @Override
    public GeospatialScaleEventsRetrieveResult getAttachedGeospatialScaleEvents(QueryParameters queryParameters, GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }

    @Override
    public Long countAttachedConceptionEntities(GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }

    @Override
    public Long countAttachedConceptionEntities(String conceptionKindName, AttributesParameters attributesParameters, boolean isDistinctMode, GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getAttachedConceptionEntities(String conceptionKindName, QueryParameters queryParameters, GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }
}
