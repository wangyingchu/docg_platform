package com.viewfunction.docg.coreRealm.realmServiceCore.term;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaAttributeFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;

import java.util.List;

public interface Classification extends MetaAttributeFeatureSupportable, AttributesMeasurable{

    public String getClassificationName();
    public String getClassificationDesc();
    public boolean isRootClassification();
    public Classification getParentClassification();
    public List<Classification> getChildClassifications();
    public InheritanceTree<Classification> getOffspringClassifications();

    public boolean attachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException;
    public boolean detachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException;
    public Classification createChildClassification(String classificationName,String classificationDesc) throws CoreRealmServiceRuntimeException;
    public boolean removeChildClassification(String classificationName) throws CoreRealmServiceRuntimeException;

    public List<ConceptionKind> getRelatedConceptionKind(String relationKindName, RelationDirection relationDirection,boolean includeOffspringClassifications,int offspringLevel) throws CoreRealmServiceRuntimeException;
    public List<RelationKind> getRelatedRelationKind(String relationKindName, RelationDirection relationDirection,boolean includeOffspringClassifications,int offspringLevel) throws CoreRealmServiceRuntimeException;
    public List<AttributeKind> getRelatedAttributeKind(String relationKindName, RelationDirection relationDirection,boolean includeOffspringClassifications,int offspringLevel) throws CoreRealmServiceRuntimeException;
    public List<AttributesViewKind> getRelatedAttributesViewKind(String relationKindName, RelationDirection relationDirection,boolean includeOffspringClassifications,int offspringLevel) throws CoreRealmServiceRuntimeException;
    public List<ConceptionEntity> getRelatedConceptionEntity(String relationKindName, RelationDirection relationDirection, QueryParameters queryParameters,boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;
}
