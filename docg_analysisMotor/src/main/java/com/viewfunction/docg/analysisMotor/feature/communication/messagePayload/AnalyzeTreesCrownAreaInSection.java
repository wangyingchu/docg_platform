package com.viewfunction.docg.analysisMotor.feature.communication.messagePayload;

public class AnalyzeTreesCrownAreaInSection extends AnalyseRequest {

    public AnalyzeTreesCrownAreaInSection(){}

    public AnalyzeTreesCrownAreaInSection(String treeCrownType,int areaSize){
        this.setTreeCrownType(treeCrownType);
        this.setAreaSize(areaSize);
    }

    private String treeCrownType;
    private int areaSize;

    public String getTreeCrownType() {
        return treeCrownType;
    }

    public void setTreeCrownType(String treeCrownType) {
        this.treeCrownType = treeCrownType;
    }

    public int getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(int areaSize) {
        this.areaSize = areaSize;
    }
}
