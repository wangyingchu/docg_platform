package com.viewfunction.docg.coreRealm.realmServiceCore.structure;

import java.util.LinkedList;

public class PathEntitiesSequence {

    private LinkedList<PathEntity> entitiesSequence;

    public PathEntitiesSequence(LinkedList<PathEntity> entitiesSequence) {
        this.entitiesSequence = entitiesSequence;
    }

    public LinkedList<PathEntity> getEntitiesSequence() {
        return entitiesSequence;
    }

    public void setEntitiesSequence(LinkedList<PathEntity> entitiesSequence) {
        this.entitiesSequence = entitiesSequence;
    }
}
