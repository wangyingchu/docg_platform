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

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public void setKindDesc(String kindDesc) {
        this.kindDesc = kindDesc;
    }

    public void setKindUID(String kindUID) {
        this.kindUID = kindUID;
    }
}
