package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ClassificationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;

public interface ClassificationKindAttachable {
    RelationEntity attachClassificationEntity(RelationAttachInfo relationAttachInfo, String classificationEntityUID) throws CoreRealmServiceRuntimeException;
    boolean detachClassificationEntity(String classificationEntityUID, String relationKindName, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException ;
    List<ClassificationEntity> getAttachedClassificationEntities(String relationKindName, RelationDirection relationDirection);
}
