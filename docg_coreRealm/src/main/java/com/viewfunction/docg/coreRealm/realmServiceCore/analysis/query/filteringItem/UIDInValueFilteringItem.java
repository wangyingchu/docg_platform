package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem;

import java.util.Set;

public class UIDInValueFilteringItem implements FilteringItem{

    private boolean reverseCondition=false;
    private Set<String> uidValues;

    public UIDInValueFilteringItem(Set<String> uidValues){
        this.uidValues = uidValues;
    }

    @Override
    public void reverseCondition() {
        this.reverseCondition=true;
    }

    @Override
    public String getAttributeName() {
        return null;
    }

    @Override
    public boolean isReversedCondition() {
        return reverseCondition;
    }

    public Set<String> getUidValues() {
        return uidValues;
    }

    public void setUidValues(Set<String> uidValues) {
        this.uidValues = uidValues;
    }
}
