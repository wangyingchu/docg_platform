package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntitiesRetrieveResult;

import java.util.List;

public interface RelationKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationAttachable {
    /**
     * 获取当前关系类型对象名称
     *
     * @return 关系类型对象名称
     */
    public String getRelationKindName();

    /**
     * 获取当前关系类型对象描述
     *
     * @return 关系类型对象描述
     */
    public String getRelationKindDesc();

    /**
     * 获取当前关系类型的父关系类型对象，该方法在 NEO4J 实现类型下无效
     *
     * @return 关系类型对象
     */
    public RelationKind getParentRelationKind() throws CoreRealmFunctionNotSupportedException;

    /**
     * 获取当前关系类型的所有子关系类型对象，该方法在 NEO4J 实现类型下无效
     *
     * @return 关系类型对象列表
     */
    public List<RelationKind> getChildRelationKinds() throws CoreRealmFunctionNotSupportedException;

    /**
     * 获取当前关系类型的所有后代关系类型对象，该方法在 NEO4J 实现类型下无效
     *
     * @return 关系类型对象继承树
     */
    public InheritanceTree<RelationKind> getOffspringRelationKinds() throws CoreRealmFunctionNotSupportedException;

    /**
     * 计算当前关系类型的所有关系实体数量
     *
     * @return 关系实体数量
     */
    public Long countRelationEntities() throws CoreRealmServiceRuntimeException;

    /**
     * 计算当前关系类型（包含所有后代关系类型）的所有关系实体数量，该方法在 NEO4J 实现类型下无效
     *
     * @return 关系实体数量
     */
    public Long countRelationEntitiesWithOffspring() throws CoreRealmFunctionNotSupportedException;

    /**
     * 查询符合过滤条件的当前关系类型的关系实体对象
     *
     * @param queryParameters QueryParameters 查询过滤条件
     *
     * @return 关系实体查询结果集
     */
    public RelationEntitiesRetrieveResult getRelationEntities(QueryParameters queryParameters)  throws CoreRealmServiceEntityExploreException;

    /**
     * 删除当前关系类型的所有关系实体
     *
     * @return 实体对象操作返回结果
     */
    public EntitiesOperationResult purgeAllRelationEntities() throws CoreRealmServiceRuntimeException;
}
