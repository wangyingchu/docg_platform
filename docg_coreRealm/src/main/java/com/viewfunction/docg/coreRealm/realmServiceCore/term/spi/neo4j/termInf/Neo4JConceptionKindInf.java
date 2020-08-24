package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JMetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf.Neo4JMetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;

import java.util.List;

public interface Neo4JConceptionKindInf extends Neo4JMetaConfigItemFeatureSupportable, Neo4JMetaAttributeFeatureSupportable, Neo4JClassificationKindAttachable {
    public String getConceptionKindName();
    public String getConceptionKindDesc();
    public Long countConceptionEntities() throws CoreRealmServiceRuntimeException;
    public List<Neo4JConceptionKindInf> getChildConceptionKinds() throws CoreRealmFunctionNotSupportedException;
    public Neo4JConceptionKindInf getParentConceptionKind() throws CoreRealmFunctionNotSupportedException;

    public Neo4JConceptionEntityInf newEntity(ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation);
    public Neo4JConceptionEntityInf newEntity(ConceptionEntityValue conceptionEntityValue, List<Neo4JRelationAttachKindInf> relationAttachKindList);
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation);
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, List<Neo4JRelationAttachKindInf> relationAttachKindList);
    public Neo4JConceptionEntityInf updateEntity(ConceptionEntityValue conceptionEntityValueForUpdate) throws CoreRealmServiceRuntimeException;
    public EntitiesOperationResult updateEntities(List<ConceptionEntityValue> entityValues);
    public boolean deleteEntity(String conceptionEntityUID) throws CoreRealmServiceRuntimeException;
    public EntitiesOperationResult deleteEntities(List<String> conceptionEntityUIDs) throws CoreRealmServiceRuntimeException;
    public EntitiesOperationResult purgeAllEntities() throws CoreRealmServiceRuntimeException;
    public ConceptionEntitiesRetrieveResult getEntities(QueryParameters queryParameters);
    public Neo4JConceptionEntityInf getEntityByUID(String conceptionEntityUID);
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKinds(List<String> attributesViewKindNames, QueryParameters exploreParameters);
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNames(List<String> attributeNames, QueryParameters exploreParameters);

    public boolean addAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException;
    public List<Neo4JAttributesViewKindInf> getContainsAttributesViewKinds(String attributesViewKindName);
    public boolean removeAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException;
    public List<Neo4JAttributesViewKindInf> getContainsAttributesViewKinds();

    public List<Neo4JAttributeKindInf> getSingleValueAttributeKinds();
    public Neo4JAttributeKindInf getSingleValueAttributeKind(String AttributeKindName);
}
