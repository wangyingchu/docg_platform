package com.viewfunction.docg.dataCompute.dataComputeServiceCore.internal.ignite.util;

import org.apache.ignite.*;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static org.apache.ignite.cache.CacheMode.PARTITIONED;

public class UnitIgniteFeatureHandler<T,S> {

    private Ignite handlerIgnite;

    public UnitIgniteFeatureHandler(Ignite handlerIgnite){
        this.handlerIgnite = handlerIgnite;
    }

    public IgniteAtomicLong createAtomicLong(long initVal){
        String atomicName = UUID.randomUUID().toString();
        return this.handlerIgnite.atomicLong(atomicName, initVal, true);
    }

    public IgniteAtomicLong getExistAtomicLong(String atomicName){
        return this.handlerIgnite.atomicLong(atomicName, 0, false);
    }

    public IgniteAtomicReference<T> createAtomicReference(@Nullable T referenceValue){
        String atomicName = UUID.randomUUID().toString();
        return this.handlerIgnite.atomicReference(atomicName,referenceValue,true);
    }

    public IgniteAtomicReference<T> getExistAtomicReference(String atomicName){
        return this.handlerIgnite.atomicReference(atomicName,null,false);
    }

    public IgniteAtomicSequence createAtomicSequence(long initVal){
        String atomicName = UUID.randomUUID().toString();
        return this.handlerIgnite.atomicSequence(atomicName, initVal, true);
    }

    public IgniteAtomicSequence getExistAtomicSequence(String atomicName){
        return this.handlerIgnite.atomicSequence(atomicName, 0, false);
    }

    public IgniteAtomicStamped<T, S> createAtomicStamped(@Nullable T initVal, @Nullable S initStamp){
        String atomicName = UUID.randomUUID().toString();
        return this.handlerIgnite.atomicStamped(atomicName,initVal,initStamp,true);
    }

    public IgniteAtomicStamped<T, S> getExistAtomicStamped(String atomicName){
        return this.handlerIgnite.atomicStamped(atomicName,null,null,false);
    }

    public IgniteCountDownLatch createCountDownLatch(int count,boolean autoDelete){
        String latchName = UUID.randomUUID().toString();
        return this.handlerIgnite.countDownLatch(latchName, count, autoDelete, true);
    }

    public IgniteCountDownLatch getExistCountDownLatch(String latchName){
        return this.handlerIgnite.countDownLatch(latchName, 0, false, false);
    }

    public IgniteSemaphore createSemaphore(int count,boolean failoverSafe){
        String semaphoreName = UUID.randomUUID().toString();
        return this.handlerIgnite.semaphore(semaphoreName,count,failoverSafe,true);
    }

    public IgniteSemaphore createSemaphore(String semaphoreName){
        return this.handlerIgnite.semaphore(semaphoreName,0,false,false);
    }

    public IgniteQueue<T> createUnlimitedQueue(){
        String queueName = UUID.randomUUID().toString();
        CollectionConfiguration colCfg = new CollectionConfiguration();
        colCfg.setCollocated(false);
        colCfg.setCacheMode(PARTITIONED);
        colCfg.setBackups(1);
        return this.handlerIgnite.queue(queueName, 0, colCfg);
    }

    public IgniteQueue<T> createFixedSizeQueue(int queueSize){
        String queueName = UUID.randomUUID().toString();
        CollectionConfiguration colCfg = new CollectionConfiguration();
        colCfg.setCollocated(false);
        colCfg.setCacheMode(PARTITIONED);
        colCfg.setBackups(1);
        return this.handlerIgnite.queue(queueName, queueSize, colCfg);
    }

    public IgniteQueue<T> getExistQueue(String queueName){
        return this.handlerIgnite.queue(queueName, 0, null);
    }

    public IgniteSet<T> createSet(){
        String setName = UUID.randomUUID().toString();
        CollectionConfiguration colCfg = new CollectionConfiguration();
        colCfg.setCollocated(false);
        colCfg.setCacheMode(PARTITIONED);
        colCfg.setBackups(1);
        return this.handlerIgnite.set(setName,colCfg);
    }

    public IgniteSet<T> getExistSet(String setName){
        return this.handlerIgnite.set(setName,null);
    }
}
