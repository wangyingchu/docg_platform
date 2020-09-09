package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JClassification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neo4JClassificationImpl implements Neo4JClassification {

    private static Logger logger = LoggerFactory.getLogger(Neo4JClassificationImpl.class);
    private String coreRealmName;
    private String classificationName;
    private String classificationDesc;
    private String classificationUID;

    public Neo4JClassificationImpl(String coreRealmName,String classificationName,String classificationDesc,String classificationUID){
        this.coreRealmName = coreRealmName;
        this.classificationName = classificationName;
        this.classificationDesc = classificationDesc;
        this.classificationUID = classificationUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public String getClassificationUID() {
        return this.classificationUID;
    }

    public String getCoreRealmName() {
        return this.coreRealmName;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return classificationUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
