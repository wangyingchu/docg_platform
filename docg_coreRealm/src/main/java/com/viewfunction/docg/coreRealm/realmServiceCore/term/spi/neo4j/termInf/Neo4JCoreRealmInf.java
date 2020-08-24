package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;

public interface Neo4JCoreRealmInf {
    public CoreRealmStorageImplTech getStorageImplTech();
    public Neo4JConceptionKindInf getConceptionKind(String conceptionKindName);
    public Neo4JConceptionKindInf createConceptionKind(String conceptionKindName, String conceptionKindDesc);
    public Neo4JConceptionKindInf createConceptionKind(String conceptionKindName, String conceptionKindDesc, String parentConceptionKindName)
            throws CoreRealmFunctionNotSupportedException;
    public boolean removeConceptionKind(String conceptionKindName,boolean deleteExistEntities) throws CoreRealmServiceRuntimeException;

    public Neo4JAttributesViewKindInf getAttributesViewKind(String attributesViewKindUID);
    public Neo4JAttributesViewKindInf createAttributesViewKind(String attributesViewKindName, String attributesViewKindDesc, Neo4JAttributesViewKindInf.AttributesViewKindDataForm attributesViewKindDataForm);
    public boolean removeAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException;

    public Neo4JAttributeKindInf getAttributeKind(String attributeKindUID);
    public Neo4JAttributeKindInf createAttributeKind(String attributeKindName, String attributeKindDesc, AttributeDataType attributeDataType);
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException;
}
