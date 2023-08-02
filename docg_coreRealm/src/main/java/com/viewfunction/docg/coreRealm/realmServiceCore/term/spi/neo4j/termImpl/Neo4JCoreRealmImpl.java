package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.EntitiesExchangeOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl.Neo4JCrossKindDataOperatorImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl.Neo4JDataScienceOperatorImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl.Neo4JEntitiesExchangeOperatorImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl.Neo4JSystemMaintenanceOperatorImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonEntitiesOperationResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.config.PropertiesHandler;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
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
    public String getCoreRealmName() {
        return coreRealmName != null ? coreRealmName: PropertiesHandler.getPropertyValue(PropertiesHandler.DEFAULT_REALM_NAME);
    }

    @Override
    public ConceptionKind getConceptionKind(String conceptionKindName) {
        if(conceptionKindName == null){
            return null;
        }

        if(conceptionKindName.startsWith("DOCG_")){
            Neo4JConceptionKindImpl neo4JConceptionKindImpl =
                    new Neo4JConceptionKindImpl(getCoreRealmName(),conceptionKindName,null,"0");
            neo4JConceptionKindImpl.setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            return neo4JConceptionKindImpl;
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ConceptionKindClass,RealmConstant._NameProperty,conceptionKindName,1);
            GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                    new GetSingleConceptionKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleConceptionKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                        new GetSingleConceptionKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,deleteCql);
                ConceptionKind resultConceptionKind = deletedConceptionKindRes != null ? (ConceptionKind)deletedConceptionKindRes : null;
                if(resultConceptionKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String conceptionKindId = ((Neo4JConceptionKindImpl)resultConceptionKind).getConceptionKindUID();
                    Neo4JConceptionKindImpl resultNeo4JConceptionKindImplForCacheOperation = new Neo4JConceptionKindImpl(getCoreRealmName(),conceptionKindName,null,conceptionKindId);
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
                    new GetSingleAttributesViewKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleAttributesViewKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                        new GetSingleAttributesViewKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                        new GetListAttributesViewKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleAttributeKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleAttributeKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                        new GetSingleAttributeKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                GetListAttributeKindTransformer getListAttributeKindTransformer = new GetListAttributeKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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

        if(relationKindName.startsWith("DOCG_")){
            Neo4JRelationKindImpl neo4JRelationKindImpl =
                    new Neo4JRelationKindImpl(getCoreRealmName(),relationKindName,null,"0");
            neo4JRelationKindImpl.setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            return neo4JRelationKindImpl;
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.RelationKindClass,RealmConstant._NameProperty,relationKindName,1);
            GetSingleRelationKindTransformer getSingleRelationKindTransformer =
                    new GetSingleRelationKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleRelationKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                        new GetSingleRelationKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deletedRelationKindRes = workingGraphOperationExecutor.executeWrite(getSingleRelationKindTransformer,deleteCql);
                RelationKind resultKind = deletedRelationKindRes != null ? (RelationKind)deletedRelationKindRes : null;
                if(resultKind == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String resultRelationKindUID = ((Neo4JRelationKindImpl)resultKind).getRelationKindUID();
                    Neo4JRelationKindImpl resultNeo4JRelationKindImplForCacheOperation = new Neo4JRelationKindImpl(getCoreRealmName(),relationKindName,null,resultRelationKindUID);
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
                GetListRelationAttachKindTransformer getListRelationAttachKindTransformer = new GetListRelationAttachKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleRelationAttachKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleRelationAttachKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                        new GetSingleRelationAttachKindTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleClassificationTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleClassificationTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
                    new GetSingleClassificationTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
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
            GetSingleTimeFlowTransformer getSingleTimeFlowTransformer =
                    new GetSingleTimeFlowTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object getDefaultTimeFlowRes = workingGraphOperationExecutor.executeRead(getSingleTimeFlowTransformer,queryCql);
            if(getDefaultTimeFlowRes == null){
                Map<String,Object> propertiesMap = new HashMap<>();
                propertiesMap.put(RealmConstant._NameProperty,RealmConstant._defaultTimeFlowName);
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.TimeFlowClass},propertiesMap);
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
            GetSingleTimeFlowTransformer getSingleTimeFlowTransformer =
                    new GetSingleTimeFlowTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object getDefaultTimeFlowRes = workingGraphOperationExecutor.executeRead(getSingleTimeFlowTransformer,queryCql);
            if(getDefaultTimeFlowRes == null){
                Map<String,Object> propertiesMap = new HashMap<>();
                propertiesMap.put(RealmConstant._NameProperty,timeFlowName);
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.TimeFlowClass},propertiesMap);
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
    public long removeTimeFlowWithEntities() {
        return removeTimeFlowWithEntitiesLogic(RealmConstant._defaultTimeFlowName);
    }

    @Override
    public long removeTimeFlowWithEntities(String timeFlowName) {
        return removeTimeFlowWithEntitiesLogic(timeFlowName);
    }

    @Override
    public List<TimeFlow> getTimeFlows() {
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(1000000);
        try {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.TimeFlowClass,queryParameters,null);
                GetListTimeFlowTransformer getListTimeFlowTransformer =
                        new GetListTimeFlowTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object timeFlowsRes = workingGraphOperationExecutor.executeRead(getListTimeFlowTransformer,queryCql);
                return timeFlowsRes != null ? (List<TimeFlow>) timeFlowsRes : null;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GeospatialRegion getOrCreateGeospatialRegion() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialRegionClass,RealmConstant._NameProperty,RealmConstant._defaultGeospatialRegionName,1);
            GetSingleGeospatialRegionTransformer getSingleGeospatialRegionTransformer =
                    new GetSingleGeospatialRegionTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object getDefaultGeospatialRegionRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialRegionTransformer,queryCql);
            if(getDefaultGeospatialRegionRes == null){
                Map<String,Object> propertiesMap = new HashMap<>();
                propertiesMap.put(RealmConstant._NameProperty,RealmConstant._defaultGeospatialRegionName);
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.GeospatialRegionClass},propertiesMap);
                Object createGeospatialRegionRes = workingGraphOperationExecutor.executeWrite(getSingleGeospatialRegionTransformer,createCql);
                GeospatialRegion resultNeo4JGeospatialRegionImpl = createGeospatialRegionRes != null ? (Neo4JGeospatialRegionImpl)createGeospatialRegionRes : null;
                return resultNeo4JGeospatialRegionImpl;
            }
            return (GeospatialRegion)getDefaultGeospatialRegionRes;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public GeospatialRegion getOrCreateGeospatialRegion(String geospatialRegionName) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialRegionClass,RealmConstant._NameProperty,geospatialRegionName,1);
            GetSingleGeospatialRegionTransformer getSingleGeospatialRegionTransformer =
                    new GetSingleGeospatialRegionTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object getGeospatialRegionRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialRegionTransformer,queryCql);
            if(getGeospatialRegionRes == null){
                Map<String,Object> propertiesMap = new HashMap<>();
                propertiesMap.put(RealmConstant._NameProperty,geospatialRegionName);
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{RealmConstant.GeospatialRegionClass},propertiesMap);
                Object createGeospatialRegionRes = workingGraphOperationExecutor.executeWrite(getSingleGeospatialRegionTransformer,createCql);
                GeospatialRegion resultTimeFlow = createGeospatialRegionRes != null ? (Neo4JGeospatialRegionImpl)createGeospatialRegionRes : null;
                return resultTimeFlow;
            }
            return (GeospatialRegion)getGeospatialRegionRes;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public long removeGeospatialRegionWithEntities() {
        return removeGeospatialRegionWithEntitiesLogic(RealmConstant._defaultGeospatialRegionName);
    }

    @Override
    public long removeGeospatialRegionWithEntities(String geospatialRegionName) {
        return removeGeospatialRegionWithEntitiesLogic(geospatialRegionName);
    }

    @Override
    public List<GeospatialRegion> getGeospatialRegions() {
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setResultNumber(1000000);
        try {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialRegionClass,queryParameters,null);
                GetListGeospatialRegionTransformer getListGeospatialRegionTransformer =
                        new GetListGeospatialRegionTransformer(getCoreRealmName(),this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object geospatialRegionsRes = workingGraphOperationExecutor.executeRead(getListGeospatialRegionTransformer,queryCql);
                return geospatialRegionsRes != null ? (List<GeospatialRegion>) geospatialRegionsRes : null;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<EntityStatisticsInfo> getConceptionEntitiesStatistics() throws CoreRealmServiceEntityExploreException {
        List<EntityStatisticsInfo> entityStatisticsInfoList = new ArrayList<>();
        String cypherProcedureString = "CALL db.labels()\n" +
            "YIELD label\n" +
            "CALL apoc.cypher.run(\"MATCH (:`\"+label+\"`) RETURN count(*) as count\", null)\n" +
            "YIELD value\n" +
            "RETURN label, value.count as count\n" +
            "ORDER BY label";
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            List<String> attributesNameList = new ArrayList<>();
            Map<String,HashMap<String,Object>> conceptionKindMetaDataMap = new HashMap<>();
            attributesNameList.add(RealmConstant._NameProperty);
            attributesNameList.add(RealmConstant._DescProperty);
            attributesNameList.add(RealmConstant._createDateProperty);
            attributesNameList.add(RealmConstant._lastModifyDateProperty);
            attributesNameList.add(RealmConstant._creatorIdProperty);
            attributesNameList.add(RealmConstant._dataOriginProperty);
            String queryCql = CypherBuilder.matchAttributesWithQueryParameters(RealmConstant.ConceptionKindClass,null,attributesNameList);
            DataTransformer conceptionKindInfoDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        String conceptionKindName = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._NameProperty).asString();
                        String conceptionKindDesc = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._DescProperty).asString();
                        ZonedDateTime createDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asZonedDateTime();
                        ZonedDateTime lastModifyDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asZonedDateTime();
                        String dataOrigin = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._dataOriginProperty).asString();
                        long conceptionKindUID = nodeRecord.get("id("+CypherBuilder.operationResultName+")").asLong();
                        String creatorId = nodeRecord.containsKey(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty) ?
                                nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty).asString():null;

                        HashMap<String,Object> metaDataMap = new HashMap<>();
                        metaDataMap.put(RealmConstant._NameProperty,conceptionKindName);
                        metaDataMap.put(RealmConstant._DescProperty,conceptionKindDesc);
                        metaDataMap.put(RealmConstant._createDateProperty,createDate);
                        metaDataMap.put(RealmConstant._lastModifyDateProperty,lastModifyDate);
                        metaDataMap.put(RealmConstant._creatorIdProperty,creatorId);
                        metaDataMap.put(RealmConstant._dataOriginProperty,dataOrigin);
                        metaDataMap.put("ConceptionKindUID",""+conceptionKindUID);
                        conceptionKindMetaDataMap.put(conceptionKindName,metaDataMap);
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(conceptionKindInfoDataTransformer,queryCql);

            DataTransformer queryResultDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    List<String> conceptionKindsNameWithDataList = new ArrayList<>();
                    while(result.hasNext()){
                        Record currentRecord = result.next();
                        String entityKind = currentRecord.get("label").asString();
                        long entityCount = currentRecord.get("count").asLong();
                        conceptionKindsNameWithDataList.add(entityKind);
                        EntityStatisticsInfo currentEntityStatisticsInfo = null;
                        if(entityKind.startsWith("DOCG_")){
                            currentEntityStatisticsInfo = new EntityStatisticsInfo(
                                    entityKind, EntityStatisticsInfo.kindType.ConceptionKind, true, entityCount);
                        }else{
                            if(conceptionKindMetaDataMap.containsKey(entityKind)){
                                currentEntityStatisticsInfo = new EntityStatisticsInfo(
                                        entityKind, EntityStatisticsInfo.kindType.ConceptionKind, false, entityCount,
                                        conceptionKindMetaDataMap.get(entityKind).get(RealmConstant._DescProperty).toString(),
                                        conceptionKindMetaDataMap.get(entityKind).get("ConceptionKindUID").toString(),
                                        (ZonedDateTime) (conceptionKindMetaDataMap.get(entityKind).get(RealmConstant._createDateProperty)),
                                        (ZonedDateTime) (conceptionKindMetaDataMap.get(entityKind).get(RealmConstant._lastModifyDateProperty)),
                                        conceptionKindMetaDataMap.get(entityKind).get(RealmConstant._creatorIdProperty).toString(),
                                        conceptionKindMetaDataMap.get(entityKind).get(RealmConstant._dataOriginProperty).toString()
                                );
                            }
                        }
                        if(currentEntityStatisticsInfo != null){
                            entityStatisticsInfoList.add(currentEntityStatisticsInfo);
                        }
                    }

                    //如果 ConceptionKind 尚未有实体数据，则 CALL db.labels() 不会返回该 Kind 记录，需要从 ConceptionKind 列表反向查找
                    Set<String> allConceptionKindNameSet = conceptionKindMetaDataMap.keySet();
                    for(String currentKindName :allConceptionKindNameSet ){
                        if(!conceptionKindsNameWithDataList.contains(currentKindName)){
                            //当前 ConceptionKind 中无数据，需要手动添加信息
                            EntityStatisticsInfo currentEntityStatisticsInfo = new EntityStatisticsInfo(
                                    currentKindName, EntityStatisticsInfo.kindType.ConceptionKind, false, 0,
                                    conceptionKindMetaDataMap.get(currentKindName).get(RealmConstant._DescProperty).toString(),
                                    conceptionKindMetaDataMap.get(currentKindName).get("ConceptionKindUID").toString(),
                                    (ZonedDateTime) (conceptionKindMetaDataMap.get(currentKindName).get(RealmConstant._createDateProperty)),
                                    (ZonedDateTime) (conceptionKindMetaDataMap.get(currentKindName).get(RealmConstant._lastModifyDateProperty)),
                                    conceptionKindMetaDataMap.get(currentKindName).get(RealmConstant._creatorIdProperty).toString(),
                                    conceptionKindMetaDataMap.get(currentKindName).get(RealmConstant._dataOriginProperty).toString()
                            );
                            entityStatisticsInfoList.add(currentEntityStatisticsInfo);
                        }
                    }
                    return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(queryResultDataTransformer,cypherProcedureString);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return entityStatisticsInfoList;
    }

    @Override
    public List<EntityStatisticsInfo> getRelationEntitiesStatistics() {
        List<EntityStatisticsInfo> entityStatisticsInfoList = new ArrayList<>();
        String cypherProcedureString = "CALL db.relationshipTypes()\n" +
                "YIELD relationshipType\n" +
                "CALL apoc.cypher.run(\"MATCH ()-[:\" + `relationshipType` + \"]->()\n" +
                "RETURN count(*) as count\", null)\n" +
                "YIELD value\n" +
                "RETURN relationshipType, value.count AS count\n" +
                "ORDER BY relationshipType";
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            List<String> attributesNameList = new ArrayList<>();
            Map<String,HashMap<String,Object>> relationKindMetaDataMap = new HashMap<>();
            attributesNameList.add(RealmConstant._NameProperty);
            attributesNameList.add(RealmConstant._DescProperty);
            attributesNameList.add(RealmConstant._createDateProperty);
            attributesNameList.add(RealmConstant._lastModifyDateProperty);
            attributesNameList.add(RealmConstant._creatorIdProperty);
            attributesNameList.add(RealmConstant._dataOriginProperty);
            String queryCql = CypherBuilder.matchAttributesWithQueryParameters(RealmConstant.RelationKindClass,null,attributesNameList);
            DataTransformer relationKindInfoDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        String relationKindName = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._NameProperty).asString();
                        String relationKindDesc = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._DescProperty).asString();
                        ZonedDateTime createDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asZonedDateTime();
                        ZonedDateTime lastModifyDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asZonedDateTime();
                        String dataOrigin = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._dataOriginProperty).asString();
                        long conceptionKindUID = nodeRecord.get("id("+CypherBuilder.operationResultName+")").asLong();
                        String creatorId = nodeRecord.containsKey(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty) ?
                                nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty).asString():null;

                        HashMap<String,Object> metaDataMap = new HashMap<>();
                        metaDataMap.put(RealmConstant._NameProperty,relationKindName);
                        metaDataMap.put(RealmConstant._DescProperty,relationKindDesc);
                        metaDataMap.put(RealmConstant._createDateProperty,createDate);
                        metaDataMap.put(RealmConstant._lastModifyDateProperty,lastModifyDate);
                        metaDataMap.put(RealmConstant._creatorIdProperty,creatorId);
                        metaDataMap.put(RealmConstant._dataOriginProperty,dataOrigin);
                        metaDataMap.put("RelationKindUID",""+conceptionKindUID);
                        relationKindMetaDataMap.put(relationKindName,metaDataMap);
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(relationKindInfoDataTransformer,queryCql);
            DataTransformer queryResultDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    List<String> conceptionKindsNameWithDataList = new ArrayList<>();
                    while(result.hasNext()){
                        Record currentRecord = result.next();
                        String entityKind = currentRecord.get("relationshipType").asString();
                        long entityCount = currentRecord.get("count").asLong();
                        conceptionKindsNameWithDataList.add(entityKind);
                        EntityStatisticsInfo currentEntityStatisticsInfo = null;
                        if(entityKind.startsWith("DOCG_")){
                            currentEntityStatisticsInfo = new EntityStatisticsInfo(
                                    entityKind, EntityStatisticsInfo.kindType.RelationKind, true, entityCount);
                        }else{
                            if(relationKindMetaDataMap.containsKey(entityKind)){
                                currentEntityStatisticsInfo = new EntityStatisticsInfo(
                                        entityKind, EntityStatisticsInfo.kindType.RelationKind, false, entityCount,
                                        relationKindMetaDataMap.get(entityKind).get(RealmConstant._DescProperty).toString(),
                                        relationKindMetaDataMap.get(entityKind).get("RelationKindUID").toString(),
                                        (ZonedDateTime) (relationKindMetaDataMap.get(entityKind).get(RealmConstant._createDateProperty)),
                                        (ZonedDateTime) (relationKindMetaDataMap.get(entityKind).get(RealmConstant._lastModifyDateProperty)),
                                        relationKindMetaDataMap.get(entityKind).get(RealmConstant._creatorIdProperty).toString(),
                                        relationKindMetaDataMap.get(entityKind).get(RealmConstant._dataOriginProperty).toString()
                                );
                            }
                        }
                        if(currentEntityStatisticsInfo != null){
                            entityStatisticsInfoList.add(currentEntityStatisticsInfo);
                        }
                    }
                    //如果 ConceptionKind 尚未有实体数据，则 CALL db.relationshipTypes() 不会返回该 Kind 记录，需要从 ConceptionKind 列表反向查找
                    Set<String> allConceptionKindNameSet = relationKindMetaDataMap.keySet();
                    for(String currentKindName :allConceptionKindNameSet ){
                        if(!conceptionKindsNameWithDataList.contains(currentKindName)){
                            //当前 ConceptionKind 中无数据，需要手动添加信息
                            EntityStatisticsInfo currentEntityStatisticsInfo = new EntityStatisticsInfo(
                                    currentKindName, EntityStatisticsInfo.kindType.ConceptionKind, false, 0,
                                    relationKindMetaDataMap.get(currentKindName).get(RealmConstant._DescProperty).toString(),
                                    relationKindMetaDataMap.get(currentKindName).get("RelationKindUID").toString(),
                                    (ZonedDateTime) (relationKindMetaDataMap.get(currentKindName).get(RealmConstant._createDateProperty)),
                                    (ZonedDateTime) (relationKindMetaDataMap.get(currentKindName).get(RealmConstant._lastModifyDateProperty)),
                                    relationKindMetaDataMap.get(currentKindName).get(RealmConstant._creatorIdProperty).toString(),
                                    relationKindMetaDataMap.get(currentKindName).get(RealmConstant._dataOriginProperty).toString()
                            );
                            entityStatisticsInfoList.add(currentEntityStatisticsInfo);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(queryResultDataTransformer,cypherProcedureString);
        } catch (CoreRealmServiceEntityExploreException e) {
            throw new RuntimeException(e);
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return entityStatisticsInfoList;
    }

    @Override
    public List<ConceptionKindCorrelationInfo> getConceptionKindsCorrelation() {
        List<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoList = new ArrayList<>();
        String cypherProcedureString = "CALL apoc.meta.graph";

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer queryResultDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {

                    if(result.hasNext()){
                        Record currentRecord = result.next();
                        List nodesList = currentRecord.get("nodes").asList();

                        Map<String,String> conceptionKindMetaInfoMap = new HashMap<>();
                        for(Object currentNode:nodesList){
                            Node currentNeo4JNode = (Node)currentNode;
                            conceptionKindMetaInfoMap.put(""+currentNeo4JNode.id(),currentNeo4JNode.get("name").asString());
                        }
                        List relationList =  currentRecord.get("relationships").asList();
                        for(Object currenRelation:relationList){
                            Relationship currentNeo4JRelation = (Relationship)currenRelation;
                            String relationKindName = currentNeo4JRelation.type();
                            String startConceptionKindName = conceptionKindMetaInfoMap.get(""+currentNeo4JRelation.startNodeId());
                            String targetConceptionKindName = conceptionKindMetaInfoMap.get(""+currentNeo4JRelation.endNodeId());
                            int relationEntityCount = currentNeo4JRelation.get("count").asInt();
                            conceptionKindCorrelationInfoList.add(new ConceptionKindCorrelationInfo(startConceptionKindName,
                                    targetConceptionKindName,relationKindName,relationEntityCount)
                            );
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(queryResultDataTransformer,cypherProcedureString);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return conceptionKindCorrelationInfoList;
    }

    @Override
    public CrossKindDataOperator getCrossKindDataOperator() {
        Neo4JCrossKindDataOperatorImpl crossKindDataOperator= new Neo4JCrossKindDataOperatorImpl(this);
        crossKindDataOperator.setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
        return crossKindDataOperator;
    }

    @Override
    public SystemMaintenanceOperator getSystemMaintenanceOperator() {
        Neo4JSystemMaintenanceOperatorImpl systemMaintenanceOperatorImpl = new Neo4JSystemMaintenanceOperatorImpl(getCoreRealmName());
        systemMaintenanceOperatorImpl.setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
        return systemMaintenanceOperatorImpl;
    }

    @Override
    public DataScienceOperator getDataScienceOperator() {
        Neo4JDataScienceOperatorImpl neo4JDataScienceOperatorImpl = new Neo4JDataScienceOperatorImpl(getCoreRealmName());
        neo4JDataScienceOperatorImpl.setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
        return neo4JDataScienceOperatorImpl;
    }

    @Override
    public EntitiesExchangeOperator getEntitiesExchangeOperator() {
        Neo4JEntitiesExchangeOperatorImpl neo4JEntitiesExchangeOperatorImpl = new Neo4JEntitiesExchangeOperatorImpl(getCoreRealmName());
        neo4JEntitiesExchangeOperatorImpl.setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
        return neo4JEntitiesExchangeOperatorImpl;
    }

    @Override
    public List<KindMetaInfo> getConceptionKindsMetaInfo() throws CoreRealmServiceEntityExploreException {
        return getKindsMetaInfo(RealmConstant.ConceptionKindClass);
    }

    @Override
    public List<KindMetaInfo> getRelationKindsMetaInfo() throws CoreRealmServiceEntityExploreException{
        return getKindsMetaInfo(RealmConstant.RelationKindClass);
    }

    @Override
    public List<AttributeKindMetaInfo> getAttributeKindsMetaInfo() throws CoreRealmServiceEntityExploreException{
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            List<String> attributesNameList = new ArrayList<>();
            attributesNameList.add(RealmConstant._NameProperty);
            attributesNameList.add(RealmConstant._DescProperty);
            attributesNameList.add(RealmConstant._createDateProperty);
            attributesNameList.add(RealmConstant._lastModifyDateProperty);
            attributesNameList.add(RealmConstant._creatorIdProperty);
            attributesNameList.add(RealmConstant._dataOriginProperty);
            attributesNameList.add(RealmConstant._attributeDataType);
            String queryCql = CypherBuilder.matchAttributesWithQueryParameters(RealmConstant.AttributeKindClass,null,attributesNameList);
            DataTransformer dataTransformer = new DataTransformer<List<AttributeKindMetaInfo>>() {
                @Override
                public List<AttributeKindMetaInfo> transformResult(Result result) {
                    List<AttributeKindMetaInfo> resultKindMetaInfoList = new ArrayList<>();
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        String kindName = nodeRecord.get(CypherBuilder.operationResultName+"."+ RealmConstant._NameProperty).asString();
                        String kindDesc = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._DescProperty).asString();
                        ZonedDateTime createDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asZonedDateTime();
                        ZonedDateTime lastModifyDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asZonedDateTime();
                        String dataOrigin = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._dataOriginProperty).asString();
                        long KindUID = nodeRecord.get("id("+CypherBuilder.operationResultName+")").asLong();
                        String creatorId = nodeRecord.containsKey(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty) ?
                                nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty).asString():null;
                        String attributeDataType = nodeRecord.get(CypherBuilder.operationResultName+"."+ RealmConstant._attributeDataType).asString();
                        resultKindMetaInfoList.add(new AttributeKindMetaInfo(kindName,kindDesc,""+KindUID,attributeDataType,createDate,lastModifyDate,creatorId,dataOrigin));
                    }
                    return resultKindMetaInfoList;
                }
            };
            Object kindMetaInfoListRes = workingGraphOperationExecutor.executeRead(dataTransformer,queryCql);
            return kindMetaInfoListRes != null ? (List<AttributeKindMetaInfo>) kindMetaInfoListRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<AttributesViewKindMetaInfo> getAttributesViewKindsMetaInfo() throws CoreRealmServiceEntityExploreException{
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            List<String> attributesNameList = new ArrayList<>();
            attributesNameList.add(RealmConstant._NameProperty);
            attributesNameList.add(RealmConstant._DescProperty);
            attributesNameList.add(RealmConstant._createDateProperty);
            attributesNameList.add(RealmConstant._lastModifyDateProperty);
            attributesNameList.add(RealmConstant._creatorIdProperty);
            attributesNameList.add(RealmConstant._dataOriginProperty);
            attributesNameList.add(RealmConstant._viewKindDataForm);
            String queryCql = CypherBuilder.matchAttributesWithQueryParameters(RealmConstant.AttributesViewKindClass,null,attributesNameList);
            DataTransformer dataTransformer = new DataTransformer<List<AttributesViewKindMetaInfo>>() {
                @Override
                public List<AttributesViewKindMetaInfo> transformResult(Result result) {
                    List<AttributesViewKindMetaInfo> resultKindMetaInfoList = new ArrayList<>();
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        String kindName = nodeRecord.get(CypherBuilder.operationResultName+"."+ RealmConstant._NameProperty).asString();
                        String kindDesc = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._DescProperty).asString();
                        ZonedDateTime createDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asZonedDateTime();
                        ZonedDateTime lastModifyDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asZonedDateTime();
                        String dataOrigin = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._dataOriginProperty).asString();
                        long KindUID = nodeRecord.get("id("+CypherBuilder.operationResultName+")").asLong();
                        String creatorId = nodeRecord.containsKey(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty) ?
                                nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._creatorIdProperty).asString():null;
                        String viewKindDataForm = nodeRecord.get(CypherBuilder.operationResultName+"."+ RealmConstant._viewKindDataForm).asString();
                        resultKindMetaInfoList.add(new AttributesViewKindMetaInfo(kindName,kindDesc,""+KindUID,viewKindDataForm,createDate,lastModifyDate,creatorId,dataOrigin));
                    }
                    return resultKindMetaInfoList;
                }
            };
            Object kindMetaInfoListRes = workingGraphOperationExecutor.executeRead(dataTransformer,queryCql);
            return kindMetaInfoListRes != null ? (List<AttributesViewKindMetaInfo>) kindMetaInfoListRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
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

    private List<KindMetaInfo> getKindsMetaInfo(String kindClassName) throws CoreRealmServiceEntityExploreException {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            List<String> attributesNameList = new ArrayList<>();
            attributesNameList.add(RealmConstant._NameProperty);
            attributesNameList.add(RealmConstant._DescProperty);
            attributesNameList.add(RealmConstant._createDateProperty);
            attributesNameList.add(RealmConstant._lastModifyDateProperty);
            attributesNameList.add(RealmConstant._creatorIdProperty);
            attributesNameList.add(RealmConstant._dataOriginProperty);
            String queryCql = CypherBuilder.matchAttributesWithQueryParameters(kindClassName,null,attributesNameList);

            GetListKindMetaInfoTransformer getListKindMetaInfoTransformer = new GetListKindMetaInfoTransformer();
            Object kindMetaInfoListRes = workingGraphOperationExecutor.executeRead(getListKindMetaInfoTransformer,queryCql);
            return kindMetaInfoListRes != null ? (List<KindMetaInfo>) kindMetaInfoListRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
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
                            new GetSingleClassificationTransformer(getCoreRealmName(), this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                    Object deletedClassificationRes = workingGraphOperationExecutor.executeWrite(getSingleClassificationTransformer, deleteCql);
                    Classification resultClassification = deletedClassificationRes != null ? (Classification) deletedClassificationRes : null;
                    if (resultClassification == null) {
                        throw new CoreRealmServiceRuntimeException();
                    } else {
                        String classificationId = ((Neo4JClassificationImpl) resultClassification).getClassificationUID();
                        Neo4JClassificationImpl resultNeo4JClassificationImplForCacheOperation = new Neo4JClassificationImpl(getCoreRealmName(), classificationName, null, classificationId);
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

    private long removeTimeFlowWithEntitiesLogic(String timeFlowName){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String deleteEntitiesCql = "CALL apoc.periodic.commit(\"MATCH (n:"+RealmConstant.TimeScaleEntityClass+") WHERE n.timeFlow='"+timeFlowName+"' WITH n LIMIT $limit DETACH DELETE n RETURN count(*)\",{limit: 10000}) YIELD updates, executions, runtime, batches RETURN updates, executions, runtime, batches";
            logger.debug("Generated Cypher Statement: {}", deleteEntitiesCql);

            DataTransformer<Long> deleteTransformer = new DataTransformer() {
                @Override
                public Long transformResult(Result result) {
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        Long deletedTimeScaleEntitiesNumber =  nodeRecord.get("updates").asLong();
                        return deletedTimeScaleEntitiesNumber;
                    }
                    return null;
                }
            };
            Object deleteEntitiesRes = workingGraphOperationExecutor.executeWrite(deleteTransformer,deleteEntitiesCql);
            long currentDeletedEntitiesCount = deleteEntitiesRes != null ? ((Long)deleteEntitiesRes).longValue():0;

            String deleteTimeFlowCql = "MATCH (n:"+RealmConstant.TimeFlowClass+") WHERE n.name='"+timeFlowName+"' DETACH DELETE n RETURN COUNT(n) as "+CypherBuilder.operationResultName+"";
            logger.debug("Generated Cypher Statement: {}", deleteTimeFlowCql);
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer();
            deleteEntitiesRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteTimeFlowCql);
            long currentDeletedFlowsCount = deleteEntitiesRes != null ? ((Long)deleteEntitiesRes).longValue():0;

            return currentDeletedEntitiesCount + currentDeletedFlowsCount;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    private long removeGeospatialRegionWithEntitiesLogic(String geospatialRegionName){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String deleteEntitiesCql = "CALL apoc.periodic.commit(\"MATCH (n:"+RealmConstant.GeospatialScaleEntityClass+") WHERE n."+RealmConstant.GeospatialRegionClass+"='"+geospatialRegionName+"' WITH n LIMIT $limit DETACH DELETE n RETURN count(*)\",{limit: 10000}) YIELD updates, executions, runtime, batches RETURN updates, executions, runtime, batches";
            logger.debug("Generated Cypher Statement: {}", deleteEntitiesCql);

            DataTransformer<Long> deleteTransformer = new DataTransformer() {
                @Override
                public Long transformResult(Result result) {
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        Long deletedTimeScaleEntitiesNumber =  nodeRecord.get("updates").asLong();
                        return deletedTimeScaleEntitiesNumber;
                    }
                    return null;
                }
            };
            Object deleteEntitiesRes = workingGraphOperationExecutor.executeWrite(deleteTransformer,deleteEntitiesCql);
            long currentDeletedEntitiesCount = deleteEntitiesRes != null ? ((Long)deleteEntitiesRes).longValue():0;

            String deleteTimeFlowCql = "MATCH (n:"+RealmConstant.GeospatialRegionClass+") WHERE n.name='"+geospatialRegionName+"' DETACH DELETE n RETURN COUNT(n) as "+CypherBuilder.operationResultName+"";
            logger.debug("Generated Cypher Statement: {}", deleteTimeFlowCql);
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer();
            deleteEntitiesRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteTimeFlowCql);
            long currentDeletedFlowsCount = deleteEntitiesRes != null ? ((Long)deleteEntitiesRes).longValue():0;

            return currentDeletedEntitiesCount + currentDeletedFlowsCount;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
