package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class AnalyzableGraphAttributeInfo {

    private String attributeName;
    private String attributeDesc;

    public AnalyzableGraphAttributeInfo(String attributeName,String attributeDesc){
        this.attributeName = attributeName;
        this.attributeDesc = attributeDesc;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeDesc() {
        return attributeDesc;
    }
}
