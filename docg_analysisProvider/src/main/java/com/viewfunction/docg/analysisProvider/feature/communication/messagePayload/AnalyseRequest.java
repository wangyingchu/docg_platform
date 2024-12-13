package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class AnalyseRequest{

    public enum ResponseDataForm {STREAM_BACK,DATA_SLICE,CONCEPTION_KIND}
    private String uuid;
    private long requestDateTime;
    private ResponseDataForm responseDataForm;
    private Object requestParameters;

    public String getRequestUUID(){
        return this.uuid;
    }
    public long getRequestDateTime(){
        return this.requestDateTime;
    }

    public AnalyseRequest(){
        this.responseDataForm = ResponseDataForm.STREAM_BACK;
    }

    public boolean generateMetaInfo(){
        if(this.uuid == null){
            this.uuid = UUID.randomUUID().toString().replaceAll("-","");
            requestDateTime = new Date().getTime() ;
            return true;
        }else{
            return false;
        }
    }

    public ResponseDataForm getResponseDataForm() {
        return responseDataForm;
    }

    public void setResponseDataForm(ResponseDataForm responseDataForm) {
        this.responseDataForm = responseDataForm;
    }

    public Object getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(HashMap<String, Object> requestParameters) {
        this.requestParameters = requestParameters;
    }
}
