package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;

import java.util.List;
import java.util.Set;

public interface GeospatialScaleCalculable {

    /**
     * 空间拓扑关系定义
     * A  ->  B
     * Contains : 包含,几何形状B的线都在几何形状A内部（区别于内含）
     * Intersects : 相交,几何形状至少有一个共有点（区别于脱节）
     * Within : 内含,几何形状A的线都在几何形状B内部
     * Equals : 相等,几何形状拓扑上相等
     * Crosses : 交叉,几何形状共享一些但不是所有的内部点
     * Touches : 接触,几何形状有至少一个公共的边界点，但是没有内部点
     * Overlaps : 重叠,几何形状共享一部分但不是所有的公共点，而且相交处有他们自己相同的区域
     * Disjoint : 脱节,几何形状没有共有的点
     * Cover : 覆盖, 面状几何形态B的所有部分都在面状几何形态A内部
     * CoveredBy : 被覆盖,面状几何形态A的所有部分都在面状几何形态B内部
     */
    public enum SpatialPredicateType {Contains,Intersects,Within,Equals,Crosses,Touches,Overlaps,Disjoint,Cover,CoveredBy}

    /**
     * 空间计算使用的地理空间尺度参考坐标系
     * Global : 全球尺度空间参考坐标系
     * Country : 国家尺度空间参考坐标系
     * Local : 地方尺度空间参考坐标系
     */
    public enum SpatialScaleLevel {Global,Country,Local}

