package com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.SpeakerListenerLabelPropagationAlgorithmConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpeakerListenerLabelPropagationAlgorithmResult {

    private String graphName;
    private SpeakerListenerLabelPropagationAlgorithmConfig speakerListenerLabelPropagationAlgorithmConfig;
    private Date algorithmExecuteStartTime;
    private Date algorithmExecuteEndTime;
    private List<CommunityDetectionResult> communityDetectionResults;

    public SpeakerListenerLabelPropagationAlgorithmResult(String graphName, SpeakerListenerLabelPropagationAlgorithmConfig speakerListenerLabelPropagationAlgorithmConfig){
        this.graphName = graphName;
        this.speakerListenerLabelPropagationAlgorithmConfig = speakerListenerLabelPropagationAlgorithmConfig;
        this.communityDetectionResults = new ArrayList<>();
        this.algorithmExecuteStartTime = new Date();
    }

    public String getGraphName() {
        return graphName;
    }

    public SpeakerListenerLabelPropagationAlgorithmConfig getSpeakerListenerLabelPropagationAlgorithmConfig() {
        return speakerListenerLabelPropagationAlgorithmConfig;
    }

    public Date getAlgorithmExecuteStartTime() {
        return algorithmExecuteStartTime;
    }

    public Date getAlgorithmExecuteEndTime() {
        return algorithmExecuteEndTime;
    }

    public void setAlgorithmExecuteEndTime(Date algorithmExecuteEndTime) {
        this.algorithmExecuteEndTime = algorithmExecuteEndTime;
    }

    public List<CommunityDetectionResult> getLabelPropagationCommunities() {
        return communityDetectionResults;
    }
}
