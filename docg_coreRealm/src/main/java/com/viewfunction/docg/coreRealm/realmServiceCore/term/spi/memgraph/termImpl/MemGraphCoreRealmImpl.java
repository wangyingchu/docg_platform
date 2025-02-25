package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.memgraph.dataTransformer.GetListKindMetaInfoTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.memgraph.dataTransformer.GetSingleConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetLongFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributeValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termInf.MemGraphCoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JClassificationImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JCoreRealmImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class MemGraphCoreRealmImpl extends Neo4JCoreRealmImpl implements MemGraphCoreRealm {

    private static Logger logger = LoggerFactory.getLogger(MemGraphCoreRealmImpl.class);
    private static final ZoneId systemDefaultZoneId = ZoneId.systemDefault();

    public MemGraphCoreRealmImpl(String coreRealmName){
        super(coreRealmName);
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public MemGraphCoreRealmImpl(){
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public CoreRealmStorageImplTech getStorageImplTech() {
        return CoreRealmStorageImplTech.MEMGRAPH;
    }

    @Override
    public ConceptionKind getConceptionKind(String conceptionKindName) {
        if(conceptionKindName == null){
            return null;
        }

        if(conceptionKindName.startsWith("DOCG_")){
            MemGraphConceptionKindImpl memGraphConceptionKindImpl =
                    new MemGraphConceptionKindImpl(getCoreRealmName(),conceptionKindName,null,"0");
            memGraphConceptionKindImpl.setGlobalGraphOperationExecutor(this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            return memGraphConceptionKindImpl;
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
                String conceptionKindUID = ((MemGraphConceptionKindImpl)targetConceptionKind).getConceptionKindUID();
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(conceptionKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                Object deletedConceptionKindRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                if(deletedConceptionKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String conceptionKindId = deletedConceptionKindRes.toString();
                    MemGraphConceptionKindImpl resultMemGraphConceptionKindImplForCacheOperation = new MemGraphConceptionKindImpl(getCoreRealmName(),conceptionKindName,null,conceptionKindId);
                    executeConceptionKindCacheOperation(resultMemGraphConceptionKindImplForCacheOperation,CacheOperationType.DELETE);
                    return true;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
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
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(attributesViewKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                Object deletedAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                if(deletedAttributesViewKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    executeAttributesViewKindCacheOperation(targetAttributesViewKind,CacheOperationType.DELETE);
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
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException {
        if(attributeKindUID == null){
            return false;
        }
        AttributeKind targetAttributeKind = this.getAttributeKind(attributeKindUID);
        if(targetAttributeKind != null){
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(attributeKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                Object deletedAttributeKindRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                if(deletedAttributeKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    executeAttributeKindCacheOperation(targetAttributeKind,CacheOperationType.DELETE);
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
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                Object deletedRelationKindRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                if(deletedRelationKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    String resultRelationKindUID = deletedRelationKindRes.toString();
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
    public boolean removeClassification(String classificationName) throws CoreRealmServiceRuntimeException {
        return this.removeClassification(classificationName,false);
    }

    @Override
    public boolean removeClassificationWithOffspring(String classificationName) throws CoreRealmServiceRuntimeException {
        return this.removeClassification(classificationName,true);
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
                    String classificationUID = targetClassification.getClassificationUID();
                    String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.valueOf(classificationUID), CypherBuilder.CypherFunctionType.ID, null);
                    GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                    Object deletedClassificationRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);
                    if (deletedClassificationRes == null) {
                        throw new CoreRealmServiceRuntimeException();
                    } else {
                        String classificationId = deletedClassificationRes.toString();
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

                    String deleteCql = CypherBuilder.deleteNodesWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, withOffspringClassificationUIDList);
                    deleteCql = deleteCql.replace("RETURN operationResult","RETURN count(operationResult)");
                    GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
                    Object deleteResultObj = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);

                    if(Long.parseLong(deleteResultObj.toString()) == withOffspringClassificationUIDList.size()){
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
                String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationAttachKindUID),CypherBuilder.CypherFunctionType.ID,null);
                GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("id");
                Object deletedRelationAttachKindRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);

                if(deletedRelationAttachKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    executeRelationAttachKindCacheOperation(targetRelationAttachKind,CacheOperationType.DELETE);
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
    public List<EntityStatisticsInfo> getConceptionEntitiesStatistics() throws CoreRealmServiceEntityExploreException {
        List<EntityStatisticsInfo> entityStatisticsInfoList = new ArrayList<>();
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            List<String> attributesNameList = new ArrayList<>();
            Map<String, HashMap<String,Object>> conceptionKindMetaDataMap = new HashMap<>();
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

                        ZonedDateTime createDate = null;
                        Object createDatePropertyObject = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asObject();
                        if(createDatePropertyObject instanceof LocalDateTime){
                            createDate = ((LocalDateTime)createDatePropertyObject).atZone(systemDefaultZoneId);
                        }else{
                            createDate = (ZonedDateTime)createDatePropertyObject;
                        }

                        ZonedDateTime lastModifyDate = null;
                        Object lastModifyDatePropertyObject = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asObject();
                        if(lastModifyDatePropertyObject instanceof LocalDateTime){
                            lastModifyDate = ((LocalDateTime)lastModifyDatePropertyObject).atZone(systemDefaultZoneId);
                        }else{
                            lastModifyDate =(ZonedDateTime)lastModifyDatePropertyObject;
                        }

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

            String cypherProcedureString ="MATCH (n) RETURN DISTINCT labels(n) as label,count(n) as count";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

            DataTransformer queryResultDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    List<String> conceptionKindsNameWithDataList = new ArrayList<>();
                    while(result.hasNext()){
                        Record currentRecord = result.next();
                        String entityKind = currentRecord.get("label").asList().get(0).toString();
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
                        ZonedDateTime createDate = nodeRecord.containsKey(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty) ?
                                nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asLocalDateTime().atZone(systemDefaultZoneId) : null;
                        ZonedDateTime lastModifyDate = nodeRecord.containsKey(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty) ?
                                nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asLocalDateTime().atZone(systemDefaultZoneId) : null;
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

            String cypherProcedureString = "MATCH ()-[r]->() RETURN type(r) AS relationshipType, count(*) AS count";
            logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

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
                                ZonedDateTime createDate = relationKindMetaDataMap.get(entityKind).containsKey(RealmConstant._createDateProperty) ?
                                        (ZonedDateTime) (relationKindMetaDataMap.get(entityKind).get(RealmConstant._createDateProperty)) : null;
                                ZonedDateTime lastModifyDate = relationKindMetaDataMap.get(entityKind).containsKey(RealmConstant._lastModifyDateProperty) ?
                                        (ZonedDateTime) (relationKindMetaDataMap.get(entityKind).get(RealmConstant._lastModifyDateProperty)) : null;
                                currentEntityStatisticsInfo = new EntityStatisticsInfo(
                                        entityKind, EntityStatisticsInfo.kindType.RelationKind, false, entityCount,
                                        relationKindMetaDataMap.get(entityKind).get(RealmConstant._DescProperty).toString(),
                                        relationKindMetaDataMap.get(entityKind).get("RelationKindUID").toString(),
                                        createDate,
                                        lastModifyDate,
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
                            ZonedDateTime createDate = relationKindMetaDataMap.get(currentKindName).containsKey(RealmConstant._createDateProperty) ?
                                    (ZonedDateTime) (relationKindMetaDataMap.get(currentKindName).get(RealmConstant._createDateProperty)) : null;
                            ZonedDateTime lastModifyDate = relationKindMetaDataMap.get(currentKindName).containsKey(RealmConstant._lastModifyDateProperty) ?
                                    (ZonedDateTime) (relationKindMetaDataMap.get(currentKindName).get(RealmConstant._lastModifyDateProperty)) : null;
                            EntityStatisticsInfo currentEntityStatisticsInfo = new EntityStatisticsInfo(
                                    currentKindName, EntityStatisticsInfo.kindType.ConceptionKind, false, 0,
                                    relationKindMetaDataMap.get(currentKindName).get(RealmConstant._DescProperty).toString(),
                                    relationKindMetaDataMap.get(currentKindName).get("RelationKindUID").toString(),
                                    createDate,
                                    lastModifyDate,
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
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

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
    public List<KindMetaInfo> getConceptionKindsMetaInfo() throws CoreRealmServiceEntityExploreException {
        return getKindsMetaInfo(RealmConstant.ConceptionKindClass);
    }

    @Override
    public List<KindMetaInfo> getRelationKindsMetaInfo() throws CoreRealmServiceEntityExploreException{
        return getKindsMetaInfo(RealmConstant.RelationKindClass);
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
                        ZonedDateTime createDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asLocalDateTime().atZone(systemDefaultZoneId);
                        ZonedDateTime lastModifyDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asLocalDateTime().atZone(systemDefaultZoneId);
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
            List<AttributesViewKindMetaInfo> resultkindMetaInfoList = kindMetaInfoListRes != null ? (List<AttributesViewKindMetaInfo>) kindMetaInfoListRes : null;
            if(resultkindMetaInfoList != null && resultkindMetaInfoList.size()>0){
                queryCql = "MATCH (DOCG_AttributeKind)<-[r1:DOCG_ViewContainsAttributeKindIs]-(n:DOCG_AttributesViewKind) RETURN id(n),count(r1) LIMIT 10000000";
                DataTransformer dataTransformer2 = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record nodeRecord = result.next();
                            long KindUID = nodeRecord.get("id(n)").asLong();
                            int attachedAttributeKindNumber = nodeRecord.get("count(r1)").asInt();
                            setContainsAttributeKindCount(resultkindMetaInfoList,""+KindUID,attachedAttributeKindNumber);
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(dataTransformer2,queryCql);

                queryCql = "MATCH (n:DOCG_AttributesViewKind) <-[r2:DOCG_ConceptionContainsViewKindIs] -(DOCG_ConceptionKind) RETURN id(n),count(r2) LIMIT 10000000";
                DataTransformer dataTransformer3 = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record nodeRecord = result.next();
                            long KindUID = nodeRecord.get("id(n)").asLong();
                            int attachedConceptionKindNumber = nodeRecord.get("count(r2)").asInt();
                            setContainerConceptionKindCount(resultkindMetaInfoList,""+KindUID,attachedConceptionKindNumber);
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(dataTransformer3,queryCql);
            }
            return resultkindMetaInfoList;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    private void setContainerConceptionKindCount(List<AttributesViewKindMetaInfo> attributesViewKindMetaInfoList,String attributesViewKindUID,int containerConceptionKindCount){
        for(AttributesViewKindMetaInfo currentAttributesViewKindMetaInfo:attributesViewKindMetaInfoList){
            if(attributesViewKindUID.equals(currentAttributesViewKindMetaInfo.getKindUID())){
                currentAttributesViewKindMetaInfo.setContainerConceptionKindCount(containerConceptionKindCount);
                return;
            }
        }
    }

    private void setContainsAttributeKindCount(List<AttributesViewKindMetaInfo> attributesViewKindMetaInfoList,String attributesViewKindUID,int containsAttributeKindCount){
        for(AttributesViewKindMetaInfo currentAttributesViewKindMetaInfo:attributesViewKindMetaInfoList){
            if(attributesViewKindUID.equals(currentAttributesViewKindMetaInfo.getKindUID())){
                currentAttributesViewKindMetaInfo.setContainsAttributeKindCount(containsAttributeKindCount);
                return;
            }
        }
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
                        ZonedDateTime createDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._createDateProperty).asLocalDateTime().atZone(systemDefaultZoneId);
                        ZonedDateTime lastModifyDate = nodeRecord.get(CypherBuilder.operationResultName+"."+RealmConstant._lastModifyDateProperty).asLocalDateTime().atZone(systemDefaultZoneId);
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
            List<AttributeKindMetaInfo> resultAttributeKindMetaInfoList = kindMetaInfoListRes != null ? (List<AttributeKindMetaInfo>) kindMetaInfoListRes : null;

            if(resultAttributeKindMetaInfoList != null && resultAttributeKindMetaInfoList.size()>0){
                queryCql = "MATCH (n:DOCG_AttributeKind)<-[r:DOCG_ViewContainsAttributeKindIs]-(DOCG_AttributesViewKind) RETURN id(n), count(r) LIMIT 10000000";
                DataTransformer dataTransformer2 = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        while(result.hasNext()){
                            Record nodeRecord = result.next();
                            long KindUID = nodeRecord.get("id(n)").asLong();
                            int attachedAttributesKindNumber = nodeRecord.get("count(r)").asInt();
                            setContainerAttributeKindCount(resultAttributeKindMetaInfoList,""+KindUID,attachedAttributesKindNumber);
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(dataTransformer2,queryCql);
            }
            return resultAttributeKindMetaInfoList;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    private void setContainerAttributeKindCount(List<AttributeKindMetaInfo> attributeKindMetaInfoList, String attributeKindUID, int containerAttributesKindCount){
        for(AttributeKindMetaInfo currentAttributeKindMetaInfo:attributeKindMetaInfoList){
            if(attributeKindUID.equals(currentAttributeKindMetaInfo.getKindUID())){
                currentAttributeKindMetaInfo.setContainerAttributesViewKindCount(containerAttributesKindCount);
                return;
            }
        }
    }

    @Override
    public boolean renameConceptionKind(String originalConceptionKindName, String newConceptionKindName, String newConceptionKindDesc) throws CoreRealmServiceRuntimeException {
        if(originalConceptionKindName == null){
            logger.error("original Conception Kind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("original ConceptionKind Name is required.");
            throw exception;
        }
        if(newConceptionKindName == null){
            logger.error("new ConceptionKind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("new ConceptionKind Name is required.");
            throw exception;
        }
        ConceptionKind originalConceptionKind =this.getConceptionKind(originalConceptionKindName);
        if(originalConceptionKind == null){
            logger.error("CoreRealm does not contains ConceptionKind with name {}.", originalConceptionKindName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("CoreRealm does not contains ConceptionKind with name " + originalConceptionKindName + ".");
            throw exception;
        }
        String originalConceptionKindUID = ((Neo4JConceptionKindImpl)originalConceptionKind).getConceptionKindUID();
        ConceptionKind newConceptionKind = this.getConceptionKind(newConceptionKindName);
        if(newConceptionKind != null){
            logger.error("CoreRealm already contains ConceptionKind with name {}.", newConceptionKindName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("CoreRealm already contains ConceptionKind with name " + newConceptionKindName + ".");
            throw exception;
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            //https://neo4j.com/labs/apoc/4.1/overview/apoc.periodic/apoc.periodic.iterate/
            String modifyConceptionEntityLabelCQL =
                    "MATCH (n:`"+originalConceptionKindName+"`) SET n:`"+newConceptionKindName+"` REMOVE n:`"+originalConceptionKindName+"`";
            logger.debug("Generated Cypher Statement: {}", modifyConceptionEntityLabelCQL);
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    System.out.println(result);
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer,modifyConceptionEntityLabelCQL);

            Map<String,Object> attributeDataMap = new HashMap<>();

            attributeDataMap.put(RealmConstant._NameProperty, newConceptionKindName);
            if(newConceptionKindDesc != null){
                attributeDataMap.put(RealmConstant._DescProperty, newConceptionKindDesc);
            }

            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(originalConceptionKindUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._NameProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,originalConceptionKindUID,false);

            AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(newConceptionKindName)){
                return true;
            }else{
                return false;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean renameRelationKind(String originalRelationKindName, String newRelationKindName, String newRelationKindDesc) throws CoreRealmServiceRuntimeException {
        if(originalRelationKindName == null){
            logger.error("original RelationKind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("original RelationKind Name is required.");
            throw exception;
        }
        if(newRelationKindName == null){
            logger.error("new RelationKind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("new RelationKind Name is required.");
            throw exception;
        }

        RelationKind originalRelationKind = this.getRelationKind(originalRelationKindName);
        if(originalRelationKind == null){
            logger.error("CoreRealm does not contains RelationKind with name {}.", originalRelationKindName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("CoreRealm does not contains RelationKind with name " + originalRelationKindName + ".");
            throw exception;
        }
        String originalRelationKindUID = ((Neo4JRelationKindImpl)originalRelationKind).getRelationKindUID();
        RelationKind newRelationKind = this.getRelationKind(newRelationKindName);
        if(newRelationKind != null){
            logger.error("CoreRealm already contains RelationKind with name {}.", newRelationKindName);
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("CoreRealm already contains RelationKind with name " + newRelationKindName + ".");
            throw exception;
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String modifyRelationEntityTypeCQL =
                    "CALL apoc.periodic.iterate(\"MATCH ()-[rel:`"+originalRelationKindName+"`]->() CALL apoc.refactor.setType(rel,'"+newRelationKindName+"') YIELD input, output RETURN input, output\",\"\",{batchSize:10000,parallel:true})";

            modifyRelationEntityTypeCQL = "MATCH ()-[r]->() WHERE type(r) = '"+originalRelationKindName+"' SET type(r) = '"+newRelationKindName+"'";

            logger.debug("Generated Cypher Statement: {}", modifyRelationEntityTypeCQL);
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    System.out.println(result);
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer,modifyRelationEntityTypeCQL);

            Map<String,Object> attributeDataMap = new HashMap<>();
            attributeDataMap.put(RealmConstant._NameProperty, newRelationKindName);
            if(newRelationKindDesc != null){
                attributeDataMap.put(RealmConstant._DescProperty, newRelationKindDesc);
            }

            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(originalRelationKindUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._NameProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,originalRelationKindUID,false);

            AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(newRelationKindName)){
                return true;
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
}
