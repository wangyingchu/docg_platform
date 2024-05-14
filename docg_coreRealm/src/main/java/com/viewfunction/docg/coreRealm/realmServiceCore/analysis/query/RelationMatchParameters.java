package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import java.util.ArrayList;
import java.util.List;

public class RelationMatchParameters {

    public enum MatchingLogic { AND, OR }
    private RelationMatchingItem defaultRelationMatchingItem;
    private MatchingLogic defaultRelationMatchingLogic;
    private List<RelationMatchingItem> andRelationMatchingItemList;
    private List<RelationMatchingItem> orRelationMatchingItemList;

    public RelationMatchParameters(){
        this.andRelationMatchingItemList = new ArrayList<>();
        this.orRelationMatchingItemList = new ArrayList<>();
        this.defaultRelationMatchingLogic = MatchingLogic.AND;
    }

    public void setDefaultMatchingItem(RelationMatchingItem relationMatchingItem, MatchingLogic matchingLogic) {
        this.defaultRelationMatchingItem = relationMatchingItem;
        if(matchingLogic != null){
            this.defaultRelationMatchingLogic = matchingLogic;
        }
    }

    public RelationMatchingItem getDefaultMatchingItem() {return defaultRelationMatchingItem;}

    public MatchingLogic getDefaultRelationMatchingLogic() {
        return defaultRelationMatchingLogic;
    }

    public List<RelationMatchingItem> getAndRelationMatchingItemList() {
        return andRelationMatchingItemList;
    }

    public List<RelationMatchingItem> getOrRelationMatchingItemList() {
        return orRelationMatchingItemList;
    }

    public void addMatchingItem(RelationMatchingItem relationMatchingItem, MatchingLogic matchingLogic) {
        if (this.defaultRelationMatchingItem == null) {
            this.defaultRelationMatchingItem = relationMatchingItem;
            this.defaultRelationMatchingLogic = matchingLogic;
        } else {
            switch (matchingLogic) {
                case AND:
                    this.getAndRelationMatchingItemList().add(relationMatchingItem);
                    break;
                case OR:
                    this.getOrRelationMatchingItemList().add(relationMatchingItem);
                    break;
            }
        }
    }
}
