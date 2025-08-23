package com.viewfunction.docg.externalCustomizedAction;

import com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction.RelationActionLogicExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TestExternalRelationActionLogicExecutor implements RelationActionLogicExecutor {

    @Override
    public Object executeActionSync(Map<String, Object> actionParameters, RelationKind relationKind, RelationEntity... relationEntity) {
        System.out.println(actionParameters);
        System.out.println(relationKind.getRelationKindName());
        System.out.println(relationEntity);

        Map<String,Object> result = new HashMap<>();
        result.put("resultMessage","helloworld");
        result.put("relationKindName",relationKind.getRelationKindName());
        result.putAll(actionParameters);
        if(relationEntity != null && relationEntity.length > 0){
            result.put("relationEntityUID",relationEntity[0].getRelationEntityUID());
        }
        return result;
    }

    @Override
    public CompletableFuture<Object> executeActionAsync(Map<String, Object> actionParameters, RelationKind relationKind, RelationEntity... relationEntity) {
        System.out.println(actionParameters);
        System.out.println(relationKind.getRelationKindName());
        System.out.println(relationKind);

        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000); // 模拟长时间操作
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            Map<String,Object> result = new HashMap<>();
            result.put("resultMessage","helloworld");
            result.put("relationKindName",relationKind.getRelationKindName());
            result.putAll(actionParameters);
            if(relationEntity != null && relationEntity.length > 0){
                result.put("relationEntityUID",relationEntity[0].getRelationEntityUID());
            }
            return result;
        });
        return future;
    }
}
