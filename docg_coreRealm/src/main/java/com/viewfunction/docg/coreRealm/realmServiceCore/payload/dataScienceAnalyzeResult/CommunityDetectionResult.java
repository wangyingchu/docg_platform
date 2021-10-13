package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import java.util.List;
import java.util.Set;

public class CommunityDetectionResult {

    private String conceptionEntityUID;
    private int communityId;
    private List<Integer> intermediateCommunityIds;
    private Set<Integer> communityIds;

    public CommunityDetectionResult(String conceptionEntityUID,int communityId){
        this.conceptionEntityUID = conceptionEntityUID;
        this.communityId = communityId;
    }

    public CommunityDetectionResult(String conceptionEntityUID,int communityId,List<Integer> intermediateCommunityIds){
        this.conceptionEntityUID = conceptionEntityUID;
        this.communityId = communityId;
        this.intermediateCommunityIds = intermediateCommunityIds;
    }

    public CommunityDetectionResult(String conceptionEntityUID,Set<Integer> communityIds){
        this.conceptionEntityUID = conceptionEntityUID;
        this.communityIds = communityIds;
    }

    public String getConceptionEntityUID() {
        return conceptionEntityUID;
    }

    public int getCommunityId() {
        return communityId;
    }

    public List<Integer> getIntermediateCommunityIds() {
        return intermediateCommunityIds;
    }

    public void setIntermediateCommunityIds(List<Integer> intermediateCommunityIds) {
        this.intermediateCommunityIds = intermediateCommunityIds;
    }

    public String toString(){
        if(this.communityIds != null){
            return this.conceptionEntityUID+" -> communityIds: "+this.communityIds;
        }else{
            return this.conceptionEntityUID+" -> communityId: "+this.communityId;
        }
    }

    public Set<Integer> getCommunityIds() {
        return communityIds;
    }
}
