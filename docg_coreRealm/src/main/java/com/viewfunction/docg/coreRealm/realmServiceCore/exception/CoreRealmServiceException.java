package com.viewfunction.docg.coreRealm.realmServiceCore.exception;

public class CoreRealmServiceException extends Exception{

    public void setCauseMessage(String message){
        Throwable throwable=new Throwable("[ "+ message + " ]");
        this.initCause(throwable);
    }
}
