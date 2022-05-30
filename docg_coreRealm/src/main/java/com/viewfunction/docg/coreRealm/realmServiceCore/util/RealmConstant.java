package com.viewfunction.docg.coreRealm.realmServiceCore.util;

public interface RealmConstant {

    public final String RealmInnerTypePerFix="DOCG_";
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
    public final String TimeScale_FirstChildIsRelationClass="DOCG_TS_FirstChildIs";
    public final String TimeScale_LastChildIsRelationClass="DOCG_TS_LastChildIs";
    public final String TimeScaleEventClass="DOCG_TimeScaleEvent";
    public final String TimeScale_TimeReferToRelationClass="DOCG_TS_TimeReferTo";
    public final String TimeScale_AttachToRelationClass="DOCG_AttachToTimeScale";

    public final String GeospatialRegionClass="DOCG_GeospatialRegion";
    public final String _defaultGeospatialRegionName = "DefaultGeospatialRegion";
    public final String GeospatialScaleEntityClass="DOCG_GeospatialScaleEntity";
    public final String GeospatialScaleContinentEntityClass="DOCG_GS_Continent";
    public final String GeospatialScaleCountryRegionEntityClass="DOCG_GS_CountryRegion";
    public final String GeospatialScaleProvinceEntityClass="DOCG_GS_Province";
    public final String GeospatialScalePrefectureEntityClass="DOCG_GS_Prefecture";
    public final String GeospatialScaleCountyEntityClass="DOCG_GS_County";
    public final String GeospatialScaleTownshipEntityClass="DOCG_GS_Township";
    public final String GeospatialScaleVillageEntityClass="DOCG_GS_Village";

    public final String GeospatialScale_SpatialContainsRelationClass="DOCG_GS_SpatialContains"; //空间包含
    public final String GeospatialScale_SpatialIdenticalRelationClass="DOCG_GS_SpatialIdentical";//空间相同
    public final String GeospatialScale_SpatialApproachRelationClass="DOCG_GS_SpatialApproach";//空间相邻
    public final String GeospatialScale_SpatialConnectRelationClass="DOCG_GS_SpatialConnect";//空间相连

    public final String GeospatialScaleEventClass="DOCG_GeospatialScaleEvent";
    public final String GeospatialScale_GeospatialReferToRelationClass="DOCG_GS_GeospatialReferTo";
    public final String GeospatialScale_AttachToRelationClass="DOCG_AttachToGeospatialScale";

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

    public final String _TimeScaleEventComment="DOCG_TimeScaleEventComment";
    public final String _TimeScaleEventReferTime="DOCG_TimeScaleEventReferTime";
    public final String _TimeScaleEventScaleGrade="DOCG_TimeScaleEventScaleGrade";
    public final String _TimeScaleEventTimeFlow="DOCG_TimeScaleEventTimeFlow";

    public final String _GeospatialScaleEventReferLocation="DOCG_GeospatialScaleEventReferLocation";
    public final String _GeospatialScaleEventComment="DOCG_GeospatialScaleEventComment";
    public final String _GeospatialScaleEventScaleGrade="DOCG_GeospatialScaleEventScaleGrade";
    public final String _GeospatialScaleEventGeospatialRegion="DOCG_GeospatialScaleEventGeospatialRegion";

    public final String GeospatialRegionProperty = "DOCG_GeospatialRegion";
    public final String GeospatialCodeProperty = "DOCG_GeospatialCode";
    public final String GeospatialChineseNameProperty = "DOCG_GeospatialChineseName";
    public final String GeospatialEnglishNameProperty = "DOCG_GeospatialEnglishName";
    public final String GeospatialScaleGradeProperty = "DOCG_GeospatialScaleGrade";

    public final String _GeospatialGeometryType="DOCG_GS_GeometryType";
    public final String _GeospatialGlobalCRSAID="DOCG_GS_GlobalCRSAID";
    public final String _GeospatialCountryCRSAID="DOCG_GS_CountryCRSAID";
    public final String _GeospatialLocalCRSAID="DOCG_GS_LocalCRSAID";
    public final String _GeospatialGLGeometryContent="DOCG_GS_GLGeometryContent";
    public final String _GeospatialCLGeometryContent="DOCG_GS_CLGeometryContent";
    public final String _GeospatialLLGeometryContent="DOCG_GS_LLGeometryContent";
    public final String _GeospatialGLGeometryPOI="DOCG_GS_GLGeometryPOI";
    public final String _GeospatialCLGeometryPOI="DOCG_GS_CLGeometryPOI";
    public final String _GeospatialLLGeometryPOI="DOCG_GS_LLGeometryPOI";
    public final String _GeospatialGLGeometryBorder="DOCG_GS_GLGeometryBorder";
    public final String _GeospatialCLGeometryBorder="DOCG_GS_CLGeometryBorder";
    public final String _GeospatialLLGeometryBorder="DOCG_GS_LLGeometryBorder";
}