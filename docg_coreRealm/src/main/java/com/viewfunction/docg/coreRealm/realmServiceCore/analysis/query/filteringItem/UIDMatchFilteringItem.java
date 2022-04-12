package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem;

public class UIDMatchFilteringItem implements FilteringItem{

    private boolean reverseCondition=false;
    private String uidValue;

    public UIDMatchFilteringItem(String uidValue){
        this.setUidValue(uidValue);
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
    public boolean isReversedCondition(){
        return reverseCondition;
    }

    public String getUidValue() {
        return uidValue;
    }

    public void setUidValue(String uidValue) {
        this.uidValue = uidValue;
    }
}
