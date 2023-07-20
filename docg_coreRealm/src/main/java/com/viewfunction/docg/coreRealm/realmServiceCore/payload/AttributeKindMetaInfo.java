package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.time.ZonedDateTime;

public class AttributeKindMetaInfo{
    private String kindName;
    private String kindDesc;
    private String kindUID;
    private ZonedDateTime createDate;
    private ZonedDateTime lastModifyDate;
    private String creatorId;
    private String dataOrigin;
    private String attributeDataType;

    public AttributeKindMetaInfo(String kindName,String kindDesc,String kindUID,String attributeDataType,ZonedDateTime createDate,
                        ZonedDateTime lastModifyDate,String creatorId,String dataOrigin){
        this.kindName = kindName;
        this.kindDesc = kindDesc;
        this.kindUID = kindUID;
        this.attributeDataType = attributeDataType;
        this.createDate = createDate;
        this.lastModifyDate = lastModifyDate;
        this.creatorId = creatorId;
        this.dataOrigin = dataOrigin;
    }

    public String getKindName() {
        return this.kindName;
    }

    public String getKindDesc() {
        return this.kindDesc;
    }

    public String getKindUID() {
        return this.kindUID;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public ZonedDateTime getLastModifyDate() {
        return lastModifyDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getDataOrigin() {
        return dataOrigin;
    }

    public String getAttributeDataType() {
        return attributeDataType;
    }
}
