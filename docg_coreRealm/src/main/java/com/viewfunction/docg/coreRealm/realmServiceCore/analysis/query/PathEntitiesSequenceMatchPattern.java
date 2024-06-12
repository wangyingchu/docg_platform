package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;

import java.util.LinkedList;

public class PathEntitiesSequenceMatchPattern {

    private LinkedList<SequenceMatchLogic> sequenceMatchLogicList;

    public PathEntitiesSequenceMatchPattern(){
        this.sequenceMatchLogicList = new LinkedList<>();
    }

    public void addSequenceMatchLogic(SequenceMatchLogic sequenceMatchLogic) throws CoreRealmServiceRuntimeException {
        SequenceMatchLogic lastSequenceMatchLogic = sequenceMatchLogicList.getLast();
        if(lastSequenceMatchLogic == null){
            sequenceMatchLogicList.add(sequenceMatchLogic);
        }else{
            if(lastSequenceMatchLogic instanceof ConceptionKindSequenceMatchLogic){
                if(sequenceMatchLogic instanceof RelationKindSequenceMatchLogic){
                    sequenceMatchLogicList.add(sequenceMatchLogic);
                }else{
                    CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
                    coreRealmServiceRuntimeException.setCauseMessage("lase SequenceMatchLogic must be RelationKindSequenceMatchLogic");
                    throw coreRealmServiceRuntimeException;
                }
            }else if(lastSequenceMatchLogic instanceof RelationKindSequenceMatchLogic){
                if(sequenceMatchLogic instanceof ConceptionKindSequenceMatchLogic){
                    sequenceMatchLogicList.add(sequenceMatchLogic);
                }else{
                    CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
                    coreRealmServiceRuntimeException.setCauseMessage("lase SequenceMatchLogic must be ConceptionKindSequenceMatchLogic");
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
}
