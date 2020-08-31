package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;

import java.util.Date;

public class EntitiesRetrieveStatistics {

    private QueryParameters queryParameters;
    private Date startTime;
    private Date finishTime;
    private long resultEntitiesCount;

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

    public long getResultEntitiesCount() {
        return resultEntitiesCount;
    }

    public void setResultEntitiesCount(long resultEntitiesCount) {
        this.resultEntitiesCount = resultEntitiesCount;
    }
}
