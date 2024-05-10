package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.KindCacheable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonConceptionEntitiesAttributesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.spi.common.structureImpl.CommonInheritanceTreeImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JClassification;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.*;

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

    @Override
    public String getClassificationUID() {
        return this.classificationUID;
    }

    @Override
    public boolean updateClassificationName(String classificationNewName) throws CoreRealmServiceRuntimeException {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Classification renamedClassification = getClassificationByName(workingGraphOperationExecutor,classificationNewName);
            if(renamedClassification != null){
                logger.error("Classification with name {} already exist.", classificationNewName);
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("Classification with name "+classificationNewName+" already exist.");
                throw exception;
            }else{
                Map<String,Object> attributeDataMap = new HashMap<>();
                attributeDataMap.put(RealmConstant._NameProperty,classificationNewName);

                String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.classificationUID),attributeDataMap);
                GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._NameProperty);
                Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
                CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.classificationUID,false);
                AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
                if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(classificationNewName)){
                    this.classificationName = classificationNewName;
                    return true;
                }else{
                    return false;
                }
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    public String getCoreRealmName() {
        return this.coreRealmName;
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
    public boolean updateClassificationDesc(String classificationDesc) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String,Object> attributeDataMap = new HashMap<>();
            attributeDataMap.put(RealmConstant._DescProperty, classificationDesc);
            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.classificationUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._DescProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.classificationUID,false);
            AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(classificationDesc)){
                this.classificationDesc = classificationDesc;
                return true;
            }else{
                return false;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean isRootClassification() {
        return getParentClassification() == null ? true : false;
    }

    @Override
    public Classification getParentClassification() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.TO, null);
            GetSingleClassificationTransformer getSingleClassificationTransformer =
                    new GetSingleClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object classificationRes = workingGraphOperationExecutor.executeWrite(getSingleClassificationTransformer,queryCql);
            return classificationRes != null?(Classification)classificationRes:null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<Classification> getChildClassifications() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.FROM, null);
            GetListClassificationTransformer getListClassificationTransformer =
                    new GetListClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object classificationListRes = workingGraphOperationExecutor.executeWrite(getListClassificationTransformer,queryCql);
            return classificationListRes != null ? (List<Classification>)classificationListRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public InheritanceTree<Classification> getOffspringClassifications() {
        Table<String,String,Classification> treeElementsTable = HashBasedTable.create();
        treeElementsTable.put(InheritanceTree.Virtual_ParentID_Of_Root_Node,this.classificationName,this);
        Map<String,String> classificationUID_NameMapping = new HashMap<>();
        classificationUID_NameMapping.put(this.classificationUID,this.classificationName);

        String currentCoreRealmName = this.coreRealmName;
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            /*
            MATCH (sourceNode)<-[relation:`DOCG_ParentClassificationIs`*]-(operationResult:`DOCG_Classification`) WHERE id(sourceNode) = 2324 RETURN operationResult,relation
            */
            String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),
                    RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.FROM,0,0, CypherBuilder.ReturnRelationableDataType.BOTH);
            DataTransformer offspringClassificationsDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    List<Record> recordList = result.list();
                    if(recordList != null){
                        for(Record nodeRecord : recordList){
                            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                            long nodeUID = resultNode.id();
                            String classificationName = resultNode.get(RealmConstant._NameProperty).asString();
                            classificationUID_NameMapping.put(""+nodeUID,classificationName);
                        }
                    }
                    if(recordList != null){
                        for(Record nodeRecord : recordList){
                            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                            long nodeUID = resultNode.id();
                            String coreRealmName = currentCoreRealmName;
                            String classificationName = resultNode.get(RealmConstant._NameProperty).asString();
                            String classificationDesc = null;
                            if(resultNode.get(RealmConstant._DescProperty) != null){
                                classificationDesc = resultNode.get(RealmConstant._DescProperty).asString();
                            }
                            String classificationUID = ""+nodeUID;
                            Neo4JClassificationImpl neo4JClassificationImpl =
                                    new Neo4JClassificationImpl(coreRealmName,classificationName,classificationDesc,classificationUID);
                            neo4JClassificationImpl.setGlobalGraphOperationExecutor(graphOperationExecutorHelper.getGlobalGraphOperationExecutor());

                            List<Object> relationships = nodeRecord.get(CypherBuilder.relationResultName).asList();
                            String parentClassificationUID = null;
                            for(Object currentRelationship : relationships){
                                Relationship currentTargetRelationship = (Relationship)currentRelationship;
                                String startNodeUID = "" + currentTargetRelationship.startNodeId();
                                String endNodeUID = "" + currentTargetRelationship.endNodeId();
                                if(startNodeUID.equals(classificationUID)){
                                    parentClassificationUID = endNodeUID;
                                    break;
                                }
                            }
                            treeElementsTable.put(classificationUID_NameMapping.get(parentClassificationUID),
                                    classificationUID_NameMapping.get(classificationUID),neo4JClassificationImpl);

                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(offspringClassificationsDataTransformer,queryCql);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        CommonInheritanceTreeImpl<Classification> resultInheritanceTree = new CommonInheritanceTreeImpl(this.classificationName,treeElementsTable);
        return resultInheritanceTree;
    }

    @Override
    public boolean attachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException {
        if(childClassificationName == null){
            return false;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                Classification childClassification = getClassificationByName(workingGraphOperationExecutor,childClassificationName);
                if(childClassification == null){
                    logger.error("Classification with name {} does not exist.", childClassificationName);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("Classification with name "+childClassificationName+" does not exist.");
                    throw exception;
                }else{
                    String parentConceptionUID = this.getClassificationUID();
                    String childConceptionUID = ((Neo4JClassificationImpl)childClassification).getClassificationUID();

                    String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(childConceptionUID),Long.parseLong(parentConceptionUID),
                            RealmConstant.Classification_ClassificationRelationClass);
                    GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                            (RealmConstant.Classification_ClassificationRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                    Object existingRelationEntityRes = workingGraphOperationExecutor.executeRead(getSingleRelationEntityTransformer, queryRelationCql);
                    if(existingRelationEntityRes != null){
                        return true;
                    }

                    Map<String,Object> relationPropertiesMap = new HashMap<>();
                    CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
                    String createRelationCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(childConceptionUID),Long.parseLong(parentConceptionUID),
                            RealmConstant.Classification_ClassificationRelationClass,relationPropertiesMap);

                    Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createRelationCql);
                    if(newRelationEntityRes == null){
                        logger.error("Set Classification {}'s parent to Classification {} fail.", childClassificationName,classificationName);
                        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                        exception.setCauseMessage("Set Classification "+childClassificationName+"'s parent to Classification "+classificationName+" fail.");
                        throw exception;
                    }else{
                        return true;
                    }
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public boolean detachChildClassification(String childClassificationName) throws CoreRealmServiceRuntimeException {
        if(childClassificationName == null){
            return false;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                Classification childClassification = getClassificationByName(workingGraphOperationExecutor,childClassificationName);
                if(childClassification == null){
                    logger.error("Classification with name {} does not exist.", childClassificationName);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("Classification with name "+childClassificationName+" does not exist.");
                    throw exception;
                }else{
                    String parentConceptionUID = this.getClassificationUID();
                    String childConceptionUID = ((Neo4JClassificationImpl)childClassification).getClassificationUID();
                    String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(childConceptionUID),Long.parseLong(parentConceptionUID),
                            RealmConstant.Classification_ClassificationRelationClass);
                    GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                            (RealmConstant.Classification_ClassificationRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                    Object existingRelationEntityRes = workingGraphOperationExecutor.executeRead(getSingleRelationEntityTransformer, queryRelationCql);
                    if(existingRelationEntityRes == null){
                        logger.error("Classification {} is not parent of Classification {}.", getClassificationName(),classificationName);
                        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                        exception.setCauseMessage("Classification "+getClassificationName()+" is not parent of Classification "+classificationName+".");
                        throw exception;
                    }else{
                        RelationEntity relationEntity = (RelationEntity)existingRelationEntityRes;
                        String deleteCql = CypherBuilder.deleteRelationWithSingleFunctionValueEqual(
                                CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationEntity.getRelationEntityUID()),null,null);
                        getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                                (RealmConstant.Classification_ClassificationRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                        Object deleteRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, deleteCql);
                        if(deleteRelationEntityRes == null){
                            logger.error("Internal error occurs during detach child classification {} from {}.",  childClassificationName,this.getClassificationName());
                            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                            exception.setCauseMessage("Internal error occurs during detach child classification "+childClassificationName+" from "+this.getClassificationName()+".");
                            throw exception;
                        }else{
                            return true;
                        }
                    }
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public Classification createChildClassification(String classificationName, String classificationDesc) throws CoreRealmServiceRuntimeException{
        if(classificationName == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                Classification childClassification = getClassificationByName(workingGraphOperationExecutor,classificationName);
                if(childClassification != null){
                    logger.error("Classification with name {} already exist.", classificationName);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("Classification with name "+classificationName+" already exist.");
                    throw exception;
                }else{
                    GetSingleClassificationTransformer getSingleClassificationTransformer =
                            new GetSingleClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());

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
                        executeClassificationCacheOperation(targetClassification, KindCacheable.CacheOperationType.INSERT);

                        String childConceptionUID = ((Neo4JClassificationImpl)targetClassification).getClassificationUID();
                        String parentConceptionUID = getClassificationUID();
                        Map<String,Object> relationPropertiesMap = new HashMap<>();
                        CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
                        String createRelationCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(childConceptionUID),Long.parseLong(parentConceptionUID),
                                RealmConstant.Classification_ClassificationRelationClass,relationPropertiesMap);
                        GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                                (RealmConstant.Classification_ClassificationRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                        Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createRelationCql);
                        if(newRelationEntityRes == null){
                            logger.error("Set Classification {}'s parent to Classification {} fail.", classificationName,getClassificationName());
                            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                            exception.setCauseMessage("Set Classification "+classificationName+"'s parent to Classification "+getClassificationName()+" fail.");
                            throw exception;
                        }
                        return targetClassification;
                    }else{
                        return null;
                    }
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public boolean removeChildClassification(String classificationName) throws CoreRealmServiceRuntimeException {
        if(classificationName == null){
            return false;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                Classification childClassification = getClassificationByName(workingGraphOperationExecutor,classificationName);
                if(childClassification == null){
                    logger.error("Classification with name {} does not exist.", classificationName);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("Classification with name "+ classificationName +" does not exist.");
                    throw exception;
                }else{
                    String childConceptionUID = ((Neo4JClassificationImpl)childClassification).getClassificationUID();
                    String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(childConceptionUID),Long.parseLong(getClassificationUID()),
                            RealmConstant.Classification_ClassificationRelationClass);
                    GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                            (RealmConstant.Classification_ClassificationRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                    Object existingRelationEntityRes = workingGraphOperationExecutor.executeRead(getSingleRelationEntityTransformer, queryRelationCql);
                    if(existingRelationEntityRes == null){
                        logger.error("Classification {} is not parent of Classification {}.", getClassificationName(),classificationName);
                        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                        exception.setCauseMessage("Classification "+getClassificationName()+" is not parent of Classification "+classificationName+".");
                        throw exception;
                    }else{
                        String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.valueOf(childConceptionUID), null, null);
                        GetSingleClassificationTransformer getSingleClassificationTransformer =
                                new GetSingleClassificationTransformer(coreRealmName, this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                        Object deletedClassificationRes = workingGraphOperationExecutor.executeWrite(getSingleClassificationTransformer, deleteCql);
                        Classification resultClassification = deletedClassificationRes != null ? (Classification) deletedClassificationRes : null;
                        if (resultClassification == null) {
                            throw new CoreRealmServiceRuntimeException();
                        } else {
                            executeClassificationCacheOperation(resultClassification, CacheOperationType.DELETE);
                            return true;
                        }
                    }
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public List<ConceptionKind> getRelatedConceptionKinds(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException {
        if(classificationName == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                List<Long> targetClassificationUIDsList = getTargetClassificationsUIDList(workingGraphOperationExecutor,includeOffspringClassifications,offspringLevel);
                RelationDirection classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                switch (relationDirection){
                    case FROM -> classificationViewpointRelationDirection = RelationDirection.TO;
                    case TO -> classificationViewpointRelationDirection = RelationDirection.FROM;
                    case TWO_WAY -> classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                }
                String queryPairsCql = CypherBuilder.matchRelatedPairFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID,
                        CommonOperationUtil.formatListLiteralValue(targetClassificationUIDsList),RealmConstant.ConceptionKindClass,relationKindName,classificationViewpointRelationDirection);
                GetListConceptionKindTransformer getListConceptionKindTransformer = new GetListConceptionKindTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListConceptionKindTransformer,queryPairsCql);
                List<ConceptionKind> resultList = queryRes != null ? (List<ConceptionKind>)queryRes: new ArrayList<>();
                return resultList;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public List<RelationKind> getRelatedRelationKinds(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException {
        if(classificationName == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                List<Long> targetClassificationUIDsList = getTargetClassificationsUIDList(workingGraphOperationExecutor,includeOffspringClassifications,offspringLevel);
                RelationDirection classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                switch (relationDirection){
                    case FROM -> classificationViewpointRelationDirection = RelationDirection.TO;
                    case TO -> classificationViewpointRelationDirection = RelationDirection.FROM;
                    case TWO_WAY -> classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                }
                String queryPairsCql = CypherBuilder.matchRelatedPairFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID,
                        CommonOperationUtil.formatListLiteralValue(targetClassificationUIDsList),RealmConstant.RelationKindClass,relationKindName,classificationViewpointRelationDirection);
                GetListRelationKindTransformer getListRelationKindTransformer = new GetListRelationKindTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListRelationKindTransformer,queryPairsCql);
                List<RelationKind> resultList = queryRes != null ? (List<RelationKind>)queryRes: new ArrayList<>();
                return resultList;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public List<AttributeKind> getRelatedAttributeKinds(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException {
        if(classificationName == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                List<Long> targetClassificationUIDsList = getTargetClassificationsUIDList(workingGraphOperationExecutor,includeOffspringClassifications,offspringLevel);
                RelationDirection classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                switch (relationDirection){
                    case FROM -> classificationViewpointRelationDirection = RelationDirection.TO;
                    case TO -> classificationViewpointRelationDirection = RelationDirection.FROM;
                    case TWO_WAY -> classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                }
                String queryPairsCql = CypherBuilder.matchRelatedPairFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID,
                        CommonOperationUtil.formatListLiteralValue(targetClassificationUIDsList),RealmConstant.AttributeKindClass,relationKindName,classificationViewpointRelationDirection);
                GetListAttributeKindTransformer getListAttributeKindTransformer = new GetListAttributeKindTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListAttributeKindTransformer,queryPairsCql);
                List<AttributeKind> resultList = queryRes != null ? (List<AttributeKind>)queryRes: new ArrayList<>();
                return resultList;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public List<AttributesViewKind> getRelatedAttributesViewKinds(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException {
        if(classificationName == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                List<Long> targetClassificationUIDsList = getTargetClassificationsUIDList(workingGraphOperationExecutor,includeOffspringClassifications,offspringLevel);
                RelationDirection classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                switch (relationDirection){
                    case FROM -> classificationViewpointRelationDirection = RelationDirection.TO;
                    case TO -> classificationViewpointRelationDirection = RelationDirection.FROM;
                    case TWO_WAY -> classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                }
                String queryPairsCql = CypherBuilder.matchRelatedPairFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID,
                        CommonOperationUtil.formatListLiteralValue(targetClassificationUIDsList),RealmConstant.AttributesViewKindClass,relationKindName,classificationViewpointRelationDirection);
                GetListAttributesViewKindTransformer getListAttributesViewKindTransformer = new GetListAttributesViewKindTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListAttributesViewKindTransformer,queryPairsCql);
                List<AttributesViewKind> resultList = queryRes != null ? (List<AttributesViewKind>)queryRes: new ArrayList<>();
                return resultList;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public List<ConceptionEntity> getRelatedConceptionEntities(String relationKindName, RelationDirection relationDirection, QueryParameters queryParameters, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        if(classificationName == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                List<ConceptionEntity> conceptionEntityList = new ArrayList<>();
                RelationDirection classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                switch (relationDirection){
                    case FROM -> classificationViewpointRelationDirection = RelationDirection.TO;
                    case TO -> classificationViewpointRelationDirection = RelationDirection.FROM;
                    case TWO_WAY -> classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                }
                List<Long> targetClassificationUIDsList = getTargetClassificationsUIDList(workingGraphOperationExecutor,includeOffspringClassifications,offspringLevel);
                String queryPairsCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID,
                        CommonOperationUtil.formatListLiteralValue(targetClassificationUIDsList),queryParameters,relationKindName,classificationViewpointRelationDirection);
                DataTransformer offspringClassificationsDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record record = result.next();
                            Node conceptionEntityNode = record.get(CypherBuilder.operationResultName).asNode();
                            //Node classificationNode = record.get(CypherBuilder.sourceNodeName).asNode();
                            String currentEntityKind = null;
                            List<String> allLabelNames = Lists.newArrayList(conceptionEntityNode.labels());
                            if(queryParameters != null && queryParameters.getEntityKind() != null){
                                boolean isMatchedKind = false;
                                if(allLabelNames.size()>0){
                                    isMatchedKind = allLabelNames.contains( queryParameters.getEntityKind());
                                }
                                if(isMatchedKind){
                                    currentEntityKind = queryParameters.getEntityKind();
                                }
                            }else{
                                currentEntityKind = allLabelNames.get(0);
                            }
                            if(currentEntityKind != null){
                                long nodeUID = conceptionEntityNode.id();
                                String conceptionEntityUID = ""+nodeUID;
                                Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                                        new Neo4JConceptionEntityImpl(currentEntityKind,conceptionEntityUID);
                                neo4jConceptionEntityImpl.setAllConceptionKindNames(allLabelNames);
                                neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                                conceptionEntityList.add(neo4jConceptionEntityImpl);
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(offspringClassificationsDataTransformer,queryPairsCql);
                return conceptionEntityList;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getRelatedConceptionEntityAttributes(String relationKindName, RelationDirection relationDirection, QueryParameters queryParameters, List<String> attributeNames, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        if(attributeNames != null && attributeNames.size()>0){
            CommonConceptionEntitiesAttributesRetrieveResultImpl commonConceptionEntitiesAttributesRetrieveResultImpl
                    = new CommonConceptionEntitiesAttributesRetrieveResultImpl();
            commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            List<ConceptionEntityValue> conceptionEntityValueList = new ArrayList<>();

            try{
                RelationDirection classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                switch (relationDirection){
                    case FROM -> classificationViewpointRelationDirection = RelationDirection.TO;
                    case TO -> classificationViewpointRelationDirection = RelationDirection.FROM;
                    case TWO_WAY -> classificationViewpointRelationDirection = RelationDirection.TWO_WAY;
                }
                List<Long> targetClassificationUIDsList = getTargetClassificationsUIDList(workingGraphOperationExecutor,includeOffspringClassifications,offspringLevel);
                String queryPairsCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID,
                        CommonOperationUtil.formatListLiteralValue(targetClassificationUIDsList),queryParameters,relationKindName,classificationViewpointRelationDirection);
                DataTransformer offspringClassificationsDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record record = result.next();
                            Node conceptionEntityNode = record.get(CypherBuilder.operationResultName).asNode();
                            //Node classificationNode = record.get(CypherBuilder.sourceNodeName).asNode();
                            String currentEntityKind = null;
                            List<String> allLabelNames = Lists.newArrayList(conceptionEntityNode.labels());
                            if(queryParameters != null && queryParameters.getEntityKind() != null){
                                boolean isMatchedKind = false;
                                if(allLabelNames.size()>0){
                                    isMatchedKind = allLabelNames.contains( queryParameters.getEntityKind());
                                }
                                if(isMatchedKind){
                                    currentEntityKind = queryParameters.getEntityKind();
                                }
                            }else{
                                currentEntityKind = allLabelNames.get(0);
                            }
                            if(currentEntityKind != null){
                                long nodeUID = conceptionEntityNode.id();
                                String conceptionEntityUID = ""+nodeUID;
                                Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                                        new Neo4JConceptionEntityImpl(currentEntityKind,conceptionEntityUID);
                                neo4jConceptionEntityImpl.setAllConceptionKindNames(allLabelNames);
                                neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(graphOperationExecutorHelper.getGlobalGraphOperationExecutor());

                                List<String> allConceptionKindNames = Lists.newArrayList(conceptionEntityNode.labels());
                                Map<String,Object> entityAttributesValue = new HashMap<>();

                                for(String currentAttribute:attributeNames){
                                    if(conceptionEntityNode.containsKey(currentAttribute)){
                                        Object attributeValueObject = conceptionEntityNode.get(currentAttribute).asObject();
                                        entityAttributesValue.put(currentAttribute,getFormattedValue(attributeValueObject));
                                    }
                                }
                                ConceptionEntityValue currentConceptionEntityValue = new ConceptionEntityValue(conceptionEntityUID,entityAttributesValue);
                                currentConceptionEntityValue.setAllConceptionKindNames(allConceptionKindNames);
                                conceptionEntityValueList.add(currentConceptionEntityValue);
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(offspringClassificationsDataTransformer,queryPairsCql);
                commonConceptionEntitiesAttributesRetrieveResultImpl.addConceptionEntitiesAttributes(conceptionEntityValueList);
                commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(conceptionEntityValueList.size());
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonConceptionEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonConceptionEntitiesAttributesRetrieveResultImpl;
        }
        return null;
    }

    @Override
    public ClassificationRuntimeStatistics getClassificationRuntimeStatistics() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            ClassificationRuntimeStatistics classificationRuntimeStatistics = new ClassificationRuntimeStatistics();
            String cql1 ="MATCH (n:DOCG_Classification) WHERE id(n) = "+classificationUID+" \n" +
                    "\n" +
                    "OPTIONAL MATCH (n) -[]-(conceptionKinds:DOCG_ConceptionKind)\n" +
                    "OPTIONAL MATCH (n) -[]-(relationKinds:DOCG_RelationKind)\n" +
                    "OPTIONAL MATCH (n) -[]-(attributesViewKinds:DOCG_AttributesViewKind)\n" +
                    "OPTIONAL MATCH (n) -[]-(attributeKinds:DOCG_AttributeKind)\n" +
                    "OPTIONAL MATCH (n) <-[DOCG_ParentClassificationIs]-(childClassification:DOCG_Classification)\n" +
                    "\n" +
                    "WITH conceptionKinds,relationKinds,attributesViewKinds,attributeKinds,childClassification\n" +
                    "\n" +
                    "RETURN count(DISTINCT conceptionKinds),count(DISTINCT relationKinds),count(DISTINCT attributesViewKinds),count(DISTINCT attributeKinds),count(DISTINCT childClassification)";
            logger.debug("Generated Cypher Statement: {}", cql1);
            DataTransformer dataTransformer1 = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record nodeRecord = result.next();
                        if(nodeRecord != null){
                            int relatedConceptionKinds = nodeRecord.get("count(DISTINCT conceptionKinds)").asInt();
                            int relatedRelationKinds = nodeRecord.get("count(DISTINCT relationKinds)").asInt();
                            int relatedAttributesViewKinds = nodeRecord.get("count(DISTINCT attributesViewKinds)").asInt();
                            int relatedAttributeKinds = nodeRecord.get("count(DISTINCT attributeKinds)").asInt();
                            int relatedChildClassification = nodeRecord.get("count(DISTINCT childClassification)").asInt();
                            classificationRuntimeStatistics.setRelatedConceptionKindCount(relatedConceptionKinds);
                            classificationRuntimeStatistics.setRelatedRelationKindCount(relatedRelationKinds);
                            classificationRuntimeStatistics.setRelatedAttributesViewKindCount(relatedAttributesViewKinds);
                            classificationRuntimeStatistics.setRelatedAttributeKindCount(relatedAttributeKinds);
                            classificationRuntimeStatistics.setChildClassificationsCount(relatedChildClassification);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer1,cql1);

            String cql2 ="MATCH (n:DOCG_Classification) WHERE id(n) = "+classificationUID+" \n" +
                    "\n" +
                    "OPTIONAL MATCH (n) -[]-(conceptionEntities) WHERE \n" +
                    "NOT 'DOCG_AttributeKind' IN labels(conceptionEntities) \n" +
                    "AND NOT 'DOCG_ConceptionKind' IN labels(conceptionEntities)\n" +
                    "AND NOT 'DOCG_RelationKind' IN labels(conceptionEntities)\n" +
                    "AND NOT 'DOCG_AttributesViewKind' IN labels(conceptionEntities)\n" +
                    "AND NOT 'DOCG_Classification' IN labels(conceptionEntities)\n" +
                    "AND NOT 'DOCG_MetaConfigItemsStorage' IN labels(conceptionEntities)\n" +
                    "\n" +
                    "WITH conceptionEntities\n" +
                    "\n" +
                    "RETURN count(DISTINCT conceptionEntities)";
            logger.debug("Generated Cypher Statement: {}", cql2);
            DataTransformer dataTransformer2 = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record nodeRecord = result.next();
                        if(nodeRecord != null){
                            int relatedConceptionEntities = nodeRecord.get("count(DISTINCT conceptionEntities)").asInt();
                            classificationRuntimeStatistics.setRelatedConceptionEntityCount(relatedConceptionEntities);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer2,cql2);

            String cql3 = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),
                    RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.FROM,0,0, CypherBuilder.ReturnRelationableDataType.COUNT_NODE);
            DataTransformer dataTransformer3 = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record nodeRecord = result.next();
                        if(nodeRecord != null){
                            int offspringClassifications = nodeRecord.get("count(DISTINCT operationResult)").asInt();
                            classificationRuntimeStatistics.setOffspringClassificationsCount(offspringClassifications);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(dataTransformer3,cql3);

            return classificationRuntimeStatistics;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<ConceptionKindAttachInfo> getAllDirectRelatedConceptionKindsInfo() {
        if(this.classificationUID == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String cql = "MATCH (n:DOCG_Classification)-[r]-(conceptionKind:DOCG_ConceptionKind) WHERE id(n) = "+this.classificationUID+" \n" +
                        "RETURN conceptionKind,r as attachedRelation,properties(r) as relationData";
                logger.debug("Generated Cypher Statement: {}", cql);
                List<ConceptionKindAttachInfo> conceptionKindAttachInfoList = new ArrayList<>();
                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record nodeRecord = result.next();
                            if(nodeRecord.containsKey("conceptionKind") && nodeRecord.containsKey("attachedRelation")){
                                Node resultNode = nodeRecord.get("conceptionKind").asNode();
                                long nodeUID = resultNode.id();
                                String conceptionKindName = resultNode.get(RealmConstant._NameProperty).asString();
                                String conceptionKindDesc = null;
                                if(resultNode.get(RealmConstant._DescProperty) != null){
                                    conceptionKindDesc = resultNode.get(RealmConstant._DescProperty).asString();
                                }
                                String conceptionKindUID = ""+nodeUID;
                                Neo4JConceptionKindImpl neo4JConceptionKindImpl =
                                        new Neo4JConceptionKindImpl(coreRealmName,conceptionKindName,conceptionKindDesc,conceptionKindUID);
                                neo4JConceptionKindImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);

                                Relationship attachedRelation = nodeRecord.get("attachedRelation").asRelationship();
                                Map<String,Object> relationDataMap = new HashMap<>();
                                relationDataMap.putAll(nodeRecord.get("relationData").asMap());
                                relationDataMap.remove(RealmConstant._createDateProperty);
                                relationDataMap.remove(RealmConstant._lastModifyDateProperty);
                                relationDataMap.remove(RealmConstant._creatorIdProperty);
                                relationDataMap.remove(RealmConstant._dataOriginProperty);

                                ConceptionKindAttachInfo conceptionKindAttachInfo = new ConceptionKindAttachInfo();
                                conceptionKindAttachInfo.setAttachedConceptionKind(neo4JConceptionKindImpl);

                                RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
                                relationAttachInfo.setRelationKind(attachedRelation.type());
                                relationAttachInfo.setRelationData(relationDataMap);
                                relationAttachInfo.setRelationEntityUID(""+attachedRelation.id());

                                String attachedRelationFromUID = ""+attachedRelation.startNodeId();
                                if(getEntityUID().equals(attachedRelationFromUID)){
                                    relationAttachInfo.setRelationDirection(RelationDirection.FROM);
                                }else{
                                    relationAttachInfo.setRelationDirection(RelationDirection.TO);
                                }
                                conceptionKindAttachInfo.setRelationAttachInfo(relationAttachInfo);

                                conceptionKindAttachInfoList.add(conceptionKindAttachInfo);
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(dataTransformer,cql);
                return conceptionKindAttachInfoList;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public List<RelationKindAttachInfo> getAllDirectRelatedRelationKindsInfo() {
        if(this.classificationUID == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String cql = "MATCH (n:DOCG_Classification)-[r]-(relationKind:DOCG_RelationKind) WHERE id(n) = "+this.classificationUID+" \n" +
                        "RETURN relationKind,r as attachedRelation,properties(r) as relationData";
                logger.debug("Generated Cypher Statement: {}", cql);
                List<RelationKindAttachInfo> relationKindAttachInfoList = new ArrayList<>();
                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record nodeRecord = result.next();
                            if(nodeRecord.containsKey("relationKind") && nodeRecord.containsKey("attachedRelation")){
                                Node resultNode = nodeRecord.get("relationKind").asNode();
                                long nodeUID = resultNode.id();
                                String relationKindName = resultNode.get(RealmConstant._NameProperty).asString();
                                String relationKindDesc = null;
                                if(resultNode.get(RealmConstant._DescProperty) != null){
                                    relationKindDesc = resultNode.get(RealmConstant._DescProperty).asString();
                                }
                                String relationKindUID = ""+nodeUID;
                                Neo4JRelationKindImpl neo4JRelationKindImpl =
                                        new Neo4JRelationKindImpl(coreRealmName,relationKindName,relationKindDesc,relationKindUID);
                                neo4JRelationKindImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);

                                Relationship attachedRelation = nodeRecord.get("attachedRelation").asRelationship();
                                Map<String,Object> relationDataMap = new HashMap<>();
                                relationDataMap.putAll(nodeRecord.get("relationData").asMap());
                                relationDataMap.remove(RealmConstant._createDateProperty);
                                relationDataMap.remove(RealmConstant._lastModifyDateProperty);
                                relationDataMap.remove(RealmConstant._creatorIdProperty);
                                relationDataMap.remove(RealmConstant._dataOriginProperty);

                                RelationKindAttachInfo relationKindAttachInfo = new RelationKindAttachInfo();
                                relationKindAttachInfo.setAttachedRelationKind(neo4JRelationKindImpl);

                                RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
                                relationAttachInfo.setRelationKind(attachedRelation.type());
                                relationAttachInfo.setRelationData(relationDataMap);
                                relationAttachInfo.setRelationEntityUID(""+attachedRelation.id());

                                String attachedRelationFromUID = ""+attachedRelation.startNodeId();
                                if(getEntityUID().equals(attachedRelationFromUID)){
                                    relationAttachInfo.setRelationDirection(RelationDirection.FROM);
                                }else{
                                    relationAttachInfo.setRelationDirection(RelationDirection.TO);
                                }
                                relationKindAttachInfo.setRelationAttachInfo(relationAttachInfo);

                                relationKindAttachInfoList.add(relationKindAttachInfo);
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(dataTransformer,cql);
                return relationKindAttachInfoList;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public List<AttributeKindAttachInfo> getAllDirectRelatedAttributeKindsInfo() {
        if(this.classificationUID == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String cql = "MATCH (n:DOCG_Classification)-[r]-(attributeKind:DOCG_AttributeKind) WHERE id(n) = "+this.classificationUID+" \n" +
                        "RETURN attributeKind,r as attachedRelation,properties(r) as relationData";
                logger.debug("Generated Cypher Statement: {}", cql);
                List<AttributeKindAttachInfo> attributeKindAttachInfoList = new ArrayList<>();
                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record nodeRecord = result.next();
                            if(nodeRecord.containsKey("attributeKind") && nodeRecord.containsKey("attachedRelation")){
                                Node resultNode = nodeRecord.get("attributeKind").asNode();
                                long nodeUID = resultNode.id();

                                String attributeKindName = resultNode.get(RealmConstant._NameProperty).asString();
                                String attributeKindNameDesc = null;
                                if(resultNode.get(RealmConstant._DescProperty) != null){
                                    attributeKindNameDesc = resultNode.get(RealmConstant._DescProperty).asString();
                                }
                                String attributesViewKindDataForm = resultNode.get(RealmConstant._attributeDataType).asString();
                                AttributeDataType attributeDataType = null;
                                switch(attributesViewKindDataForm){
                                    case "BOOLEAN":attributeDataType = AttributeDataType.BOOLEAN;
                                        break;
                                    case "INT":attributeDataType = AttributeDataType.INT;
                                        break;
                                    case "SHORT":attributeDataType = AttributeDataType.SHORT;
                                        break;
                                    case "LONG":attributeDataType = AttributeDataType.LONG;
                                        break;
                                    case "FLOAT":attributeDataType = AttributeDataType.FLOAT;
                                        break;
                                    case "DOUBLE":attributeDataType = AttributeDataType.DOUBLE;
                                        break;
                                    case "TIMESTAMP":attributeDataType = AttributeDataType.TIMESTAMP;
                                        break;
                                    case "STRING":attributeDataType = AttributeDataType.STRING;
                                        break;
                                    case "BINARY":attributeDataType = AttributeDataType.BINARY;
                                        break;
                                    case "BYTE":attributeDataType = AttributeDataType.BYTE;
                                        break;
                                    case "DECIMAL":attributeDataType = AttributeDataType.DECIMAL;
                                        break;
                                    case "BOOLEAN_ARRAY":attributeDataType = AttributeDataType.BOOLEAN_ARRAY;
                                        break;
                                    case "INT_ARRAY":attributeDataType = AttributeDataType.INT_ARRAY;
                                        break;
                                    case "SHORT_ARRAY":attributeDataType = AttributeDataType.SHORT_ARRAY;
                                        break;
                                    case "LONG_ARRAY":attributeDataType = AttributeDataType.LONG_ARRAY;
                                        break;
                                    case "FLOAT_ARRAY":attributeDataType = AttributeDataType.FLOAT_ARRAY;
                                        break;
                                    case "DOUBLE_ARRAY":attributeDataType = AttributeDataType.DOUBLE_ARRAY;
                                        break;
                                    case "TIMESTAMP_ARRAY":attributeDataType = AttributeDataType.TIMESTAMP_ARRAY;
                                        break;
                                    case "STRING_ARRAY":attributeDataType = AttributeDataType.STRING_ARRAY;
                                        break;
                                    case "BYTE_ARRAY":attributeDataType = AttributeDataType.BYTE_ARRAY;
                                        break;
                                    case "DECIMAL_ARRAY":attributeDataType = AttributeDataType.DECIMAL_ARRAY;
                                        break;
                                    case "DATE":attributeDataType = AttributeDataType.DATE;
                                        break;
                                    case "DATETIME":attributeDataType = AttributeDataType.DATETIME;
                                        break;
                                    case "TIME":attributeDataType = AttributeDataType.TIME;
                                        break;
                                    case "DATE_ARRAY":attributeDataType = AttributeDataType.DATE_ARRAY;
                                        break;
                                    case "DATETIME_ARRAY":attributeDataType = AttributeDataType.DATETIME_ARRAY;
                                        break;
                                    case "TIME_ARRAY":attributeDataType = AttributeDataType.TIME_ARRAY;
                                }
                                String attributeKindUID = ""+nodeUID;
                                Neo4JAttributeKindImpl Neo4jAttributeKindImpl =
                                        new Neo4JAttributeKindImpl(coreRealmName,attributeKindName,attributeKindNameDesc,attributeDataType,attributeKindUID);
                                Neo4jAttributeKindImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);

                                Relationship attachedRelation = nodeRecord.get("attachedRelation").asRelationship();
                                Map<String,Object> relationDataMap = new HashMap<>();
                                relationDataMap.putAll(nodeRecord.get("relationData").asMap());
                                relationDataMap.remove(RealmConstant._createDateProperty);
                                relationDataMap.remove(RealmConstant._lastModifyDateProperty);
                                relationDataMap.remove(RealmConstant._creatorIdProperty);
                                relationDataMap.remove(RealmConstant._dataOriginProperty);

                                AttributeKindAttachInfo attributeKindAttachInfo = new AttributeKindAttachInfo();
                                attributeKindAttachInfo.setAttachedAttributeKind(Neo4jAttributeKindImpl);

                                RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
                                relationAttachInfo.setRelationKind(attachedRelation.type());
                                relationAttachInfo.setRelationData(relationDataMap);
                                relationAttachInfo.setRelationEntityUID(""+attachedRelation.id());

                                String attachedRelationFromUID = ""+attachedRelation.startNodeId();
                                if(getEntityUID().equals(attachedRelationFromUID)){
                                    relationAttachInfo.setRelationDirection(RelationDirection.FROM);
                                }else{
                                    relationAttachInfo.setRelationDirection(RelationDirection.TO);
                                }
                                attributeKindAttachInfo.setRelationAttachInfo(relationAttachInfo);

                                attributeKindAttachInfoList.add(attributeKindAttachInfo);
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(dataTransformer,cql);
                return attributeKindAttachInfoList;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    @Override
    public List<AttributesViewKindAttachInfo> getAllDirectRelatedAttributesViewKindsInfo() {
        if(this.classificationUID == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String cql = "MATCH (n:DOCG_Classification)-[r]-(attributesViewKind:DOCG_AttributesViewKind) WHERE id(n) = "+this.classificationUID+" \n" +
                        "RETURN attributesViewKind,r as attachedRelation,properties(r) as relationData";
                logger.debug("Generated Cypher Statement: {}", cql);
                List<AttributesViewKindAttachInfo> attributesViewKindAttachInfoList = new ArrayList<>();
                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record nodeRecord = result.next();
                            if(nodeRecord.containsKey("attributesViewKind") && nodeRecord.containsKey("attachedRelation")){
                                Node resultNode = nodeRecord.get("attributesViewKind").asNode();
                                long nodeUID = resultNode.id();

                                String attributesViewKindName = resultNode.get(RealmConstant._NameProperty).asString();
                                String attributesViewKindNameDesc = null;
                                if(resultNode.get(RealmConstant._DescProperty) != null){
                                    attributesViewKindNameDesc = resultNode.get(RealmConstant._DescProperty).asString();
                                }
                                String attributesViewKindDataForm = resultNode.get(RealmConstant._viewKindDataForm).asString();

                                AttributesViewKind.AttributesViewKindDataForm currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE;
                                switch(attributesViewKindDataForm){
                                    case "SINGLE_VALUE":currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE;
                                        break;
                                    case "LIST_VALUE":currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.LIST_VALUE;
                                        break;
                                    case "RELATED_VALUE":currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.RELATED_VALUE;
                                        break;
                                    case "EXTERNAL_VALUE":currentAttributesViewKindDataForm = AttributesViewKind.AttributesViewKindDataForm.EXTERNAL_VALUE;
                                }

                                String attributesViewKindUID = ""+nodeUID;
                                Neo4JAttributesViewKindImpl neo4jAttributesViewKindImpl =
                                        new Neo4JAttributesViewKindImpl(coreRealmName,attributesViewKindName,attributesViewKindNameDesc,currentAttributesViewKindDataForm,attributesViewKindUID);
                                neo4jAttributesViewKindImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);

                                Relationship attachedRelation = nodeRecord.get("attachedRelation").asRelationship();
                                Map<String,Object> relationDataMap = new HashMap<>();
                                relationDataMap.putAll(nodeRecord.get("relationData").asMap());
                                relationDataMap.remove(RealmConstant._createDateProperty);
                                relationDataMap.remove(RealmConstant._lastModifyDateProperty);
                                relationDataMap.remove(RealmConstant._creatorIdProperty);
                                relationDataMap.remove(RealmConstant._dataOriginProperty);

                                AttributesViewKindAttachInfo attributesViewKindAttachInfo = new AttributesViewKindAttachInfo();
                                attributesViewKindAttachInfo.setAttachedAttributesViewKind(neo4jAttributesViewKindImpl);

                                RelationAttachInfo relationAttachInfo = new RelationAttachInfo();
                                relationAttachInfo.setRelationKind(attachedRelation.type());
                                relationAttachInfo.setRelationData(relationDataMap);
                                relationAttachInfo.setRelationEntityUID(""+attachedRelation.id());

                                String attachedRelationFromUID = ""+attachedRelation.startNodeId();
                                if(getEntityUID().equals(attachedRelationFromUID)){
                                    relationAttachInfo.setRelationDirection(RelationDirection.FROM);
                                }else{
                                    relationAttachInfo.setRelationDirection(RelationDirection.TO);
                                }
                                attributesViewKindAttachInfo.setRelationAttachInfo(relationAttachInfo);

                                attributesViewKindAttachInfoList.add(attributesViewKindAttachInfo);
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(dataTransformer,cql);
                return attributesViewKindAttachInfoList;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
    }

    private List<Long> getTargetClassificationsUIDList(GraphOperationExecutor workingGraphOperationExecutor,boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException{
        if(includeOffspringClassifications & offspringLevel < 1){
            logger.error("Classification Offspring Level must great or equal 1, current value is {}.", offspringLevel);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Classification Offspring Level must great or equal 1, current value is "+ offspringLevel +".");
            throw exception;
        }
        List<Long> classificationsUIDList = new ArrayList<>();
        classificationsUIDList.add(Long.parseLong(this.classificationUID));
        if(includeOffspringClassifications){
            String queryCql = CypherBuilder.matchRelatedNodesAndRelationsFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID, Long.parseLong(classificationUID),
                    RealmConstant.ClassificationClass,RealmConstant.Classification_ClassificationRelationClass, RelationDirection.FROM,1,offspringLevel, CypherBuilder.ReturnRelationableDataType.NODE);
            DataTransformer offspringClassificationsDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    while(result.hasNext()){
                        Record record = result.next();
                        Node classificationNode = record.get(CypherBuilder.operationResultName).asNode();
                        List<String> allLabelNames = Lists.newArrayList(classificationNode.labels());
                        boolean isMatchedKind = false;
                        if(allLabelNames.size() > 0){
                            isMatchedKind = allLabelNames.contains(RealmConstant.ClassificationClass);
                        }
                        if(isMatchedKind){
                            classificationsUIDList.add(classificationNode.id());
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(offspringClassificationsDataTransformer,queryCql);
        }
        return classificationsUIDList;
    }

    private Classification getClassificationByName(GraphOperationExecutor workingGraphOperationExecutor,String classificationName){
        String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ClassificationClass,RealmConstant._NameProperty,classificationName,1);
        GetSingleClassificationTransformer getSingleClassificationTransformer =
                new GetSingleClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
        Object classificationRes = workingGraphOperationExecutor.executeRead(getSingleClassificationTransformer,queryCql);
        return classificationRes != null ? (Classification)classificationRes : null;
    }

    private Object getFormattedValue(Object attributeValue){
        if(attributeValue != null) {
            if(attributeValue instanceof Boolean || attributeValue instanceof String || attributeValue instanceof Number) {
                return attributeValue;
            }else if (attributeValue instanceof ZonedDateTime) {
                ZonedDateTime targetZonedDateTime = (ZonedDateTime) attributeValue;
                Date currentDate = Date.from(targetZonedDateTime.toInstant());
                return currentDate;
            }else if (attributeValue instanceof List && ((List<?>) attributeValue).size() > 0) {
                Object firstAttributeValue = ((List<?>) attributeValue).get(0);
                if (firstAttributeValue instanceof Boolean) {
                    List<Boolean> booleanValueList = (List<Boolean>) attributeValue;
                    Boolean[] returnBooleanValueArray = booleanValueList.toArray(new Boolean[booleanValueList.size()]);
                    return returnBooleanValueArray;
                }else if (firstAttributeValue instanceof ZonedDateTime) {
                    List<ZonedDateTime> valueList = (List<ZonedDateTime>) attributeValue;
                    Date[] returnDateValueArray = new Date[valueList.size()];
                    for (int i = 0; i < valueList.size(); i++) {
                        returnDateValueArray[i] = Date.from(valueList.get(i).toInstant());
                    }
                    return returnDateValueArray;
                }else if (firstAttributeValue instanceof String) {
                    List<String> stringValueList = (List<String>) attributeValue;
                    String[] returnStringValueArray = stringValueList.toArray(new String[stringValueList.size()]);
                    return returnStringValueArray;
                }
            }else {
                return attributeValue;
            }
        }
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        super.setGlobalGraphOperationExecutor(graphOperationExecutor);
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
