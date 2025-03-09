package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.tugraph.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.tugraph.termInf.TuGraphCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;

public class TuGraphCoreRealmImpl extends Neo4JCoreRealmImpl implements TuGraphCoreRealm {
    public TuGraphCoreRealmImpl(String coreRealmName){
        super(coreRealmName);
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public TuGraphCoreRealmImpl(){
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.TUGRAPH;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
