package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termInf.MemGraphCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;

public class MemGraphCoreRealmImpl extends Neo4JCoreRealmImpl implements MemGraphCoreRealm {

    public MemGraphCoreRealmImpl(String coreRealmName){
        super(coreRealmName);
    }

    public MemGraphCoreRealmImpl(){}

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.MEMGRAPH;
    }
}
