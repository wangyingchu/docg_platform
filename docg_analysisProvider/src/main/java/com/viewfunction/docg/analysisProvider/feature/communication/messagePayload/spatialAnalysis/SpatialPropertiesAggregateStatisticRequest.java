package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis;

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;

public class SpatialPropertiesAggregateStatisticRequest extends AnalyseRequest {

    public CalculationOperator getCalculationOperator() {
        return calculationOperator;
    }

    public void setCalculationOperator(CalculationOperator calculationOperator) {
        this.calculationOperator = calculationOperator;
    }

    public enum ObjectAggregationType {
        SUM,AVG,STDDEV,COUNT,MAX,MIN,VARIANCE
    }

    public enum CalculationOperator {
        Add, Subtract, Multiply, Divide
    }

    private String subjectConception;
    private String objectConception;
    private SpatialCommonConfig.PredicateType predicateType;

    private String subjectIdentityProperty;
    private String[] subjectReturnProperties;
    private String subjectCalculationProperty;

    private String objectCalculationProperty;
    private ObjectAggregationType objectAggregationType;

    private CalculationOperator calculationOperator;
    private String statisticResultProperty;

    public String getSubjectConception() {
        return subjectConception;
    }

    public void setSubjectConception(String subjectConception) {
        this.subjectConception = subjectConception;
    }

    public String getObjectConception() {
        return objectConception;
    }

    public void setObjectConception(String objectConception) {
        this.objectConception = objectConception;
    }

    public SpatialCommonConfig.PredicateType getPredicateType() {
        return predicateType;
    }

    public void setPredicateType(SpatialCommonConfig.PredicateType predicateType) {
        this.predicateType = predicateType;
    }

    public String getSubjectIdentityProperty() {
        return subjectIdentityProperty;
    }

    public void setSubjectIdentityProperty(String subjectIdentityProperty) {
        this.subjectIdentityProperty = subjectIdentityProperty;
    }

    public String[] getSubjectReturnProperties() {
        return subjectReturnProperties;
    }

    public void setSubjectReturnProperties(String[] subjectReturnProperties) {
        this.subjectReturnProperties = subjectReturnProperties;
    }

    public String getSubjectCalculationProperty() {
        return subjectCalculationProperty;
    }

    public void setSubjectCalculationProperty(String subjectCalculationProperty) {
        this.subjectCalculationProperty = subjectCalculationProperty;
    }

    public String getObjectCalculationProperty() {
        return objectCalculationProperty;
    }

    public void setObjectCalculationProperty(String objectCalculationProperty) {
        this.objectCalculationProperty = objectCalculationProperty;
    }

    public ObjectAggregationType getObjectAggregationType() {
        return objectAggregationType;
    }

    public void setObjectAggregationType(ObjectAggregationType objectAggregationType) {
        this.objectAggregationType = objectAggregationType;
    }

    public String getStatisticResultProperty() {
        return statisticResultProperty;
    }

    public void setStatisticResultProperty(String statisticResultProperty) {
        this.statisticResultProperty = statisticResultProperty;
    }
}
