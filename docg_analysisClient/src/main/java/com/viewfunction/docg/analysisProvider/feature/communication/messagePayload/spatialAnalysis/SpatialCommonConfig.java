package com.viewfunction.docg.analysisProvider.feature.communication.messagePayload.spatialAnalysis;

public class SpatialCommonConfig {

    public enum PredicateType {
        Contains,Intersects,Crosses,Within,Equals,Touches,Overlaps
    }

    public enum GeospatialScaleGrade {
        Continent,Country_Region,Province,Prefecture,County,Township,Village
    }

    public enum GeospatialScaleLevel {
        GlobalLevel,CountryLevel,LocalLevel
    }
}