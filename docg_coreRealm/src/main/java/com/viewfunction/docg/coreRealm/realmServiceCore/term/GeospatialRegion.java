package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;

import java.util.List;

public interface GeospatialRegion {

    //中国行政区划
    //省级行政区(Province) 省、直辖市、自治区、特别行政区
    //地级行政区(Prefecture) 地级市、地区、自治州、盟
    //县级行政区(County) 市辖区、县级市、县、自治县等
    //乡级行政区(Township) 街道、镇、乡、民族乡
    //村级行政区(Village) 村
    public enum GeospatialScaleGrade {CONTINENT,COUNTRY_REGION,PROVINCE,PREFECTURE,COUNTY,TOWNSHIP,VILLAGE}

    public enum GeospatialProperty {GeospatialCode,ChineseName,EnglishName}

    public String getGeospatialRegionName();

    public boolean createGeospatialScaleEntities();
    public GeospatialScaleEntity getEntityByGeospatialCode(String geospatialCode);

    public List<GeospatialScaleEntity> listContinentEntities();
    public GeospatialScaleEntity getContinentEntity(GeospatialProperty geospatialProperty,String continentValue);

    public List<GeospatialScaleEntity> listCountryRegionEntities(GeospatialProperty geospatialProperty,String countryValue);
    public GeospatialScaleEntity getCountryRegionEntity(GeospatialProperty geospatialProperty,String countryValue);

    public List<GeospatialScaleEntity> listProvinceEntities(GeospatialProperty geospatialProperty,String countryValue,String provinceValue) throws CoreRealmServiceRuntimeException;
    public GeospatialScaleEntity getProvinceEntity(GeospatialProperty geospatialProperty,String countryValue,String provinceValue) throws CoreRealmServiceRuntimeException;

    public List<GeospatialScaleEntity> listPrefectureEntities(GeospatialProperty geospatialProperty,String countryValue,String provinceValue,String prefectureValue) throws CoreRealmServiceRuntimeException;
    public GeospatialScaleEntity getPrefectureEntity(GeospatialProperty geospatialProperty,String countryValue,String provinceValue,String prefectureValue) throws CoreRealmServiceRuntimeException;

    public List<GeospatialScaleEntity> listCountyEntities(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue) throws CoreRealmServiceRuntimeException;
    public GeospatialScaleEntity getCountyEntity(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue) throws CoreRealmServiceRuntimeException;

    public List<GeospatialScaleEntity> listTownshipEntities(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue,String townshipValue) throws CoreRealmServiceRuntimeException;
    public GeospatialScaleEntity getTownshipEntity(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue,String townshipValue) throws CoreRealmServiceRuntimeException;

    public List<GeospatialScaleEntity> listVillageEntities(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue,String townshipValue,String villageValue) throws CoreRealmServiceRuntimeException;
    public GeospatialScaleEntity getVillageEntity(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue,String townshipValue,String villageValue) throws CoreRealmServiceRuntimeException;
}
