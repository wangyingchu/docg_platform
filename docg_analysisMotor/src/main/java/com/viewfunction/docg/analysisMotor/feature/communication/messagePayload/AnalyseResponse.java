package com.viewfunction.docg.analysisMotor.feature.communication.messagePayload;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class AnalyseResponse implements Serializable {

    private String requestUUID;
    private String responseUUID;
    private long responseDateTime;

    public AnalyseResponse(String requestUUID){
        this.requestUUID = requestUUID;
    }

    public String getRequestUUID(){
        return requestUUID;
    }

    public String getResponseUUID(){
        return responseUUID;
    }

    public long getResponseDateTime(){
        return responseDateTime;
    }

    public boolean generateMetaInfo(){
        if(this.responseUUID == null){
            this.responseUUID = UUID.randomUUID().toString().replaceAll("-","");
            responseDateTime = new Date().getTime() ;
            return true;
        }else{
            return false;
        }
    }
}
