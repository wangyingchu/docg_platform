package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class KindAttributeDistributionInfo {

    private String[] kindNames;
    private String[] attributeNames;

    public KindAttributeDistributionInfo(String[] kindNames,String[] attributeNames){
        this.kindNames = kindNames;
        this.attributeNames = attributeNames;
    }

    public String[] getKindNames() {
        return kindNames;
    }

    public String[] getAttributeNames() {
        return attributeNames;
    }
}
