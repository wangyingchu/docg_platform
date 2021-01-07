package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleMoment;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;

import java.util.LinkedList;

public class Neo4JTimeFlowImpl implements TimeFlow {

    @Override
    public String getTimeFlowName() {
        return null;
    }

    @Override
    public TimeScaleEntity getYearEntity(int year) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getYearEntities(int fromYear, int toYear) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getYearEntities(int... year) {
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getMonthEntity(int year, int month) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getMonthEntities(TimeScaleMoment fromMonthMoment, TimeScaleMoment toMonthMoment) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getMonthEntities(TimeScaleMoment... monthMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getDayEntity(int year, int month, int day) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getDayEntities(TimeScaleMoment fromDayMoment, TimeScaleMoment toDayMoment) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getDayEntities(TimeScaleMoment... dayMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getHourEntity(int year, int month, int day, int hour) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getHourEntities(TimeScaleMoment fromHourMoment, TimeScaleMoment toHourMoment) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getHourEntities(TimeScaleMoment... hourMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getMinuteEntity(int year, int month, int day, int hour, int minute) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getMinuteEntities(TimeScaleMoment fromMinuteMoment, TimeScaleMoment toMinuteMoment) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getMinuteEntities(TimeScaleMoment... minuteMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public TimeScaleEntity getSecondEntity(int year, int month, int day, int hour, int minute, int second) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getSecondEntities(TimeScaleMoment fromSecondMoment, TimeScaleMoment toSecondMoment) {
        return null;
    }

    @Override
    public TimeScaleEntity[] getSecondEntities(TimeScaleMoment... secondMoments) {
        return new TimeScaleEntity[0];
    }

    @Override
    public LinkedList<TimeScaleEntity> getChildEntities(TimeScaleMoment timeScaleMoments) {
        return null;
    }

    @Override
    public LinkedList<TimeScaleEntity> getFellowEntities(TimeScaleMoment timeScaleMoments) {
        return null;
    }

    @Override
    public TimeScaleEntity getFirstChildEntity(TimeScaleMoment timeScaleMoments) {
        return null;
    }

    @Override
    public TimeScaleEntity getLastChildEntity(TimeScaleMoment timeScaleMoments) {
        return null;
    }

    @Override
    public InheritanceTree<TimeScaleEntity> getOffspringEntities(TimeScaleMoment timeScaleMoments) {
        return null;
    }
}
