package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleMoment;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.util.LinkedList;
import java.util.List;

public interface TimeFlow {

    public enum TimeScaleGrade {YEAR,MONTH,DAY,HOUR,MINUTE,SECOND}

    public String getTimeFlowName();

    public boolean createTimeSpanEntities(int fromYear, int toYear) throws CoreRealmServiceRuntimeException;
    public boolean createTimeSpanEntities(int targetYear) throws CoreRealmServiceRuntimeException;

    public List<Integer> getAvailableTimeSpanYears();

    public TimeScaleEntity getYearEntity(int year);
    public LinkedList<TimeScaleEntity> getYearEntities(int fromYear, int toYear) throws CoreRealmServiceRuntimeException;
    public TimeScaleEntity[] getSpecificYearEntities(int... year);

    public TimeScaleEntity getMonthEntity(int year,int month);
    public LinkedList<TimeScaleEntity> getMonthEntities(TimeScaleMoment fromMonthMoment, TimeScaleMoment toMonthMoment) throws CoreRealmServiceRuntimeException;
    public TimeScaleEntity[] getSpecificMonthEntities(TimeScaleMoment... monthMoments);

    public TimeScaleEntity getDayEntity(int year,int month,int day);
    public LinkedList<TimeScaleEntity> getDayEntities(TimeScaleMoment fromDayMoment, TimeScaleMoment toDayMoment) throws CoreRealmServiceRuntimeException;
    public TimeScaleEntity[] getSpecificDayEntities(TimeScaleMoment... dayMoments);

    public TimeScaleEntity getHourEntity(int year, int month,int day,int hour);
    public LinkedList<TimeScaleEntity> getHourEntities(TimeScaleMoment fromHourMoment, TimeScaleMoment toHourMoment) throws CoreRealmServiceRuntimeException;
    public TimeScaleEntity[] getSpecificHourEntities(TimeScaleMoment... hourMoments);

    public TimeScaleEntity getMinuteEntity(int year, int month,int day,int hour,int minute);
    public LinkedList<TimeScaleEntity> getMinuteEntities(TimeScaleMoment fromMinuteMoment, TimeScaleMoment toMinuteMoment) throws CoreRealmServiceRuntimeException;
    public TimeScaleEntity[] getSpecificMinuteEntities(TimeScaleMoment... minuteMoments);

    public TimeScaleEntity getSecondEntity(int year, int month,int day,int hour,int minute,int second);
    public LinkedList<TimeScaleEntity> getSecondEntities(TimeScaleMoment fromSecondMoment, TimeScaleMoment toSecondMoment) throws CoreRealmServiceRuntimeException;
    public TimeScaleEntity[] getSpecificSecondEntities(TimeScaleMoment... secondMoments);

    public LinkedList<TimeScaleEntity> getChildEntities(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade);
    public LinkedList<TimeScaleEntity> getFellowEntities(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade);

    public InheritanceTree<TimeScaleEntity> getOffspringEntities(TimeScaleMoment timeScaleMoment,TimeScaleGrade timeScaleGrade);
}
