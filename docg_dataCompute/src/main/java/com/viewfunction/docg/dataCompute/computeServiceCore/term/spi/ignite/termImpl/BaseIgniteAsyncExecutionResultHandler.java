package com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termImpl;

import com.viewfunction.docg.dataCompute.computeServiceCore.term.spi.ignite.termInf.IgniteAsyncExecutionResultHandler;
import org.apache.ignite.Ignite;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.ServiceContextResource;
import org.apache.ignite.services.ServiceContext;

import java.util.concurrent.TimeUnit;

public class BaseIgniteAsyncExecutionResultHandler<V> implements IgniteAsyncExecutionResultHandler<V> {

    /** Ignite instance. */
    @IgniteInstanceResource
    private Ignite ignite;

    /** Service context. */
    @ServiceContextResource
    private ServiceContext ctx;

    @Override
    public boolean cancelAsyncExecution() {
        return false;
    }

    @Override
    public boolean isExecutionCanceled() {
        return false;
    }

    @Override
    public boolean isExecutionFinished() {
        return false;
    }

    @Override
    public V syncWaitForExecutionResult() {
        return null;
    }

    @Override
    public V waitForExecutionResult(long timeout, TimeUnit unit) {
        return null;
    }

    @Override
    public void setExecutionResultListener() {

    }
}
