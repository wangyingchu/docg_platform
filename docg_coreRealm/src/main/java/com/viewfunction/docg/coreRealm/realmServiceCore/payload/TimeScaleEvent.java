package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;

import java.util.Map;

public class TimeScaleEvent {

    private String timeFlowName;
    private long referTime;
    private Map<String, Object> eventData;
    private String eventComment;
    private TimeFlow.TimeScaleGrade timeScaleGrade;
    private String timeScaleEventUID;

    public String getTimeFlowName() {
        return timeFlowName;
    }

    public void setTimeFlowName(String timeFlowName) {
        this.timeFlowName = timeFlowName;
    }

    public long getReferTime() {
        return referTime;
    }

    public void setReferTime(long referTime) {
        this.referTime = referTime;
    }

    public Map<String, Object> getEventData() {
        return eventData;
    }

    public void setEventData(Map<String, Object> eventData) {
        this.eventData = eventData;
    }

    public TimeFlow.TimeScaleGrade getTimeScaleGrade() {
        return timeScaleGrade;
    }

    public void setTimeScaleGrade(TimeFlow.TimeScaleGrade timeScaleGrade) {
        this.timeScaleGrade = timeScaleGrade;
    }

    public String getTimeScaleEventUID() {
        return timeScaleEventUID;
    }

    public void setTimeScaleEventUID(String timeScaleEventUID) {
        this.timeScaleEventUID = timeScaleEventUID;
    }

    public String getEventComment() {
        return eventComment;
    }

    public void setEventComment(String eventComment) {
        this.eventComment = eventComment;
    }
}
