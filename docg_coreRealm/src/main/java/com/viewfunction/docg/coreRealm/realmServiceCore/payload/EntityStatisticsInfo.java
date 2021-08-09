package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

public class EntityStatisticsInfo {

    public enum kindType {ConceptionKind,RelationKind}

    private String entityKindName;
    private long entitiesCount;
    private kindType entityKindType;
    private boolean isSystemKind;

    public EntityStatisticsInfo(String entityKindName,kindType entityKindType,boolean isSystemKind,long entitiesCount){
        this.entityKindName = entityKindName;
        this.entitiesCount = entitiesCount;
        this.entityKindType = entityKindType;
        this.isSystemKind = isSystemKind;
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

}
