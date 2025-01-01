package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConceptionEntityExternalAttributesValueRetrieveResult {

    private QueryParameters queryParameters;
    private Date startTime;
    private Date finishTime;
    private long resultRowsCount;
    private String conceptionEntityUID;
    private AttributesViewKind attributesViewKind;
    private List<Map<String,Object>> externalAttributesValue;

    public ConceptionEntityExternalAttributesValueRetrieveResult(QueryParameters queryParameters, Date startTime, Date finishTime, long resultRowsCount, String conceptionEntityUID, AttributesViewKind attributesViewKind, List<Map<String,Object>> externalAttributesValue) {
        this.setQueryParameters(queryParameters);
        this.setStartTime(startTime);
        this.setFinishTime(finishTime);
        this.setResultRowsCount(resultRowsCount);
        this.setConceptionEntityUID(conceptionEntityUID);
        this.setAttributesViewKind(attributesViewKind);
        this.setExternalAttributesValue(externalAttributesValue);
    }

    public ConceptionEntityExternalAttributesValueRetrieveResult(){}

    public QueryParameters getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(QueryParameters queryParameters) {
        this.queryParameters = queryParameters;
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

    public long getResultRowsCount() {
        return resultRowsCount;
    }

    public void setResultRowsCount(long resultRowsCount) {
        this.resultRowsCount = resultRowsCount;
    }

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public void setConceptionEntityUID(String conceptionEntityUID) {
        this.conceptionEntityUID = conceptionEntityUID;
    }

    public AttributesViewKind getAttributesViewKind() {
        return attributesViewKind;
    }

    public void setAttributesViewKind(AttributesViewKind attributesViewKind) {
        this.attributesViewKind = attributesViewKind;
    }

    public List<Map<String, Object>> getExternalAttributesValue() {
        return externalAttributesValue;
    }

    public void setExternalAttributesValue(List<Map<String, Object>> externalAttributesValue) {
        this.externalAttributesValue = externalAttributesValue;
    }
}
