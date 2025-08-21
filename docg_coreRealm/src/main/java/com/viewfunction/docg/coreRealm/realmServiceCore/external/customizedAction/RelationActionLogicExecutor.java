package com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface RelationActionLogicExecutor {

    public Object executeActionSync(Map<String,Object> actionParameters, RelationKind relationKind, RelationEntity... relationEntity);

    public CompletableFuture<Object> executeActionAsync(Map<String,Object> actionParameters, RelationKind relationKind, RelationEntity... relationEntity);
}
