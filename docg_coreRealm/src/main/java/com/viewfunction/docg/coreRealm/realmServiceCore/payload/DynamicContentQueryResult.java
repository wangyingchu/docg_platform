package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DynamicContentQueryResult {

    private String adhocQuerySentence;
    private Date startTime;
    private Date finishTime;
    private long dynamicContentValuesCount;
    private List<Map<String,DynamicContentValue>> dynamicContentResultValueList;
    private Map<String,DynamicContentValue.ContentValueType> dynamicContentAttributesValueTypeMap;

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

    public Map<String, DynamicContentValue.ContentValueType> getDynamicContentAttributesValueTypeMap() {
        return dynamicContentAttributesValueTypeMap;
    }

    public void setDynamicContentAttributesValueTypeMap(Map<String, DynamicContentValue.ContentValueType> dynamicContentAttributesValueTypeMap) {
        this.dynamicContentAttributesValueTypeMap = dynamicContentAttributesValueTypeMap;
    }

    public List<Map<String, DynamicContentValue>> getDynamicContentResultValueList() {
        return dynamicContentResultValueList;
    }

    public void setDynamicContentResultValueList(List<Map<String, DynamicContentValue>> dynamicContentResultValueList) {
        this.dynamicContentResultValueList = dynamicContentResultValueList;
    }
}
