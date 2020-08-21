package com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationKindAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ClassificationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public interface Neo4JClassificationKindAttachable extends ClassificationKindAttachable,Neo4JKeyResourcesRetrievable {

    default RelationEntity attachClassificationEntity(RelationAttachInfo relationAttachInfo, String classificationEntityUID) throws CoreRealmServiceRuntimeException{
        return null;
    }
    default boolean detachClassificationEntity(String classificationEntityUID, String relationKindName, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException {
    return false;
    }
    default List<ClassificationEntity> getAttachedClassificationEntities(String relationKindName, RelationDirection relationDirection){
        return null;
    }
}
