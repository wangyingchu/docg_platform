package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;

public class DynamicContentUnionQueryResult {

    private Date startTime;
    private Date finishTime;

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
}
