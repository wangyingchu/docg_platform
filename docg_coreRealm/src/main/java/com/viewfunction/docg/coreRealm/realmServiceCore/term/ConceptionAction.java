package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ConceptionAction extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable {

    /**
     * 获取当前自定义动作名称
     *
     * @return 自定义动作名称
     */
    public String getActionName();

    /**
     * 获取当前自定义动作描述
     *
     * @return 自定义动作描述
     */
    public String getActionDesc();

    /**
     * 更新当前自定义动作描述
     *
     * @param actionDesc String 新的自定义动作描述
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean updateActionDesc(String actionDesc);

    /**
     * 获取当前自定义动作唯一ID
     *
     * @return 自定义动作唯一ID
     */
    public String getActionUID();

    /**
     * 获取当前自定义动作实现类全路径名称
     *
     * @return 自定义动作实现类全路径名称
     */
    public String getActionImplementationClass();

    /**
     * 更新当前自定义动作实现类全路径名称
     *
     * @param actionImplementationClassFullName String 新的自定义动作实现类全路径名称
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean updateActionImplementationClass(String actionImplementationClassFullName);

    /**
     * 同步执行当前自定义动作实现类的操作逻辑，并返回执行结果
     *
     * @param actionParameters Map<String,Object> 执行实现类操作逻辑所需的参数
     *
     * @return 实现类操作逻辑同步返回的运行结果
     */
    public Object executeActionSync(Map<String,Object> actionParameters) throws CoreRealmServiceRuntimeException;

    /**
     * 异步执行当前自定义动作实现类的操作逻辑，并返回执行结果
     *
     * @param actionParameters Map<String,Object> 执行实现类操作逻辑所需的参数
     *
     * @return 实现类操作逻辑异步返回的运行结果
     */
    public CompletableFuture<Object> executeActionAsync(Map<String,Object> actionParameters) throws CoreRealmServiceRuntimeException;

    /**
     * 同步执行当前自定义动作实现类的操作逻辑，并返回执行结果
     *
     * @param actionParameters Map<String,Object> 执行实现类操作逻辑所需的参数
     * @param conceptionEntity ConceptionEntity... 执行实现类操作逻辑所需的其他概念实体对象
     *
     * @return 实现类操作逻辑同步返回的运行结果
     */
    public Object executeActionSync(Map<String,Object> actionParameters,ConceptionEntity... conceptionEntity) throws CoreRealmServiceRuntimeException;

    /**
     * 异步执行当前自定义动作实现类的操作逻辑，并返回执行结果
     *
     * @param actionParameters Map<String,Object> 执行实现类操作逻辑所需的参数
     * @param conceptionEntity ConceptionEntity... 执行实现类操作逻辑所需的其他概念实体对象
     *
     * @return 实现类操作逻辑异步返回的运行结果
     */
    public CompletableFuture<Object> executeActionAsync(Map<String,Object> actionParameters,ConceptionEntity... conceptionEntity) throws CoreRealmServiceRuntimeException;

    /**
     * 获取包含当前自定义动作的概念类型对象
     *
     * @return 概念类型对象
     */
    public ConceptionKind getContainerConceptionKind();
}
