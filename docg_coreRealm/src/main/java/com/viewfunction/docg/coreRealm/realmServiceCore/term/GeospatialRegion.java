package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import java.util.List;

public interface GeospatialRegion {

    //中国行政区划
    //省级行政区(Province) 省、直辖市、自治区、特别行政区
    //地级行政区(Prefecture) 地级市、地区、自治州、盟
    //县级行政区(County) 市辖区、县级市、县、自治县等
    //乡级行政区(Township) 街道、镇、乡、民族乡
    //村级行政区(Village) 村
    public enum GeospatialScaleGrade {CONTINENT,COUNTRY_REGION,PROVINCE,PREFECTURE,COUNTY,TOWNSHIP,VILLAGE}

    public String getGeospatialRegionName();

    public List<GeospatialScaleEntity> listContinentEntities();
    public GeospatialScaleEntity getContinentEntity(String continentName);

    public List<GeospatialScaleEntity> listCountryRegionEntities(String countryName);
    public GeospatialScaleEntity getCountryRegionEntity(String countryName);

    public List<GeospatialScaleEntity> listProvinceEntities(String countryName,String provinceName);
    public GeospatialScaleEntity getProvinceEntity(String countryName,String provinceName);
    public GeospatialScaleEntity getProvinceEntity(String divisionCode);

    public List<GeospatialScaleEntity> listPrefectureEntities(String countryName,String provinceName,String prefectureName);
    public GeospatialScaleEntity getPrefectureEntity(String countryName,String provinceName,String prefectureName);
    public GeospatialScaleEntity getPrefectureEntity(String divisionCode);

    public List<GeospatialScaleEntity> listCountyEntities(String countryName, String provinceName, String prefectureName, String countyName);
    public GeospatialScaleEntity getCountyEntity(String countryName, String provinceName, String prefectureName, String countyName);
    public GeospatialScaleEntity getCountyEntity(String divisionCode);

    public List<GeospatialScaleEntity> listTownshipEntities(String countryName, String provinceName, String prefectureName, String countyName,String townshipName);
    public GeospatialScaleEntity getTownshipEntity(String countryName, String provinceName, String prefectureName, String countyName,String townshipName);
    public GeospatialScaleEntity getTownshipEntity(String divisionCode);

    public List<GeospatialScaleEntity> listVillageEntities(String countryName, String provinceName, String prefectureName, String countyName,String townshipName,String villageName);
    public GeospatialScaleEntity getVillageEntity(String countryName, String provinceName, String prefectureName, String countyName,String townshipName,String villageName);
    public GeospatialScaleEntity getVillageEntity(String divisionCode);

}
