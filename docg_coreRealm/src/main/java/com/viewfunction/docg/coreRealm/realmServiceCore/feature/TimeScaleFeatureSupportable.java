package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleDataPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;

import java.util.List;
import java.util.Map;

public interface TimeScaleFeatureSupportable {
    /**
     * 为当前对象在默认时间流上附加时间刻度事件
     *
     * @param dateTime long 事件发生时间
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(long dateTime, String eventComment, Map<String, Object> eventData,
                                               TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前对象在指定时间流上附加时间刻度事件
     *
     * @param dateTime long 事件发生时间
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(String timeFlowName,long dateTime, String eventComment,
                                               Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前对象上关联的指定时间刻度事件
     *
     * @param timeScaleEventUID String 时间刻度事件唯一ID
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public boolean detachTimeScaleEvent(String timeScaleEventUID) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前对象上关联的所有时间刻度事件
     *
     * @return 时间刻度事件对象列表
     */
    public List<TimeScaleEvent> getAttachedTimeScaleEvents();

    /**
     * 获取当前对象上关联的所有时间刻度实体
     *
     * @return 时间刻度实体对象列表
     */
    public List<TimeScaleEntity> getAttachedTimeScaleEntities();

    /**
     * 获取当前对象上关联的所有时间刻度数据对
     *
     * @return 时间刻度数据对对象列表
     */
    public List<TimeScaleDataPair> getAttachedTimeScaleDataPairs();
}
