package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachLinkLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JRelationAttachKind;

import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4JRelationAttachKindImpl implements Neo4JRelationAttachKind {

    private static Logger logger = LoggerFactory.getLogger(Neo4JRelationAttachKindImpl.class);

    private String coreRealmName;
    private String relationAttachKindName;
    private String relationAttachKindDesc;
    private String relationAttachKindUID;
    private String sourceConceptionKindName;
    private String targetConceptionKindName;
    private String relationKindName;
    private boolean allowRepeatableRelationKind;

    public Neo4JRelationAttachKindImpl(String coreRealmName, String relationAttachKindName, String relationAttachKindDesc, String relationAttachKindUID,
                                       String sourceConceptionKindName, String targetConceptionKindName,String relationKindName,boolean allowRepeatableRelationKind){
        this.coreRealmName = coreRealmName;
        this.relationAttachKindName = relationAttachKindName;
        this.relationAttachKindDesc = relationAttachKindDesc;
        this.relationAttachKindUID = relationAttachKindUID;
        this.sourceConceptionKindName = sourceConceptionKindName;
        this.targetConceptionKindName = targetConceptionKindName;
        this.relationKindName = relationKindName;
        this.allowRepeatableRelationKind = allowRepeatableRelationKind;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getRelationAttachKindUID() {
        return this.relationAttachKindUID;
    }

    @Override
    public String getSourceConceptionKindName() {
        return this.sourceConceptionKindName;
    }

    @Override
    public String getTargetConceptionKindName() {
        return this.targetConceptionKindName;
    }

    @Override
    public String getRelationKindName() {
        return this.relationKindName;
    }

    @Override
    public String getRelationAttachKindName() {
        return this.relationAttachKindName;
    }

    @Override
    public String getRelationAttachKindDesc() {
        return this.relationAttachKindDesc;
    }

    @Override
    public boolean updateRelationAttachKindDesc(String newDesc) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String,Object> attributeDataMap = new HashMap<>();
            attributeDataMap.put(RealmConstant._DescProperty, newDesc);
            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.relationAttachKindUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._DescProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.relationAttachKindUID,false);
            AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(newDesc)){
                this.relationAttachKindDesc = newDesc;
                return true;
            }else{
                return false;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<RelationAttachLinkLogic> getRelationAttachLinkLogic() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(this.relationAttachKindUID),
                    RealmConstant.RelationAttachLinkLogicClass,RealmConstant.RelationAttachKind_RelationAttachLinkLogicRelationClass, RelationDirection.TO, null);
            GetListRelationAttachLinkLogicTransformer getListRelationAttachLinkLogicTransformer = new GetListRelationAttachLinkLogicTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object relationAttachLinkLogicsRes = workingGraphOperationExecutor.executeWrite(getListRelationAttachLinkLogicTransformer,queryCql);
            return relationAttachLinkLogicsRes != null ? (List<RelationAttachLinkLogic>) relationAttachLinkLogicsRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public RelationAttachLinkLogic createRelationAttachLinkLogic(RelationAttachLinkLogic relationAttachLinkLogic) throws CoreRealmServiceRuntimeException {
        if(relationAttachLinkLogic.getLinkLogicType().equals(LinkLogicType.DEFAULT)){
            List<RelationAttachLinkLogic> relationAttachLinkLogicList = getRelationAttachLinkLogic();
            for(RelationAttachLinkLogic currentRelationAttachLinkLogic:relationAttachLinkLogicList){
                if(currentRelationAttachLinkLogic.getLinkLogicType().equals(LinkLogicType.DEFAULT)){
                    logger.error("RelationAttachKind {} already contains DEFAULT LinkLogicType.", this.relationAttachKindName);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("RelationAttachKind "+this.relationAttachKindName+" already contains DEFAULT LinkLogicType.");
                    throw exception;
                }
            }
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String,Object> propertiesMap = new HashMap<>();
            propertiesMap.put(RealmConstant._attachLinkLogicType,relationAttachLinkLogic.getLinkLogicType().toString());
            propertiesMap.put(RealmConstant._attachLinkLogicCondition,relationAttachLinkLogic.getLinkLogicCondition().toString());
            propertiesMap.put(RealmConstant._attachLinkLogicKnownAttribute,relationAttachLinkLogic.getKnownEntityLinkAttributeName());
            propertiesMap.put(RealmConstant._attachLinkLogicUnKnownAttribute,relationAttachLinkLogic.getUnKnownEntitiesLinkAttributeName());
            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
            String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.RelationAttachLinkLogicClass},propertiesMap);
            GetSingleRelationAttachLinkLogicTransformer getSingleRelationAttachLinkLogicTransformer =
                    new GetSingleRelationAttachLinkLogicTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createLinkLogicRes = workingGraphOperationExecutor.executeWrite(getSingleRelationAttachLinkLogicTransformer,createCql);
            RelationAttachLinkLogic targetRelationAttachLinkLogic = createLinkLogicRes != null ? (RelationAttachLinkLogic)createLinkLogicRes : null;

            Map<String,Object> relationPropertiesMap = new HashMap<>();
            CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
            String linkCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(this.relationAttachKindUID),Long.parseLong(targetRelationAttachLinkLogic.getRelationAttachLinkLogicUID()),
                    RealmConstant.RelationAttachKind_RelationAttachLinkLogicRelationClass,relationPropertiesMap);
            GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                    (RealmConstant.RelationAttachKind_RelationAttachLinkLogicRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, linkCql);
            if(newRelationEntityRes == null){
                throw new CoreRealmServiceRuntimeException();
            }
            return targetRelationAttachLinkLogic;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeRelationAttachLinkLogic(String relationAttachLinkLogicUID) throws CoreRealmServiceRuntimeException {
        boolean isValidRelationAttachLinkLogic = false;
        List<RelationAttachLinkLogic> relationAttachLinkLogicList = getRelationAttachLinkLogic();
        for(RelationAttachLinkLogic currentRelationAttachLinkLogic:relationAttachLinkLogicList){
            if(currentRelationAttachLinkLogic.getRelationAttachLinkLogicUID().equals(relationAttachLinkLogicUID)){
                isValidRelationAttachLinkLogic = true;
                break;
            }
        }
        if(!isValidRelationAttachLinkLogic){
            logger.error("RelationAttachKind {} does not contain relationAttachLinkLogic with UID {}.", this.relationAttachKindName,relationAttachLinkLogicUID);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("RelationAttachKind "+this.relationAttachKindName+" does not contain relationAttachLinkLogic with UID "+relationAttachLinkLogicUID+".");
            throw exception;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationAttachLinkLogicUID),null,null);
            GetSingleRelationAttachLinkLogicTransformer getSingleRelationAttachLinkLogicTransformer =
                    new GetSingleRelationAttachLinkLogicTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object deletedAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleRelationAttachLinkLogicTransformer,deleteCql);
            RelationAttachLinkLogic resultKind = deletedAttributesViewKindRes != null ? (RelationAttachLinkLogic)deletedAttributesViewKindRes : null;
            if(resultKind == null){
                throw new CoreRealmServiceRuntimeException();
            }else{
                return true;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean newRelationEntities(String conceptionEntityUID, EntityRelateRole entityRelateRole, Map<String,Object> relationData) {
        return false;
    }

    @Override
    public EntitiesOperationResult newUniversalRelationEntities(Map<String,Object> relationData) {
        return null;
    }

    @Override
    public boolean isRepeatableRelationKindAllow() {
        return this.allowRepeatableRelationKind;
    }

    @Override
    public boolean setAllowRepeatableRelationKind(boolean allowRepeatableRelationKind) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String,Object> attributeDataMap = new HashMap<>();
            attributeDataMap.put(RealmConstant._relationAttachRepeatableRelationKind, allowRepeatableRelationKind);
            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.relationAttachKindUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._relationAttachRepeatableRelationKind);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.relationAttachKindUID,false);
            AttributeValue resultAttributeValue = updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null ){
                Boolean currentValue = (Boolean)resultAttributeValue.getAttributeValue();
                this.allowRepeatableRelationKind = currentValue.booleanValue();
                return currentValue.booleanValue();
            }else{
                return false;
            }
        } finally {
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
        return this.relationAttachKindUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
