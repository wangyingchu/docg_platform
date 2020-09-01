package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;

import java.util.*;

public class QueryParameters {

    private int pageSize;
    private int startPage;
    private int endPage;
    private int resultNumber;
    private String entityKind;
    private String customQuerySentence;
    private boolean distinctMode = false;
    private List<SortingItem> sortingItems;
    private FilteringItem defaultFilteringItem;
    public enum FilteringLogic { AND, OR }
    public enum SortingLogic { ASC, DESC }
    private List<FilteringItem> andFilteringItemList;
    private List<FilteringItem> orFilteringItemList;

    public QueryParameters() {
        andFilteringItemList = new ArrayList<>();
        orFilteringItemList = new ArrayList<>();
        sortingItems = new ArrayList<>();
    }

    public String getEntityKind() {
        return entityKind;
    }

    public void setEntityKind(String entityKind) {
        this.entityKind = entityKind;
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

    public void setDefaultFilteringItem(FilteringItem filteringItem) {
        this.defaultFilteringItem = filteringItem;
    }

    public void addFilteringItem(FilteringItem filteringItem, FilteringLogic filteringLogic) {
    if (this.defaultFilteringItem == null) {
            this.defaultFilteringItem = filteringItem;
        } else {
            switch (filteringLogic) {
                case AND:
                    this.getAndFilteringItemsList().add(filteringItem);
                    break;
                case OR:
                    this.getOrFilteringItemsList().add(filteringItem);
                    break;
            }
        }
    }

    public FilteringItem getDefaultFilteringItem() {
        return defaultFilteringItem;
    }

    public List<FilteringItem> getAndFilteringItemsList() {
        return andFilteringItemList;
    }

    public List<FilteringItem> getOrFilteringItemsList() {
        return orFilteringItemList;
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

    public void addSortingAttribute(String sortAttribute,SortingLogic sortingLogic) {
        this.sortingItems.add(new SortingItem(sortAttribute,sortingLogic));
    }

    public String getCustomQuerySentence() {
        return customQuerySentence;
    }

    public void setCustomQuerySentence(String customQuerySentence) {
        this.customQuerySentence = customQuerySentence;
    }
}
