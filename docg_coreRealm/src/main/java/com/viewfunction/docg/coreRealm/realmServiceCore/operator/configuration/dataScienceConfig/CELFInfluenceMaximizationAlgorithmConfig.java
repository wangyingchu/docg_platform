package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

public class CELFInfluenceMaximizationAlgorithmConfig  extends DataScienceBaseAlgorithmConfig {

    private int seedSetSize;
    private int monteCarloSimulations = 1000;
    private float propagationProbability = 0.1f;

    public CELFInfluenceMaximizationAlgorithmConfig(int seedSetSize){
        this.seedSetSize = seedSetSize;
    }

    public CELFInfluenceMaximizationAlgorithmConfig(int seedSetSize,int monteCarloSimulations,float propagationProbability){
        this.seedSetSize = seedSetSize;
        this.monteCarloSimulations = monteCarloSimulations;
        this.propagationProbability = propagationProbability;
    }

    public int getSeedSetSize() {
        return seedSetSize;
    }

    public int getMonteCarloSimulations() {
        return monteCarloSimulations;
    }

    public float getPropagationProbability() {
        return propagationProbability;
    }
}
