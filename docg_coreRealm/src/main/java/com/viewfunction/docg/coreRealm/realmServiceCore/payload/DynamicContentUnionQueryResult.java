package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class DynamicContentUnionQueryResult {

    private Date startTime;
    private Date finishTime;

    private Map<String, List<ConceptionEntityValue>> conceptionKindsEntityValueMap;
    private Map<String, List<RelationEntityValue>> relationKindsEntityValueMap;




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

    public Map<String, List<ConceptionEntityValue>> getConceptionKindsEntityValueMap() {
        return conceptionKindsEntityValueMap;
    }

    public void setConceptionKindsEntityValueMap(Map<String, List<ConceptionEntityValue>> conceptionKindsEntityValueMap) {
        this.conceptionKindsEntityValueMap = conceptionKindsEntityValueMap;
    }
}
