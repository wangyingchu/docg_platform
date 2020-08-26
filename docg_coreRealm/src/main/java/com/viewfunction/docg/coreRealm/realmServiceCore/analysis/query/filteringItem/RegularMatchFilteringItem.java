package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem;

public class RegularMatchFilteringItem implements FilteringItem{
    private boolean reverseCondition=false;
    private String attributeName;
    private String attributeValue;
    public RegularMatchFilteringItem(String attributeName,String attributeValue){
        this.attributeName=attributeName;
        this.attributeValue=attributeValue;
    }

    @Override
    public String getFilteringLogic() {
        String filteringLogic=attributeName+" MATCHES '"+attributeValue+"'";
        if(!reverseCondition){
            return filteringLogic;
        }else{
            return "NOT("+filteringLogic+")";
        }
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
