package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem;

public class GreaterThanEqualFilteringItem  implements FilteringItem{

    private boolean reverseCondition=false;
    private String attributeName;
    private Object attributeValue;

    public GreaterThanEqualFilteringItem(String attributeName, Object attributeValue){
        this.attributeName=attributeName;
        this.attributeValue=attributeValue;
    }

    @Override
    public void reverseCondition() {
        this.reverseCondition=true;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public boolean isReversedCondition(){
        return reverseCondition;
    }

    public Object getAttributeValue() {
        return attributeValue;
    }
}
