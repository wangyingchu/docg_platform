package com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;

import java.util.Map;

public interface ActionLogicExecutor {

    public Object executeActionSync(Map<String,Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity... conceptionEntity);

    public void executeActionAsync(Map<String,Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity... conceptionEntity);
}
