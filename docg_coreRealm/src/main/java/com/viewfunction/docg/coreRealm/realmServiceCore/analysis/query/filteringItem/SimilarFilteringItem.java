package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem;

public class SimilarFilteringItem implements FilteringItem {

    public enum MatchingType {
        BeginWith, EndWith, Contain  //NOSONAR
    }

    private boolean reverseCondition = false;
    private String attributeName;
    private String attributeValue;
    private MatchingType matchingType;
    private boolean isCaseSensitive = true;

    public SimilarFilteringItem(String attributeName, String attributeValue, MatchingType matchingType) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.matchingType = matchingType;
    }

    public SimilarFilteringItem(String attributeName, String attributeValue, MatchingType matchingType, boolean isCaseSensitive) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.matchingType = matchingType;
        this.isCaseSensitive = isCaseSensitive;
    }

    @Override
    public String getFilteringLogic() {
        String filteringValueStr = "";
        String filteringLogic;
        if (isCaseSensitive) {
            switch (matchingType) {
                case BeginWith:
                    filteringValueStr = "'" + attributeValue + "%'";
                    break;
                case EndWith:
                    filteringValueStr = "'%" + attributeValue + "'";
                    break;
                case Contain:
                    filteringValueStr = "'%" + attributeValue + "%'";
                    break;
            }
            filteringLogic = attributeName + " LIKE " + filteringValueStr;
        } else {
            String attributeValueUpperCase = attributeValue.toUpperCase();
            switch (matchingType) {
                case BeginWith:
                    filteringValueStr = "'" + attributeValueUpperCase + "%'";
                    break;
                case EndWith:
                    filteringValueStr = "'%" + attributeValueUpperCase + "'";
                    break;
                case Contain:
                    filteringValueStr = "'%" + attributeValueUpperCase + "%'";
                    break;
            }
            filteringLogic = attributeName + ".toUpperCase()" + " LIKE " + filteringValueStr;
        }
        if (!reverseCondition) {
            return filteringLogic;
        } else {
            return "NOT(" + filteringLogic + ")";
        }
    }

    @Override
    public void reverseCondition() {
        this.reverseCondition = true;
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
