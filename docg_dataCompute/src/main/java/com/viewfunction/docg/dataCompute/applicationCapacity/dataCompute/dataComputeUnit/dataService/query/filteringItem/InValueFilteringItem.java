package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.filteringItem;

import java.util.List;

public class InValueFilteringItem implements FilteringItem {

    private boolean reverseCondition=false;
    private String attributeName;
    private List<Object> attributeValue;

    public InValueFilteringItem(String attributeName,List<Object> attributeValue){
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

    public List<Object> getAttributeValues() {
        return attributeValue;
    }
}
