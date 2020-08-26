package com.viewfunction.docg.coreRealm.realmServiceCore.util.cache;

public interface ResourceCacheQueryLogic <K,V> {
    public boolean filterLogic(K _kValue, V _VValue);
}
