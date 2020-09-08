package com.viewfunction.docg.coreRealm.realmServiceCore.util;

public interface RealmConstant {
    public final String ConceptionKindClass="DOCG_ConceptionKind";
    public final String AttributesViewKindClass="DOCG_AttributesViewKind";
    public final String AttributeKindClass="DOCG_AttributeKind";
    public final String RelationKindClass="DOCG_RelationKind";
    public final String MetaConfigItemsStorageClass="DOCG_MetaConfigItemsStorage";
    public final String ClassificationClass="DOCG_Classification";
    public final String ConceptionKind_AttributesViewKindRelationClass="DOCG_ConceptionContainsViewKindIs";
    public final String Kind_MetaConfigItemsStorageRelationClass ="DOCG_MetaConfigItemsStorageIs";
    public final String AttributesViewKind_AttributeKindRelationClass="DOCG_ViewContainsAttributeKindIs";

    public final String _NameProperty = "name";
    public final String _DescProperty = "description";
    public final String _createDateProperty = "createDate";
    public final String _lastModifyDateProperty = "lastModifyDate";
    public final String _creatorIdProperty = "creatorId";
    public final String _dataOriginProperty = "dataOrigin";
    public final String _viewKindDataForm = "viewKindDataForm";
    public final String _attributeDataType = "attributeDataType";

}