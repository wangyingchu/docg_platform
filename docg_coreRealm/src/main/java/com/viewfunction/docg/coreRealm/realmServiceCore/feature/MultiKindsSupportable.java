package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;

public interface MultiKindsSupportable {

    public boolean joinKinds(String[] newKindNames) throws CoreRealmServiceRuntimeException;
    public boolean retreatFromKind(String kindName) throws CoreRealmServiceRuntimeException;

}
