package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload;

public class SpatialPropertiesStatisticRequest extends AnalyseRequest{

    public static enum PredicateType {
        Contains,Intersects,Crosses,Within,Equals,Touches,Overlaps
    }

    private String subjectConception;
    private String objectConception;
    private PredicateType predicateType;

}
