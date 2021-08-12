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
    private LinkedList<List<RelationKindMatchLogic>> relationKindFlowMatchLogics;
    private LinkedList<List<ConceptionKindMatchLogic>> conceptionKindFlowMatchLogics;
    private TraversalMethod traversalMethod;
    private List<String> mustHaveConceptionEntityUIDs;
    private List<String> notAllowConceptionEntityUIDs;
    private List<String> endWithConceptionEntityUIDs;
    private List<String> terminateAtConceptionEntityUIDs;
    private boolean matchStartConceptionEntity;
    private LinkedList<List<EntityKindMatchLogic>> entityPathFlowMatchLogics;
    private boolean matchStartEntityForPathFlow;

    public List<RelationKindMatchLogic> getRelationKindMatchLogics() {
        return relationKindMatchLogics;
    }



    public RelationDirection getDefaultDirectionForNoneRelationKindMatch() {
        return defaultDirectionForNoneRelationKindMatch;
    }

    public void setDefaultDirectionForNoneRelationKindMatch(RelationDirection defaultDirectionForNoneRelationKindMatch) {
        this.defaultDirectionForNoneRelationKindMatch = defaultDirectionForNoneRelationKindMatch;
    }

    public List<ConceptionKindMatchLogic> getConceptionKindMatchLogics() {
        return conceptionKindMatchLogics;
    }

    public int getMinJump() {
        return minJump;
    }

    public void setMinJump(int minJump) {
        this.minJump = minJump;
    }

    public int getMaxJump() {
        return maxJump;
    }

    public void setMaxJump(int maxJump) {
        this.maxJump = maxJump;
    }

    public int getResultNumber() {
        return resultNumber;
    }

    public void setResultNumber(int resultNumber) {
        this.resultNumber = resultNumber;
    }

    public LinkedList<List<RelationKindMatchLogic>> getRelationKindFlowMatchLogics() {
        return relationKindFlowMatchLogics;
    }

    public LinkedList<List<ConceptionKindMatchLogic>> getConceptionKindFlowMatchLogics() {
        return conceptionKindFlowMatchLogics;
    }

    public TraversalMethod getTraversalMethod() {
        return traversalMethod;
    }

    public void setTraversalMethod(TraversalMethod traversalMethod) {
        this.traversalMethod = traversalMethod;
    }

    public List<String> getMustHaveConceptionEntityUIDs() {
        return mustHaveConceptionEntityUIDs;
    }

    public void setMustHaveConceptionEntityUIDs(List<String> mustHaveConceptionEntityUIDs) {
        this.mustHaveConceptionEntityUIDs = mustHaveConceptionEntityUIDs;
    }

    public List<String> getNotAllowConceptionEntityUIDs() {
        return notAllowConceptionEntityUIDs;
    }

    public void setNotAllowConceptionEntityUIDs(List<String> notAllowConceptionEntityUIDs) {
        this.notAllowConceptionEntityUIDs = notAllowConceptionEntityUIDs;
    }

    public List<String> getEndWithConceptionEntityUIDs() {
        return endWithConceptionEntityUIDs;
    }

    public void setEndWithConceptionEntityUIDs(List<String> endWithConceptionEntityUIDs) {
        this.endWithConceptionEntityUIDs = endWithConceptionEntityUIDs;
    }

    public List<String> getTerminateAtConceptionEntityUIDs() {
        return terminateAtConceptionEntityUIDs;
    }

    public void setTerminateAtConceptionEntityUIDs(List<String> terminateAtConceptionEntityUIDs) {
        this.terminateAtConceptionEntityUIDs = terminateAtConceptionEntityUIDs;
    }

    public boolean isMatchStartConceptionEntity() {
        return matchStartConceptionEntity;
    }

    public void setMatchStartConceptionEntity(boolean matchStartConceptionEntity) {
        this.matchStartConceptionEntity = matchStartConceptionEntity;
    }

    public LinkedList<List<EntityKindMatchLogic>> getEntityPathFlowMatchLogics() {
        return entityPathFlowMatchLogics;
    }

    public boolean isMatchStartEntityForPathFlow() {
        return matchStartEntityForPathFlow;
    }

    public void setMatchStartEntityForPathFlow(boolean matchStartEntityForPathFlow) {
        this.matchStartEntityForPathFlow = matchStartEntityForPathFlow;
    }
}
