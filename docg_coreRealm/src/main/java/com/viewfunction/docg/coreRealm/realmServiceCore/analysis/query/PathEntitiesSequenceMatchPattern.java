package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;

import java.util.LinkedList;

public class PathEntitiesSequenceMatchPattern {

    private LinkedList<SequenceMatchLogic> sequenceMatchLogicList;
    private int resultNumber;

    public PathEntitiesSequenceMatchPattern(){
        this.sequenceMatchLogicList = new LinkedList<>();
    }

    public void addSequenceMatchLogic(SequenceMatchLogic sequenceMatchLogic) throws CoreRealmServiceRuntimeException {
        if(sequenceMatchLogicList.isEmpty()){
            sequenceMatchLogicList.add(sequenceMatchLogic);
        }else{
            SequenceMatchLogic lastSequenceMatchLogic = sequenceMatchLogicList.getLast();
            if(lastSequenceMatchLogic instanceof ConceptionKindSequenceMatchLogic){
                if(sequenceMatchLogic instanceof RelationKindSequenceMatchLogic){
                    sequenceMatchLogicList.add(sequenceMatchLogic);
                }else{
                    CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
                    coreRealmServiceRuntimeException.setCauseMessage("last SequenceMatchLogic must be RelationKindSequenceMatchLogic");
                    throw coreRealmServiceRuntimeException;
                }
            }else if(lastSequenceMatchLogic instanceof RelationKindSequenceMatchLogic){
                if(sequenceMatchLogic instanceof ConceptionKindSequenceMatchLogic){
                    sequenceMatchLogicList.add(sequenceMatchLogic);
                }else{
                    CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
                    coreRealmServiceRuntimeException.setCauseMessage("last SequenceMatchLogic must be ConceptionKindSequenceMatchLogic");
                    throw coreRealmServiceRuntimeException;
                }
            }
        }
    }

    public LinkedList<SequenceMatchLogic> getSequenceMatchLogicList() {
        return sequenceMatchLogicList;
    }

    public void clearSequenceMatchLogicList(LinkedList<SequenceMatchLogic> sequenceMatchLogicList) {
        this.sequenceMatchLogicList.clear();
    }

    public int getResultNumber() {
        return resultNumber;
    }

    public void setResultNumber(int resultNumber) {
        this.resultNumber = resultNumber;
    }
}
