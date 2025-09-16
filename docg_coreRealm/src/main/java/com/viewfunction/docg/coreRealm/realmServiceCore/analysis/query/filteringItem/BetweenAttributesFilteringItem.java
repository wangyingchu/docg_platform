package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem;

public class BetweenAttributesFilteringItem implements FilteringItem{

    private boolean reverseCondition=false;
    private String fromAttributeName;
    private String toAttributeName;
    private Object attributeValue;

    public BetweenAttributesFilteringItem(String fromAttributeName, String toAttributeName,Object attributeValue){
        this.fromAttributeName=fromAttributeName;
        this.toAttributeName=toAttributeName;
        this.attributeValue=attributeValue;
    }

    @Override
    public void reverseCondition() {
        this.reverseCondition=true;
    }

    @Override
    public String getAttributeName() {
        return fromAttributeName + "-" + toAttributeName;
    }

    @Override
    public boolean isReversedCondition(){
        return reverseCondition;
    }

    public Object getAttributeValue(){
        return attributeValue;
    }

    public String getFromAttributeName(){
        return fromAttributeName;
    }

    public String getToAttributeName(){
        return toAttributeName;
    }
}
