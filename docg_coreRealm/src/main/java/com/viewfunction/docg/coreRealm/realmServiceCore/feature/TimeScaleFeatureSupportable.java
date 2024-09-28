package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleDataPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * @param timeFlowName String 指定时间流名称
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
     * 为当前对象在默认时间流上附加时间刻度事件
     *
     * @param dateTime LocalDateTime 事件发生时间
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(LocalDateTime dateTime, String eventComment, Map<String, Object> eventData,
                                               TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前对象在指定时间流上附加时间刻度事件
     *
     * @param timeFlowName String 指定时间流名称
     * @param dateTime LocalDateTime 事件发生时间
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(String timeFlowName,LocalDateTime dateTime, String eventComment,
                                               Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前对象在默认时间流上附加 TimeScaleGrade.DAY 粒度的时间刻度事件
     *
     * @param date LocalDate 事件发生日期
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(LocalDate date, String eventComment, Map<String, Object> eventData) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前对象在指定时间流上附加时间刻度事件
     *
     * @param timeFlowName String 指定时间流名称
     * @param date LocalDate 事件发生日期
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(String timeFlowName,LocalDate date, String eventComment,
                                               Map<String, Object> eventData) throws CoreRealmServiceRuntimeException;

    /**
     * 在当前对象与指定时间刻度实体之间附加时间刻度事件
     *
     * @param timeScaleEntityUID String 时间刻度实体唯一ID
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEventByTimeScaleEntityUID(String timeScaleEntityUID, String eventComment,
                                               Map<String, Object> eventData) throws CoreRealmServiceRuntimeException;
    
    /**
     * 为当前对象在默认时间流上附加时间刻度事件
     *
     * @param dateTime long 事件发生时间
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     * @param eventAdditionalConceptionKind String 新建时间刻度事件同时属于的常规概念类型名称
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(long dateTime, String eventComment, Map<String, Object> eventData,
                                               TimeFlow.TimeScaleGrade timeScaleGrade,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前对象在指定时间流上附加时间刻度事件
     *
     * @param timeFlowName String 指定时间流名称
     * @param dateTime long 事件发生时间
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     * @param eventAdditionalConceptionKind String 新建时间刻度事件同时属于的常规概念类型名称
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(String timeFlowName,long dateTime, String eventComment,
                                               Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前对象在默认时间流上附加时间刻度事件
     *
     * @param dateTime LocalDateTime 事件发生时间
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     * @param eventAdditionalConceptionKind String 新建时间刻度事件同时属于的常规概念类型名称
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(LocalDateTime dateTime, String eventComment, Map<String, Object> eventData,
                                               TimeFlow.TimeScaleGrade timeScaleGrade,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前对象在指定时间流上附加时间刻度事件
     *
     * @param timeFlowName String 指定时间流名称
     * @param dateTime LocalDateTime 事件发生时间
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param timeScaleGrade TimeFlow.TimeScaleGrade 事件时间刻度
     * @param eventAdditionalConceptionKind String 新建时间刻度事件同时属于的常规概念类型名称
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(String timeFlowName,LocalDateTime dateTime, String eventComment,
                                               Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前对象在默认时间流上附加 TimeScaleGrade.DAY 粒度的时间刻度事件
     *
     * @param date LocalDate 事件发生日期
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param eventAdditionalConceptionKind String 新建时间刻度事件同时属于的常规概念类型名称
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(LocalDate date, String eventComment, Map<String, Object> eventData,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException;

    /**
     * 为当前对象在指定时间流上附加时间刻度事件
     *
     * @param timeFlowName String 指定时间流名称
     * @param date LocalDate 事件发生日期
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param eventAdditionalConceptionKind String 新建时间刻度事件同时属于的常规概念类型名称
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEvent(String timeFlowName,LocalDate date, String eventComment,
                                               Map<String, Object> eventData,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException;

    /**
     * 在当前对象与指定时间刻度实体之间附加时间刻度事件
     *
     * @param timeScaleEntityUID String 时间刻度实体唯一ID
     * @param eventComment String 事件备注
     * @param eventData Map<String, Object> 事件数据
     * @param eventAdditionalConceptionKind String 新建时间刻度事件同时属于的常规概念类型名称
     *
     * @return 如操作成功，返回结果为相应时间刻度事件对象
     */
    public TimeScaleEvent attachTimeScaleEventByTimeScaleEntityUID(String timeScaleEntityUID, String eventComment,
                                                                   Map<String, Object> eventData,String eventAdditionalConceptionKind) throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前对象上关联的指定时间刻度事件
     *
     * @param timeScaleEventUID String 时间刻度事件唯一ID
     *
     * @return 如操作成功，返回 true
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
