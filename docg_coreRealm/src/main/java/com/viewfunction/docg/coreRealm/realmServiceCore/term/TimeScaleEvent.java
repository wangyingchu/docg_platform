package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MultiConceptionKindsSupportable;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeScaleEvent extends AttributesMeasurable, ClassificationAttachable, MultiConceptionKindsSupportable {
    /**
     * 获取当前时间刻度事件所属的时间流名称
     *
     * @return 时间流名称
     */
    public String getTimeFlowName();

    /**
     * 获取当前时间刻度事件发生的时间值
     *
     * @return 时间值
     */
    public LocalDateTime getReferTime();

    /**
     * 获取当前时间刻度事件的时间刻度等级
     *
     * @return 时间刻度等级
     */
    public TimeFlow.TimeScaleGrade getTimeScaleGrade();

    /**
     * 获取当前时间刻度事件的唯一ID
     *
     * @return 时间刻度事件唯一ID
     */
    public String getTimeScaleEventUID();

    /**
     * 获取当前时间刻度事件的事件备注
     *
     * @return 时间刻度事件事件备注
     */
    public String getEventComment();

    /**
     * 获取当前时间刻度事件相关的时间刻度实体
     *
     * @return 时间刻度实体对象
     */
    public TimeScaleEntity getReferTimeScaleEntity();

    /**
     * 获取当前时间刻度事件相关的常规概念实体
     *
     * @return 常规概念实体对象
     */
    public ConceptionEntity getAttachConceptionEntity();

    /**
     * 获取当前时间刻度事件的概念类型别名列表
     *
     * @return 概念类型别名列表
     */
    public List<String> getAliasConceptionKindNames();
}
