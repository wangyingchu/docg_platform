package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public interface ClassificationAttachable {
    /**
     * 将当前对象关联到指定的分类上
     *
     * @param relationAttachInfo RelationAttachInfo 关联附着信息
     * @param classificationName String 分类名称
     *
     * @return 关联成功创建的关系实体
     */
    RelationEntity attachClassification(RelationAttachInfo relationAttachInfo, String classificationName) throws CoreRealmServiceRuntimeException;

    /**
     * 删除当前对象到指定分类的已有关联
     *
     * @param classificationName String 分类名称
     * @param relationKindName String 关系类型名称
     * @param relationDirection RelationDirection 关联方向
     *
     * @return 如操作成功，返回结果为 true
     */
    boolean detachClassification(String classificationName, String relationKindName, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException ;

    /**
     * 获取当前对象已经关联的分类
     *
     * @param relationKindName String 关系类型名称
     * @param relationDirection RelationDirection 关联方向
     *
     * @return 符合条件的分类列表
     */
    List<Classification> getAttachedClassifications(String relationKindName, RelationDirection relationDirection);
}

