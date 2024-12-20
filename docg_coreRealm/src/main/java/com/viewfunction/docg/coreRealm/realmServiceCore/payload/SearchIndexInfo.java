package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchIndexInfo {

    private String indexName;
    private float populationPercent;
    private String searchKindName;
    private Set<String> indexedAttributeNames ;

    public SearchIndexInfo(String indexName,float populationPercent,String indexType,List kindNames,List attributeNames){
        this.indexName = indexName;
        this.populationPercent = populationPercent;
        this.searchKindName = kindNames.get(0).toString();
        indexedAttributeNames = new HashSet<>();
        for(Object currentAttribute:attributeNames){
            indexedAttributeNames.add(currentAttribute.toString());
        }
    }

    public String getIndexName() {
        return indexName;
    }

    public float getPopulationPercent() {
        return populationPercent;
    }

    public String getSearchKindName() {
        return searchKindName;
    }

    public Set<String> getIndexedAttributeNames() {
        return indexedAttributeNames;
    }
}
