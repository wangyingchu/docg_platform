package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleEventsRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JGeospatialScaleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Neo4JGeospatialScaleEntityImpl implements Neo4JGeospatialScaleEntity {

    private static Logger logger = LoggerFactory.getLogger(Neo4JGeospatialScaleEntityImpl.class);
    private String coreRealmName;
    private String geospatialScaleEntityUID;
    private String geospatialRegionName;
    private GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade;
    private String geospatialCode;
    private String entityChineseName;
    private String entityEnglishName;

    public Neo4JGeospatialScaleEntityImpl(String coreRealmName, String geospatialRegionName, String geospatialScaleEntityUID,
                                          GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade, String geospatialCode,
                                          String entityChineseName,String entityEnglishName){
        this.coreRealmName = coreRealmName;
        this.geospatialRegionName = geospatialRegionName;
        this.geospatialScaleEntityUID = geospatialScaleEntityUID;
        this.geospatialScaleGrade = geospatialScaleGrade;
        this.geospatialCode = geospatialCode;
        this.entityChineseName = entityChineseName;
        this.entityEnglishName = entityEnglishName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public GeospatialRegion.GeospatialScaleGrade getGeospatialScaleGrade() {
        return this.geospatialScaleGrade;
    }

    @Override
    public String getGeospatialCode() {
        return this.geospatialCode;
    }

    @Override
    public String getChineseName() {
        return this.entityChineseName;
    }

    @Override
    public String getEnglishName() {
        return this.entityEnglishName;
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

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
