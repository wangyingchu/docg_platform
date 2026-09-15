package com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query;

import java.util.List;

public class KindAttributesMatchLogic {

    private List<String> attributeNames;
    private QueryParameters queryParameters;

    public KindAttributesMatchLogic() {}

    public KindAttributesMatchLogic(QueryParameters queryParameters,List<String> attributeNames) {
        this.attributeNames = attributeNames;
        this.queryParameters = queryParameters;
    }

    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames;
    }

    public QueryParameters getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(QueryParameters queryParameters) {
        this.queryParameters = queryParameters;
    }
}
