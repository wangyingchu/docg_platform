package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.spi.common.structureImpl.CommonInheritanceTreeImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JClassification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Neo4JClassificationImpl extends Neo4JAttributesMeasurableImpl implements Neo4JClassification {

    private static Logger logger = LoggerFactory.getLogger(Neo4JClassificationImpl.class);
    private String coreRealmName;
    private String classificationName;
    private String classificationDesc;
    private String classificationUID;

    public Neo4JClassificationImpl(String coreRealmName,String classificationName,String classificationDesc,String classificationUID){
        super(classificationUID);
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

    @Override
    public String getClassificationName() {
        return this.classificationName;
    }

    @Override
    public String getClassificationDesc() {
        return this.classificationDesc;
    }

    @Override
    public boolean isRootClassification() {
        return false;
    }

    @Override
    public Classification getParentClassification() {
        return null;
    }

    @Override
    public List<Classification> getChildClassifications() {
        return null;
    }

    @Override
    public InheritanceTree<Classification> getOffspringClassifications() {

        CommonInheritanceTreeImpl<Classification> resultInheritanceTree = new CommonInheritanceTreeImpl(this.classificationUID,null);
        return resultInheritanceTree;
    }

    @Override
    public boolean attachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public boolean detachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public Classification createChildClassification(String classificationName, String classificationDesc) {
        return null;
    }

    @Override
    public boolean removeChildClassification(String classificationName) throws CoreRealmServiceRuntimeException {
        return false;
    }
}
