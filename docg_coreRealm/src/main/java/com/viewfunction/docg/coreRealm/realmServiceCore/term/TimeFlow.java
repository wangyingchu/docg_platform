package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeFlowRuntimeStatistics;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleMoment;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.util.LinkedList;
import java.util.List;

public interface TimeFlow {
    /**
     * 时间刻度等级
     * YEAR : 表示年度粒度的时间点，例如 2015年
     * MONTH : 表示年度粒度的时间点，例如 2015年 7月
     * DAY : 表示年度粒度的时间点，例如 2015年 7月 23日
     * HOUR : 表示年度粒度的时间点，例如 2015年 7月 23日 23点
     * MINUTE : 表示年度粒度的时间点，例如 2015年 7月 23日 23点 15分
     * SECOND : 表示年度粒度的时间点，例如 2015年 7月 23日 23点 15分 15秒
     */
    public enum TimeScaleGrade {YEAR,MONTH,DAY,HOUR,MINUTE,SECOND}

    /**
     * 获取当前时间流名称
     *
     * @return 时间流名称
     */
    public String getTimeFlowName();

    /**
     * 创建指定时间范围内的所有时间刻度实体并构建相应的时间流片段
     *
     * @param fromYear int 起始年度
     * @param toYear int 结束年度
     * @param createMinuteData boolean 是否创建分钟粒度的数据
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean createTimeSpanEntities(int fromYear, int toYear, boolean createMinuteData) throws CoreRealmServiceRuntimeException;

    /**
     * 创建指定年度内的所有时间刻度实体并构建相应的时间流片段
     *
     * @param targetYear int 指定目标年度
     * @param createMinuteData boolean 是否创建分钟粒度的数据
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean createTimeSpanEntities(int targetYear, boolean createMinuteData) throws CoreRealmServiceRuntimeException;

    /**
     * 获取当前时间流中已经包含的年度
     *
     * @return 已经包含的年度列表
     */
    public List<Integer> getAvailableTimeSpanYears();

    /**
     * 获取指定年度的时间刻度实体
     *
     * @param year int 指定目标年度
     *
     * @return 目标年度的时间刻度实体对象
     */
    public TimeScaleEntity getYearEntity(int year);

    /**
     * 获取指定年度区间的时间刻度实体
     *
     * @param fromYear int 起始年度
     * @param toYear int 结束年度
     *
     * @return 目标年度区间的时间刻度实体对象列表
     */
    public LinkedList<TimeScaleEntity> getYearEntities(int fromYear, int toYear) throws CoreRealmServiceRuntimeException;

    /**
     * 获取特定的若干年度的时间刻度实体
     *
     * @param year int... 目标年度数组
     *
     * @return 目标年度的时间刻度实体对象数组
     */
    public TimeScaleEntity[] getSpecificYearEntities(int... year);

    /**
     * 获取指定月度的时间刻度实体
     *
     * @param year int 指定目标年度
     * @param month int 指定目标月度
     *
     * @return 目标月度的时间刻度实体对象
     */
    public TimeScaleEntity getMonthEntity(int year,int month);

    /**
     * 获取指定月度区间的时间刻度实体
     *
     * @param fromMonthMoment TimeScaleMoment 起始月度
     * @param toMonthMoment TimeScaleMoment 结束月度
     *
     * @return 目标月度区间的时间刻度实体对象列表
     */
    public LinkedList<TimeScaleEntity> getMonthEntities(TimeScaleMoment fromMonthMoment, TimeScaleMoment toMonthMoment) throws CoreRealmServiceRuntimeException;

    /**
     * 获取特定的若干月度的时间刻度实体
     *
     * @param monthMoments TimeScaleMoment... 目标月度数组
     *
     * @return 目标月度的时间刻度实体对象数组
     */
    public TimeScaleEntity[] getSpecificMonthEntities(TimeScaleMoment... monthMoments);

    /**
     * 获取指定日的时间刻度实体
     *
     * @param year int 指定目标年度
     * @param month int 指定目标月度
     * @param day int 指定目标日
     *
     * @return 目标日的时间刻度实体对象
     */
    public TimeScaleEntity getDayEntity(int year,int month,int day);

    /**
     * 获取指定日区间的时间刻度实体
     *
     * @param fromDayMoment TimeScaleMoment 起始日
     * @param toDayMoment TimeScaleMoment 结束日
     *
     * @return 目标日区间的时间刻度实体对象列表
     */
    public LinkedList<TimeScaleEntity> getDayEntities(TimeScaleMoment fromDayMoment, TimeScaleMoment toDayMoment) throws CoreRealmServiceRuntimeException;

    /**
     * 获取特定的若干日的时间刻度实体
     *
     * @param dayMoments TimeScaleMoment... 目标日数组
     *
     * @return 目标日的时间刻度实体对象数组
     */
    public TimeScaleEntity[] getSpecificDayEntities(TimeScaleMoment... dayMoments);

    /**
     * 获取指定小时的时间刻度实体
     *
     * @param year int 指定目标年度
     * @param month int 指定目标月度
     * @param day int 指定目标日
     * @param hour int 指定目标小时
     *
     * @return 目标小时的时间刻度实体对象
     */
    public TimeScaleEntity getHourEntity(int year, int month,int day,int hour);

