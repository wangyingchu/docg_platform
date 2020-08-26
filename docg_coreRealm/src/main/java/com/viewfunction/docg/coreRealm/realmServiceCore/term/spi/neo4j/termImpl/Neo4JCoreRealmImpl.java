package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Neo4JCoreRealmImpl implements Neo4JCoreRealm {

    private static Logger logger = LoggerFactory.getLogger(Neo4JCoreRealmImpl.class);
    private String coreRealmName = null;

    public Neo4JCoreRealmImpl(){
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public Neo4JCoreRealmImpl(String coreRealmName){
        this.coreRealmName = coreRealmName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.NEO4J;
    }

    @Override
    public ConceptionKind getConceptionKind(String conceptionKindName) {
        if(conceptionKindName == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ConceptionKindClass,RealmConstant._NameProperty,conceptionKindName,1);
            GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                    new GetSingleConceptionKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,queryCql);
            return createConceptionKindRes != null?(ConceptionKind)createConceptionKindRes:null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc) {
        if(conceptionKindName == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String checkCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ConceptionKindClass,RealmConstant._NameProperty,conceptionKindName,1);
            Object conceptionKindExistenceRes = workingGraphOperationExecutor.executeRead(new CheckResultExistenceTransformer(),checkCql);
            if(((Boolean)conceptionKindExistenceRes).booleanValue()){
                return null;
            }
            Map<String,Object> propertiesMap = new HashMap<>();
            propertiesMap.put(RealmConstant._NameProperty,conceptionKindName);
            if(conceptionKindDesc != null) {
                propertiesMap.put(RealmConstant._DescProperty, conceptionKindDesc);
            }
            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
            String createCql = CypherBuilder.createLabeledNodeWithProperties(RealmConstant.ConceptionKindClass,propertiesMap);
            GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                    new GetSingleConceptionKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,createCql);
            ConceptionKind targetConceptionKind = createConceptionKindRes != null ? (ConceptionKind)createConceptionKindRes : null;
            executeConceptionKindCacheOperation(targetConceptionKind,CacheOperationType.INSERT);
            return targetConceptionKind;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName, String conceptionKindDesc, String parentConceptionKindName) throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public boolean removeConceptionKind(String conceptionKindName, boolean deleteExistEntities) throws CoreRealmServiceRuntimeException{
        if(conceptionKindName == null){
            return false;
        }
        ConceptionKind targetConceptionKind =this.getConceptionKind(conceptionKindName);
        if(targetConceptionKind == null){
            logger.error("CoreRealm does not contains ConceptionKind with name {}.", conceptionKindName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("CoreRealm does not contains ConceptionKind with name " + conceptionKindName + ".");
            throw exception;
        }else{
            if(deleteExistEntities){
                targetConceptionKind.purgeAllEntities();
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String conceptionKindUID = ((Neo4JConceptionKindImpl)targetConceptionKind).getConceptionKindUID();
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(conceptionKindUID),null,null);
                GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                        new GetSingleConceptionKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,deleteCql);
                if(deletedConceptionKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public AttributesViewKind getAttributesViewKind(String attributesViewKindUID) {
        if(attributesViewKindUID == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributesViewKindUID), null, null);
            GetSingleAttributesViewKindTransformer getSingleAttributesViewKindTransformer =
                    new GetSingleAttributesViewKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributesViewKindTransformer,queryCql);
            return createAttributesViewKindRes != null ? (AttributesViewKind)createAttributesViewKindRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public AttributesViewKind createAttributesViewKind(String attributesViewKindName, String attributesViewKindDesc, AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm) {
        if(attributesViewKindName == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            Map<String,Object> propertiesMap = new HashMap<>();
            propertiesMap.put(RealmConstant._NameProperty,attributesViewKindName);
            if(attributesViewKindDesc != null) {
                propertiesMap.put(RealmConstant._DescProperty, attributesViewKindDesc);
            }
            if(attributesViewKindDataForm != null) {
                propertiesMap.put(RealmConstant._viewKindDataForm, attributesViewKindDataForm.toString());
            }else{
                propertiesMap.put(RealmConstant._viewKindDataForm, AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE.toString());
            }
            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
            String createCql = CypherBuilder.createLabeledNodeWithProperties(RealmConstant.AttributesViewKindClass,propertiesMap);

            GetSingleAttributesViewKindTransformer getSingleAttributesViewKindTransformer =
                    new GetSingleAttributesViewKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributesViewKindTransformer,createCql);
            return createAttributesViewKindRes != null ? (AttributesViewKind)createAttributesViewKindRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException {
        if(attributesViewKindUID == null){
            return false;
        }
        AttributesViewKind targetAttributesViewKind = this.getAttributesViewKind(attributesViewKindUID);
        if(targetAttributesViewKind != null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(attributesViewKindUID),null,null);
                GetSingleAttributesViewKindTransformer getSingleAttributesViewKindTransformer =
                        new GetSingleAttributesViewKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributesViewKindTransformer,deleteCql);
                if(deletedAttributesViewKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }else{
            logger.error("AttributesViewKind does not contains entity with UID {}.", attributesViewKindUID);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("AttributesViewKind does not contains entity with UID " + attributesViewKindUID + ".");
            throw exception;
        }
    }

    @Override
    public AttributeKind getAttributeKind(String attributeKindUID) {
        if(attributeKindUID == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributeKindUID), null, null);
            GetSingleAttributeKindTransformer getSingleAttributeKindTransformer =
                    new GetSingleAttributeKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object getAttributeKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeKindTransformer,queryCql);
            return getAttributeKindRes != null ? (AttributeKind)getAttributeKindRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public AttributeKind createAttributeKind(String attributeKindName, String attributeKindDesc, AttributeDataType attributeDataType) {
        if(attributeKindName == null || attributeDataType == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            Map<String,Object> propertiesMap = new HashMap<>();
            propertiesMap.put(RealmConstant._NameProperty,attributeKindName);
            if(attributeKindDesc != null) {
                propertiesMap.put(RealmConstant._DescProperty, attributeKindDesc);
            }
            propertiesMap.put(RealmConstant._attributeDataType, attributeDataType.toString());
            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
            String createCql = CypherBuilder.createLabeledNodeWithProperties(RealmConstant.AttributeKindClass,propertiesMap);

            GetSingleAttributeKindTransformer getSingleAttributeKindTransformer =
                    new GetSingleAttributeKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeKindTransformer,createCql);
            return createAttributesViewKindRes != null ? (AttributeKind)createAttributesViewKindRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException {
        if(attributeKindUID == null){
            return false;
        }
        AttributeKind targetAttributeKind = this.getAttributeKind(attributeKindUID);
        if(targetAttributeKind != null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(attributeKindUID),null,null);
                GetSingleAttributeKindTransformer getSingleAttributeKindTransformer =
                        new GetSingleAttributeKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedAttributeKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeKindTransformer,deleteCql);
                if(deletedAttributeKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }else{
            logger.error("AttributeKind does not contains entity with UID {}.", attributeKindUID);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("AttributeKind does not contains entity with UID " + attributeKindUID + ".");
            throw exception;
        }
    }

    @Override
    public RelationKind getRelationKind(String relationKindName) {
        if(relationKindName == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.RelationKindClass,RealmConstant._NameProperty,relationKindName,1);
            GetSingleRelationKindTransformer getSingleRelationKindTransformer =
                    new GetSingleRelationKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object getRelationKindRes = workingGraphOperationExecutor.executeWrite(getSingleRelationKindTransformer,queryCql);
            return getRelationKindRes != null ? (RelationKind)getRelationKindRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public RelationKind createRelationKind(String relationKindName, String relationKindDesc) {
        if(relationKindName == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String checkCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.RelationKindClass,RealmConstant._NameProperty,relationKindName,1);
            Object relationKindExistenceRes = workingGraphOperationExecutor.executeRead(new CheckResultExistenceTransformer(),checkCql);
            if(((Boolean)relationKindExistenceRes).booleanValue()){
                return null;
            }
            Map<String,Object> propertiesMap = new HashMap<>();
            propertiesMap.put(RealmConstant._NameProperty,relationKindName);
            if(relationKindDesc != null) {
                propertiesMap.put(RealmConstant._DescProperty, relationKindDesc);
            }
            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
            String createCql = CypherBuilder.createLabeledNodeWithProperties(RealmConstant.RelationKindClass,propertiesMap);
            GetSingleRelationKindTransformer getSingleRelationKindTransformer =
                    new GetSingleRelationKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createRelationKindRes = workingGraphOperationExecutor.executeWrite(getSingleRelationKindTransformer,createCql);
            return createRelationKindRes != null ? (RelationKind)createRelationKindRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public RelationKind createRelationKind(String relationKindName, String relationKindDesc, String parentRelationKindName) throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public boolean removeRelationKind(String relationKindName, boolean deleteExistEntities) throws CoreRealmServiceRuntimeException {
        if(relationKindName == null){
            return false;
        }
        RelationKind targetRelationKind = this.getRelationKind(relationKindName);
        if(targetRelationKind == null){
            logger.error("RelationKind does not contains entity with UID {}.", relationKindName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("RelationKind does not contains entity with UID " + relationKindName + ".");
            throw exception;
        }else{
            if(deleteExistEntities){
                targetRelationKind.purgeAllRelationEntities();
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String relationKindUID = ((Neo4JRelationKindImpl)targetRelationKind).getRelationKindUID();
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationKindUID),null,null);
                GetSingleRelationKindTransformer getSingleRelationKindTransformer =
                        new GetSingleRelationKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedRelationKindRes = workingGraphOperationExecutor.executeWrite(getSingleRelationKindTransformer,deleteCql);
                if(deletedRelationKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
