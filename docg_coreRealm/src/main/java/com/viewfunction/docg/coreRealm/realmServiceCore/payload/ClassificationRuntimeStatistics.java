package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class ClassificationRuntimeStatistics {
    private int childClassificationsCount;
    private int offspringClassificationsCount;
    private int relatedConceptionKindCount;
    private int relatedRelationKindCount;
    private int relatedAttributeKindCount;
    private int relatedAttributesViewKindCount;
    private int relatedConceptionEntityCount;

    public ClassificationRuntimeStatistics(){}

    public int getChildClassificationsCount() {
        return childClassificationsCount;
    }

    public void setChildClassificationsCount(int childClassificationsCount) {
        this.childClassificationsCount = childClassificationsCount;
    }

    public int getOffspringClassificationsCount() {
        return offspringClassificationsCount;
    }

    public void setOffspringClassificationsCount(int offspringClassificationsCount) {
        this.offspringClassificationsCount = offspringClassificationsCount;
    }

    public int getRelatedConceptionKindCount() {
        return relatedConceptionKindCount;
    }

    public void setRelatedConceptionKindCount(int relatedConceptionKindCount) {
        this.relatedConceptionKindCount = relatedConceptionKindCount;
    }

    public int getRelatedRelationKindCount() {
        return relatedRelationKindCount;
    }

    public void setRelatedRelationKindCount(int relatedRelationKindCount) {
        this.relatedRelationKindCount = relatedRelationKindCount;
    }

    public int getRelatedAttributeKindCount() {
        return relatedAttributeKindCount;
    }

    public void setRelatedAttributeKindCount(int relatedAttributeKindCount) {
        this.relatedAttributeKindCount = relatedAttributeKindCount;
    }

    public int getRelatedAttributesViewKindCount() {
        return relatedAttributesViewKindCount;
    }

    public void setRelatedAttributesViewKindCount(int relatedAttributesViewKindCount) {
        this.relatedAttributesViewKindCount = relatedAttributesViewKindCount;
    }

    public int getRelatedConceptionEntityCount() {
        return relatedConceptionEntityCount;
    }

    public void setRelatedConceptionEntityCount(int relatedConceptionEntityCount) {
        this.relatedConceptionEntityCount = relatedConceptionEntityCount;
    }
}
