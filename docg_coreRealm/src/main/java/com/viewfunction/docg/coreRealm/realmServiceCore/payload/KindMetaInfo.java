package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class KindMetaInfo {

    private String kindName;
    private String kindDesc;
    private String kindUID;

    public KindMetaInfo(String kindName,String kindDesc,String kindUID){
        this.kindName = kindName;
        this.kindDesc = kindDesc;
        this.kindUID = kindUID;
    }

    public String getKindName() {
        return this.kindName;
    }

    public String getKindDesc() {
        return this.kindDesc;
    }

    public String getKindUID() {
        return this.kindUID;
    }
}
