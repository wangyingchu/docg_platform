package com.viewfunction.docg.dataCollector.payload;

public class RelationEntityMetaInfo {

    private String relationEntityUID;
    private String sourceEntityUID;
    private String targetEntityUID;
    private String relationKind;

    public RelationEntityMetaInfo(String relationKind,String relationEntityUID,String sourceEntityUID,String targetEntityUID){
        this.relationKind = relationKind;
        this.relationEntityUID = relationEntityUID;
        this.sourceEntityUID = sourceEntityUID;
        this.targetEntityUID = targetEntityUID;
    }

    public String getRelationEntityUID() {
        return relationEntityUID;
    }

    public String getSourceEntityUID() {
        return sourceEntityUID;
    }

    public String getTargetEntityUID() {
        return targetEntityUID;
    }

    public String getRelationKind() {
        return relationKind;
    }
}
