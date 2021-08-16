package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TravelParameters {

    public enum TraversalMethod {BFS, DFS}

    private List<RelationKindMatchLogic> relationKindMatchLogics;
    private RelationDirection defaultDirectionForNoneRelationKindMatch;
    private List<ConceptionKindMatchLogic> conceptionKindMatchLogics;
    private int minJump = -1;
    private int maxJump = -1;
    private int resultNumber = -1;
    private LinkedList<List<RelationKindMatchLogic>> relationKindFlowMatchLogics;
    private LinkedList<List<ConceptionKindMatchLogic>> conceptionKindFlowMatchLogics;
    private TraversalMethod traversalMethod = TraversalMethod.BFS;
    private List<String> mustHaveConceptionEntityUIDs;
    private List<String> notAllowConceptionEntityUIDs;
    private List<String> endWithConceptionEntityUIDs;
    private List<String> terminateAtConceptionEntityUIDs;
    private boolean matchStartConceptionEntity = false;
    private LinkedList<List<? extends EntityKindMatchLogic>> entityPathFlowMatchLogics;
    private boolean matchStartRelationEntity = true;

    public TravelParameters(){
        this.relationKindMatchLogics = new ArrayList<>();
        this.conceptionKindMatchLogics = new ArrayList<>();
        this.relationKindFlowMatchLogics = new LinkedList<>();
        this.conceptionKindFlowMatchLogics = new LinkedList<>();
        this.entityPathFlowMatchLogics = new LinkedList<>();
    }

    public void addRelationKindMatchLogic(RelationKindMatchLogic relationKindMatchLogic){
        this.relationKindMatchLogics.add(relationKindMatchLogic);
    }

    public List<RelationKindMatchLogic> getRelationKindMatchLogics() {
        return relationKindMatchLogics;
    }

    public RelationDirection getDefaultDirectionForNoneRelationKindMatch() {
        return defaultDirectionForNoneRelationKindMatch;
    }

    public void setDefaultDirectionForNoneRelationKindMatch(RelationDirection defaultDirectionForNoneRelationKindMatch) {
        this.defaultDirectionForNoneRelationKindMatch = defaultDirectionForNoneRelationKindMatch;
    }

    public void addConceptionKindMatchLogic(ConceptionKindMatchLogic conceptionKindMatchLogic){
        this.conceptionKindMatchLogics.add(conceptionKindMatchLogic);
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

    public void addRelationKindFlowMatchLogic(List<RelationKindMatchLogic> relationKindFlowMatchLogic){
        this.relationKindFlowMatchLogics.add(relationKindFlowMatchLogic);
    }

    public LinkedList<List<RelationKindMatchLogic>> getRelationKindFlowMatchLogics() {
        return relationKindFlowMatchLogics;
    }

    public void addConceptionKindFlowMatchLogic(List<ConceptionKindMatchLogic> conceptionKindFlowMatchLogic){
        this.conceptionKindFlowMatchLogics.add(conceptionKindFlowMatchLogic);
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

    public void addConceptionEntityPathFlowMatchLogic(List<ConceptionKindMatchLogic> entityPathFlowMatchLogic){
        this.entityPathFlowMatchLogics.add(entityPathFlowMatchLogic);
    }

    public void addRelationEntityPathFlowMatchLogic(List<RelationKindMatchLogic> entityPathFlowMatchLogic){
        this.entityPathFlowMatchLogics.add(entityPathFlowMatchLogic);
    }

    public LinkedList<List<? extends EntityKindMatchLogic>> getEntityPathFlowMatchLogics() {
        return entityPathFlowMatchLogics;
    }

    public boolean isMatchStartRelationEntity() {
        return matchStartRelationEntity;
    }

    public void setMatchStartRelationEntity(boolean matchStartRelationEntity) {
        this.matchStartRelationEntity = matchStartRelationEntity;
    }
}
