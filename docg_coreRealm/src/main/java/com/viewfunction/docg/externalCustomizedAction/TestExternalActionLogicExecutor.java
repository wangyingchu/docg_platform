package com.viewfunction.docg.externalCustomizedAction;

import com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction.ActionLogicExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;

import java.util.HashMap;
import java.util.Map;

public class TestExternalActionLogicExecutor implements ActionLogicExecutor {
    @Override
    public Object executeActionSync(Map<String, Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity... conceptionEntity) {
        System.out.println(actionParameters);
        System.out.println(conceptionKind.getConceptionKindName());
        System.out.println(conceptionEntity);

        Map<String,Object> result = new HashMap<>();
        result.put("resultMessage","helloworld");
        result.put("conceptionKindName",conceptionKind.getConceptionKindName());
        result.putAll(actionParameters);
        if(conceptionEntity != null && conceptionEntity.length > 0){
            result.put("conceptionEntityUID",conceptionEntity[0].getConceptionEntityUID());
        }

        return result;
    }

    @Override
    public void executeActionAsync(Map<String, Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity... conceptionEntity) {

    }
}
