package com.viewfunction.docg.dataCompute.computeServiceCore.analysis.query;

public class SortingItem {

    private String attributeName;
    private QueryParameters.SortingLogic sortingLogic = QueryParameters.SortingLogic.ASC;

    public SortingItem(){}

    public SortingItem(String attributeName, QueryParameters.SortingLogic sortingLogic){
        this.attributeName = attributeName;
        this.sortingLogic = sortingLogic;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public QueryParameters.SortingLogic getSortingLogic() {
        return sortingLogic;
    }

    public void setSortingLogic(QueryParameters.SortingLogic sortingLogic) {
        this.sortingLogic = sortingLogic;
    }
}
