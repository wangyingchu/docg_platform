package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesSpanningTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.List;

public interface PathTravelable {

    public List<EntitiesPath> expandPath(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                         List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int minJump, int maxJump);

    public EntitiesGraph expandGraph(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                        List<ConceptionKindMatchLogic> conceptionKindMatchLogics,boolean containsSelf,int maxJump);

    public EntitiesSpanningTree expandSpanningTree(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                   List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int maxJump);

    public List<EntitiesPath> advancedExpandPath(TravelParameters travelParameters);

    public EntitiesGraph advancedExpandGraph(TravelParameters travelParameters);

    public EntitiesSpanningTree advancedExpandSpanningTree(TravelParameters travelParameters);

    public List<EntitiesPath> getAllPathBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,int maxJump);

    public EntitiesPath getShortestPathBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                     PathEntityFilterParameters relationPathEntityFilterParameters, PathEntityFilterParameters conceptionPathEntityFilterParameters);

    public List<EntitiesPath> getAllShortestPathsBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                     PathEntityFilterParameters relationPathEntityFilterParameters, PathEntityFilterParameters conceptionPathEntityFilterParameters);
    /*
    public EntitiesPath getShortestPathBetweenEntity(String targetEntityUID, String relationKindName);

    public List<EntitiesPath> getAllShortestPathBetweenEntity(String targetEntityUID,String relationKindName);

    public EntitiesPath getShortestPathWithWeightBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch);
    */
}
