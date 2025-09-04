package com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ConceptionActionLogicExecutor {

    /**
     * ConceptionAction 的操作逻辑实现类方法，用于同步执行 ConceptionAction 的操作逻辑
     *
     * @param actionParameters Map<String,Object> 执行实现类操作逻辑所需的参数
     * @param conceptionKind ConceptionKind 执行实现类操作逻辑所需的概念类型
     * @param conceptionEntity ConceptionEntity... 执行实现类操作逻辑所需的其他概念实体对象
     *
     * @return 实现类操作逻辑同步返回的运行结果
     */
    public Object executeActionSync(Map<String,Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity... conceptionEntity);

    /**
     * ConceptionAction 的操作逻辑实现类方法，用于异步执行 ConceptionAction 的操作逻辑
     *
     * @param actionParameters Map<String,Object> 执行实现类操作逻辑所需的参数
     * @param conceptionKind ConceptionKind 执行实现类操作逻辑所需的概念类型
     * @param conceptionEntity ConceptionEntity... 执行实现类操作逻辑所需的其他概念实体对象
     *
     * @return 实现类操作逻辑异步返回的运行结果
     */
    public CompletableFuture<Object> executeActionAsync(Map<String,Object> actionParameters, ConceptionKind conceptionKind, ConceptionEntity... conceptionEntity);
}
