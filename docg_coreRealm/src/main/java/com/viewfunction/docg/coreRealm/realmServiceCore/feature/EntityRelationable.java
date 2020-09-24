package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;
import java.util.Map;

public interface EntityRelationable {

    public Long countRelations();
    public List<RelationEntity> getAllRelations();
    public List<RelationEntity> getAllSpecifiedRelations(String relationKind, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;
    public List<RelationEntity> getSpecifiedRelations(QueryParameters exploreParameters, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;
    public Long getRelationCount(String relationType,RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;
    public RelationEntity addFromRelation(EntityRelationable targetRelationable, String relationKind, Map<String,Object> initRelationProperties,boolean repeatable) throws CoreRealmServiceRuntimeException;
    public RelationEntity addToRelation(EntityRelationable targetRelationable,String relationKind,Map<String,Object> initRelationProperties,boolean repeatable) throws CoreRealmServiceRuntimeException;
    public boolean removeRelation(String relationEntityUID) throws CoreRealmServiceRuntimeException;
    public List<String> removeAllRelations();
    public List<String> removeSpecifiedRelations(String relationType,RelationDirection relationDirection) throws CoreRealmServiceRuntimeException;

}
