package com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.query.filteringItem;

public class NullValueFilteringItem implements FilteringItem {

    private boolean reverseCondition=false;
    private String attributeName;

    public NullValueFilteringItem(String attributeName){
        this.attributeName=attributeName;
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
}
