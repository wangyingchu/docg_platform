package com.viewfunction.docg.dataCompute.dataComputeServiceCore.analysis.query.filteringItem;

public class NotEqualFilteringItem implements FilteringItem {

    private boolean reverseCondition=false;
    private String attributeName;
    private Object attributeValue;

    public NotEqualFilteringItem(String attributeName, Object attributeValue){
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
