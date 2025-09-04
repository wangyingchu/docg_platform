package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;
import java.util.List;

public class DynamicContentQueryResult {

    private String adhocQuerySentence;
    private Date startTime;
    private Date finishTime;
    private long dynamicContentValuesCount;
    private List<DynamicContentValue> dynamicContentValueList;

    public String getAdhocQuerySentence() {
        return adhocQuerySentence;
    }

    public void setAdhocQuerySentence(String adhocQuerySentence) {
        this.adhocQuerySentence = adhocQuerySentence;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public long getDynamicContentValuesCount() {
        return dynamicContentValuesCount;
    }

    public void setDynamicContentValuesCount(long dynamicContentValuesCount) {
        this.dynamicContentValuesCount = dynamicContentValuesCount;
    }

    public List<DynamicContentValue> getDynamicContentValueList() {
        return dynamicContentValueList;
    }

    public void setDynamicContentValueList(List<DynamicContentValue> dynamicContentValueList) {
        this.dynamicContentValueList = dynamicContentValueList;
    }
}
