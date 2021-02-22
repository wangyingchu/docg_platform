package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataService.query.filteringItem;

public class SimilarFilteringItem implements FilteringItem {

    public MatchingType getMatchingType() {
        return matchingType;
    }

    public void setMatchingType(MatchingType matchingType) {
        this.matchingType = matchingType;
    }

    public enum MatchingType {
        BeginWith, EndWith, Contain
    }

    private boolean reverseCondition = false;
    private String attributeName;
    private String attributeValue;
    private MatchingType matchingType = MatchingType.Contain;
    private boolean isCaseSensitive = true;

    public SimilarFilteringItem(String attributeName, String attributeValue, MatchingType matchingType) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.setMatchingType(matchingType);
    }

    public SimilarFilteringItem(String attributeName, String attributeValue, MatchingType matchingType, boolean isCaseSensitive) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.setMatchingType(matchingType);
        this.isCaseSensitive = isCaseSensitive;
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
