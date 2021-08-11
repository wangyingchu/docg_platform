package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.ConceptionKindMatchLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.RelationKindMatchLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.List;

public interface PathTravelable {

    public List<EntitiesPath> expandPath(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                         List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int minJump, int maxJump);

    public EntitiesGraph expandSubGraph(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                        List<ConceptionKindMatchLogic> conceptionKindMatchLogics);
}
