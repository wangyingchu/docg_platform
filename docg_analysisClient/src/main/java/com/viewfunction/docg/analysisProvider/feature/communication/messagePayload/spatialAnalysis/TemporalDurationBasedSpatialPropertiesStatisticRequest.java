package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis;

import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TemporalDurationBasedSpatialPropertiesStatisticRequest extends AnalyseRequest {

    public enum ObjectAggregationType {
        SUM,AVG,STDDEV,COUNT,MAX,MIN,VARIANCE
    }

    private String subjectConception;
    private String subjectGroup;
    private String objectConception;
    private String objectGroup;

    private SpatialCommonConfig.PredicateType predicateType;
    private SpatialCommonConfig.GeospatialScaleLevel geospatialScaleLevel;
    private ObjectAggregationType objectAggregationType;

    private String subjectIdentityProperty;
    private String[] subjectReturnProperties;

    private String objectStatisticProperty;
    private String objectTemporalProperty;

    private LocalDateTime statisticStartTime;
    private LocalDateTime statisticEndTime;
    private ChronoUnit temporalDurationType;
    private long durationCount;
    private String statisticResultTemporalProperty;

    public String getSubjectConception() {
        return subjectConception;
    }

    public void setSubjectConception(String subjectConception) {
        this.subjectConception = subjectConception;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    public void setSubjectGroup(String subjectGroup) {
        this.subjectGroup = subjectGroup;
    }

    public String getObjectConception() {
        return objectConception;
    }

    public void setObjectConception(String objectConception) {
        this.objectConception = objectConception;
    }

    public String getObjectGroup() {
        return objectGroup;
    }

    public void setObjectGroup(String objectGroup) {
        this.objectGroup = objectGroup;
    }

    public SpatialCommonConfig.PredicateType getPredicateType() {
        return predicateType;
    }

    public void setPredicateType(SpatialCommonConfig.PredicateType predicateType) {
        this.predicateType = predicateType;
    }

    public SpatialCommonConfig.GeospatialScaleLevel getGeospatialScaleLevel() {
        return geospatialScaleLevel;
    }

    public void setGeospatialScaleLevel(SpatialCommonConfig.GeospatialScaleLevel geospatialScaleLevel) {
        this.geospatialScaleLevel = geospatialScaleLevel;
    }

    public ObjectAggregationType getObjectAggregationType() {
        return objectAggregationType;
    }

    public void setObjectAggregationType(ObjectAggregationType objectAggregationType) {
        this.objectAggregationType = objectAggregationType;
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

    public String getObjectStatisticProperty() {
        return objectStatisticProperty;
    }

    public void setObjectStatisticProperty(String objectStatisticProperty) {
        this.objectStatisticProperty = objectStatisticProperty;
    }

    public String getObjectTemporalProperty() {
        return objectTemporalProperty;
    }

    public void setObjectTemporalProperty(String objectTemporalProperty) {
        this.objectTemporalProperty = objectTemporalProperty;
    }

    public LocalDateTime getStatisticStartTime() {
        return statisticStartTime;
    }

    public void setStatisticStartTime(LocalDateTime statisticStartTime) {
        this.statisticStartTime = statisticStartTime;
    }

    public LocalDateTime getStatisticEndTime() {
        return statisticEndTime;
    }

    public void setStatisticEndTime(LocalDateTime statisticEndTime) {
        this.statisticEndTime = statisticEndTime;
    }

    public ChronoUnit getTemporalDurationType() {
        return temporalDurationType;
    }

    public void setTemporalDurationType(ChronoUnit temporalDurationType) {
        this.temporalDurationType = temporalDurationType;
    }

    public long getDurationCount() {
        return durationCount;
    }

    public void setDurationCount(long durationCount) {
        this.durationCount = durationCount;
    }

    public String getStatisticResultTemporalProperty() {
        return statisticResultTemporalProperty;
    }

    public void setStatisticResultTemporalProperty(String statisticResultTemporalProperty) {
        this.statisticResultTemporalProperty = statisticResultTemporalProperty;
    }
}
