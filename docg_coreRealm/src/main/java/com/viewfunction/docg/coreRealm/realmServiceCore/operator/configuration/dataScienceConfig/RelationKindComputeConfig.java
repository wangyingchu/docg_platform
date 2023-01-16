package com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig;

import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;

import java.util.HashSet;
import java.util.Set;

public class RelationKindComputeConfig {

    private String relationKindName;
    private DataScienceOperator.ComputeOrientation relationComputeOrientation = DataScienceOperator.ComputeOrientation.NATURAL;
    private DataScienceOperator.ComputeAggregation relationComputeAggregation = DataScienceOperator.ComputeAggregation.NONE;
    private Set<String> relationKindAttributes;

    public RelationKindComputeConfig(String relationKindName){
        this.relationKindName = relationKindName;
        this.setRelationKindAttributes(new HashSet<>());
    }

    public RelationKindComputeConfig(String relationKindName, DataScienceOperator.ComputeOrientation relationComputeOrientation){
        this.relationKindName = relationKindName;
        this.relationComputeOrientation = relationComputeOrientation;
        this.setRelationKindAttributes(new HashSet<>());
    }

    public RelationKindComputeConfig(String relationKindName, DataScienceOperator.ComputeAggregation relationComputeAggregation){
        this.relationKindName = relationKindName;
        this.relationComputeAggregation = relationComputeAggregation;
        this.setRelationKindAttributes(new HashSet<>());
    }

    public RelationKindComputeConfig(String relationKindName, DataScienceOperator.ComputeOrientation relationComputeOrientation, DataScienceOperator.ComputeAggregation relationComputeAggregation){
        this.relationKindName = relationKindName;
        this.relationComputeOrientation = relationComputeOrientation;
        this.relationComputeAggregation = relationComputeAggregation;
        this.setRelationKindAttributes(new HashSet<>());
    }

    public String getRelationKindName() {
        return relationKindName;
    }

    public DataScienceOperator.ComputeOrientation getRelationComputeOrientation() {
        return relationComputeOrientation;
    }

    public void setRelationComputeOrientation(DataScienceOperator.ComputeOrientation relationComputeOrientation) {
        if(relationComputeOrientation != null){
            this.relationComputeOrientation = relationComputeOrientation;
        }
    }

    public DataScienceOperator.ComputeAggregation getRelationComputeAggregation() {
        return relationComputeAggregation;
    }

    public void setRelationComputeAggregation(DataScienceOperator.ComputeAggregation relationComputeAggregation) {
        if(relationComputeAggregation != null){
            this.relationComputeAggregation = relationComputeAggregation;
        }
    }

    public Set<String> getRelationKindAttributes() {
        return relationKindAttributes;
    }

    public void setRelationKindAttributes(Set<String> relationKindAttributes) {
        this.relationKindAttributes = relationKindAttributes;
    }
}
