package com.viewfunction.docg.dataCompute.dataComputeUnit.dataService.query.filteringItem;

public class BetweenFilteringItem implements FilteringItem {

    private boolean reverseCondition=false;
    private String attributeName;
    private Object attributeFromValue;
    private Object attributeToValue;

    public BetweenFilteringItem(String attributeName, Object attributeFromValue,Object attributeToValue){
        this.attributeName=attributeName;
        this.attributeFromValue=attributeFromValue;
        this.attributeToValue=attributeToValue;
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

    public Object getAttributeFromValue() {
        return attributeFromValue;
    }

    public Object getAttributeToValue() {
        return attributeToValue;
    }
}
