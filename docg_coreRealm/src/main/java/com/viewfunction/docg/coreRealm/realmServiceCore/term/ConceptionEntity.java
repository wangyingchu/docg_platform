package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntity;

import java.util.List;

public interface ConceptionEntity extends AttributesMeasurable, EntityRelationable, ClassificationAttachable, MultiConceptionKindsSupportable, TimeScaleFeatureSupportable, GeospatialScaleFeatureSupportable, PathTravelable, PathEntity, GeospatialScaleCalculable, ExternalAttributesValueAccessible {
    /**
     * 获取当前概念实体对象唯一ID
     *
     * @return 概念实体对象唯一ID
     */
    public String getConceptionEntityUID();

    /**
     * 获取当前操作上下文中概念实体对象所属的概念类型名称
     *
     * @return 概念类型名称
     */
    public String getConceptionKindName();

    /**
     * 获取当前概念实体对象所属的所有概念类型名称
     *
     * @return 概念类型名称列表
     */
    public List<String> getAllConceptionKindNames();
}
