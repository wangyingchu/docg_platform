package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.time.ZonedDateTime;

public class AttributeSystemInfo {

    private String attributeName;
    private String dataType;
    private boolean usedInIndex;
    private boolean uniqueAttribute;
    private boolean constraintAttribute;
    private ZonedDateTime createDate;

    public AttributeSystemInfo(String attributeName,String dataType,boolean usedInIndex,boolean uniqueAttribute,
                               boolean constraintAttribute){
        this.attributeName = attributeName;
        this.dataType = dataType;
        this.usedInIndex = usedInIndex;
        this.uniqueAttribute = uniqueAttribute;
        this.constraintAttribute = constraintAttribute;
    }

    public AttributeSystemInfo(String attributeName,String dataType,boolean usedInIndex,boolean uniqueAttribute,
                               boolean constraintAttribute,ZonedDateTime createDate){
        this.attributeName = attributeName;
        this.dataType = dataType;
        this.usedInIndex = usedInIndex;
        this.uniqueAttribute = uniqueAttribute;
        this.constraintAttribute = constraintAttribute;
        this.createDate = createDate;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getDataType() {
        return dataType;
    }

    public boolean isUsedInIndex() {
        return usedInIndex;
    }

    public boolean isUniqueAttribute() {
        return uniqueAttribute;
    }

    public boolean isConstraintAttribute() {
        return constraintAttribute;
    }

    public ZonedDateTime getCreateDate(){
        return createDate;
    }
}
