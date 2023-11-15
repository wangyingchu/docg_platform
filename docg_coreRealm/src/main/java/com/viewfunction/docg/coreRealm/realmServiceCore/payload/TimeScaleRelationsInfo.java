package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.TimeFlow;

import java.time.LocalDateTime;

public class TimeScaleRelationsInfo {
    private String timeScaleEntityUID;
    private String timeScaleEventUID;
    private TimeFlow.TimeScaleGrade timeScaleGrade;
    private LocalDateTime referTime;
    private String conceptionEntityUID;
    private String conceptionKindName;
    private String timeReferToRelationEntityUID;
    private String attachToTimeScaleRelationEntityUID;
    private String timeScaleEventComment;

    public TimeScaleRelationsInfo(String timeScaleEntityUID,String timeReferToRelationEntityUID,String timeScaleEventUID,
                                  String attachToTimeScaleRelationEntityUID,String conceptionEntityUID,String conceptionKindName,
                                  TimeFlow.TimeScaleGrade timeScaleGrade,LocalDateTime referTime,String timeScaleEventComment){
        this.timeScaleEntityUID = timeScaleEntityUID;
        this.timeReferToRelationEntityUID = timeReferToRelationEntityUID;
        this.timeScaleEventUID = timeScaleEventUID;
        this.attachToTimeScaleRelationEntityUID = attachToTimeScaleRelationEntityUID;
        this.conceptionEntityUID = conceptionEntityUID;
        this.conceptionKindName = conceptionKindName;
        this.timeScaleGrade = timeScaleGrade;
        this.referTime = referTime;
        this.timeScaleEventComment = timeScaleEventComment;
    }

    public String getTimeScaleEntityUID() {
        return timeScaleEntityUID;
    }

    public String getTimeScaleEventUID() {
        return timeScaleEventUID;
    }

    public TimeFlow.TimeScaleGrade getTimeScaleGrade() {
        return timeScaleGrade;
    }

    public LocalDateTime getReferTime() {
        return referTime;
    }

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public String getConceptionKindName() {
        return conceptionKindName;
    }

    public String getTimeReferToRelationEntityUID() {
        return timeReferToRelationEntityUID;
    }

    public String getAttachToTimeScaleRelationEntityUID() {
        return attachToTimeScaleRelationEntityUID;
    }

    public String getTimeScaleEventComment() {
        return timeScaleEventComment;
    }
}
