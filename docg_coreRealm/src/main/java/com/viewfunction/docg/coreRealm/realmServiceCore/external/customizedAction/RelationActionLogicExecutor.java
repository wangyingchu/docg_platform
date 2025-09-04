package com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface RelationActionLogicExecutor {

    /**
     * RelationAction 的操作逻辑实现类方法，用于同步执行 RelationAction 的操作逻辑
     *
     * @param actionParameters Map<String,Object> 执行实现类操作逻辑所需的参数
     * @param relationKind RelationKind 执行实现类操作逻辑所需的关系类型
     * @param relationEntity RelationEntity... 执行实现类操作逻辑所需的其他关系实体对象
     *
     * @return 实现类操作逻辑同步返回的运行结果
     */
    public Object executeActionSync(Map<String,Object> actionParameters, RelationKind relationKind, RelationEntity... relationEntity);

    /**
     * RelationAction 的操作逻辑实现类方法，用于异步执行 RelationAction 的操作逻辑
     *
     * @param actionParameters Map<String,Object> 执行实现类操作逻辑所需的参数
     * @param relationKind RelationKind 执行实现类操作逻辑所需的关系类型
     * @param relationEntity RelationEntity... 执行实现类操作逻辑所需的其他关系实体对象
     *
     * @return 实现类操作逻辑异步返回的运行结果
     */
    public CompletableFuture<Object> executeActionAsync(Map<String,Object> actionParameters, RelationKind relationKind, RelationEntity... relationEntity);
}
