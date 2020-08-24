package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;

public interface Neo4JKeyResourcesRetrievable {

    public String getEntityUID();
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper();

}
