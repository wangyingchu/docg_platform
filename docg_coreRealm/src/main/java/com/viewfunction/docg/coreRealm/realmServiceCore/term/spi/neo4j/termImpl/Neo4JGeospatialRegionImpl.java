package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JGeospatialRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Neo4JGeospatialRegionImpl implements Neo4JGeospatialRegion {

    private static Logger logger = LoggerFactory.getLogger(Neo4JGeospatialRegionImpl.class);
    private String coreRealmName;
    private String geospatialRegionName;
    private String geospatialRegionUID;

    public Neo4JGeospatialRegionImpl(String coreRealmName, String geospatialRegionName,String geospatialRegionUID){
        this.coreRealmName = coreRealmName;
        this.geospatialRegionName = geospatialRegionName;
        this.geospatialRegionUID = geospatialRegionUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getGeospatialRegionName() {
        return this.geospatialRegionName;
    }

    @Override
    public boolean createGeospatialScaleEntities() {
        return false;
    }

    @Override
    public List<GeospatialScaleEntity> listContinentEntities() {
        return null;
    }

    @Override
    public GeospatialScaleEntity getContinentEntity(String continentName) {
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listCountryRegionEntities(String countryName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getCountryRegionEntity(String countryName) {
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listProvinceEntities(String countryName, String provinceName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getProvinceEntity(String countryName, String provinceName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getProvinceEntity(String divisionCode) {
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listPrefectureEntities(String countryName, String provinceName, String prefectureName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getPrefectureEntity(String countryName, String provinceName, String prefectureName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getPrefectureEntity(String divisionCode) {
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listCountyEntities(String countryName, String provinceName, String prefectureName, String countyName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getCountyEntity(String countryName, String provinceName, String prefectureName, String countyName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getCountyEntity(String divisionCode) {
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listTownshipEntities(String countryName, String provinceName, String prefectureName, String countyName, String townshipName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getTownshipEntity(String countryName, String provinceName, String prefectureName, String countyName, String townshipName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getTownshipEntity(String divisionCode) {
        return null;
    }

    @Override
    public List<GeospatialScaleEntity> listVillageEntities(String countryName, String provinceName, String prefectureName, String countyName, String townshipName, String villageName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getVillageEntity(String countryName, String provinceName, String prefectureName, String countyName, String townshipName, String villageName) {
        return null;
    }

    @Override
    public GeospatialScaleEntity getVillageEntity(String divisionCode) {
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
