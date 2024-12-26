package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis;

import com.viewfunction.docg.analysisProvider.client.exception.AnalyseRequestFormatException;
import com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.AnalyseRequest;

public class AdministrativeDivisionSpatialCalculateRequest extends AnalyseRequest {

    private String subjectConception;
    private String subjectGroup;
    private String[] subjectReturnProperties;
    private String[] administrativeDivisionReturnProperties;
    private SpatialCommonConfig.PredicateType predicateType;
    private SpatialCommonConfig.GeospatialScaleGrade geospatialScaleGrade;
    private SpatialCommonConfig.GeospatialScaleLevel geospatialScaleLevel;
    private double sampleValue = 1;

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

    public String[] getSubjectReturnProperties() {
        return subjectReturnProperties;
    }

    public void setSubjectReturnProperties(String[] subjectReturnProperties) {
        this.subjectReturnProperties = subjectReturnProperties;
    }

    public String[] getAdministrativeDivisionReturnProperties() {
        return administrativeDivisionReturnProperties;
    }

    public void setAdministrativeDivisionReturnProperties(String[] administrativeDivisionReturnProperties) {
        this.administrativeDivisionReturnProperties = administrativeDivisionReturnProperties;
    }

    public SpatialCommonConfig.PredicateType getPredicateType() {
        return predicateType;
    }

    public void setPredicateType(SpatialCommonConfig.PredicateType predicateType) {
        this.predicateType = predicateType;
    }

    public SpatialCommonConfig.GeospatialScaleGrade getGeospatialScaleGrade() {
        return geospatialScaleGrade;
    }

    public void setGeospatialScaleGrade(SpatialCommonConfig.GeospatialScaleGrade geospatialScaleGrade) {
        this.geospatialScaleGrade = geospatialScaleGrade;
    }

    public SpatialCommonConfig.GeospatialScaleLevel getGeospatialScaleLevel() {
        return geospatialScaleLevel;
    }

    public void setGeospatialScaleLevel(SpatialCommonConfig.GeospatialScaleLevel geospatialScaleLevel) {
        this.geospatialScaleLevel = geospatialScaleLevel;
    }

    public double getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(double sampleValue) throws AnalyseRequestFormatException {
        if(sampleValue <= 0 |sampleValue > 1){
            AnalyseRequestFormatException analyseRequestFormatException = new AnalyseRequestFormatException();
            analyseRequestFormatException.setCauseMessage("sampleValue should in (0,1] range");
            throw analyseRequestFormatException;
        }
        this.sampleValue = sampleValue;
    }
}
