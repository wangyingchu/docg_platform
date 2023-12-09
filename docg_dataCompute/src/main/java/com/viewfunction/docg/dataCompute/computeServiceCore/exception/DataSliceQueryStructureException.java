package com.viewfunction.docg.dataCompute.computeServiceCore.exception;

public class DataSliceQueryStructureException extends Exception{

    public void setCauseMessage(String message){
        Throwable throwable=new Throwable("[ "+ message + " ]");
        this.initCause(throwable);
    }
}