    /**
     * 获取指定小时区间的时间刻度实体
     *
     * @param fromHourMoment TimeScaleMoment 起始小时
     * @param toHourMoment TimeScaleMoment 结束小时
     *
     * @return 目标小时区间的时间刻度实体对象列表
     */
    public LinkedList<TimeScaleEntity> getHourEntities(TimeScaleMoment fromHourMoment, TimeScaleMoment toHourMoment) throws CoreRealmServiceRuntimeException;

    /**
     * 获取特定的若干小时的时间刻度实体
     *
     * @param hourMoments TimeScaleMoment... 目标小时数组
     *
     * @return 目标小时的时间刻度实体对象数组
     */
    public TimeScaleEntity[] getSpecificHourEntities(TimeScaleMoment... hourMoments);

    /**
     * 获取指定分钟的时间刻度实体
     *
     * @param year int 指定目标年度
     * @param month int 指定目标月度
     * @param day int 指定目标日
     * @param hour int 指定目标小时
     * @param minute int 指定目标分钟
     *
     * @return 目标分钟的时间刻度实体对象
     */
    public TimeScaleEntity getMinuteEntity(int year, int month,int day,int hour,int minute);

    /**
     * 获取指定分钟区间的时间刻度实体
     *
     * @param fromMinuteMoment TimeScaleMoment 起始分钟
     * @param toMinuteMoment TimeScaleMoment 结束分钟
     *
     * @return 目标分钟区间的时间刻度实体对象列表
     */
    public LinkedList<TimeScaleEntity> getMinuteEntities(TimeScaleMoment fromMinuteMoment, TimeScaleMoment toMinuteMoment) throws CoreRealmServiceRuntimeException;

    /**
     * 获取特定的若干分钟的时间刻度实体
     *
     * @param minuteMoments TimeScaleMoment... 目标分钟数组
     *
     * @return 目标分钟的时间刻度实体对象数组
     */
    public TimeScaleEntity[] getSpecificMinuteEntities(TimeScaleMoment... minuteMoments);

    /**
     * 获取指定秒的时间刻度实体（该方法功能尚未实现）
     *
     * @param year int 指定目标年度
     * @param month int 指定目标月度
     * @param day int 指定目标日
     * @param hour int 指定目标小时
     * @param minute int 指定目标分钟
     * @param minute int 指定目标秒
     *
     * @return 目标秒的时间刻度实体对象
     */
    public TimeScaleEntity getSecondEntity(int year, int month,int day,int hour,int minute,int second);

    /**
     * 获取指定秒区间的时间刻度实体（该方法功能尚未实现）
     *
     * @param fromSecondMoment TimeScaleMoment 起始秒
     * @param toSecondMoment TimeScaleMoment 结束秒
     *
     * @return 目标秒区间的时间刻度实体对象列表
     */
    public LinkedList<TimeScaleEntity> getSecondEntities(TimeScaleMoment fromSecondMoment, TimeScaleMoment toSecondMoment) throws CoreRealmServiceRuntimeException;

    /**
     * 获取特定的若干秒的时间刻度实体（该方法功能尚未实现）
     *
     * @param secondMoments TimeScaleMoment... 目标秒数组
     *
     * @return 目标秒的时间刻度实体对象数组
     */
    public TimeScaleEntity[] getSpecificSecondEntities(TimeScaleMoment... secondMoments);

    /**
     * 获取指定时间点的所有下一级时间刻度实体
     *
     * @param timeScaleMoment TimeScaleMoment 目标时间点
     * @param timeScaleGrade TimeScaleGrade 目标时间刻度等级
     *
     * @return 所有下一级时间刻度实体对象列表
     */
    public LinkedList<TimeScaleEntity> getChildEntities(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade);

    /**
     * 获取指定时间点的所有同级时间刻度实体
     *
     * @param timeScaleMoment TimeScaleMoment 目标时间点
     * @param timeScaleGrade TimeScaleGrade 目标时间刻度等级
     *
     * @return 所有同级时间刻度实体对象列表
     */
    public LinkedList<TimeScaleEntity> getFellowEntities(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade);

    /**
     * 遍历获取指定时间点的所有子集时间刻度实体
     *
     * @param timeScaleMoment TimeScaleMoment 目标时间点
     * @param timeScaleGrade TimeScaleGrade 目标时间刻度等级
     *
     * @return 所有子集时间刻度实体对象的继承数
     */
    public InheritanceTree<TimeScaleEntity> getOffspringEntities(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade);

    /**
     * 删除当前时间流涉及的全部时间刻度事件
     * @return 删除的时间刻度事件数量
     */
    public long removeRefersTimeScaleEvents();

    /**
     * 根据唯一ID获取当前时间流的时间刻度实体对象
     *
     * @param timeScaleEntityUID String 需要获取的时间刻度实体对象唯一ID
     *
     * @return 时间刻度实体对象
     */
    public TimeScaleEntity getTimeScaleEntityByUID(String timeScaleEntityUID);

    /**
     * 获取当前时间流相关的各类数据的实时统计信息
     *
     * @return 时间流相关数据实时统计信息
     */
    public TimeFlowRuntimeStatistics getTimeFlowRuntimeStatistics();
}
