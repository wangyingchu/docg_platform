package com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.exception;

public class MemoryTableQueryStructureException extends Exception{

    public void setCauseMessage(String message){
        Throwable throwable=new Throwable("[ "+ message + " ]");
        this.initCause(throwable);
    }
}
