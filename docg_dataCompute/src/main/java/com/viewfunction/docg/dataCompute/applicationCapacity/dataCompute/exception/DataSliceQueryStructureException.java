package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.exception;

public class DataSliceQueryStructureException extends Exception{

    public void setCauseMessage(String message){
        Throwable throwable=new Throwable("[ "+ message + " ]");
        this.initCause(throwable);
    }
}