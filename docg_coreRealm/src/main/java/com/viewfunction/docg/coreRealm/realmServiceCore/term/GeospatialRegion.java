package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialRegionRuntimeStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialRegionSummaryStatistics;

import java.util.List;

public interface GeospatialRegion {
    /**
     * 地理空间刻度等级
     * CONTINENT : 表示洲际粒度的时间地理空间
     * COUNTRY_REGION : 表示国家，地区粒度的地理空间
     * PROVINCE : 表示省级行政区粒度的地理空间，例如 省、直辖市、自治区、特别行政区
     * PREFECTURE : 表示地级行政区粒度的地理空间，例如 地级市、地区、自治州、盟
     * COUNTY : 表示县级行政区粒度的地理空间，例如 市辖区、县级市、县、自治县等
     * TOWNSHIP : 表示乡级行政区粒度的地理空间，例如 街道、镇、乡、民族乡
     * VILLAGE : 表示村级行政区粒度的地理空间，例如 村庄，社区
     */
    public enum GeospatialScaleGrade {CONTINENT,COUNTRY_REGION,PROVINCE,PREFECTURE,COUNTY,TOWNSHIP,VILLAGE}

    /**
     * 地理空间刻度实体标准属性
     * GeospatialCode : 实体全局地理空间编码
     * ChineseName : 实体中文名称
     * EnglishName : 实体英文名称
     */
    public enum GeospatialProperty {GeospatialCode,ChineseName,EnglishName}

    /**
     * 获取当前地理空间区域名称
     *
     * @return 地理空间区域名称
     */
    public String getGeospatialRegionName();

    /**
     * 创建当前地理空间区域范围内的所有各级地理空间刻度实体并构建相应的关联关系
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean createGeospatialScaleEntities();

    /**
     * 获取当前地理空间区域中指定全局地理空间编码的地理空间刻度实体
     *
     * @param geospatialCode String 指定目标全局地理空间编码（完全精确匹配）
     *
     * @return 目标地理空间刻度实体对象
     */
    public GeospatialScaleEntity getEntityByGeospatialCode(String geospatialCode);

    /**
     * 获取当前地理空间区域中的洲际地理空间刻度实体列表
     *
     * @return 洲际地理空间刻度实体对象列表
     */
    public List<GeospatialScaleEntity> listContinentEntities();

    /**
     * 获取当前地理空间区域范围内的指定洲际地理空间刻度实体
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param continentValue String 指定洲际目标属性值（完全精确匹配）
     *
     * @return 目标地理空间刻度实体对象
     */
    public GeospatialScaleEntity getContinentEntity(GeospatialProperty geospatialProperty,String continentValue);

    /**
     * 获取当前地理空间区域范围内的指定国家地区地理空间刻度实体列表
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param continentValue String 指定国家地区所属洲际目标属性值（完全精确匹配）
     * @param countryValue String 指定国家地区目标属性值（模糊匹配）
     *
     * @return 目标地理空间刻度实体对象列表
     */
    public List<GeospatialScaleEntity> listCountryRegionEntities(GeospatialProperty geospatialProperty,String continentValue,String countryValue);

    /**
     * 获取当前地理空间区域范围内的指定国家地区地理空间刻度实体列表
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（模糊匹配）
     *
     * @return 目标地理空间刻度实体对象列表
     */
    public List<GeospatialScaleEntity> listCountryRegionEntities(GeospatialProperty geospatialProperty,String countryValue);

    /**
     * 获取当前地理空间区域范围内的指定国家地区地理空间刻度实体
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     *
     * @return 目标地理空间刻度实体对象
     */
    public GeospatialScaleEntity getCountryRegionEntity(GeospatialProperty geospatialProperty,String countryValue);

