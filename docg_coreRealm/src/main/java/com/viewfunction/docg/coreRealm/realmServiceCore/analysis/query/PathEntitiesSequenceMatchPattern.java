package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import java.util.LinkedList;

public class PathEntitiesSequenceMatchPattern {

    private LinkedList<SequenceMatchLogic> sequenceMatchLogicList;

    public PathEntitiesSequenceMatchPattern(){
        this.sequenceMatchLogicList = new LinkedList<>();
    }

    public PathEntitiesSequenceMatchPattern(LinkedList<SequenceMatchLogic> sequenceMatchLogicList) {
        this.sequenceMatchLogicList = sequenceMatchLogicList;
    }

    public void addSequenceMatchLogic(SequenceMatchLogic sequenceMatchLogic){
        SequenceMatchLogic lastSequenceMatchLogic = sequenceMatchLogicList.getLast();
        if(lastSequenceMatchLogic == null){
            sequenceMatchLogicList.add(sequenceMatchLogic);
        }else{
            if(lastSequenceMatchLogic instanceof ConceptionKindSequenceMatchLogic){

            }
        }
    }

    public LinkedList<SequenceMatchLogic> getSequenceMatchLogicList() {
        return sequenceMatchLogicList;
    }

    public void setSequenceMatchLogicList(LinkedList<SequenceMatchLogic> sequenceMatchLogicList) {
        this.sequenceMatchLogicList = sequenceMatchLogicList;
    }
}
