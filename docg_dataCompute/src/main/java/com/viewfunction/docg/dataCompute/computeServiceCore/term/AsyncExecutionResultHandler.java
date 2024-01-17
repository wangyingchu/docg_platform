package com.viewfunction.docg.dataCompute.computeServiceCore.term;

import java.util.concurrent.TimeUnit;

public interface AsyncExecutionResultHandler<V> {

    public boolean cancelAsyncExecution();

    public boolean isExecutionCanceled();

    public boolean isExecutionFinished();

    public V syncWaitForExecutionResult();

    public V waitForExecutionResult(long timeout, TimeUnit unit);

    public void setExecutionResultListener();


}
