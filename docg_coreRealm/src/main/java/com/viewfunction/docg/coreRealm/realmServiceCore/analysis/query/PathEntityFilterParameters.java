package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

public class PathEntityFilterParameters {

    public enum PathFilterScope {AllEntity,NoneEntity,AnyEntity}

    private AttributesParameters entityAttributesFilterParameters;
    private PathFilterScope pathFilterScope;

    public PathEntityFilterParameters(PathFilterScope pathFilterScope,AttributesParameters entityAttributesFilterParameters){
        this.setPathFilterScope(pathFilterScope);
        this.setEntityAttributesFilterParameters(entityAttributesFilterParameters);
    }

    public AttributesParameters getEntityAttributesFilterParameters() {
        return entityAttributesFilterParameters;
    }

    private void setEntityAttributesFilterParameters(AttributesParameters entityAttributesFilterParameters) {
        this.entityAttributesFilterParameters = entityAttributesFilterParameters;
    }

    public PathFilterScope getPathFilterScope() {
        return pathFilterScope;
    }

    private void setPathFilterScope(PathFilterScope pathFilterScope) {
        this.pathFilterScope = pathFilterScope;
    }
}
