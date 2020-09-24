package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.ClassificationAttachable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.EntityRelationable;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;

import java.util.List;
import java.util.Map;

public interface Neo4JEntityRelationable extends ClassificationAttachable,Neo4JKeyResourcesRetrievable {



    default public Long countRelations(){
        return null;
    }
    default public List<RelationEntity> getAllRelations(){
        return null;
    }
    default public List<RelationEntity> getAllSpecifiedRelations(String relationKind, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        return null;
    }
    default public List<RelationEntity> getSpecifiedRelations(QueryParameters exploreParameters, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        return null;
    }
    default public Long getRelationCount(String relationType,RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        return null;
    }

    default public RelationEntity addFromRelation(EntityRelationable targetRelationable, String relationKind, Map<String,Object> initRelationProperties, boolean repeatable) throws CoreRealmServiceRuntimeException{
        return null;
    }
    default public RelationEntity addToRelation(EntityRelationable targetRelationable,String relationKind,Map<String,Object> initRelationProperties,boolean repeatable) throws CoreRealmServiceRuntimeException{
        return null;
    }


    default public boolean removeRelation(String relationEntityUID) throws CoreRealmServiceRuntimeException{
        return false;
    }
    default public List<String> removeAllRelations(){
        return null;
    }
    default public List<String> removeSpecifiedRelations(String relationType,RelationDirection relationDirection) throws CoreRealmServiceRuntimeException{
        return null;
    }
}
