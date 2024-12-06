package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class AnalyseResponse implements Serializable {

    private String requestUUID;
    private String responseUUID;
    private Object responseData;
    private AnalyseRequest.ResponseDataForm responseDataForm;
    private Date responseDateTime;

    public AnalyseResponse(String requestUUID){
        this.requestUUID = requestUUID;
        this.responseUUID = "R"+UUID.randomUUID().toString().replaceAll("-","");
    }

    public String getRequestUUID(){
        return requestUUID;
    }

    public String getResponseUUID(){
        return responseUUID;
    }

    public Object getResponseData() {
        return responseData;
    }

    public void setResponseData(Object responseData) {
        this.responseData = responseData;
    }

    public AnalyseRequest.ResponseDataForm getResponseDataForm() {
        return responseDataForm;
    }

    public void setResponseDataForm(AnalyseRequest.ResponseDataForm responseDataForm) {
        this.responseDataForm = responseDataForm;
    }

    public Date getResponseDateTime() {
        return responseDateTime;
    }

    public void setResponseDateTime(Date responseDateTime) {
        this.responseDateTime = responseDateTime;
    }
}
