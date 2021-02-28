package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JGeospatialRegion;

import java.util.List;

public class Neo4JGeospatialRegionImpl implements Neo4JGeospatialRegion {
    @Override
    public String getGeospatialRegionName() {
        return null;
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
}
