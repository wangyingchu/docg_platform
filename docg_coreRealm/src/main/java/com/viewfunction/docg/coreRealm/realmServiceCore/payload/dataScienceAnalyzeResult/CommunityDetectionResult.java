package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

public class CommunityDetectionResult {

    private String conceptionEntityUID;
    private int communityId;
    private int[] intermediateCommunityIds;

    public CommunityDetectionResult(String conceptionEntityUID,int communityId){
        this.conceptionEntityUID = conceptionEntityUID;
        this.communityId = communityId;
    }

    public CommunityDetectionResult(String conceptionEntityUID,int communityId,int[] intermediateCommunityIds){
        this.conceptionEntityUID = conceptionEntityUID;
        this.communityId = communityId;
        this.intermediateCommunityIds = intermediateCommunityIds;
    }

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public int getCommunityId() {
        return communityId;
    }

    public int[] getIntermediateCommunityIds() {
        return intermediateCommunityIds;
    }

    public void setIntermediateCommunityIds(int[] intermediateCommunityIds) {
        this.intermediateCommunityIds = intermediateCommunityIds;
    }

    public String toString(){
        return this.conceptionEntityUID+" -> communityId: "+this.communityId;
    }
}
