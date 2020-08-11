package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;

public interface CoreRealm {
    public CoreRealmStorageImplTech getStorageImplTech();
    public ConceptionKind getConceptionKind(String conceptionKindName);
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc);
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc,String parentConceptionKindName)
            throws CoreRealmFunctionNotSupportedException;

    //public ConceptionKind removeConceptionKind(String conceptionKindName,boolean checkEntityExistence); ?





}
