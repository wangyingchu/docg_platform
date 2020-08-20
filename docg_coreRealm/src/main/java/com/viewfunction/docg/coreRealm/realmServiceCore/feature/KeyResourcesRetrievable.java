package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;

public interface KeyResourcesRetrievable {

    public String getEntityUID();
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper();

}
