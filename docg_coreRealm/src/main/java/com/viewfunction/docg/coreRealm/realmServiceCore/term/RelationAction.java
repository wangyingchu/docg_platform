package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface RelationAction {

    public String getActionName();

    public String getActionDesc();

    public boolean updateActionDesc(String actionDesc);

    public String getActionUID();

    public String getActionImplementationClass();

    public boolean updateActionImplementationClass(String actionImplementationClassFullName);

    public Object executeActionSync(Map<String,Object> actionParameters) throws CoreRealmServiceRuntimeException;

    public CompletableFuture<Object> executeActionAsync(Map<String,Object> actionParameters) throws CoreRealmServiceRuntimeException;

    public Object executeActionSync(Map<String,Object> actionParameters,RelationEntity... relationEntity) throws CoreRealmServiceRuntimeException;

    public CompletableFuture<Object> executeActionAsync(Map<String,Object> actionParameters,RelationEntity... relationEntity) throws CoreRealmServiceRuntimeException;

    public RelationKind getContainerRelationKind();
}
