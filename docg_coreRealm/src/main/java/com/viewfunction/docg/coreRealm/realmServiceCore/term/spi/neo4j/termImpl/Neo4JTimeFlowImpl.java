package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.TimeScaleOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.TimeScaleMoment;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class Neo4JTimeFlowImpl implements TimeFlow {

    private static Logger logger = LoggerFactory.getLogger(Neo4JTimeFlowImpl.class);
    private String coreRealmName;
    private String timeFlowName;
    private String timeFlowUID;

    public Neo4JTimeFlowImpl(String coreRealmName, String timeFlowName,String timeFlowUID){
        this.coreRealmName = coreRealmName;
        this.timeFlowName = timeFlowName;
        this.timeFlowUID = timeFlowUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public String getTimeFlowUID() {
        return this.timeFlowUID;
    }

    @Override
    public String getTimeFlowName() {
        return this.timeFlowName;
    }

    @Override
    public void createTimeSpanEntities(int fromYear, int toYear) throws CoreRealmServiceRuntimeException {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            TimeScaleOperationUtil.generateTimeFlowScaleEntities(workingGraphOperationExecutor,RealmConstant._defaultTimeFlowName,fromYear,toYear);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public void createTimeSpanEntities(int targetYear) throws CoreRealmServiceRuntimeException {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            TimeScaleOperationUtil.generateTimeFlowScaleEntities(workingGraphOperationExecutor, RealmConstant._defaultTimeFlowName,targetYear);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public LinkedList<Integer> getAvailableTimeSpanYears() {
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

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
