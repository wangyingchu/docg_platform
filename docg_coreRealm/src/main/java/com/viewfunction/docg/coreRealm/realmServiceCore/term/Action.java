package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;

import java.util.Map;

public interface Action extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable {

    public String getActionName();

    public String getActionDesc();

    public boolean updateActionDesc(String actionDesc);

    public String getActionUID();

    public String getActionImplementationClass();

    public boolean updateActionImplementationClass(String actionImplementationClassFullName);

    public Object executeActionSync(Map<String,Object> actionParameters) throws CoreRealmServiceRuntimeException;

    public void executeActionAsync(Map<String,Object> actionParameters) throws CoreRealmServiceRuntimeException;

    public Object executeActionSync(Map<String,Object> actionParameters,ConceptionEntity conceptionEntity) throws CoreRealmServiceRuntimeException;

    public void executeActionAsync(Map<String,Object> actionParameters,ConceptionEntity conceptionEntity) throws CoreRealmServiceRuntimeException;

    public ConceptionKind getContainerConceptionKind();
}
