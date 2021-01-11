package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.TimeScaleOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationAttachLinkLogic;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonEntitiesOperationResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.*;

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
            String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.ConceptionKindClass},propertiesMap);
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
                ConceptionKind resultConceptionKind = deletedConceptionKindRes != null ? (ConceptionKind)deletedConceptionKindRes : null;
                if(resultConceptionKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String conceptionKindId = ((Neo4JConceptionKindImpl)resultConceptionKind).getConceptionKindUID();
                    Neo4JConceptionKindImpl resultNeo4JConceptionKindImplForCacheOperation = new Neo4JConceptionKindImpl(coreRealmName,conceptionKindName,null,conceptionKindId);
                    executeConceptionKindCacheOperation(resultNeo4JConceptionKindImplForCacheOperation,CacheOperationType.DELETE);
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
            String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.AttributesViewKindClass},propertiesMap);

            GetSingleAttributesViewKindTransformer getSingleAttributesViewKindTransformer =
                    new GetSingleAttributesViewKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributesViewKindTransformer,createCql);
            AttributesViewKind resultKind = createAttributesViewKindRes != null ? (AttributesViewKind)createAttributesViewKindRes : null;
            executeAttributesViewKindCacheOperation(resultKind,CacheOperationType.INSERT);
            return resultKind;
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
                AttributesViewKind resultKind = deletedAttributesViewKindRes != null ? (AttributesViewKind)deletedAttributesViewKindRes : null;
                if(resultKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    executeAttributesViewKindCacheOperation(resultKind,CacheOperationType.DELETE);
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
    public List<AttributesViewKind> getAttributesViewKinds(String attributesViewKindName, String attributesViewKindDesc, AttributesViewKind.AttributesViewKindDataForm attributesViewKindDataForm) {
        boolean alreadyHaveDefaultFilteringItem = false;
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(1000000);
        if(attributesViewKindName != null){
            queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._NameProperty,attributesViewKindName));
            alreadyHaveDefaultFilteringItem = true;
        }
        if(attributesViewKindDesc != null){
            if(alreadyHaveDefaultFilteringItem){
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._DescProperty,attributesViewKindDesc), QueryParameters.FilteringLogic.AND);
            }else{
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._DescProperty,attributesViewKindDesc));
                alreadyHaveDefaultFilteringItem = true;
            }
        }
        if(attributesViewKindDataForm != null){
            if(alreadyHaveDefaultFilteringItem){
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._viewKindDataForm,attributesViewKindDataForm.toString()), QueryParameters.FilteringLogic.AND);
            }else{
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._viewKindDataForm,attributesViewKindDataForm.toString()));
            }
        }
        try {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.AttributesViewKindClass,queryParameters,null);
                GetListAttributesViewKindTransformer getListAttributesViewKindTransformer =
                        new GetListAttributesViewKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object attributesViewKindsRes = workingGraphOperationExecutor.executeWrite(getListAttributesViewKindTransformer,queryCql);
                return attributesViewKindsRes != null ? (List<AttributesViewKind>) attributesViewKindsRes : null;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
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
            String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.AttributeKindClass},propertiesMap);

            GetSingleAttributeKindTransformer getSingleAttributeKindTransformer =
                    new GetSingleAttributeKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeKindTransformer,createCql);
            AttributeKind resultKind = createAttributesViewKindRes != null ? (AttributeKind)createAttributesViewKindRes : null;
            executeAttributeKindCacheOperation(resultKind,CacheOperationType.INSERT);
            return resultKind;
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
                AttributeKind resultKind = deletedAttributeKindRes != null ? (AttributeKind)deletedAttributeKindRes : null;
                if(resultKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    executeAttributeKindCacheOperation(resultKind,CacheOperationType.DELETE);
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
    public List<AttributeKind> getAttributeKinds(String attributeKindName, String attributeKindDesc, AttributeDataType attributeDataType) {
        boolean alreadyHaveDefaultFilteringItem = false;
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(1000000);
        if(attributeKindName != null){
            queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._NameProperty,attributeKindName));
            alreadyHaveDefaultFilteringItem = true;
        }
        if(attributeKindDesc != null){
            if(alreadyHaveDefaultFilteringItem){
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._DescProperty,attributeKindDesc), QueryParameters.FilteringLogic.AND);
            }else{
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._DescProperty,attributeKindDesc));
                alreadyHaveDefaultFilteringItem = true;
            }
        }
        if(attributeDataType != null){
            if(alreadyHaveDefaultFilteringItem){
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._attributeDataType,attributeDataType.toString()), QueryParameters.FilteringLogic.AND);
            }else{
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._attributeDataType,attributeDataType.toString()));
            }
        }
        try {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.AttributeKindClass,queryParameters,null);
                GetListAttributeKindTransformer getListAttributeKindTransformer = new GetListAttributeKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object attributeKindsRes = workingGraphOperationExecutor.executeWrite(getListAttributeKindTransformer,queryCql);
                return attributeKindsRes != null ? (List<AttributeKind>) attributeKindsRes : null;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
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
            String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.RelationKindClass},propertiesMap);
            GetSingleRelationKindTransformer getSingleRelationKindTransformer =
                    new GetSingleRelationKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createRelationKindRes = workingGraphOperationExecutor.executeWrite(getSingleRelationKindTransformer,createCql);
            RelationKind resultKind = createRelationKindRes != null ? (RelationKind)createRelationKindRes : null;
            executeRelationKindCacheOperation(resultKind,CacheOperationType.INSERT);
            return resultKind;
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
                RelationKind resultKind = deletedRelationKindRes != null ? (RelationKind)deletedRelationKindRes : null;
                if(resultKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String resultRelationKindUID = ((Neo4JRelationKindImpl)resultKind).getRelationKindUID();
                    Neo4JRelationKindImpl resultNeo4JRelationKindImplForCacheOperation = new Neo4JRelationKindImpl(coreRealmName,relationKindName,null,resultRelationKindUID);
                    executeRelationKindCacheOperation(resultNeo4JRelationKindImplForCacheOperation,CacheOperationType.DELETE);
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public List<RelationAttachKind> getRelationAttachKinds(String relationAttachKindName, String relationAttachKindDesc, String sourceConceptionKindName, String targetConceptionKindName, String relationKindName, Boolean allowRepeatableRelationKind) {
        boolean alreadyHaveDefaultFilteringItem = false;
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(1000000);
        if(relationAttachKindName != null){
            queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._NameProperty,relationAttachKindName));
            alreadyHaveDefaultFilteringItem = true;
        }
        if(relationAttachKindDesc != null){
            if(alreadyHaveDefaultFilteringItem){
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._DescProperty,relationAttachKindDesc), QueryParameters.FilteringLogic.AND);
            }else{
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._DescProperty,relationAttachKindDesc));
                alreadyHaveDefaultFilteringItem = true;
            }
        }
        if(sourceConceptionKindName != null){
            if(alreadyHaveDefaultFilteringItem){
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachSourceKind,sourceConceptionKindName), QueryParameters.FilteringLogic.AND);
            }else{
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachSourceKind,sourceConceptionKindName));
                alreadyHaveDefaultFilteringItem = true;
            }
        }
        if(targetConceptionKindName != null){
            if(alreadyHaveDefaultFilteringItem){
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachTargetKind,targetConceptionKindName), QueryParameters.FilteringLogic.AND);
            }else{
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachTargetKind,targetConceptionKindName));
                alreadyHaveDefaultFilteringItem = true;
            }
        }
        if(relationKindName != null){
            if(alreadyHaveDefaultFilteringItem){
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachRelationKind,relationKindName), QueryParameters.FilteringLogic.AND);
            }else{
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachRelationKind,relationKindName));
                alreadyHaveDefaultFilteringItem = true;
            }
        }

        if(allowRepeatableRelationKind != null){
            boolean allowRepeatableRelationKindValue = allowRepeatableRelationKind.booleanValue();
            if(alreadyHaveDefaultFilteringItem){
                queryParameters.addFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachRepeatableRelationKind,allowRepeatableRelationKindValue), QueryParameters.FilteringLogic.AND);
            }else{
                queryParameters.setDefaultFilteringItem(new EqualFilteringItem(RealmConstant._relationAttachRepeatableRelationKind,allowRepeatableRelationKindValue));
            }
        }

        try {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.RelationAttachKindClass,queryParameters,null);
                GetListRelationAttachKindTransformer getListRelationAttachKindTransformer = new GetListRelationAttachKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object relationAttachKindsRes = workingGraphOperationExecutor.executeWrite(getListRelationAttachKindTransformer,queryCql);
                return relationAttachKindsRes != null ? (List<RelationAttachKind>) relationAttachKindsRes : null;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RelationAttachKind getRelationAttachKind(String relationAttachKindUID) {
        if(relationAttachKindUID == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(relationAttachKindUID), null, null);
            GetSingleRelationAttachKindTransformer getSingleRelationAttachKindTransformer =
                    new GetSingleRelationAttachKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object getRelationAttachKindRes = workingGraphOperationExecutor.executeWrite(getSingleRelationAttachKindTransformer,queryCql);
            return getRelationAttachKindRes != null ? (RelationAttachKind)getRelationAttachKindRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public RelationAttachKind createRelationAttachKind(String relationAttachKindName, String relationAttachKindDesc, String sourceConceptionKindName, String targetConceptionKindName, String relationKindName, boolean allowRepeatableRelationKind) throws CoreRealmFunctionNotSupportedException {
        if(relationAttachKindName == null || sourceConceptionKindName== null || targetConceptionKindName == null || relationKindName == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String checkCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.RelationAttachKindClass,RealmConstant._NameProperty,relationAttachKindName,1);
            Object relationAttachKindExistenceRes = workingGraphOperationExecutor.executeRead(new CheckResultExistenceTransformer(),checkCql);
            if(((Boolean)relationAttachKindExistenceRes).booleanValue()){
                return null;
            }

            Map<String,Object> propertiesMap = new HashMap<>();
            propertiesMap.put(RealmConstant._NameProperty,relationAttachKindName);
            if(relationAttachKindDesc != null) {
                propertiesMap.put(RealmConstant._DescProperty, relationAttachKindDesc);
            }
            propertiesMap.put(RealmConstant._relationAttachSourceKind,sourceConceptionKindName);
            propertiesMap.put(RealmConstant._relationAttachTargetKind,targetConceptionKindName);
            propertiesMap.put(RealmConstant._relationAttachRelationKind,relationKindName);
            propertiesMap.put(RealmConstant._relationAttachRepeatableRelationKind,allowRepeatableRelationKind);
            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);

            String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.RelationAttachKindClass},propertiesMap);

            GetSingleRelationAttachKindTransformer getSingleRelationAttachKindTransformer =
                    new GetSingleRelationAttachKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createRelationAttachKindRes = workingGraphOperationExecutor.executeWrite(getSingleRelationAttachKindTransformer,createCql);
            RelationAttachKind resultKind = createRelationAttachKindRes != null ? (RelationAttachKind)createRelationAttachKindRes : null;
            executeRelationAttachKindCacheOperation(resultKind,CacheOperationType.INSERT);
            return resultKind;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeRelationAttachKind(String relationAttachKindUID) throws CoreRealmServiceRuntimeException {
        if(relationAttachKindUID == null){
            return false;
        }
        RelationAttachKind targetRelationAttachKind = this.getRelationAttachKind(relationAttachKindUID);
        if(targetRelationAttachKind != null){
            List<RelationAttachLinkLogic> relationAttachLinkLogicList = targetRelationAttachKind.getRelationAttachLinkLogic();
            if(relationAttachLinkLogicList != null){
                for(RelationAttachLinkLogic currentRelationAttachLinkLogic:relationAttachLinkLogicList){
                    targetRelationAttachKind.removeRelationAttachLinkLogic(currentRelationAttachLinkLogic.getRelationAttachLinkLogicUID());
                }
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationAttachKindUID),null,null);
                GetSingleRelationAttachKindTransformer getSingleRelationAttachKindTransformer =
                        new GetSingleRelationAttachKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedRelationAttachKindRes = workingGraphOperationExecutor.executeWrite(getSingleRelationAttachKindTransformer,deleteCql);
                RelationAttachKind resultKind = deletedRelationAttachKindRes != null ? (RelationAttachKind)deletedRelationAttachKindRes : null;
                if(resultKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    executeRelationAttachKindCacheOperation(resultKind,CacheOperationType.DELETE);
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }else{
            logger.error("RelationAttachKind does not contains entity with UID {}.", relationAttachKindUID);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("RelationAttachKind does not contains entity with UID " + relationAttachKindUID + ".");
            throw exception;
        }
    }

    @Override
    public Classification getClassification(String classificationName) {
        if(classificationName == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ClassificationClass,RealmConstant._NameProperty,classificationName,1);
            GetSingleClassificationTransformer getSingleClassificationTransformer =
                    new GetSingleClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object classificationRes = workingGraphOperationExecutor.executeWrite(getSingleClassificationTransformer,queryCql);
            return classificationRes != null?(Classification)classificationRes:null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Classification createClassification(String classificationName, String classificationDesc) {
        if(classificationName == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String checkCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ClassificationClass,RealmConstant._NameProperty,classificationName,1);
            Object classificationExistenceRes = workingGraphOperationExecutor.executeRead(new CheckResultExistenceTransformer(),checkCql);
            if(((Boolean)classificationExistenceRes).booleanValue()){
                return null;
            }
            Map<String,Object> propertiesMap = new HashMap<>();
            propertiesMap.put(RealmConstant._NameProperty,classificationName);
            if(classificationDesc != null) {
                propertiesMap.put(RealmConstant._DescProperty, classificationDesc);
            }
            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
            String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.ClassificationClass},propertiesMap);
            GetSingleClassificationTransformer getSingleClassificationTransformer =
                    new GetSingleClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createClassificationRes = workingGraphOperationExecutor.executeWrite(getSingleClassificationTransformer,createCql);
            Classification targetClassification = createClassificationRes != null ? (Classification)createClassificationRes : null;
            executeClassificationCacheOperation(targetClassification,CacheOperationType.INSERT);
            return targetClassification;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Classification createClassification(String classificationName, String classificationDesc, String parentClassificationName) throws CoreRealmServiceRuntimeException {
        if(classificationName == null || parentClassificationName == null){
            return null;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            //check Parent Classification exist
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ClassificationClass,RealmConstant._NameProperty,parentClassificationName,1);
            GetSingleClassificationTransformer getSingleClassificationTransformer =
                    new GetSingleClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object parentClassificationRes = workingGraphOperationExecutor.executeWrite(getSingleClassificationTransformer,queryCql);
            if(parentClassificationRes == null){
                logger.error("Classification named {} doesn't exist.", parentClassificationName);
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("Classification named "+parentClassificationName+" doesn't exist.");
                throw exception;
            }
            String checkCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ClassificationClass,RealmConstant._NameProperty,classificationName,1);
            Object classificationExistenceRes = workingGraphOperationExecutor.executeRead(new CheckResultExistenceTransformer(),checkCql);
            if(((Boolean)classificationExistenceRes).booleanValue()){
                return null;
            }
            Map<String,Object> propertiesMap = new HashMap<>();
            propertiesMap.put(RealmConstant._NameProperty,classificationName);
            if(classificationDesc != null) {
                propertiesMap.put(RealmConstant._DescProperty, classificationDesc);
            }
            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
            String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.ClassificationClass},propertiesMap);
            Object createClassificationRes = workingGraphOperationExecutor.executeWrite(getSingleClassificationTransformer,createCql);
            Classification targetClassification = createClassificationRes != null ? (Classification)createClassificationRes : null;
            if(targetClassification != null){
                executeClassificationCacheOperation(targetClassification,CacheOperationType.INSERT);

                String childConceptionUID = ((Neo4JClassificationImpl)targetClassification).getClassificationUID();
                String parentConceptionUID = ((Neo4JClassificationImpl)parentClassificationRes).getClassificationUID();
                Map<String,Object> relationPropertiesMap = new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
                String createRelationCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(childConceptionUID),Long.parseLong(parentConceptionUID),
                        RealmConstant.Classification_ClassificationRelationClass,relationPropertiesMap);
                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.Classification_ClassificationRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createRelationCql);
                if(newRelationEntityRes == null){
                    logger.error("Set Classification {}'s parent to Classification {} fail.", classificationName,parentClassificationName);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("Set Classification "+classificationName+"'s parent to Classification "+parentClassificationName+" fail.");
                    throw exception;
                }
            }
            return targetClassification;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeClassification(String classificationName) throws CoreRealmServiceRuntimeException {
        return this.removeClassification(classificationName,false);
    }

    @Override
    public boolean removeClassificationWithOffspring(String classificationName) throws CoreRealmServiceRuntimeException {
        return this.removeClassification(classificationName,true);
    }

    @Override
    public ConceptionEntity newMultiConceptionEntity(String[] conceptionKindNames, ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation) throws CoreRealmServiceRuntimeException {
        if(conceptionKindNames == null || conceptionKindNames.length == 0){
            logger.error("At least one Conception Kind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("At least one Conception Kind Name is required.");
            throw exception;
        }
        if (conceptionEntityValue != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                Map<String, Object> propertiesMap = conceptionEntityValue.getEntityAttributesValue() != null ?
                        conceptionEntityValue.getEntityAttributesValue() : new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(conceptionKindNames, propertiesMap);
                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(conceptionKindNames[0], this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, createCql);
                ConceptionEntity resultEntity = newEntityRes != null ? (ConceptionEntity) newEntityRes : null;
                if(addPerDefinedRelation && resultEntity != null){
                    List<String> uidList = new ArrayList<>();
                    uidList.add(resultEntity.getConceptionEntityUID());
                    for(String currentConceptionKindName:conceptionKindNames){
                        CommonOperationUtil.attachEntities(currentConceptionKindName,uidList,workingGraphOperationExecutor);
                    }
                }
                return resultEntity;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public ConceptionEntity newMultiConceptionEntity(String[] conceptionKindNames, ConceptionEntityValue conceptionEntityValue, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) throws CoreRealmServiceRuntimeException {
        ConceptionEntity conceptionEntity = newMultiConceptionEntity(conceptionKindNames,conceptionEntityValue,false);
        if(relationAttachKindList != null){
            for(RelationAttachKind currentRelationAttachKind : relationAttachKindList){
                currentRelationAttachKind.newRelationEntities(conceptionEntity.getConceptionEntityUID(),entityRelateRole,null);
            }
        }
        return conceptionEntity;
    }

    @Override
    public EntitiesOperationResult newMultiConceptionEntities(String[] conceptionKindNames, List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation) throws CoreRealmServiceRuntimeException {
        if(conceptionKindNames == null || conceptionKindNames.length == 0){
            logger.error("At least one Conception Kind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("At least one Conception Kind Name is required.");
            throw exception;
        }

        if(conceptionEntityValues !=null && conceptionEntityValues.size()>0){
            CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();

            ZonedDateTime currentDateTime = ZonedDateTime.now();
            List<Map<String,Object>> attributesValueMap = new ArrayList<>();
            for(ConceptionEntityValue currentConceptionEntityValue:conceptionEntityValues){
                Map<String,Object> currentDateAttributesMap = currentConceptionEntityValue.getEntityAttributesValue();
                CommonOperationUtil.generateEntityMetaAttributes(currentDateAttributesMap,currentDateTime);
                attributesValueMap.add(currentDateAttributesMap);
            }

            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String createCql = CypherBuilder.createMultiLabeledNodesWithProperties(conceptionKindNames, attributesValueMap);
                GetMapFormatAggregatedReturnValueTransformer getMapFormatAggregatedReturnValueTransformer =
                        new GetMapFormatAggregatedReturnValueTransformer();
                Object newEntityRes = workingGraphOperationExecutor.executeWrite(getMapFormatAggregatedReturnValueTransformer, createCql);
                if(newEntityRes!=null){
                    Map<String, Node> resultNodesMap = (Map<String, Node>)newEntityRes;
                    Iterator<Map.Entry<String,Node>> iter = resultNodesMap.entrySet().iterator();
                    while(iter.hasNext()){
                        Map.Entry<String,Node> entry = iter.next();
                        Node value = entry.getValue();
                        commonEntitiesOperationResultImpl.getSuccessEntityUIDs().add(""+value.id());
                    }
                    commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(resultNodesMap.size());
                    commonEntitiesOperationResultImpl.getOperationStatistics().
                            setOperationSummary("newEntities operation for multi conceptionKind success.");
                }
                commonEntitiesOperationResultImpl.finishEntitiesOperation();
                if(addPerDefinedRelation && commonEntitiesOperationResultImpl.getSuccessEntityUIDs() != null){
                    for(String currentConceptionKindName:conceptionKindNames){
                        CommonOperationUtil.attachEntities(currentConceptionKindName,commonEntitiesOperationResultImpl.getSuccessEntityUIDs(),workingGraphOperationExecutor);
                    }
                }
                return commonEntitiesOperationResultImpl;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public EntitiesOperationResult newMultiConceptionEntities(String[] conceptionKindNames, List<ConceptionEntityValue> conceptionEntityValues, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) throws CoreRealmServiceRuntimeException {
        EntitiesOperationResult entitiesOperationResult = newMultiConceptionEntities(conceptionKindNames,conceptionEntityValues,false);
        if(relationAttachKindList != null){
            for(RelationAttachKind currentRelationAttachKind : relationAttachKindList){
                currentRelationAttachKind.newRelationEntities(entitiesOperationResult.getSuccessEntityUIDs(),entityRelateRole,null);
            }
        }
        return entitiesOperationResult;
    }

    @Override
    public List<Map<String,Map<String,Object>>> executeCustomQuery(String customQuerySentence) throws CoreRealmServiceRuntimeException {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetListMapTransformer getListMapTransformer = new GetListMapTransformer();
            Object newEntityRes = workingGraphOperationExecutor.executeWrite(getListMapTransformer, customQuerySentence);
            return newEntityRes != null ? (List<Map<String,Map<String,Object>>>)newEntityRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Map<String, Number> executeCustomStatistic(String customQuerySentence) throws CoreRealmServiceRuntimeException {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            DataTransformer resultHandleDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Map<String,Number> resultStatisticMap = new HashMap<>();
                        Record returnRecord = result.next();
                        Map<String,Object> returnValueMap = returnRecord.asMap();
                        Set<String> keySet = returnValueMap.keySet();
                        for(String currentKey : keySet){
                            String currentStatisticKey = currentKey.replace(CypherBuilder.operationResultName+".","");
                            Number currentStatisticValue = (Number)returnValueMap.get(currentKey);
                            resultStatisticMap.put(currentStatisticKey,currentStatisticValue);
                        }
                        return resultStatisticMap;
                    }
                    return null;
                }
            };
            Object statisticCqlRes = workingGraphOperationExecutor.executeRead(resultHandleDataTransformer,customQuerySentence);
            if(statisticCqlRes != null){
                return (Map<String,Number>)statisticCqlRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public TimeFlow getOrCreateTimeFlow() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.TimeFlowClass,RealmConstant._NameProperty,RealmConstant._defaultTimeFlowName,1);
            GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                    new GetSingleConceptionEntityTransformer(RealmConstant.TimeFlowClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object getDefaultTimeFlowRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,queryCql);
            if(getDefaultTimeFlowRes == null){
                Map<String,Object> propertiesMap = new HashMap<>();
                propertiesMap.put(RealmConstant._NameProperty,RealmConstant._defaultTimeFlowName);
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.TimeFlowClass},propertiesMap);
                GetSingleTimeFlowTransformer getSingleTimeFlowTransformer = new GetSingleTimeFlowTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object createTimeFlowRes = workingGraphOperationExecutor.executeWrite(getSingleTimeFlowTransformer,createCql);
                TimeFlow resultTimeFlow = createTimeFlowRes != null ? (TimeFlow)createTimeFlowRes : null;
                return resultTimeFlow;
            }
            return (TimeFlow)getDefaultTimeFlowRes;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public TimeFlow getOrCreateTimeFlow(String timeFlowName) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.TimeFlowClass,RealmConstant._NameProperty,timeFlowName,1);
            GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                    new GetSingleConceptionEntityTransformer(RealmConstant.TimeFlowClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object getDefaultTimeFlowRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,queryCql);
            if(getDefaultTimeFlowRes == null){
                Map<String,Object> propertiesMap = new HashMap<>();
                propertiesMap.put(RealmConstant._NameProperty,timeFlowName);
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.TimeFlowClass},propertiesMap);
                GetSingleTimeFlowTransformer getSingleTimeFlowTransformer = new GetSingleTimeFlowTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object createTimeFlowRes = workingGraphOperationExecutor.executeWrite(getSingleTimeFlowTransformer,createCql);
                TimeFlow resultTimeFlow = createTimeFlowRes != null ? (TimeFlow)createTimeFlowRes : null;
                return resultTimeFlow;
            }
            return (TimeFlow)getDefaultTimeFlowRes;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeTimeFlow(String timeFlowName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<TimeFlow> getTimeFlows() {
        return null;
    }

    @Override
    public void openGlobalSession() {
        GraphOperationExecutor graphOperationExecutor = new GraphOperationExecutor();
        this.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public void closeGlobalSession() {
        if(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor() != null){
            this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor().close();
            this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(null);
        }
    }

    private boolean removeClassification(String classificationName, boolean cascadingDeleteOffspring) throws CoreRealmServiceRuntimeException {
        if(classificationName == null){
            return false;
        }
        Classification targetClassification = this.getClassification(classificationName);
        if(targetClassification == null){
            logger.error("CoreRealm does not contains Classification with name {}.", classificationName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("CoreRealm does not contains Classification with name " + classificationName + ".");
            throw exception;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                if(!cascadingDeleteOffspring) {
                    String classificationUID = ((Neo4JClassificationImpl) targetClassification).getClassificationUID();
                    String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.valueOf(classificationUID), null, null);
                    GetSingleClassificationTransformer getSingleClassificationTransformer =
                            new GetSingleClassificationTransformer(coreRealmName, this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                    Object deletedClassificationRes = workingGraphOperationExecutor.executeWrite(getSingleClassificationTransformer, deleteCql);
                    Classification resultClassification = deletedClassificationRes != null ? (Classification) deletedClassificationRes : null;
                    if (resultClassification == null) {
                        throw new CoreRealmServiceRuntimeException();
                    } else {
                        String classificationId = ((Neo4JClassificationImpl) resultClassification).getClassificationUID();
                        Neo4JClassificationImpl resultNeo4JClassificationImplForCacheOperation = new Neo4JClassificationImpl(coreRealmName, classificationName, null, classificationId);
                        executeClassificationCacheOperation(resultNeo4JClassificationImplForCacheOperation, CacheOperationType.DELETE);
                        return true;
                    }
                }else{
                    String classificationUID = ((Neo4JClassificationImpl) targetClassification).getClassificationUID();
                    List<Object> withOffspringClassificationUIDList = new ArrayList<>();
                    String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),
                            RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.FROM,0,0, CypherBuilder.ReturnRelationableDataType.BOTH);
                    withOffspringClassificationUIDList.add(Long.parseLong(classificationUID));
                    DataTransformer offspringClassificationsDataTransformer = new DataTransformer() {
                        @Override
                        public Object transformResult(Result result) {
                            List<Record> recordList = result.list();
                            if(recordList != null){
                                for(Record nodeRecord : recordList){
                                    Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                                    long nodeUID = resultNode.id();
                                    withOffspringClassificationUIDList.add(nodeUID);
                                }
                            }
                            return null;
                        }
                    };
                    workingGraphOperationExecutor.executeRead(offspringClassificationsDataTransformer,queryCql);

                    List<Object> deletedClassificationUIDList = new ArrayList<>();
                    String deleteCql = CypherBuilder.deleteNodesWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, withOffspringClassificationUIDList);
                    DataTransformer deleteOffspringClassificationsDataTransformer = new DataTransformer() {
                        @Override
                        public Object transformResult(Result result) {
                            List<Record> recordList = result.list();
                            if(recordList != null){
                                for(Record nodeRecord : recordList){
                                    Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                                    long nodeUID = resultNode.id();
                                    deletedClassificationUIDList.add(nodeUID);
                                }
                            }
                            return null;
                        }
                    };
                    workingGraphOperationExecutor.executeWrite(deleteOffspringClassificationsDataTransformer,deleteCql);

                    if(deletedClassificationUIDList.size() == withOffspringClassificationUIDList.size()){
                        return true;
                    }else{
                        logger.error("Not all offspring classifications of Classification {} are successful removed.", classificationName);
                        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                        exception.setCauseMessage("Not all offspring classifications of Classification "+classificationName+" are successful removed.");
                        throw exception;

                    }
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
