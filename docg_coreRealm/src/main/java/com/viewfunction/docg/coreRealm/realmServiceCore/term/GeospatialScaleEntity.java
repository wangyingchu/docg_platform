package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleEventsRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.util.List;

public interface GeospatialScaleEntity {
    /**
     * 地理空间刻度层级
     * SELF : 表示当前地理空间刻度实体自身
     * CHILD : 表示当前地理空间刻度实体下一层级
     * OFFSPRING : 表示当前地理空间刻度实体所有后代层级
     */
    public enum GeospatialScaleLevel {SELF,CHILD,OFFSPRING}

    /**
     * 获取当前地理空间刻度实体对象的地理空间刻度等级
     *
     * @return 地理空间刻度等级
     */
    public GeospatialRegion.GeospatialScaleGrade getGeospatialScaleGrade();

    /**
     * 获取当前地理空间刻度实体对象的全局地理空间编码
     *
     * @return 全局地理空间编码
     */
    public String getGeospatialCode();

    /**
     * 获取当前地理空间刻度实体对象的实体中文名称
     *
     * @return 实体中文名称
     */
    public String getChineseName();

    /**
     * 获取当前地理空间刻度实体对象的实体英文名称
     *
     * @return 实体英文名称
     */
    public String getEnglishName();

    /**
     * 获取当前地理空间刻度实体对象的唯一ID
     *
     * @return 地理空间刻度实体对象唯一ID
     */
    public String getGeospatialScaleEntityUID();

    /**
     * 获取当前对象的上一级地理空间刻度实体对象
     *
     * @return 上一级地理空间刻度实体对象
     */
    public GeospatialScaleEntity getParentEntity();

    /**
     * 获取当前对象同一地理空间刻度中的所有同一上层对象的地理空间刻度实体列表，例如广东省对应的同级地理空间刻度实体为中国的全部由34个省级行政区划对象
     *
     * @return 同一上层对象的时间刻度实体列表
     */
    public List<GeospatialScaleEntity> getFellowEntities();

    /**
     * 获取当前对象的所有下一级地理空间刻度中的地理空间刻度实体列表，例如中国对应的下级地理空间刻度实体为中国的全部由34个省级行政区划对象
     *
     * @return 所有下一级地理空间刻度的地理空间刻度实体列表
     */
    public List<GeospatialScaleEntity> getChildEntities();

    /**
     * 遍历获取当前对象的所有后代地理空间刻度实体
     *
     * @return 所有后代地理空间刻度实体构成的继承树
     */
    public InheritanceTree<GeospatialScaleEntity> getOffspringEntities();

    /**
     * 获取当前地理空间刻度实体对象上附着的地理空间刻度事件的数量
     *
     * @param attributesParameters AttributesParameters 地理空间刻度事件查询条件
     * @param isDistinctMode boolean 是否剔除重复返回值
     * @param geospatialScaleLevel GeospatialScaleLevel 目标地理空间刻度等级，如 SELF只返回自身关联事件，CHILD返回包含下一级地理空间刻度实体关联的事件，OFFSPRING返回包含所有后代地理空间刻度实体关联的事件
     *
     * @return 时间刻度事件数量
     */
    public Long countAttachedGeospatialScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode, GeospatialScaleLevel geospatialScaleLevel);

    /**
     * 获取当前地理空间刻度实体对象上附着的地理空间刻度事件
     *
     * @param queryParameters QueryParameters 地理空间刻度事件查询条件
     * @param geospatialScaleLevel GeospatialScaleLevel 目标地理空间刻度等级，如 SELF只返回自身关联事件，CHILD返回包含下一级地理空间刻度实体关联的事件，OFFSPRING返回包含所有后代地理空间刻度实体关联的事件
     *
     * @return 地理空间刻度事件返回结果集
     */
    public GeospatialScaleEventsRetrieveResult getAttachedGeospatialScaleEvents(QueryParameters queryParameters, GeospatialScaleLevel geospatialScaleLevel);

    /**
     * 获取当前地理空间刻度实体对象上附着的常规概念实体的数量
     *
     * @param geospatialScaleLevel GeospatialScaleLevel 目标地理空间刻度等级，如 SELF只返回自身关联事件，CHILD返回包含下一级地理空间刻度实体关联的事件，OFFSPRING返回包含所有后代地理空间刻度实体关联的事件
     *
     * @return 常规概念实体的数量
     */
    public Long countAttachedConceptionEntities(GeospatialScaleLevel geospatialScaleLevel);

    /**
     * 获取当前地理空间刻度实体对象上附着的常规概念实体的数量
     *
     * @param conceptionKindName String 目标概念类型名称
     * @param attributesParameters AttributesParameters 概念实体查询条件
     * @param isDistinctMode boolean 是否剔除重复返回值
     * @param geospatialScaleLevel GeospatialScaleLevel 目标地理空间刻度等级，如 SELF只返回自身关联事件，CHILD返回包含下一级地理空间刻度实体关联的事件，OFFSPRING返回包含所有后代地理空间刻度实体关联的事件
     *
     * @return 常规概念实体的数量
     */
    public Long countAttachedConceptionEntities(String conceptionKindName,AttributesParameters attributesParameters, boolean isDistinctMode, GeospatialScaleLevel geospatialScaleLevel);

    /**
     * 获取当前地理空间刻度实体对象上附着的常规概念实体对象
     *
     * @param conceptionKindName String 目标概念类型名称
     * @param queryParameters QueryParameters 概念实体查询条件
     * @param geospatialScaleLevel GeospatialScaleLevel 目标地理空间刻度等级，如 SELF只返回自身关联事件，CHILD返回包含下一级地理空间刻度实体关联的事件，OFFSPRING返回包含所有后代地理空间刻度实体关联的事件
     *
     * @return 概念实体返回结果集
     */
    public ConceptionEntitiesRetrieveResult getAttachedConceptionEntities(String conceptionKindName, QueryParameters queryParameters, GeospatialScaleLevel geospatialScaleLevel);
}
