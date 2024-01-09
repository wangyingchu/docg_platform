package com.viewfunction.docg.dataCompute.computeServiceCore.internal.ignite.util;

import org.apache.ignite.*;
import org.apache.ignite.configuration.CollectionConfiguration;

import java.util.UUID;

import static org.apache.ignite.cache.CacheMode.PARTITIONED;

public class UnitIgniteFeatureHandler {
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

    public IgniteAtomicReference createAtomicReference(Object referenceValue){
        String atomicName = UUID.randomUUID().toString();
        return this.handlerIgnite.atomicReference(atomicName,referenceValue,true);
    }

    public IgniteAtomicReference getExistAtomicReference(String atomicName){
        return this.handlerIgnite.atomicReference(atomicName,null,false);
    }

    public IgniteAtomicSequence createAtomicSequence(long initVal){
        String atomicName = UUID.randomUUID().toString();
        return this.handlerIgnite.atomicSequence(atomicName, initVal, true);
    }

    public IgniteAtomicSequence getExistAtomicSequence(String atomicName){
        return this.handlerIgnite.atomicSequence(atomicName, 0, false);
    }

    public IgniteAtomicStamped createAtomicStamped(Object initVal, Object initStamp){
        String atomicName = UUID.randomUUID().toString();
        return this.handlerIgnite.atomicStamped(atomicName,initVal,initStamp,true);
    }

    public IgniteAtomicStamped getExistAtomicStamped(String atomicName){
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

    public IgniteQueue createUnlimitedQueue(){
        String queueName = UUID.randomUUID().toString();
        CollectionConfiguration colCfg = new CollectionConfiguration();
        colCfg.setCollocated(false);
        colCfg.setCacheMode(PARTITIONED);
        colCfg.setBackups(1);
        return this.handlerIgnite.queue(queueName, 0, colCfg);
    }

    public IgniteQueue createFixedSizeQueue(int queueSize){
        String queueName = UUID.randomUUID().toString();
        CollectionConfiguration colCfg = new CollectionConfiguration();
        colCfg.setCollocated(false);
        colCfg.setCacheMode(PARTITIONED);
        colCfg.setBackups(1);
        return this.handlerIgnite.queue(queueName, queueSize, colCfg);
    }

    public IgniteQueue getExistQueue(String queueName){
        return this.handlerIgnite.queue(queueName, 0, null);
    }

    public IgniteSet createSet(){
        String setName = UUID.randomUUID().toString();
        CollectionConfiguration colCfg = new CollectionConfiguration();
        colCfg.setCollocated(false);
        colCfg.setCacheMode(PARTITIONED);
        colCfg.setBackups(1);
        return this.handlerIgnite.set(setName,colCfg);
    }

    public IgniteSet getExistSet(String setName){
        return this.handlerIgnite.set(setName,null);
    }
}
