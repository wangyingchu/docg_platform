package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;

public interface MultiConceptionKindsSupportable {
    /**
     * 将当前实体对象加入更多的概念类型中
     *
     * @param newKindNames String[] 需要加入的概念类型列表
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean joinConceptionKinds(String[] newKindNames) throws CoreRealmServiceRuntimeException;

    /**
     * 将当前实体对象退出指定概念类型
     *
     * @param kindName String 需要退出的概念类型
     *
     * @return 如操作成功，返回结果为 true
     */
    public boolean retreatFromConceptionKind(String kindName) throws CoreRealmServiceRuntimeException;
}

