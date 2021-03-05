package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

public interface GeospatialScaleFeatureSupportable {
    /**
     * WKT 结构的空间数据类型
     * POINT : 点状数据
     * LINESTRING : 线状数据
     * POLYGON : 面状数据
     * MULTIPOINT : 多点状数据
     * MULTILINESTRING : 多线状数据
     * MULTIPOLYGON : 多面状数据
     * GEOMETRYCOLLECTION : 复杂结构状数据
     */
    public enum WKTGeometryType {
        POINT,LINESTRING,POLYGON,MULTIPOINT,MULTILINESTRING, MULTIPOLYGON, GEOMETRYCOLLECTION}

    /**
     * 空间计算的距离单位
     * M : 米
     * KM : 公里
     */
    public enum Unit{M,KM}

    /**
     * 空间坐标系类型
     * EARTH : 大地坐标系
     * PROJECTED : 投影坐标系
     */
    public enum CoordinateSystemType{EARTH,PROJECTED}

    /**
     * 获取当前对象的空间参考坐标系(CoordinateReferenceSystem)编码
     *
     * @return 空间参考坐标系代码
     */
    public String getCRS();

    /**
     * 为当前对象添加或更新空间参考坐标系(CoordinateReferenceSystem)编码
     *
     * @param coordinateReferenceSystem String 空间参考坐标系代码
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean addOrUpdateCRS(String coordinateReferenceSystem);

    /**
     * 获取当前对象的空间数据结构
     *
     * @return WKTGeometryType
     */
    public WKTGeometryType getGeometryType();

    /**
     * 为当前对象添加或更新空间数据结构
     *
     * @param wKTGeometryType WKTGeometryType 空间数据结构
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean addOrUpdateGeometryType(WKTGeometryType wKTGeometryType);

    /**
     * 获取当前对象的空间距离单位
     *
     * @return Unit
     */
    public Unit getUnit();

    /**
     * 为当前对象添加或更新空间距离单位
     *
     * @param unit Unit 空间距离单位
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean addOrUpdateUnit(Unit unit);

    /**
     * 获取当前对象的空间坐标系类型
     *
     * @return CoordinateSystemType
     */
    public CoordinateSystemType getCSType();

    /**
     * 为当前对象添加或更新空间坐标系类型
     *
     * @param coordinateSystemType CoordinateSystemType 空间坐标系类型
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean addOrUpdateCSType(CoordinateSystemType coordinateSystemType);
}
