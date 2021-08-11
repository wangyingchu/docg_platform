package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.LinkedList;
import java.util.List;

public class TravelParameters {

    public enum TraversalMethod {BFS, DFS}

    private List<RelationKindMatchLogic> relationKindMatchLogics;
    private RelationDirection defaultDirectionForNoneRelationKindMatch;
    private List<ConceptionKindMatchLogic> conceptionKindMatchLogics;
    private int minJump;
    private int maxJump;
    private int resultNumber;

    private LinkedList<RelationKindMatchLogic> relationKindFlowLogics;
    private LinkedList<ConceptionKindMatchLogic> conceptionKindFlowLogics;

    private TraversalMethod traversalMethod;
    private List<String> mustHaveConceptionEntityUIDs;
    private List<String> notHaveConceptionEntityUIDs;
    private List<String> endWithConceptionEntityUIDs;
    private List<String> terminateAtConceptionEntityUIDs;
}
