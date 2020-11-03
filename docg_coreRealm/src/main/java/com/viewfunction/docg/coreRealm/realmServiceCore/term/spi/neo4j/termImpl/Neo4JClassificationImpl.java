package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.KindCacheable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureImpl.Neo4JAttributesMeasurableImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListClassificationTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleClassificationTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleRelationEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            neo4JClassificationImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);

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
                    String createCql = CypherBuilder.createLabeledNodeWithProperties(RealmConstant.ClassificationClass,propertiesMap);
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
    public List<ConceptionKind> getRelatedConceptionKind(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException {
        if(classificationName == null){
            return null;
        }else{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                //Classification childClassification = getClassificationByName(workingGraphOperationExecutor,classificationName);
                List<Long> targetClassificationUIDsList = getTargetClassificationsUIDList(workingGraphOperationExecutor,includeOffspringClassifications,offspringLevel);

                System.out.println(targetClassificationUIDsList);





            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public List<RelationKind> getRelatedRelationKind(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public List<AttributeKind> getRelatedAttributeKind(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public List<AttributesViewKind> getRelatedAttributesViewKind(String relationKindName, RelationDirection relationDirection, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public List<ConceptionEntity> getRelatedConceptionEntity(String relationKindName, RelationDirection relationDirection, QueryParameters queryParameters, boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException {
        return null;
    }

    private List<Long> getTargetClassificationsUIDList(GraphOperationExecutor workingGraphOperationExecutor,boolean includeOffspringClassifications, int offspringLevel) throws CoreRealmServiceRuntimeException{
        /*
        Classification childClassification = getClassificationByName(workingGraphOperationExecutor,classificationName);
        if(childClassification == null){
            logger.error("Classification with name {} does not exist.", classificationName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Classification with name "+ classificationName +" does not exist.");
            throw exception;
        }
        */

        if(includeOffspringClassifications & offspringLevel < 1){
            logger.error("Classification Offspring Level must great or equal 1, current value is {}.", offspringLevel);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Classification Offspring Level must great or equal 1, current value is "+ offspringLevel +".");
            throw exception;
        }
        List<Long> classificationsUIDList = new ArrayList<>();
        classificationsUIDList.add(Long.parseLong(this.classificationUID));

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
                    if(allLabelNames.size()>0){
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
        return classificationsUIDList;
    }

    private Classification getClassificationByName(GraphOperationExecutor workingGraphOperationExecutor,String classificationName){
        String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ClassificationClass,RealmConstant._NameProperty,classificationName,1);
        GetSingleClassificationTransformer getSingleClassificationTransformer =
                new GetSingleClassificationTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
        Object classificationRes = workingGraphOperationExecutor.executeRead(getSingleClassificationTransformer,queryCql);
        return classificationRes != null ? (Classification)classificationRes : null;
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