    /**
     * 获取当前地理空间区域范围内的指定省级行政区地理空间刻度实体列表
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（模糊匹配）
     *
     * @return 目标地理空间刻度实体对象列表
     */
    public List<GeospatialScaleEntity> listProvinceEntities(GeospatialProperty geospatialProperty,String countryValue,String provinceValue) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前地理空间区域范围内的指定省级行政区地理空间刻度实体
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（完全精确匹配）
     *
     * @return 目标地理空间刻度实体对象
     */
    public GeospatialScaleEntity getProvinceEntity(GeospatialProperty geospatialProperty,String countryValue,String provinceValue) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前地理空间区域范围内的指定地级行政区地理空间刻度实体列表
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（完全精确匹配）
     * @param prefectureValue String 指定地级行政区目标属性值（模糊匹配）
     *
     * @return 目标地理空间刻度实体对象列表
     */
    public List<GeospatialScaleEntity> listPrefectureEntities(GeospatialProperty geospatialProperty,String countryValue,String provinceValue,String prefectureValue) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前地理空间区域范围内的指定地级行政区地理空间刻度实体
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（完全精确匹配）
     * @param prefectureValue String 指定地级行政区目标属性值（完全精确匹配）
     *
     * @return 目标地理空间刻度实体对象
     */
    public GeospatialScaleEntity getPrefectureEntity(GeospatialProperty geospatialProperty,String countryValue,String provinceValue,String prefectureValue) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前地理空间区域范围内的指定县级行政区地理空间刻度实体列表
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（完全精确匹配）
     * @param prefectureValue String 指定地级行政区目标属性值（完全精确匹配）
     * @param countyValue String 指定县级行政区目标属性值（模糊匹配）
     *
     * @return 目标地理空间刻度实体对象列表
     */
    public List<GeospatialScaleEntity> listCountyEntities(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前地理空间区域范围内的指定县级行政区地理空间刻度实体
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（完全精确匹配）
     * @param prefectureValue String 指定地级行政区目标属性值（完全精确匹配）
     * @param countyValue String 指定县级行政区目标属性值（完全精确匹配）
     *
     * @return 目标地理空间刻度实体对象
     */
    public GeospatialScaleEntity getCountyEntity(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前地理空间区域范围内的指定乡级行政区地理空间刻度实体列表
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（完全精确匹配）
     * @param prefectureValue String 指定地级行政区目标属性值（完全精确匹配）
     * @param countyValue String 指定县级行政区目标属性值（完全精确匹配）
     * @param townshipValue String 指定乡级行政区目标属性值（模糊匹配）
     *
     * @return 目标地理空间刻度实体对象列表
     */
    public List<GeospatialScaleEntity> listTownshipEntities(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue,String townshipValue) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前地理空间区域范围内的指定乡级行政区地理空间刻度实体
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（完全精确匹配）
     * @param prefectureValue String 指定地级行政区目标属性值（完全精确匹配）
     * @param countyValue String 指定县级行政区目标属性值（完全精确匹配）
     * @param townshipValue String 指定乡级行政区目标属性值（完全精确匹配）
     *
     * @return 目标地理空间刻度实体对象
     */
    public GeospatialScaleEntity getTownshipEntity(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue,String townshipValue) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前地理空间区域范围内的指定村级行政区地理空间刻度实体列表
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（完全精确匹配）
     * @param prefectureValue String 指定地级行政区目标属性值（完全精确匹配）
     * @param countyValue String 指定县级行政区目标属性值（完全精确匹配）
     * @param townshipValue String 指定乡级行政区目标属性值（完全精确匹配）
     * @param villageValue String 指定村级行政区目标属性值（模糊匹配）
     *
     * @return 目标地理空间刻度实体对象列表
     */
    public List<GeospatialScaleEntity> listVillageEntities(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue,String townshipValue,String villageValue) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前地理空间区域范围内的指定村级行政区地理空间刻度实体
     *
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param countryValue String 指定国家地区目标属性值（完全精确匹配）
     * @param provinceValue String 指定省级行政区目标属性值（完全精确匹配）
     * @param prefectureValue String 指定地级行政区目标属性值（完全精确匹配）
     * @param countyValue String 指定县级行政区目标属性值（完全精确匹配）
     * @param townshipValue String 指定乡级行政区目标属性值（完全精确匹配）
     * @param villageValue String 指定村级行政区目标属性值（完全精确匹配）
     *
     * @return 目标地理空间刻度实体对象
     */
    public GeospatialScaleEntity getVillageEntity(GeospatialProperty geospatialProperty,String countryValue, String provinceValue, String prefectureValue, String countyValue,String townshipValue,String villageValue) throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前地理空间区域范围涉及的全部地理空间刻度事件
     * @return 删除的地理空间刻度事件数量
     */
    public long removeRefersGeospatialScaleEvents();

    /**
     * 获取当前地理空间区域范围相关的各类数据的实时统计信息
     *
     * @return 地理空间区域范围相关数据实时统计信息
     */
    public GeospatialRegionRuntimeStatistics getGeospatialRegionRuntimeStatistics();

    /**
     * 获取当前地理空间区域范围相关数据的概要统计信息
     *
     * @return 地理空间区域范围相关数据概要统计信息
     */
    public GeospatialRegionSummaryStatistics getGeospatialRegionSummaryStatistics();

    /**
     * 查询获取当前地理空间区域范围内的特定地理空间刻度实体列表
     *
     * @param geospatialScaleGrade GeospatialScaleGrade 实体地理空间刻度等级
     * @param geospatialProperty GeospatialProperty 地理空间刻度实体标准属性类型
     * @param propertyValue String 指定地理空间区域目标属性值（模糊匹配）
     *
     * @return 目标地理空间刻度实体对象列表
     */
    public List<GeospatialScaleEntity> listGeospatialScaleEntities(GeospatialScaleGrade geospatialScaleGrade,GeospatialProperty geospatialProperty,String propertyValue);

    /**
     * 根据唯一ID获取与当前地理空间区域范围相关的地理空间事件对象
     *
     * @param geospatialScaleEventUID String 需要获取的地理空间事件对象唯一ID
     *
     * @return 地理空间事件对象
     */
    public GeospatialScaleEvent getGeospatialScaleEventByUID(String geospatialScaleEventUID);
}
