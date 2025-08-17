package com.viewfunction.docg.externalCustomizedAction;

import com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction.ActionLogicExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;

import java.util.Map;

public class TestExternalActionLogicExecutor implements ActionLogicExecutor {
    @Override
    public Object executeActionSync(Map<String, Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity conceptionEntity) {
        System.out.println(actionParameters);
        System.out.println(conceptionKind.getConceptionKindName());
        System.out.println(conceptionEntity);
        return "helloworld";
    }

    @Override
    public void executeActionAsync(Map<String, Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity conceptionEntity) {

    }
}
