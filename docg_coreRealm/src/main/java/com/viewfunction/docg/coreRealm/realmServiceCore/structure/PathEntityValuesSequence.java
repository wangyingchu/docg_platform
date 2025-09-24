package com.viewfunction.docg.coreRealm.realmServiceCore.structure;

import java.util.LinkedList;

public class PathEntityValuesSequence {

    private LinkedList<PathEntityValue> entityValuesSequence;

    public PathEntityValuesSequence(){
        this.entityValuesSequence = new LinkedList<PathEntityValue>();
    }

    public PathEntityValuesSequence(LinkedList<PathEntityValue> entityValuesSequence){
        this.entityValuesSequence = entityValuesSequence;
    }

    public LinkedList<PathEntityValue> getEntityValuesSequence() {
        return this.entityValuesSequence;
    }

    public void setEntityValuesSequence(LinkedList<PathEntityValue> entityValuesSequence) {
        this.entityValuesSequence = entityValuesSequence;
    }

    public void addEntityValue(PathEntityValue entityValue){
        this.entityValuesSequence.add(entityValue);
    }
}
