package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataSliceQueryResult {

    private Date startTime;
    private Date finishTime;
    private String operationSummary;
    private List<Map<String,Object>> resultRecords;
    private String queryLogic;

    public DataSliceQueryResult(){
        this.startTime = new Date();
        this.resultRecords = new ArrayList<>();
    }

    public void finishOperation() {
        this.finishTime = new Date();
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public String getOperationSummary() {
        return operationSummary;
    }

    public void setOperationSummary(String operationSummary) {
        this.operationSummary = operationSummary;
    }

    public List<Map<String, Object>> getResultRecords() {
        return resultRecords;
    }

    public String getQueryLogic() {
        return queryLogic;
    }

    public void setQueryLogic(String queryLogic) {
        this.queryLogic = queryLogic;
    }
}
