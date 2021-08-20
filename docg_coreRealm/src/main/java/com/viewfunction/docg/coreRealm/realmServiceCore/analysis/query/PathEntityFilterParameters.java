package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

public class PathEntityFilterParameters {

    public enum PathFilterScope {AllEntity,NoneEntity,AnyEntity}

    private AttributesParameters entityAttributesFilterParameters;
    private PathFilterScope pathFilterScope;

    public PathEntityFilterParameters(PathFilterScope pathFilterScope,AttributesParameters entityAttributesFilterParameters){
        this.pathFilterScope = pathFilterScope;
        this.entityAttributesFilterParameters = entityAttributesFilterParameters;
    }
}
