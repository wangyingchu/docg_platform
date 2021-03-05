package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

public interface GeospatialMeasurable {
    /**
     * 获取当前对象的(WKT格式)地理空间数据内容
     *
     * @return (WKT格式)地理空间数据内容
     */
    public String getGeometryContent();

    /**
     * 为当前对象添加或更新(WKT格式)地理空间数据内容
     *
     * @param wKTContent String (WKT格式)地理空间数据内容
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean addOrUpdateGeometryContent(String wKTContent);
}
