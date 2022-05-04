package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Date;

public class EntityStatisticsInfo {


    public enum kindType {ConceptionKind,RelationKind}

    private String entityKindName;
    private long entitiesCount;
    private kindType entityKindType;
    private boolean isSystemKind;
    private String entityKindDesc;
    private String entityKindUID;
    private Date createDateTime;
    private Date lastModifyDateTime;
    private String creatorId;
    private String dataOrigin;

    public EntityStatisticsInfo(String entityKindName,kindType entityKindType,boolean isSystemKind,long entitiesCount){
        this.entityKindName = entityKindName;
        this.entitiesCount = entitiesCount;
        this.entityKindType = entityKindType;
        this.isSystemKind = isSystemKind;
    }

    public EntityStatisticsInfo(String entityKindName,kindType entityKindType,boolean isSystemKind,long entitiesCount,
                                String entityKindDesc,String entityKindUID,Date createDateTime,Date lastModifyDateTime,
                                String creatorId,String dataOrigin){
        this.entityKindName = entityKindName;
        this.entitiesCount = entitiesCount;
        this.entityKindType = entityKindType;
        this.isSystemKind = isSystemKind;
        this.entityKindDesc = entityKindDesc;
        this.entityKindUID = entityKindUID;
        this.createDateTime = createDateTime;
        this.lastModifyDateTime = lastModifyDateTime;
        this.creatorId = creatorId;
        this.dataOrigin = dataOrigin;
    }

    public String getEntityKindName() {
        return entityKindName;
    }

    public long getEntitiesCount() {
        return entitiesCount;
    }

    public kindType getEntityKindType() {
        return entityKindType;
    }

    public boolean isSystemKind() {
        return isSystemKind;
    }

    public String getEntityKindDesc() {
        return entityKindDesc;
    }

    public String getEntityKindUID() {
        return entityKindUID;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public Date getLastModifyDateTime() {
        return lastModifyDateTime;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getDataOrigin() {
        return dataOrigin;
    }
}
