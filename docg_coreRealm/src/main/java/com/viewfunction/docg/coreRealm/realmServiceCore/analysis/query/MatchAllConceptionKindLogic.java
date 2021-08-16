package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

public class MatchAllConceptionKindLogic extends ConceptionKindMatchLogic{

    public MatchAllConceptionKindLogic(){
        super("*",ConceptionKindExistenceRule.MUST_HAVE);
    }
}
