package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;

public interface MultiConceptionKindsSupportable {

    public boolean joinConceptionKinds(String[] newKindNames) throws CoreRealmServiceRuntimeException;
    public boolean retreatFromConceptionKind(String kindName) throws CoreRealmServiceRuntimeException;

}

