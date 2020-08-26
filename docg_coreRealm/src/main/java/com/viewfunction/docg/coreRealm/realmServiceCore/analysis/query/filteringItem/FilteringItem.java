package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem;

public interface FilteringItem {

    public String getFilteringLogic();
    public void reverseCondition();
    public String getAttributeName();
    public boolean isReversedCondition();
}
