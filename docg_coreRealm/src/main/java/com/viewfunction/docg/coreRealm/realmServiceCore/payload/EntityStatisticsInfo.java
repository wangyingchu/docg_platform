package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.time.ZonedDateTime;

public class EntityStatisticsInfo {

    public enum kindType {ConceptionKind,RelationKind}

    private String entityKindName;
    private long entitiesCount;
    private kindType entityKindType;
    private boolean isSystemKind;
    private String entityKindDesc;
    private String entityKindUID;
    private ZonedDateTime createDateTime;
    private ZonedDateTime lastModifyDateTime;
    private String creatorId;
    private String dataOrigin;

    public EntityStatisticsInfo(String entityKindName,kindType entityKindType,boolean isSystemKind,long entitiesCount){
        this.entityKindName = entityKindName;
        this.setEntitiesCount(entitiesCount);
        this.entityKindType = entityKindType;
        this.isSystemKind = isSystemKind;
    }

    public EntityStatisticsInfo(String entityKindName,kindType entityKindType,boolean isSystemKind,long entitiesCount,
                                String entityKindDesc,String entityKindUID,ZonedDateTime createDateTime,ZonedDateTime lastModifyDateTime,
                                String creatorId,String dataOrigin){
        this.entityKindName = entityKindName;
        this.setEntitiesCount(entitiesCount);
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

    public void setEntitiesCount(long entitiesCount) {
        this.entitiesCount = entitiesCount;
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

    public ZonedDateTime getCreateDateTime() {
        return createDateTime;
    }

    public ZonedDateTime getLastModifyDateTime() {
        return lastModifyDateTime;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getDataOrigin() {
        return dataOrigin;
    }
}
