package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;

public interface RelationEntity extends AttributesMeasurable {
    /**
     * 获取当前关系实体对象唯一ID
     *
     * @return 关系实体对象唯一ID
     */
    public String getRelationEntityUID();

    /**
     * 获取当前关系实体所属关系类型名称
     *
     * @return 关系类型名称
     */
    public String getRelationKindName();

    /**
     * 获取当前关系实体的来源概念实体唯一ID
     *
     * @return 概念实体对象唯一ID
     */
    public String getFromConceptionEntityUID();

    /**
     * 获取当前关系实体的目标概念实体唯一ID
     *
     * @return 概念实体对象唯一ID
     */
    public String getToConceptionEntityUID();
}
