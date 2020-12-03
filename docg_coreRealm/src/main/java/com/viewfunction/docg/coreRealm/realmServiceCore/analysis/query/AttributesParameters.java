package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;

import java.util.ArrayList;
import java.util.List;

public class AttributesParameters {

    private FilteringItem defaultFilteringItem;
    private List<FilteringItem> andFilteringItemList;
    private List<FilteringItem> orFilteringItemList;
    private boolean distinctMode = false;

    public boolean isDistinctMode() {
        return distinctMode;
    }

    public void setDistinctMode(boolean distinctMode) {
        this.distinctMode = distinctMode;
    }

    public AttributesParameters() {
        andFilteringItemList = new ArrayList<>();
        orFilteringItemList = new ArrayList<>();
    }

    public void setDefaultFilteringItem(FilteringItem filteringItem) {
        this.defaultFilteringItem = filteringItem;
    }

    public void addFilteringItem(FilteringItem filteringItem, QueryParameters.FilteringLogic filteringLogic) {
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
}
