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
    public final String Classification_ClassificationRelationClass="DOCG_ParentClassificationIs";
    public final String RelationAttachKindClass="DOCG_RelationAttachKind";
    public final String RelationAttachLinkLogicClass="DOCG_RelationAttachLinkLogic";
    public final String RelationAttachKind_RelationAttachLinkLogicRelationClass="DOCG_AttachKindContainsAttachLinkLogicIs";

    public final String TimeFlowClass="DOCG_TimeFlow";
    public final String _defaultTimeFlowName = "DefaultTimeFlow";
    public final String TimeScaleEntityClass="DOCG_TimeScaleEntity";
    public final String TimeScaleYearEntityClass="DOCG_TS_Year";
    public final String TimeScaleMonthEntityClass="DOCG_TS_Month";
    public final String TimeScaleDayEntityClass="DOCG_TS_Day";
    public final String TimeScaleHourEntityClass="DOCG_TS_Hour";
    public final String TimeScaleMinuteEntityClass="DOCG_TS_Minute";
    public final String TimeScale_ContainsRelationClass="DOCG_TS_Contains";
    public final String TimeScale_NextIsRelationClass="DOCG_TS_NextIs";
    public final String TimeScaleEventClass="DOCG_TimeScaleEvent";

    public final String _NameProperty = "name";
    public final String _DescProperty = "description";
    public final String _createDateProperty = "createDate";
    public final String _lastModifyDateProperty = "lastModifyDate";
    public final String _creatorIdProperty = "creatorId";
    public final String _dataOriginProperty = "dataOrigin";

    public final String _viewKindDataForm = "viewKindDataForm";

    public final String _attributeDataType = "attributeDataType";

    public final String _relationAttachSourceKind = "attachSourceKind";
    public final String _relationAttachTargetKind = "attachTargetKind";
    public final String _relationAttachRelationKind = "attachRelationKind";
    public final String _relationAttachRepeatableRelationKind = "attachAllowRepeatRelation";

    public final String _attachLinkLogicType = "linkLogicType";
    public final String _attachLinkLogicCondition = "linkLogicCondition";
    public final String _attachLinkLogicSourceAttribute = "linkLogicSourceAttribute";
    public final String _attachLinkLogicTargetAttribute = "linkLogicTargetAttribute";
}