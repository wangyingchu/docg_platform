package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListAttributeKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributeKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JAttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4JAttributesViewKindImpl implements Neo4JAttributesViewKind {

    private static Logger logger = LoggerFactory.getLogger(Neo4JConceptionKindImpl.class);
    private String coreRealmName;
    private String attributesViewKindName;
    private String attributesViewKindDesc;
    private String attributesViewKindUID;
    private AttributesViewKindDataForm attributesViewKindDataForm;

    public Neo4JAttributesViewKindImpl(String coreRealmName, String attributesViewKindName, String attributesViewKindDesc, AttributesViewKindDataForm attributesViewKindDataForm, String attributesViewKindUID){
        this.coreRealmName = coreRealmName;
        this.attributesViewKindName = attributesViewKindName;
        this.attributesViewKindDesc = attributesViewKindDesc;
        this.attributesViewKindUID = attributesViewKindUID;
        this.attributesViewKindDataForm = attributesViewKindDataForm != null ? attributesViewKindDataForm : AttributesViewKindDataForm.SINGLE_VALUE;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getAttributesViewKindUID() {
        return attributesViewKindUID;
    }

    @Override
    public String getAttributesViewKindName() {
        return attributesViewKindName;
    }

    @Override
    public String getAttributesViewKindDesc() {
        return attributesViewKindDesc;
    }

    @Override
    public boolean isCollectionAttributesViewKind() {
        boolean isCollectionAttributesViewKind = false;
        switch (attributesViewKindDataForm){
            case SINGLE_VALUE: isCollectionAttributesViewKind = false;
                break;
            case LIST_VALUE: isCollectionAttributesViewKind = true;
                break;
            case RELATED_VALUE: isCollectionAttributesViewKind = true;
                break;
            case EXTERNAL_VALUE: isCollectionAttributesViewKind = true;
        }
        return isCollectionAttributesViewKind;
    }

    @Override
    public AttributesViewKindDataForm getAttributesViewKindDataForm() {
        return attributesViewKindDataForm;
    }

    @Override
    public boolean attachAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException{
        if(attributeKindUID == null){
            return false;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributeKindUID), null, null);
            GetSingleAttributeKindTransformer getSingleAttributeKindTransformer = new GetSingleAttributeKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object checkAttributeKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeKindTransformer,queryCql);
            if(checkAttributeKindRes != null){
                String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(attributesViewKindUID),Long.parseLong(attributeKindUID),
                        RealmConstant.AttributesViewKind_AttributeKindRelationClass);

                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.AttributesViewKind_AttributeKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object existingRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, queryRelationCql);
                if(existingRelationEntityRes != null){
                    return true;
                }

                Map<String,Object> relationPropertiesMap = new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
                String createCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(attributesViewKindUID),Long.parseLong(attributeKindUID),
                        RealmConstant.AttributesViewKind_AttributeKindRelationClass,relationPropertiesMap);
                getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.AttributesViewKind_AttributeKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createCql);
                if(newRelationEntityRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    return true;
                }
            }else{
                logger.error("AttributeKind does not contains entity with UID {}.", attributeKindUID);
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("AttributeKind does not contains entity with UID " + attributeKindUID + ".");
                throw exception;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean detachAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException{
        if(attributeKindUID == null){
            return false;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributeKindUID), null, null);
            GetSingleAttributeKindTransformer getSingleAttributeKindTransformer = new GetSingleAttributeKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object checkAttributeKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeKindTransformer,queryCql);
            if(checkAttributeKindRes != null){
                String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(attributesViewKindUID),Long.parseLong(attributeKindUID),
                        RealmConstant.AttributesViewKind_AttributeKindRelationClass);

                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.AttributesViewKind_AttributeKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object existingRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, queryRelationCql);
                if(existingRelationEntityRes == null){
                    return false;
                }
                RelationEntity relationEntity = (RelationEntity)existingRelationEntityRes;

                String deleteCql = CypherBuilder.deleteRelationWithSingleFunctionValueEqual(
                        CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationEntity.getRelationEntityUID()),null,null);

                getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.AttributesViewKind_AttributeKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deleteRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, deleteCql);
                if(deleteRelationEntityRes == null){
                    return false;
                }else{
                    return true;
                }
            }else{
                logger.error("AttributeKind does not contains entity with UID {}.", attributeKindUID);
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("AttributeKind does not contains entity with UID " + attributeKindUID + ".");
                throw exception;
            }

        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<AttributeKind> getContainsAttributeKinds() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributesViewKindUID),
                    RealmConstant.AttributeKindClass,RealmConstant.AttributesViewKind_AttributeKindRelationClass, RelationDirection.TO, null);
            GetListAttributeKindTransformer getListAttributeKindTransformer = new GetListAttributeKindTransformer(RealmConstant.AttributeKindClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object attributeKindsRes = workingGraphOperationExecutor.executeWrite(getListAttributeKindTransformer,queryCql);
            return attributeKindsRes != null ? (List<AttributeKind>) attributeKindsRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<ConceptionKind> getContainerConceptionKinds() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributesViewKindUID),
                    RealmConstant.ConceptionKindClass,RealmConstant.ConceptionKind_AttributesViewKindRelationClass,RelationDirection.FROM, null);
            GetListConceptionKindTransformer getListConceptionKindTransformer = new GetListConceptionKindTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object conceptionKindsRes = workingGraphOperationExecutor.executeWrite(getListConceptionKindTransformer,queryCql);
            return conceptionKindsRes != null ? (List<ConceptionKind>) conceptionKindsRes : null;

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
        return this.attributesViewKindUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return this.graphOperationExecutorHelper;
    }
}