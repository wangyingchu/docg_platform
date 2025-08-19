package com.viewfunction.docg.externalCustomizedAction;

import com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction.ConceptionActionLogicExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TestExternalConceptionActionLogicExecutor implements ConceptionActionLogicExecutor {
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
    public CompletableFuture<Object> executeActionAsync(Map<String, Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity... conceptionEntity) {
        System.out.println(actionParameters);
        System.out.println(conceptionKind.getConceptionKindName());
        System.out.println(conceptionEntity);

        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000); // 模拟长时间操作
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            Map<String,Object> result = new HashMap<>();
            result.put("resultMessage","helloworld");
            result.put("conceptionKindName",conceptionKind.getConceptionKindName());
            result.putAll(actionParameters);
            if(conceptionEntity != null && conceptionEntity.length > 0){
                result.put("conceptionEntityUID",conceptionEntity[0].getConceptionEntityUID());
            }
            return result;
        });
        return future;
    }
}
