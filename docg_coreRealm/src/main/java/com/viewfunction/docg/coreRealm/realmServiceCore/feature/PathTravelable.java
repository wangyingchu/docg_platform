package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesSpanningTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.List;

public interface PathTravelable {

    public List<EntitiesPath> expandPath(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                         List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int minJump, int maxJump);

    public EntitiesGraph expandGraph(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                        List<ConceptionKindMatchLogic> conceptionKindMatchLogics,boolean containsSelf,int maxJump);

    public EntitiesSpanningTree expandSpanningTree(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                   List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int maxJump);

    public List<EntitiesPath> advancedExpandPath(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    public EntitiesGraph advancedExpandGraph(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    public EntitiesSpanningTree advancedExpandSpanningTree(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    public List<ConceptionEntity> getEndConceptionEntitiesByPathPattern(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    public List<EntitiesPath> getAllPathBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics,
                                                      RelationDirection defaultDirectionForNoneRelationKindMatch,int maxJump,
                                                      PathEntityFilterParameters relationPathEntityFilterParameters,
                                                      PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public EntitiesPath getShortestPathBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                     PathEntityFilterParameters relationPathEntityFilterParameters,
                                                     PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public EntitiesPath getShortestPathWithWeightBetweenEntity(String targetEntityUID, List<RelationKindMatchLogic> relationKindMatchLogics,
                                                                RelationDirection defaultDirectionForNoneRelationKindMatch,String relationWeightProperty,
                                                                PathEntityFilterParameters relationPathEntityFilterParameters,
                                                                PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public List<EntitiesPath> getShortestPathsWithWeightBetweenEntity(String targetEntityUID, List<RelationKindMatchLogic> relationKindMatchLogics,
                                                                RelationDirection defaultDirectionForNoneRelationKindMatch,String relationWeightProperty,float defaultWeight,int maxPathNumber,
                                                                PathEntityFilterParameters relationPathEntityFilterParameters,
                                                                PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public List<EntitiesPath> getAllShortestPathsBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                     PathEntityFilterParameters relationPathEntityFilterParameters,
                                                               PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public List<EntitiesPath> getLongestPathsBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics,
                                                      RelationDirection defaultDirectionForNoneRelationKindMatch,int maxJump,int maxPathNumber,
                                                      PathEntityFilterParameters relationPathEntityFilterParameters,
                                                           PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;
}
