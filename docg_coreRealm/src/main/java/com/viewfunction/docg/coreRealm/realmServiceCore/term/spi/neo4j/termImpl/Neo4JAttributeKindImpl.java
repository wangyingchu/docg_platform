package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListAttributesViewKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JAttributeKind;

import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Neo4JAttributeKindImpl implements Neo4JAttributeKind {

    private static Logger logger = LoggerFactory.getLogger(Neo4JAttributeKindImpl.class);
    private String coreRealmName;
    private String attributeKindName;
    private String attributeKindDesc;
    private String attributeKindUID;
    private AttributeDataType attributeDataType;

    public Neo4JAttributeKindImpl(String coreRealmName, String attributeKindName, String attributeKindDesc, AttributeDataType attributeDataType, String attributeKindUID){
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
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributeKindUID),
                    RealmConstant.AttributesViewKindClass,RealmConstant.AttributesViewKind_AttributeKindRelationClass, RelationDirection.FROM, null);
            GetListAttributesViewKindTransformer getListAttributesViewKindTransformer =
                    new GetListAttributesViewKindTransformer(RealmConstant.AttributesViewKind_AttributeKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object attributesViewKindsRes = workingGraphOperationExecutor.executeWrite(getListAttributesViewKindTransformer,queryCql);
            return attributesViewKindsRes != null ? (List<AttributesViewKind>) attributesViewKindsRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
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