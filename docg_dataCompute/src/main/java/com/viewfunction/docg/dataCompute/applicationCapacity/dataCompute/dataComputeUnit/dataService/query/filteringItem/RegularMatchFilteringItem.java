package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.filteringItem;

public class RegularMatchFilteringItem implements FilteringItem {

    private boolean reverseCondition=false;
    private String attributeName;
    private String attributeValue;

    public RegularMatchFilteringItem(String attributeName,String attributeValue){
        this.attributeName=attributeName;
        this.setAttributeValue(attributeValue);
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

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
