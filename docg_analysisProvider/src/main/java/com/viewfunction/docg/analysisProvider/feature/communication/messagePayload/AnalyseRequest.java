package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload;

import java.util.Date;
import java.util.UUID;

public class AnalyseRequest{

    private String uuid;
    private long requestDateTime;

    public String getRequestUUID(){
        return this.uuid;
    }
    public long getRequestDateTime(){
        return this.requestDateTime;
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
}
