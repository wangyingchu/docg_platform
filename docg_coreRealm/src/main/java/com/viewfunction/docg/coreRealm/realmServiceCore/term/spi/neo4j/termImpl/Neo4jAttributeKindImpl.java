package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JAttributeKind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Neo4jAttributeKindImpl implements Neo4JAttributeKind {

    private static Logger logger = LoggerFactory.getLogger(Neo4jAttributeKindImpl.class);
    private String coreRealmName;
    private String attributeKindName;
    private String attributeKindDesc;
    private String attributeKindUID;
    private AttributeDataType attributeDataType;

    public Neo4jAttributeKindImpl(String coreRealmName, String attributeKindName, String attributeKindDesc, AttributeDataType attributeDataType, String attributeKindUID){
        this.coreRealmName = coreRealmName;
        this.attributeKindName = attributeKindName;
        this.attributeKindDesc = attributeKindDesc;
        this.attributeDataType = attributeDataType;
        this.attributeKindUID = attributeKindUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getAttributeKindName() {
        return attributeKindName;
    }

    @Override
    public String getAttributeKindUID() {
        return attributeKindUID;
    }

    @Override
    public String getAttributeKindDesc() {
        return attributeKindDesc;
    }

    @Override
    public AttributeDataType getAttributeDataType() {
        return attributeDataType;
    }

    @Override
    public List<AttributesViewKind> getContainerAttributesViewKinds() {
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return this.attributeKindUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return this.graphOperationExecutorHelper;
    }
}
