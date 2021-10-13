package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

public class GreedyInfluenceMaximizationAlgorithmConfig extends DataScienceBaseAlgorithmConfig {

    private int seedSetSize;
    private int monteCarloSimulations = 1000;
    private float propagationProbability = 0.1f;

    public GreedyInfluenceMaximizationAlgorithmConfig(int seedSetSize){
        this.seedSetSize = seedSetSize;
    }

    public GreedyInfluenceMaximizationAlgorithmConfig(int seedSetSize,int monteCarloSimulations,float propagationProbability){
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
