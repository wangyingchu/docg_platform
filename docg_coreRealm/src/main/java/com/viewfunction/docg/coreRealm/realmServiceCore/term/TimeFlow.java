package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleMoment;

import java.util.LinkedList;

public interface TimeFlow {

    public enum TimeScaleGrade {YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,WEEK}

    public String getTimeFlowName();

    public TimeScaleEntity getYearEntity(int year);
    public LinkedList<TimeScaleEntity> getYearEntities(int fromYear, int toYear);
    public TimeScaleEntity[] getYearEntities(int... year);

    public TimeScaleEntity getMonthEntity(int year,int month);
    public LinkedList<TimeScaleEntity> getMonthEntities(TimeScaleMoment fromMonthMoment, TimeScaleMoment toMonthMoment);
    public TimeScaleEntity[] getMonthEntities(TimeScaleMoment... monthMoments);

    public TimeScaleEntity getDayEntity(int year,int month,int day);
    public LinkedList<TimeScaleEntity> getDayEntities(TimeScaleMoment fromDayMoment, TimeScaleMoment toDayMoment);
    public TimeScaleEntity[] getDayEntities(TimeScaleMoment... dayMoments);

    public TimeScaleEntity getHourEntity(int year, int month,int day,int hour);
    public LinkedList<TimeScaleEntity> getHourEntities(TimeScaleMoment fromHourMoment, TimeScaleMoment toHourMoment);
    public TimeScaleEntity[] getHourEntities(TimeScaleMoment... hourMoments);

    public TimeScaleEntity getMinuteEntity(int year, int month,int day,int hour,int minute);
    public LinkedList<TimeScaleEntity> getMinuteEntities(TimeScaleMoment fromMinuteMoment, TimeScaleMoment toMinuteMoment);
    public TimeScaleEntity[] getMinuteEntities(TimeScaleMoment... minuteMoments);

    public TimeScaleEntity getSecondEntity(int year, int month,int day,int hour,int minute,int second);
    public LinkedList<TimeScaleEntity> getSecondEntities(TimeScaleMoment fromSecondMoment, TimeScaleMoment toSecondMoment);
    public TimeScaleEntity[] getSecondEntities(TimeScaleMoment... secondMoments);
}