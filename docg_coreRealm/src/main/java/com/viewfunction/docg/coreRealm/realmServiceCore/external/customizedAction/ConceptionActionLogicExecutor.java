package com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ConceptionActionLogicExecutor {

    public Object executeActionSync(Map<String,Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity... conceptionEntity);

    public CompletableFuture<Object> executeActionAsync(Map<String,Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity... conceptionEntity);
}
