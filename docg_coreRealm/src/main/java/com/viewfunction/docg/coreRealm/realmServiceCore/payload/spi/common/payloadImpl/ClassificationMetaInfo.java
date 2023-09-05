package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import java.time.ZonedDateTime;

public class ClassificationMetaInfo {
    private String classificationName;
    private String classificationDesc;
    private ZonedDateTime createDate;
    private ZonedDateTime lastModifyDate;
    private String creatorId;
    private String dataOrigin;
    private String parentClassificationName;
    private int childClassificationCount;
    private boolean rootClassification;

    public ClassificationMetaInfo(String classificationName, String classificationDesc, ZonedDateTime createDate,
                                  ZonedDateTime lastModifyDate, String creatorId, String dataOrigin,String parentClassificationName,int childClassificationCount,boolean rootClassification){
        this.classificationName = classificationName;
        this.classificationDesc = classificationDesc;
        this.createDate = createDate;
        this.lastModifyDate = lastModifyDate;
        this.creatorId = creatorId;
        this.dataOrigin = dataOrigin;
        this.parentClassificationName = parentClassificationName;
        this.childClassificationCount = childClassificationCount;
        this.rootClassification = rootClassification;
    }

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    public String getClassificationDesc() {
        return classificationDesc;
    }

    public void setClassificationDesc(String classificationDesc) {
        this.classificationDesc = classificationDesc;
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

    public String getParentClassificationName() {
        return parentClassificationName;
    }

    public void setParentClassificationName(String parentClassificationName) {
        this.parentClassificationName = parentClassificationName;
    }

    public int getChildClassificationCount() {
        return childClassificationCount;
    }

    public void setChildClassificationCount(int childClassificationCount) {
        this.childClassificationCount = childClassificationCount;
    }

    public boolean isRootClassification() {
        return rootClassification;
    }

    public void setRootClassification(boolean rootClassification) {
        this.rootClassification = rootClassification;
    }
}
