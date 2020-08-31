package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;

import java.util.List;

public interface ConceptionKind extends MetaConfigItemFeatureSupportable, MetaAttributeFeatureSupportable, ClassificationKindAttachable {
    public String getConceptionKindName();
    public String getConceptionKindDesc();
    public Long countConceptionEntities() throws CoreRealmServiceRuntimeException;
    public List<ConceptionKind> getChildConceptionKinds() throws CoreRealmFunctionNotSupportedException;
    public ConceptionKind getParentConceptionKind() throws CoreRealmFunctionNotSupportedException;

    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation);
    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue,List<RelationAttachKind> relationAttachKindList);
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation);
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, List<RelationAttachKind> relationAttachKindList);
    public ConceptionEntity updateEntity(ConceptionEntityValue conceptionEntityValueForUpdate) throws CoreRealmServiceRuntimeException;
    public EntitiesOperationResult updateEntities(List<ConceptionEntityValue> entityValues);
    public boolean deleteEntity(String conceptionEntityUID) throws CoreRealmServiceRuntimeException;
    public EntitiesOperationResult deleteEntities(List<String> conceptionEntityUIDs) throws CoreRealmServiceRuntimeException;
    public EntitiesOperationResult purgeAllEntities() throws CoreRealmServiceRuntimeException;
    public ConceptionEntitiesRetrieveResult getEntities(QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException;
    public ConceptionEntity getEntityByUID(String conceptionEntityUID);
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKinds(List<String> attributesViewKindNames, QueryParameters exploreParameters);
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNames(List<String> attributeNames, QueryParameters exploreParameters);

    public boolean attachAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException;
    public List<AttributesViewKind> getContainsAttributesViewKinds();
    public List<AttributesViewKind> getContainsAttributesViewKinds(String attributesViewKindName);
    public boolean detachAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException;

    public List<AttributeKind> getContainsSingleValueAttributeKinds();
    public List<AttributeKind> getContainsSingleValueAttributeKinds(String attributeKindName);
}
