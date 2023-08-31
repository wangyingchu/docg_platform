package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.time.ZonedDateTime;

public class AttributesViewKindMetaInfo {
    private String kindName;
    private String kindDesc;
    private String kindUID;
    private ZonedDateTime createDate;
    private ZonedDateTime lastModifyDate;
    private String creatorId;
    private String dataOrigin;
    private String viewKindDataForm;
    private int containerConceptionKindCount;
    private int containsAttributeKindCount;
    public AttributesViewKindMetaInfo(String kindName, String kindDesc, String kindUID, String viewKindDataForm, ZonedDateTime createDate,
                                      ZonedDateTime lastModifyDate, String creatorId, String dataOrigin){
        this.kindName = kindName;
        this.kindDesc = kindDesc;
        this.kindUID = kindUID;
        this.viewKindDataForm = viewKindDataForm;
        this.createDate = createDate;
        this.lastModifyDate = lastModifyDate;
        this.creatorId = creatorId;
        this.dataOrigin = dataOrigin;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public String getKindDesc() {
        return kindDesc;
    }

    public void setKindDesc(String kindDesc) {
        this.kindDesc = kindDesc;
    }

    public String getKindUID() {
        return kindUID;
    }

    public void setKindUID(String kindUID) {
        this.kindUID = kindUID;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    public ZonedDateTime getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(ZonedDateTime lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getDataOrigin() {
        return dataOrigin;
    }

    public void setDataOrigin(String dataOrigin) {
        this.dataOrigin = dataOrigin;
    }

    public String getViewKindDataForm() {
        return viewKindDataForm;
    }

    public void setViewKindDataForm(String viewKindDataForm) {
        this.viewKindDataForm = viewKindDataForm;
    }

    public int getContainerConceptionKindCount() {
        return containerConceptionKindCount;
    }

    public void setContainerConceptionKindCount(int containerConceptionKindCount) {
        this.containerConceptionKindCount = containerConceptionKindCount;
    }

    public int getContainsAttributeKindCount() {
        return containsAttributeKindCount;
    }

    public void setContainsAttributeKindCount(int containsAttributeKindCount) {
        this.containsAttributeKindCount = containsAttributeKindCount;
    }
}