    /**
     * 选择一个概念类型中的若干目标概念实体，与当前概念实体进行平面地理空间计算，并过滤返回符合计算规则的目标概念实体列表
     *
     * @param targetConceptionKind String 执行空间计算的目标概念类型名称
     * @param attributesParameters AttributesParameters 选择目标概念实体的查询过滤条件
     * @param spatialPredicateType SpatialPredicateType 空间计算使用的空间拓扑关系类型
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 符合空间计算规则定义的目标概念实体列表
     */
    public List<ConceptionEntity> getSpatialPredicateMatchedConceptionEntities(String targetConceptionKind, AttributesParameters attributesParameters,
                                                                               SpatialPredicateType spatialPredicateType, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException;

    /* 根据当前概念类型实体的地理空间 WKT 数据计算其与系统内置的地理空间区域实体（行政区划）的特定空间关系，返回符合计算逻辑的地理空间区域实体
     *
     * @param spatialScaleLevel GeospatialScaleCalculable.SpatialScaleLevel 空间计算使用的地理空间尺度参考坐标系
     * @param spatialPredicateType GeospatialScaleCalculable.SpatialPredicateType 空间计算使用的空间拓扑关系定义
     * @param geospatialScaleGrade GeospatialRegion.GeospatialScaleGrade 空间计算的目标地理空间区域实体的地理空间刻度等级
     * @param geospatialRegionName String 指定地理空间区域名称,输入 null 则选择默认地理空间区域
     *
     * @return 符合空间计算规则定义的目标地理空间刻度实体列表
     */
    List<GeospatialScaleEntity> getSpatialPredicateMatchedGeospatialScaleEntities(SpatialScaleLevel spatialScaleLevel, SpatialPredicateType spatialPredicateType,
                                                                                  GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade,String geospatialRegionName) throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException;

    /**
     * 选择一个概念类型中的若干目标概念实体，与由当前概念实体计算获取的缓冲区进行平面地理空间计算，并过滤返回符合计算规则的目标概念实体列表
     *
     * @param targetConceptionKind String 执行空间计算的目标概念类型名称
     * @param attributesParameters AttributesParameters 选择目标概念实体的查询过滤条件
     * @param bufferDistanceValue double 针对当前概念实体执行计算的缓冲值，其数值的度量单位与所使用的空间坐标系相同
     * @param spatialPredicateType SpatialPredicateType 空间计算使用的空间拓扑关系类型
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 符合空间计算规则定义的目标概念实体列表
     */
    public List<ConceptionEntity> getSpatialBufferMatchedConceptionEntities(String targetConceptionKind, AttributesParameters attributesParameters,
                                                                            double bufferDistanceValue,SpatialPredicateType spatialPredicateType,
                                                                            SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException;

    /**
     * 选择一个目标概念实体，与当前概念实体进行平面地理空间计算
     *
     * @param spatialPredicateType SpatialPredicateType 空间计算使用的空间拓扑关系类型
     * @param targetConceptionEntityUID String 目标概念实体的唯一ID
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 空间计算的计算结果
     */
    public boolean isSpatialPredicateMatchedWith(SpatialPredicateType spatialPredicateType,String targetConceptionEntityUID,
                                                 SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 选择一组目标概念实体，并形成一个单一的空间计算目标，与当前概念实体进行平面地理空间计算
     *
     * @param spatialPredicateType SpatialPredicateType 空间计算使用的空间拓扑关系类型
     * @param targetConceptionEntityUIDsSet Set<String> 目标概念实体唯一ID的集合
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 空间计算的计算结果
     */
    public boolean isSpatialPredicateMatchedWith(SpatialPredicateType spatialPredicateType, Set<String> targetConceptionEntityUIDsSet,
                                                 SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前概念实体的WKT结构空间数据类型
     *
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 指定地理空间尺度坐标系下的WKT结构空间数据类型
     */
    public GeospatialScaleFeatureSupportable.WKTGeometryType getEntityGeometryType(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 选择一个目标概念实体，与当前概念实体进行平面地理空间距离计算
     *
     * @param targetConceptionEntityUID String 目标概念实体的唯一ID
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 空间距离计算的结果，其数值的度量单位与所使用的空间坐标系相同
     */
    public double getEntitiesSpatialDistance(String targetConceptionEntityUID, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 选择一个目标概念实体，计算其是否与当前概念实体在指定的空间距离之内
     *
     * @param targetConceptionEntityUID String 目标概念实体的唯一ID
     * @param distanceValue double 空间距离计算的最大目标数值，其数值的度量单位与所使用的空间坐标系相同
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 如空间距离计算的结果在空间距离计算的最大目标数值之内则返回 true
     */
    public boolean isSpatialDistanceWithinEntity(String targetConceptionEntityUID, double distanceValue, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 选择一组目标概念实体，并形成一个单一的空间计算目标，计算其是否与当前概念实体在指定的空间距离之内
     *
     * @param targetConceptionEntityUIDsSet Set<String> 目标概念实体唯一ID的集合
     * @param distanceValue double 空间距离计算的最大目标数值，其数值的度量单位与所使用的空间坐标系相同
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 如空间距离计算的结果在空间距离计算的最大目标数值之内则返回 true
     */
    public boolean isSpatialDistanceWithinEntities(Set<String> targetConceptionEntityUIDsSet, double distanceValue, SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取由当前概念实体计算缓冲区获得的 WKT结构空间数据文本
     *
     * @param bufferDistanceValue double 针对当前概念实体执行计算的缓冲值，其数值的度量单位与所使用的空间坐标系相同
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 计算结果缓冲区（面状）WKT的文本表示
     */
    public String getEntitySpatialBufferWKTGeometryContent(double bufferDistanceValue,SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取由当前概念实体计算 Envelope 获得的 WKT结构空间数据文本
     *
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 计算结果 Envelope（面状）WKT的文本表示
     */
    public String getEntitySpatialEnvelopeWKTGeometryContent(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取由当前概念实体计算 CentroidPoint 获得的 WKT结构空间数据文本
     *
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 计算结果 CentroidPoint（点状）WKT的文本表示
     */
    public String getEntitySpatialCentroidPointWKTGeometryContent(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取由当前概念实体计算 InteriorPoint 获得的 WKT结构空间数据文本
     *
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 计算结果 InteriorPoint（点状）WKT的文本表示
     */
    public String getEntitySpatialInteriorPointWKTGeometryContent(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前概念实体的地理空间面积，当 SpatialScaleLevel 为 local 时，数值的度量单位与所使用的空间坐标系相同，其他情况下数值的度量单位为平方米
     *
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 概念实体的地理空间面积值
     */
    public double getEntityGeometryArea(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前概念实体的地理空间周长，当 SpatialScaleLevel 为 local 时，数值的度量单位与所使用的空间坐标系相同，其他情况下数值的度量单位为米
     *
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 概念实体的地理空间周长值
     */
    public double getEntityGeometryPerimeter(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前概念实体的地理空间长度，当 SpatialScaleLevel 为 local 时，数值的度量单位与所使用的空间坐标系相同，其他情况下数值的度量单位为米
     *
     * @param spatialScaleLevel SpatialScaleLevel 空间计算针对的地理空间尺度坐标系数据
     *
     * @return 概念实体的地理空间长度
     */
    public double getEntityGeometryLineLength(SpatialScaleLevel spatialScaleLevel) throws CoreRealmServiceRuntimeException;
}
