package com.viewfunction.docg.analysisProvider.feature.ignite.memoryTable.query.filteringItem;

public interface FilteringItem {

    public void reverseCondition();
    public String getAttributeName();
    public boolean isReversedCondition();

}
