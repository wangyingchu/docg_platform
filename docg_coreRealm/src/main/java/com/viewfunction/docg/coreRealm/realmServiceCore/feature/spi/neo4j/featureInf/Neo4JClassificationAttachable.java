package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public interface Neo4JClassificationAttachable extends ClassificationAttachable,Neo4JKeyResourcesRetrievable {

    default RelationEntity attachClassification(RelationAttachInfo relationAttachInfo, String classificationEntityUID) throws CoreRealmServiceRuntimeException{
        return null;
    }

    default boolean detachClassification(String classificationEntityUID, String relationKindName, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException {
        return false;
    }

    default List<Classification> getAttachedClassifications(String relationKindName, RelationDirection relationDirection){
        return null;
    }
}
