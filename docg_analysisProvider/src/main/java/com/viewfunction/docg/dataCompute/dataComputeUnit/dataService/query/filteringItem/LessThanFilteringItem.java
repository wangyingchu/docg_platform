package com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.query.filteringItem;

public class LessThanFilteringItem implements FilteringItem {

    private boolean reverseCondition=false;
    private String attributeName;
    private Object attributeValue;

    public LessThanFilteringItem(String attributeName, Object attributeValue){
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