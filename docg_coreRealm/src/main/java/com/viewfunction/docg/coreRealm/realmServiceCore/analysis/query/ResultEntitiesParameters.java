package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import java.util.ArrayList;
import java.util.List;

public class ResultEntitiesParameters {

    private int pageSize;
    private int startPage;
    private int endPage;
    private int resultNumber;

    private boolean distinctMode = false;
    private List<SortingItem> sortingItems;

    public ResultEntitiesParameters() {
        sortingItems = new ArrayList<>();
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public int getResultNumber() {
        return resultNumber;
    }

    public void setResultNumber(int resultNumber) {
        this.resultNumber = resultNumber;
    }

    public boolean isDistinctMode() {
        return distinctMode;
    }

    public void setDistinctMode(boolean distinctMode) {
        this.distinctMode = distinctMode;
    }

    public List<SortingItem> getSortingItems() {
        return sortingItems;
    }

    public void addSortingAttribute(String sortAttribute, QueryParameters.SortingLogic sortingLogic) {
        this.sortingItems.add(new SortingItem(sortAttribute,sortingLogic));
    }
}
