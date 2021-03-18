package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleEventsRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.util.List;

public interface GeospatialScaleEntity {

    public enum GeospatialScaleLevel {SELF,CHILD,OFFSPRING}

    public GeospatialRegion.GeospatialScaleGrade getGeospatialScaleGrade();
    public String getGeospatialCode();
    public String getChineseName();
    public String getEnglishName();

    public GeospatialScaleEntity getParentEntity();
    public List<GeospatialScaleEntity> getFellowEntities();
    public List<GeospatialScaleEntity> getChildEntities();
    public InheritanceTree<GeospatialScaleEntity> getOffspringEntities();

    public Long countAttachedGeospatialScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode, GeospatialScaleLevel geospatialScaleLevel);
    public GeospatialScaleEventsRetrieveResult getAttachedGeospatialScaleEvents(QueryParameters queryParameters, GeospatialScaleLevel geospatialScaleLevel);

    public Long countAttachedConceptionEntities(GeospatialScaleLevel geospatialScaleLevel);

    public Long countAttachedConceptionEntities(String conceptionKindName,AttributesParameters attributesParameters, boolean isDistinctMode, GeospatialScaleLevel geospatialScaleLevel);
    public ConceptionEntitiesRetrieveResult getAttachedConceptionEntities(String conceptionKindName, QueryParameters queryParameters, GeospatialScaleLevel geospatialScaleLevel);
}
