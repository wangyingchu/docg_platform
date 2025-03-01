package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MultiConceptionKindsSupportable;

import java.util.List;

public interface GeospatialScaleEvent  extends AttributesMeasurable, ClassificationAttachable, MultiConceptionKindsSupportable {
    /**
     * 获取当前地理空间刻度事件所属的地理空间区域名称
     *
     * @return 地理空间区域名称
     */
    public String getGeospatialRegionName();

    /**
     * 获取当前地理空间刻度事件发生的地理空间刻度实体的全局空间编码
     *
     * @return 时间值
     */
    public String getReferLocation();

    /**
     * 获取当前地理空间刻度事件的地理空间刻度等级
     *
     * @return 地理空间刻度等级
     */
    public GeospatialRegion.GeospatialScaleGrade getGeospatialScaleGrade();

    /**
     * 获取当前地理空间刻度事件的唯一ID
     *
     * @return 地理空间刻度事件唯一ID
     */
    public String getGeospatialScaleEventUID();

    /**
     * 获取当前地理空间刻度事件的事件备注
     *
     * @return 地理空间刻度事件事件备注
     */
    public String getEventComment();

    /**
     * 获取当前地理空间刻度事件相关的地理空间刻度实体
     *
     * @return 地理空间刻度实体对象
     */
    public GeospatialScaleEntity getReferGeospatialScaleEntity();

    /**
     * 获取当前地理空间刻度事件相关的常规概念实体
     *
     * @return 常规概念实体对象
     */
    public ConceptionEntity getAttachConceptionEntity();

    /**
     * 获取当前地理空间刻度事件的概念类型别名列表
     *
     * @return 概念类型别名列表
     */
    public List<String> getAliasConceptionKindNames();
}
