package com.viewfunction.docg.coreRealm.realmServiceCore.term;

public interface TimeFlow {

    public enum TimeScaleGrade {YEAR,MONTH,DAY,HOUR,MINUTE,SECOND,WEEK}

    public String getTimeFlowName();

    public TimeScaleEntity getYearEntity(int year);
    public TimeScaleEntity getYearEntities(int fromYear,int toYear);
    public TimeScaleEntity[] getYearEntities(int... year);

    public TimeScaleEntity getMonthEntity(int year,int month);
    public TimeScaleEntity getDayEntity(int year,int month,int day);
    public TimeScaleEntity getHourEntity(int year, int month,int day,int hour);
    public TimeScaleEntity getMinuteEntity(int year, int month,int day,int hour,int minute);
    public TimeScaleEntity getSecondEntity(int year, int month,int day,int hour,int minute,int second);
}
