package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.EqualFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.NullValueFilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.GeospatialScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.TemporalScaleCalculable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.BatchDataOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Neo4JConceptionKindImpl implements Neo4JConceptionKind {

    private static Logger logger = LoggerFactory.getLogger(Neo4JConceptionKindImpl.class);
    private String coreRealmName;
    private String conceptionKindName;
    private String conceptionKindDesc;
    private String conceptionKindUID;
    private static Map<String, Object> singleValueAttributesViewKindTypeFilter = new HashMap<>();
    static {
        singleValueAttributesViewKindTypeFilter.put(RealmConstant._viewKindDataForm,""+AttributesViewKind.AttributesViewKindDataForm.SINGLE_VALUE);
    }

    public Neo4JConceptionKindImpl(String coreRealmName,String conceptionKindName,String conceptionKindDesc,String conceptionKindUID){
        this.coreRealmName = coreRealmName;
        this.conceptionKindName = conceptionKindName;
        this.conceptionKindDesc = conceptionKindDesc;
        this.conceptionKindUID = conceptionKindUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public String getConceptionKindUID() {
        return this.conceptionKindUID;
    }

    public String getCoreRealmName() {
        return this.coreRealmName;
    }

    @Override
    public String getConceptionKindName() {
        return this.conceptionKindName;
    }

    @Override
    public String getConceptionKindDesc() {
        return this.conceptionKindDesc;
    }

    @Override
    public boolean updateConceptionKindDesc(String kindDesc) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String,Object> attributeDataMap = new HashMap<>();
            attributeDataMap.put(RealmConstant._DescProperty, kindDesc);
            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.conceptionKindUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._DescProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.conceptionKindUID,false);
            AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(kindDesc)){
                this.conceptionKindDesc = kindDesc;
                return true;
            }else{
                return false;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Long countConceptionEntities() throws CoreRealmServiceRuntimeException{
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String queryCql = CypherBuilder.matchLabelWithSinglePropertyValueAndFunction(getConceptionKindName(), CypherBuilder.CypherFunctionType.COUNT, null, null);
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer, queryCql);
            if (countConceptionEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
                return (Long) countConceptionEntitiesRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Long countConceptionEntitiesWithOffspring() throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public List<ConceptionKind> getChildConceptionKinds() throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public ConceptionKind getParentConceptionKind() throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public InheritanceTree<ConceptionKind> getOffspringConceptionKinds() throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation) {
        if (conceptionEntityValue != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                Map<String, Object> propertiesMap = conceptionEntityValue.getEntityAttributesValue() != null ?
                        conceptionEntityValue.getEntityAttributesValue() : new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
                String createCql = CypherBuilder.createLabeledNodeWithProperties(new String[]{this.conceptionKindName}, propertiesMap);
                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(this.conceptionKindName, this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, createCql);

                ConceptionEntity resultEntity = newEntityRes != null ? (ConceptionEntity) newEntityRes : null;
                if(addPerDefinedRelation && resultEntity != null){
                    List<String> uidList = new ArrayList<>();
                    uidList.add(resultEntity.getConceptionEntityUID());
                    CommonOperationUtil.attachEntities(this.conceptionKindName,uidList,workingGraphOperationExecutor);
                }
                return resultEntity;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) {
        ConceptionEntity resultConceptionEntity = newEntity(conceptionEntityValue,false);
        if(relationAttachKindList != null){
            for(RelationAttachKind currentRelationAttachKind : relationAttachKindList){
                currentRelationAttachKind.newRelationEntities(resultConceptionEntity.getConceptionEntityUID(),entityRelateRole,null);
            }
        }
        return resultConceptionEntity;
    }

    @Override
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation) {
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
                String createCql = CypherBuilder.createMultiLabeledNodesWithProperties(new String[]{this.conceptionKindName}, attributesValueMap);
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
                            setOperationSummary("newEntities operation for conceptionKind "+this.conceptionKindName+" success.");
                }
                commonEntitiesOperationResultImpl.finishEntitiesOperation();
                if(addPerDefinedRelation && commonEntitiesOperationResultImpl.getSuccessEntityUIDs() != null){
                    CommonOperationUtil.attachEntities(this.conceptionKindName,commonEntitiesOperationResultImpl.getSuccessEntityUIDs(),workingGraphOperationExecutor);
                }
                return commonEntitiesOperationResultImpl;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, List<RelationAttachKind> relationAttachKindList, RelationAttachKind.EntityRelateRole entityRelateRole) {
        EntitiesOperationResult entitiesOperationResult =  newEntities(conceptionEntityValues,false);
        if(relationAttachKindList != null){
            for(RelationAttachKind currentRelationAttachKind : relationAttachKindList){
                currentRelationAttachKind.newRelationEntities(entitiesOperationResult.getSuccessEntityUIDs(),entityRelateRole,null);
            }
        }
        return entitiesOperationResult;
    }

    @Override
    public ConceptionEntity updateEntity(ConceptionEntityValue conceptionEntityValueForUpdate) throws CoreRealmServiceRuntimeException{
        if(conceptionEntityValueForUpdate != null && conceptionEntityValueForUpdate.getConceptionEntityUID() != null){
            ConceptionEntity targetConceptionEntity = this.getEntityByUID(conceptionEntityValueForUpdate.getConceptionEntityUID());
            if(targetConceptionEntity != null){
                Map<String,Object> newValueMap = conceptionEntityValueForUpdate.getEntityAttributesValue();
                targetConceptionEntity.updateAttributes(newValueMap);
                return this.getEntityByUID(conceptionEntityValueForUpdate.getConceptionEntityUID());
            }else{
                logger.error("ConceptionKind {} does not contains entity with UID {}.", this.conceptionKindName, conceptionEntityValueForUpdate.getConceptionEntityUID());
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("ConceptionKind " + this.conceptionKindName + " does not contains entity with UID " +  conceptionEntityValueForUpdate.getConceptionEntityUID() + ".");
                throw exception;
            }
        }
        return null;
    }

    @Override
    public EntitiesOperationResult updateEntities(List<ConceptionEntityValue> entityValues) {
        if(entityValues != null && entityValues.size()>0){
            CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
            boolean countFail = false;
            for(ConceptionEntityValue currentConceptionEntityValue:entityValues){
                ConceptionEntity targetConceptionEntity = this.getEntityByUID(currentConceptionEntityValue.getConceptionEntityUID());
                if(targetConceptionEntity != null){
                    Map<String,Object> newValueMap = currentConceptionEntityValue.getEntityAttributesValue();
                    List<String> updateSuccessResult = targetConceptionEntity.updateAttributes(newValueMap);
                    if(updateSuccessResult != null && updateSuccessResult.size()>0){
                        commonEntitiesOperationResultImpl.getSuccessEntityUIDs().add(currentConceptionEntityValue.getConceptionEntityUID());
                        commonEntitiesOperationResultImpl.getOperationStatistics().increaseSuccessCount();
                    }else{
                        commonEntitiesOperationResultImpl.getOperationStatistics().increaseFailCount();
                        countFail = true;
                    }
                }else{
                    commonEntitiesOperationResultImpl.getOperationStatistics().increaseFailCount();
                    countFail = true;
                }
            }
            if(countFail){
                commonEntitiesOperationResultImpl.getOperationStatistics().
                        setOperationSummary("updateEntities operation for conceptionKind "+this.conceptionKindName+" partial success.");
            }else{
                commonEntitiesOperationResultImpl.getOperationStatistics().
                        setOperationSummary("updateEntities operation for conceptionKind "+this.conceptionKindName+" success.");
            }
            commonEntitiesOperationResultImpl.finishEntitiesOperation();
            return commonEntitiesOperationResultImpl;
        }
        return null;
    }

    @Override
    public EntitiesOperationResult updateEntities(AttributesParameters attributesParameters, Map<String, Object> conceptionEntitiesAttributesMap) throws CoreRealmServiceEntityExploreException{
        CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
        String queryCql = null;
        if (attributesParameters != null) {
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
            queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters, null);
            queryCql = queryCql.replace(" RETURN operationResult LIMIT 100000000","");
        }else{
            queryCql = "MATCH (operationResult:`"+this.conceptionKindName+"`)";
        }

        String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong("99999"),conceptionEntitiesAttributesMap);
        updateCql = updateCql.replace("MATCH (operationResult) WHERE id(operationResult) = 99999","");
        int returnIndex = updateCql.indexOf("RETURN");
        updateCql = updateCql.substring(0,returnIndex);
        queryCql = queryCql+updateCql+ "RETURN operationResult";
        logger.debug("Generated Cypher Statement: {}", queryCql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(this.conceptionKindName,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeWrite(getListConceptionEntityTransformer,queryCql);
            if(queryRes != null){
                List<ConceptionEntity> resultConceptionEntityList = (List<ConceptionEntity>)queryRes;
                for(ConceptionEntity currentConceptionEntity:resultConceptionEntityList){
                    commonEntitiesOperationResultImpl.getSuccessEntityUIDs().add(currentConceptionEntity.getConceptionEntityUID());
                }
                commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(resultConceptionEntityList.size());
                commonEntitiesOperationResultImpl.getOperationStatistics().setFailItemsCount(0);
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        commonEntitiesOperationResultImpl.getOperationStatistics().
                setOperationSummary("updateEntities operation for conceptionKind "+this.conceptionKindName+" success.");
        commonEntitiesOperationResultImpl.finishEntitiesOperation();
        return commonEntitiesOperationResultImpl;
    }

    @Override
    public boolean deleteEntity(String conceptionEntityUID) throws CoreRealmServiceRuntimeException{
        if(conceptionEntityUID != null){
            ConceptionEntity targetConceptionEntity = this.getEntityByUID(conceptionEntityUID);
            if(targetConceptionEntity != null){
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                try{
                    String deleteCql = CypherBuilder.deleteNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.valueOf(conceptionEntityUID),null,null);
                    GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                            new GetSingleConceptionEntityTransformer(this.conceptionKindName, this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                    Object deletedEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer, deleteCql);
                    if(deletedEntityRes == null){
                        throw new CoreRealmServiceRuntimeException();
                    }else{
                        return true;
                    }
                }finally {
                    this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
                }
            }else{
                logger.error("ConceptionKind {} does not contains entity with UID {}.", this.conceptionKindName, conceptionEntityUID);
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("ConceptionKind " + this.conceptionKindName + " does not contains entity with UID " + conceptionEntityUID + ".");
                throw exception;
            }
        }
        return false;
    }

    @Override
    public EntitiesOperationResult deleteEntities(List<String> conceptionEntityUIDs) {
        if(conceptionEntityUIDs != null && conceptionEntityUIDs.size()>0){
            CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
            boolean countFail = false;
            for(String currentConceptionEntityUID:conceptionEntityUIDs) {
                ConceptionEntity targetConceptionEntity = this.getEntityByUID(currentConceptionEntityUID);
                if(targetConceptionEntity != null){
                    try {
                        boolean deleteCurrentEntityResult = deleteEntity(currentConceptionEntityUID);
                        if(deleteCurrentEntityResult){
                            commonEntitiesOperationResultImpl.getSuccessEntityUIDs().add(currentConceptionEntityUID);
                            commonEntitiesOperationResultImpl.getOperationStatistics().increaseSuccessCount();
                        }else{
                            commonEntitiesOperationResultImpl.getOperationStatistics().getFailItemsCount();
                        }
                    } catch (CoreRealmServiceRuntimeException e) {
                        e.printStackTrace();
                        commonEntitiesOperationResultImpl.getOperationStatistics().getFailItemsCount();
                        logger.error("Exception occurred during delete entity with UID {} of ConceptionKind {}.", currentConceptionEntityUID , this.conceptionKindName);
                    }
                }else{
                    commonEntitiesOperationResultImpl.getOperationStatistics().increaseFailCount();
                    countFail = true;
                }
            }
            if(countFail){
                commonEntitiesOperationResultImpl.getOperationStatistics().
                        setOperationSummary("deleteEntities operation for conceptionKind "+this.conceptionKindName+" partial success.");
            }else{
                commonEntitiesOperationResultImpl.getOperationStatistics().
                        setOperationSummary("deleteEntities operation for conceptionKind "+this.conceptionKindName+" success.");
            }
            commonEntitiesOperationResultImpl.finishEntitiesOperation();
            return commonEntitiesOperationResultImpl;
        }
        return null;
    }

    @Override
    public EntitiesOperationResult purgeAllEntities() throws CoreRealmServiceRuntimeException {
        try{
            CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            /*
            CALL apoc.periodic.iterate(
              'MATCH (n:TestLoad) RETURN n',
              'DETACH DELETE n',
              {batchSize:100000, iterateList:true})
             */
            //return : "batches"│"total"│"timeTaken"│"committedOperations"│"failedOperations"│"failedBatches"│"retries"│"errorMessages"│"batch"│"operations"
            String cql = "CALL apoc.periodic.iterate(\n" +
                    "          'MATCH (n:`"+this.conceptionKindName+"`) RETURN n',\n" +
                    "          'DETACH DELETE n',\n" +
                    "          {batchSize:100000, iterateList:true})";
            logger.debug("Generated Cypher Statement: {}", cql);
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record resultRecord = result.next();
                        if(resultRecord.containsKey("total")){
                            long deletedRecordCount = resultRecord.get("total").asLong();
                            commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(deletedRecordCount);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
            commonEntitiesOperationResultImpl.getOperationStatistics().
                    setOperationSummary("purgeAllEntities operation for conceptionKind "+this.conceptionKindName+" success.");

            commonEntitiesOperationResultImpl.finishEntitiesOperation();
            return commonEntitiesOperationResultImpl;

        /*
            // Using below solution for improving performance or execute operation success
            //https://neo4j.com/developer/kb/how-to-bulk-delete-dense-nodes/
            //https://www.freesion.com/article/24571268014/
            String bulkDeleteCql ="MATCH (n:`"+this.conceptionKindName+"`)\n" +
                    "WITH collect(n) AS nn\n" +
                    "CALL apoc.periodic.commit(\"\n" +
                    "  UNWIND $nodes AS n\n" +
                    "  WITH sum(size([p=(n)-[]-() | p])) AS count_remaining,\n" +
                    "       collect(n) AS nn\n" +
                    "  UNWIND nn AS n\n" +
                    "  OPTIONAL MATCH (n)-[r]-()\n" +
                    "  WITH n, r, count_remaining\n" +
                    "  LIMIT $limit\n" +
                    "  DELETE r\n" +
                    "  RETURN count_remaining\n" +
                    "\",{limit:10000, nodes:nn}) yield updates, executions, runtime, batches, failedBatches, batchErrors, failedCommits, commitErrors\n" +
                    "UNWIND nn AS n\n" +
                    "DETACH DELETE n\n" +
                    "RETURN updates, executions, runtime, batches";

            String countQueryCql = CypherBuilder.matchLabelWithSinglePropertyValueAndFunction(getConceptionKindName(), CypherBuilder.CypherFunctionType.COUNT, null, null);
            long beforeExecuteConceptionEntityCount = 0;
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer, countQueryCql);
            if (countConceptionEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
               beforeExecuteConceptionEntityCount = (Long) countConceptionEntitiesRes;
            }

            logger.debug("Generated Cypher Statement: {}", bulkDeleteCql);
            workingGraphOperationExecutor.executeWrite(new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    return null;
                }
            },bulkDeleteCql);

            long afterExecuteConceptionEntityCount = 0;
            countConceptionEntitiesRes = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer, countQueryCql);
            if (countConceptionEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
                afterExecuteConceptionEntityCount = (Long) countConceptionEntitiesRes;
            }

            commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(beforeExecuteConceptionEntityCount-afterExecuteConceptionEntityCount);
            commonEntitiesOperationResultImpl.getOperationStatistics().
                    setOperationSummary("purgeAllEntities operation for conceptionKind "+this.conceptionKindName+" success.");

            commonEntitiesOperationResultImpl.finishEntitiesOperation();
            return commonEntitiesOperationResultImpl;
        */
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public EntitiesOperationResult purgeExclusiveEntities() throws CoreRealmServiceRuntimeException {
        try{
            CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

            String cql = "MATCH (node:`"+this.conceptionKindName+"`) WHERE size(apoc.node.labels(node))>1 \n" +
                    "          REMOVE node:`"+this.conceptionKindName+"` \n" +
                    "          RETURN count(node) AS total;";
            logger.debug("Generated Cypher Statement: {}", cql);
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record resultRecord = result.next();
                        if(resultRecord.containsKey("total")){
                            long deletedRecordCount = resultRecord.get("total").asLong();
                            long currentSuccessOperationCount = commonEntitiesOperationResultImpl.getOperationStatistics().getSuccessItemsCount();
                            commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(deletedRecordCount+currentSuccessOperationCount);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);

            /*
            CALL apoc.periodic.iterate(
              'MATCH (n:TestLoad) RETURN n',
              'DETACH DELETE n',
              {batchSize:100000, iterateList:true})
             */
            //return : "batches"│"total"│"timeTaken"│"committedOperations"│"failedOperations"│"failedBatches"│"retries"│"errorMessages"│"batch"│"operations"
            cql = "CALL apoc.periodic.iterate(\n" +
                    "          'MATCH (n:`"+this.conceptionKindName+"`) RETURN n',\n" +
                    "          'DETACH DELETE n',\n" +
                    "          {batchSize:100000, iterateList:true})";
            logger.debug("Generated Cypher Statement: {}", cql);
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
            commonEntitiesOperationResultImpl.getOperationStatistics().
                    setOperationSummary("purgeExclusiveEntities operation for conceptionKind "+this.conceptionKindName+" success.");

            commonEntitiesOperationResultImpl.finishEntitiesOperation();
            return commonEntitiesOperationResultImpl;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Long countEntities(AttributesParameters attributesParameters,boolean isDistinctMode) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        if (attributesParameters != null) {
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters, CypherBuilder.CypherFunctionType.COUNT);
                GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
                Object queryRes = workingGraphOperationExecutor.executeRead(GetLongFormatAggregatedReturnValueTransformer,queryCql);
                if(queryRes != null){
                    return (Long)queryRes;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            return null;

        }else{
            return countConceptionEntities();
        }
    }

    @Override
    public ConceptionEntitiesRetrieveResult getEntities(QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        if (queryParameters != null) {
            CommonConceptionEntitiesRetrieveResultImpl commonConceptionEntitiesRetrieveResultImpl = new CommonConceptionEntitiesRetrieveResultImpl();
            commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters,null);
                GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(this.conceptionKindName,
                        this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
                if(queryRes != null){
                    List<ConceptionEntity> resultConceptionEntityList = (List<ConceptionEntity>)queryRes;
                    commonConceptionEntitiesRetrieveResultImpl.addConceptionEntities(resultConceptionEntityList);
                    commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultConceptionEntityList.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonConceptionEntitiesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonConceptionEntitiesRetrieveResultImpl;
        }
       return null;
    }

    @Override
    public Long countEntitiesWithRelationsMatch(AttributesParameters attributesParameters, boolean isDistinctMode, RelationMatchParameters relationMatchParameters) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        QueryParameters queryParameters = null;
        if (attributesParameters != null) {
            queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters, CypherBuilder.CypherFunctionType.COUNT);

            if(relationMatchParameters != null && relationMatchParameters.getDefaultMatchingItem() != null){
                RelationMatchingItem defaultRelationMatchingItem = relationMatchParameters.getDefaultMatchingItem();
                RelationMatchParameters.MatchingLogic defaultMatchingLogic = relationMatchParameters.getDefaultRelationMatchingLogic();
                List<RelationMatchingItem> orRelationMatchingItemList = relationMatchParameters.getOrRelationMatchingItemList();
                List<RelationMatchingItem> andRelationMatchingItemList = relationMatchParameters.getAndRelationMatchingItemList();

                String relationMatchCQL = generateRelationMatchCQLPart(CypherBuilder.operationResultName,defaultRelationMatchingItem,null);

                for(RelationMatchingItem currentRelationMatchingItem:andRelationMatchingItemList){
                    relationMatchCQL = relationMatchCQL + generateRelationMatchCQLPart(CypherBuilder.operationResultName,currentRelationMatchingItem, RelationMatchParameters.MatchingLogic.AND);
                }

                for(RelationMatchingItem currentRelationMatchingItem:orRelationMatchingItemList){
                    relationMatchCQL = relationMatchCQL + generateRelationMatchCQLPart(CypherBuilder.operationResultName,currentRelationMatchingItem, RelationMatchParameters.MatchingLogic.OR);
                }

                if(queryCql.contains("WHERE")){
                    String relationMatchFullCQL = " AND " + relationMatchCQL;
                    switch (defaultMatchingLogic){
                        case AND -> relationMatchFullCQL = " AND " + relationMatchCQL;
                        case OR -> relationMatchFullCQL = " OR " + relationMatchCQL;
                    }
                    queryCql = queryCql.replace(" RETURN ",relationMatchFullCQL+" RETURN ");
                }else{
                    String relationMatchFullCQL = " WHERE " + relationMatchCQL;
                    queryCql = queryCql.replace(" RETURN ",relationMatchFullCQL+" RETURN ");
                }
                logger.debug("Generated Cypher Statement: {}", queryCql);
            }

            GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object queryRes = workingGraphOperationExecutor.executeRead(GetLongFormatAggregatedReturnValueTransformer,queryCql);
            if(queryRes != null){
                return (Long)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getEntitiesWithRelationsMatch(QueryParameters queryParameters, RelationMatchParameters relationMatchParameters) throws CoreRealmServiceEntityExploreException {
        CommonConceptionEntitiesRetrieveResultImpl commonConceptionEntitiesRetrieveResultImpl = new CommonConceptionEntitiesRetrieveResultImpl();
        commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters,null);

            if(relationMatchParameters != null && relationMatchParameters.getDefaultMatchingItem() != null){
                RelationMatchingItem defaultRelationMatchingItem = relationMatchParameters.getDefaultMatchingItem();
                RelationMatchParameters.MatchingLogic defaultMatchingLogic = relationMatchParameters.getDefaultRelationMatchingLogic();
                List<RelationMatchingItem> orRelationMatchingItemList = relationMatchParameters.getOrRelationMatchingItemList();
                List<RelationMatchingItem> andRelationMatchingItemList = relationMatchParameters.getAndRelationMatchingItemList();

                String relationMatchCQL = generateRelationMatchCQLPart(CypherBuilder.operationResultName,defaultRelationMatchingItem,null);

                for(RelationMatchingItem currentRelationMatchingItem:andRelationMatchingItemList){
                    relationMatchCQL = relationMatchCQL + generateRelationMatchCQLPart(CypherBuilder.operationResultName,currentRelationMatchingItem, RelationMatchParameters.MatchingLogic.AND);
                }

                for(RelationMatchingItem currentRelationMatchingItem:orRelationMatchingItemList){
                    relationMatchCQL = relationMatchCQL + generateRelationMatchCQLPart(CypherBuilder.operationResultName,currentRelationMatchingItem, RelationMatchParameters.MatchingLogic.OR);
                }

                if(queryCql.contains("WHERE")){
                    String relationMatchFullCQL = " AND " + relationMatchCQL;
                    switch (defaultMatchingLogic){
                        case AND -> relationMatchFullCQL = " AND " + relationMatchCQL;
                        case OR -> relationMatchFullCQL = " OR " + relationMatchCQL;
                    }
                    queryCql = queryCql.replace(" RETURN ",relationMatchFullCQL+" RETURN ");
                }else{
                    String relationMatchFullCQL = " WHERE " + relationMatchCQL;
                    queryCql = queryCql.replace(" RETURN ",relationMatchFullCQL+" RETURN ");
                }
                logger.debug("Generated Cypher Statement: {}", queryCql);
            }

            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(this.conceptionKindName,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
            if(queryRes != null){
                List<ConceptionEntity> resultConceptionEntityList = (List<ConceptionEntity>)queryRes;
                commonConceptionEntitiesRetrieveResultImpl.addConceptionEntities(resultConceptionEntityList);
                commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultConceptionEntityList.size());
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        commonConceptionEntitiesRetrieveResultImpl.finishEntitiesRetrieving();
        return commonConceptionEntitiesRetrieveResultImpl;
    }

    @Override
    public Long countEntitiesWithClassificationsAttached(AttributesParameters attributesParameters, boolean isDistinctMode,Set<ClassificationAttachParameters> classificationAttachParametersSet) throws CoreRealmServiceEntityExploreException {
        QueryParameters queryParameters = null;
        if (attributesParameters != null) {
            queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            //base queryCQL
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters,CypherBuilder.CypherFunctionType.COUNT);

            /*
            MATCH (result:Station)-[:LINK]-> (:DOCG_Classification{name: 'xxx'})
            MATCH (result:Station)-[:LINK]-> (:DOCG_Classification)-[classificationPLink1:DOCG_ParentClassificationIs]->+(:DOCG_Classification {name: 'xxx'})
            MATCH (result:Station)<-[:LINK]-(:DOCG_Classification{name: 'yyy'})
            MATCH (result:Station)<-[:LINK]-(:DOCG_Classification)-[classificationPLink2:DOCG_ParentClassificationIs]->+(:DOCG_Classification {name: 'yyy'})
            RETURN result
            */
            StringBuilder classificationsQueryPart = new StringBuilder();
            if(classificationAttachParametersSet != null && classificationAttachParametersSet.size() > 0){
                int classificationIdx = 0;
                for(ClassificationAttachParameters currentRelationMatchParameters:classificationAttachParametersSet) {
                    String classificationName = currentRelationMatchParameters.getAttachedClassification();
                    RelationDirection relationDirection = currentRelationMatchParameters.getRelationDirection();
                    String currentClassificationQueryPart= "(c"+classificationIdx+":"+RealmConstant.ClassificationClass+" {name: '"+classificationName+"'})";
                    classificationIdx++;
                    String relationKind = currentRelationMatchParameters.getRelationKind();
                    boolean isOffspringAttach = currentRelationMatchParameters.isOffspringAttach();
                    if(isOffspringAttach){
                        switch(relationDirection){
                            case FROM ->
                                    classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            case TO ->
                                    classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            case TWO_WAY ->
                                    classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                        }
                    }else{
                        switch(relationDirection){
                            case FROM -> classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->"+currentClassificationQueryPart);
                            case TO -> classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-"+currentClassificationQueryPart);
                            case TWO_WAY -> classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-"+currentClassificationQueryPart);
                        }
                    }
                    classificationsQueryPart.append("\n");
                }
                //MATCH (operationResult:`+this.conceptionKindName+`) WHERE ......
                queryCql = queryCql.replace("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)",classificationsQueryPart.toString());
            }
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object queryRes = workingGraphOperationExecutor.executeRead(GetLongFormatAggregatedReturnValueTransformer,queryCql);
            if(queryRes != null){
                return (Long)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getEntitiesWithClassificationsAttached(QueryParameters queryParameters, Set<ClassificationAttachParameters> classificationAttachParametersSet) throws CoreRealmServiceEntityExploreException {
        CommonConceptionEntitiesRetrieveResultImpl commonConceptionEntitiesRetrieveResultImpl = new CommonConceptionEntitiesRetrieveResultImpl();
        commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            //base queryCQL
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters,null);

            /*
            MATCH (result:Station)-[:LINK]-> (:DOCG_Classification{name: 'xxx'})
            MATCH (result:Station)-[:LINK]-> (:DOCG_Classification)-[classificationPLink1:DOCG_ParentClassificationIs]->+(:DOCG_Classification {name: 'xxx'})
            MATCH (result:Station)<-[:LINK]-(:DOCG_Classification{name: 'yyy'})
            MATCH (result:Station)<-[:LINK]-(:DOCG_Classification)-[classificationPLink2:DOCG_ParentClassificationIs]->+(:DOCG_Classification {name: 'yyy'})
            RETURN result
            */
            StringBuilder classificationsQueryPart = new StringBuilder();
            if(classificationAttachParametersSet != null && classificationAttachParametersSet.size() > 0){
                int classificationIdx = 0;
                for(ClassificationAttachParameters currentRelationMatchParameters:classificationAttachParametersSet) {
                    String classificationName = currentRelationMatchParameters.getAttachedClassification();
                    RelationDirection relationDirection = currentRelationMatchParameters.getRelationDirection();
                    String currentClassificationQueryPart= "(c"+classificationIdx+":"+RealmConstant.ClassificationClass+" {name: '"+classificationName+"'})";
                    classificationIdx++;
                    String relationKind = currentRelationMatchParameters.getRelationKind();
                    boolean isOffspringAttach = currentRelationMatchParameters.isOffspringAttach();
                    if(isOffspringAttach){
                        switch(relationDirection){
                            case FROM ->
                                    classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            case TO ->
                                    classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            case TWO_WAY ->
                                    classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                        }
                    }else{
                        switch(relationDirection){
                            case FROM -> classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->"+currentClassificationQueryPart);
                            case TO -> classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-"+currentClassificationQueryPart);
                            case TWO_WAY -> classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-"+currentClassificationQueryPart);
                        }
                    }
                    classificationsQueryPart.append("\n");
                }
                //MATCH (operationResult:`+this.conceptionKindName+`) WHERE ......
                queryCql = queryCql.replace("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)",classificationsQueryPart.toString());
            }
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(this.conceptionKindName,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
            if(queryRes != null){
                List<ConceptionEntity> resultConceptionEntityList = (List<ConceptionEntity>)queryRes;
                commonConceptionEntitiesRetrieveResultImpl.addConceptionEntities(resultConceptionEntityList);
                commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultConceptionEntityList.size());
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        commonConceptionEntitiesRetrieveResultImpl.finishEntitiesRetrieving();
        return commonConceptionEntitiesRetrieveResultImpl;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNamesWithClassificationsAttached(List<String> attributeNames, QueryParameters queryParameters, Set<ClassificationAttachParameters> classificationAttachParametersSet) throws CoreRealmServiceEntityExploreException {
        if(attributeNames != null && attributeNames.size()>0){
            CommonConceptionEntitiesAttributesRetrieveResultImpl commonConceptionEntitiesAttributesRetrieveResultImpl
                    = new CommonConceptionEntitiesAttributesRetrieveResultImpl();
            commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchAttributesWithQueryParameters(this.conceptionKindName,queryParameters,attributeNames);
                StringBuilder classificationsQueryPart = new StringBuilder();
                if(classificationAttachParametersSet != null && classificationAttachParametersSet.size() > 0){
                    int classificationIdx = 0;
                    for(ClassificationAttachParameters currentRelationMatchParameters:classificationAttachParametersSet) {
                        String classificationName = currentRelationMatchParameters.getAttachedClassification();
                        RelationDirection relationDirection = currentRelationMatchParameters.getRelationDirection();
                        String currentClassificationQueryPart= "(c"+classificationIdx+":"+RealmConstant.ClassificationClass+" {name: '"+classificationName+"'})";
                        classificationIdx++;
                        String relationKind = currentRelationMatchParameters.getRelationKind();
                        boolean isOffspringAttach = currentRelationMatchParameters.isOffspringAttach();
                        if(isOffspringAttach){
                            switch(relationDirection){
                                case FROM ->
                                        classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                                case TO ->
                                        classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                                case TWO_WAY ->
                                        classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            }
                        }else{
                            switch(relationDirection){
                                case FROM -> classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->"+currentClassificationQueryPart);
                                case TO -> classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-"+currentClassificationQueryPart);
                                case TWO_WAY -> classificationsQueryPart.append("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-"+currentClassificationQueryPart);
                            }
                        }
                        classificationsQueryPart.append("\n");
                    }
                    //MATCH (operationResult:`+this.conceptionKindName+`) WHERE ......
                    queryCql = queryCql.replace("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)",classificationsQueryPart.toString());
                }
                logger.debug("Generated Cypher Statement: {}", queryCql);

                List<AttributeKind> containsAttributesKinds = getContainsSingleValueAttributeKinds(workingGraphOperationExecutor);
                GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer =
                        new GetListConceptionEntityValueTransformer(attributeNames,containsAttributesKinds);
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer, queryCql);
                if(resEntityRes != null){
                    List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
                    commonConceptionEntitiesAttributesRetrieveResultImpl.addConceptionEntitiesAttributes(resultEntitiesValues);
                    commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultEntitiesValues.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonConceptionEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonConceptionEntitiesAttributesRetrieveResultImpl;
        }
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKindsWithClassificationsAttached(List<String> attributesViewKindNames, QueryParameters queryParameters, Set<ClassificationAttachParameters> classificationAttachParametersSet) throws CoreRealmServiceEntityExploreException {
        if(attributesViewKindNames != null && attributesViewKindNames.size()>0){
            List<AttributesViewKind> resultRealAttributesViewKindList = new ArrayList<>();
            for(String currentTargetViewKindName:attributesViewKindNames){
                List<AttributesViewKind> currentAttributesViewKinds = getContainsAttributesViewKinds(currentTargetViewKindName);
                if(currentAttributesViewKinds != null){
                    resultRealAttributesViewKindList.addAll(currentAttributesViewKinds);
                }
            }
            List<AttributeKind> allResultTargetAttributeKindList = new ArrayList<>();
            for(AttributesViewKind resultAttributesViewKind:resultRealAttributesViewKindList){
                List<AttributeKind> currentAttributeKinds = resultAttributesViewKind.getContainsAttributeKinds();
                if(currentAttributeKinds != null){
                    allResultTargetAttributeKindList.addAll(currentAttributeKinds);
                }
            }
            List<String> targetAttributeKindNameList = filterSingleValueAttributeKindNames(allResultTargetAttributeKindList);
            return getSingleValueEntityAttributesByAttributeNamesWithClassificationsAttached(targetAttributeKindNameList,queryParameters,classificationAttachParametersSet);
        }
        return null;
    }

    @Override
    public Long countEntitiesWithClassificationsAttached(AttributesParameters attributesParameters, boolean isDistinctMode, Set<ClassificationAttachParameters> classificationAttachParametersSet, FixConceptionEntityAttachParameters fixConceptionEntityAttachParameters) throws CoreRealmServiceEntityExploreException {
        QueryParameters queryParameters = null;
        if (attributesParameters != null) {
            queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            //base queryCQL
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters,CypherBuilder.CypherFunctionType.COUNT);

            /*
            MATCH (result:Station)-[:LINK]-> (:DOCG_Classification{name: 'xxx'})
            MATCH (result:Station)-[:LINK]-> (:DOCG_Classification)-[classificationPLink1:DOCG_ParentClassificationIs]->+(:DOCG_Classification {name: 'xxx'})
            MATCH (result:Station)<-[:LINK]-(:DOCG_Classification{name: 'yyy'})
            MATCH (result:Station)<-[:LINK]-(:DOCG_Classification)-[classificationPLink2:DOCG_ParentClassificationIs]->+(:DOCG_Classification {name: 'yyy'})
            RETURN result
            */

            String fixConceptionEntityAttachQueryPart = "";
            String fixConceptionEntityMatchByIdPart = "";
            if(fixConceptionEntityAttachParameters!=null){
                String conceptionEntityUID = fixConceptionEntityAttachParameters.getConceptionEntityUID();
                RelationDirection relationDirection = fixConceptionEntityAttachParameters.getRelationDirection();
                String relationKind = fixConceptionEntityAttachParameters.getRelationKind();
                if(conceptionEntityUID!=null & relationDirection!=null&relationKind!= null){
                    fixConceptionEntityMatchByIdPart = "MATCH (fixEntity) WHERE id(fixEntity) = "+Long.parseLong(conceptionEntityUID) + "\n";
                    switch(relationDirection){
                        case FROM -> fixConceptionEntityAttachQueryPart = "(fixEntity) <-["+relationKind+"]-";
                        case TO -> fixConceptionEntityAttachQueryPart = "(fixEntity) -["+relationKind+"]->";
                        case TWO_WAY -> fixConceptionEntityAttachQueryPart = "(fixEntity) -["+relationKind+"]-";
                    }
                }
            }

            StringBuilder classificationsQueryPart = new StringBuilder();
            classificationsQueryPart.append(fixConceptionEntityMatchByIdPart);
            if(classificationAttachParametersSet != null && classificationAttachParametersSet.size() > 0){
                int classificationIdx = 0;
                for(ClassificationAttachParameters currentRelationMatchParameters:classificationAttachParametersSet) {
                    String classificationName = currentRelationMatchParameters.getAttachedClassification();
                    RelationDirection relationDirection = currentRelationMatchParameters.getRelationDirection();
                    String currentClassificationQueryPart= "(c"+classificationIdx+":"+RealmConstant.ClassificationClass+" {name: '"+classificationName+"'})";
                    classificationIdx++;
                    String relationKind = currentRelationMatchParameters.getRelationKind();
                    boolean isOffspringAttach = currentRelationMatchParameters.isOffspringAttach();
                    if(isOffspringAttach){
                        switch(relationDirection){
                            case FROM ->
                                    classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            case TO ->
                                    classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            case TWO_WAY ->
                                    classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                        }
                    }else{
                        switch(relationDirection){
                            case FROM -> classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->"+currentClassificationQueryPart);
                            case TO -> classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-"+currentClassificationQueryPart);
                            case TWO_WAY -> classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-"+currentClassificationQueryPart);
                        }
                    }
                    classificationsQueryPart.append("\n");
                }
                //MATCH (operationResult:`+this.conceptionKindName+`) WHERE ......
                queryCql = queryCql.replace("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)",classificationsQueryPart.toString());
            }
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GetLongFormatAggregatedReturnValueTransformer GetLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object queryRes = workingGraphOperationExecutor.executeRead(GetLongFormatAggregatedReturnValueTransformer,queryCql);
            if(queryRes != null){
                return (Long)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getEntitiesWithClassificationsAttached(QueryParameters queryParameters, Set<ClassificationAttachParameters> classificationAttachParametersSet, FixConceptionEntityAttachParameters fixConceptionEntityAttachParameters) throws CoreRealmServiceEntityExploreException {
        CommonConceptionEntitiesRetrieveResultImpl commonConceptionEntitiesRetrieveResultImpl = new CommonConceptionEntitiesRetrieveResultImpl();
        commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            //base queryCQL
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters,null);

            /*
            MATCH (result:Station)-[:LINK]-> (:DOCG_Classification{name: 'xxx'})
            MATCH (result:Station)-[:LINK]-> (:DOCG_Classification)-[classificationPLink1:DOCG_ParentClassificationIs]->+(:DOCG_Classification {name: 'xxx'})
            MATCH (result:Station)<-[:LINK]-(:DOCG_Classification{name: 'yyy'})
            MATCH (result:Station)<-[:LINK]-(:DOCG_Classification)-[classificationPLink2:DOCG_ParentClassificationIs]->+(:DOCG_Classification {name: 'yyy'})
            RETURN result
            */

            String fixConceptionEntityAttachQueryPart = "";
            String fixConceptionEntityMatchByIdPart = "";
            if(fixConceptionEntityAttachParameters!=null){
                String conceptionEntityUID = fixConceptionEntityAttachParameters.getConceptionEntityUID();
                RelationDirection relationDirection = fixConceptionEntityAttachParameters.getRelationDirection();
                String relationKind = fixConceptionEntityAttachParameters.getRelationKind();
                if(conceptionEntityUID!=null & relationDirection!=null&relationKind!= null){
                    fixConceptionEntityMatchByIdPart = "MATCH (fixEntity) WHERE id(fixEntity) = "+Long.parseLong(conceptionEntityUID) + "\n";
                    switch(relationDirection){
                        case FROM -> fixConceptionEntityAttachQueryPart = "(fixEntity) <-["+relationKind+"]-";
                        case TO -> fixConceptionEntityAttachQueryPart = "(fixEntity) -["+relationKind+"]->";
                        case TWO_WAY -> fixConceptionEntityAttachQueryPart = "(fixEntity) -["+relationKind+"]-";
                    }
                }
            }

            StringBuilder classificationsQueryPart = new StringBuilder();
            classificationsQueryPart.append(fixConceptionEntityMatchByIdPart);
            if(classificationAttachParametersSet != null && classificationAttachParametersSet.size() > 0){
                int classificationIdx = 0;
                for(ClassificationAttachParameters currentRelationMatchParameters:classificationAttachParametersSet) {
                    String classificationName = currentRelationMatchParameters.getAttachedClassification();
                    RelationDirection relationDirection = currentRelationMatchParameters.getRelationDirection();
                    String currentClassificationQueryPart= "(c"+classificationIdx+":"+RealmConstant.ClassificationClass+" {name: '"+classificationName+"'})";
                    classificationIdx++;
                    String relationKind = currentRelationMatchParameters.getRelationKind();
                    boolean isOffspringAttach = currentRelationMatchParameters.isOffspringAttach();
                    if(isOffspringAttach){
                        switch(relationDirection){
                            case FROM ->
                                    classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            case TO ->
                                    classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            case TWO_WAY ->
                                    classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                        }
                    }else{
                        switch(relationDirection){
                            case FROM -> classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->"+currentClassificationQueryPart);
                            case TO -> classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-"+currentClassificationQueryPart);
                            case TWO_WAY -> classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-"+currentClassificationQueryPart);
                        }
                    }
                    classificationsQueryPart.append("\n");
                }
                //MATCH (operationResult:`+this.conceptionKindName+`) WHERE ......
                queryCql = queryCql.replace("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)",classificationsQueryPart.toString());
            }
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(this.conceptionKindName,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
            if(queryRes != null){
                List<ConceptionEntity> resultConceptionEntityList = (List<ConceptionEntity>)queryRes;
                commonConceptionEntitiesRetrieveResultImpl.addConceptionEntities(resultConceptionEntityList);
                commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultConceptionEntityList.size());
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        commonConceptionEntitiesRetrieveResultImpl.finishEntitiesRetrieving();
        return commonConceptionEntitiesRetrieveResultImpl;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNamesWithClassificationsAttached(List<String> attributeNames, QueryParameters queryParameters, Set<ClassificationAttachParameters> classificationAttachParametersSet, FixConceptionEntityAttachParameters fixConceptionEntityAttachParameters) throws CoreRealmServiceEntityExploreException {
        if(attributeNames != null && attributeNames.size()>0){
            CommonConceptionEntitiesAttributesRetrieveResultImpl commonConceptionEntitiesAttributesRetrieveResultImpl
                    = new CommonConceptionEntitiesAttributesRetrieveResultImpl();
            commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);

            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                //base queryCQL
                String queryCql = CypherBuilder.matchAttributesWithQueryParameters(this.conceptionKindName,queryParameters,attributeNames);
                String fixConceptionEntityAttachQueryPart = "";
                String fixConceptionEntityMatchByIdPart = "";
                if(fixConceptionEntityAttachParameters!=null){
                    String conceptionEntityUID = fixConceptionEntityAttachParameters.getConceptionEntityUID();
                    RelationDirection relationDirection = fixConceptionEntityAttachParameters.getRelationDirection();
                    String relationKind = fixConceptionEntityAttachParameters.getRelationKind();
                    if(conceptionEntityUID!=null & relationDirection!=null&relationKind!= null){
                        fixConceptionEntityMatchByIdPart = "MATCH (fixEntity) WHERE id(fixEntity) = "+Long.parseLong(conceptionEntityUID) + "\n";
                        switch(relationDirection){
                            case FROM -> fixConceptionEntityAttachQueryPart = "(fixEntity) <-["+relationKind+"]-";
                            case TO -> fixConceptionEntityAttachQueryPart = "(fixEntity) -["+relationKind+"]->";
                            case TWO_WAY -> fixConceptionEntityAttachQueryPart = "(fixEntity) -["+relationKind+"]-";
                        }
                    }
                }

                StringBuilder classificationsQueryPart = new StringBuilder();
                classificationsQueryPart.append(fixConceptionEntityMatchByIdPart);
                if(classificationAttachParametersSet != null && classificationAttachParametersSet.size() > 0){
                    int classificationIdx = 0;
                    for(ClassificationAttachParameters currentRelationMatchParameters:classificationAttachParametersSet) {
                        String classificationName = currentRelationMatchParameters.getAttachedClassification();
                        RelationDirection relationDirection = currentRelationMatchParameters.getRelationDirection();
                        String currentClassificationQueryPart= "(c"+classificationIdx+":"+RealmConstant.ClassificationClass+" {name: '"+classificationName+"'})";
                        classificationIdx++;
                        String relationKind = currentRelationMatchParameters.getRelationKind();
                        boolean isOffspringAttach = currentRelationMatchParameters.isOffspringAttach();
                        if(isOffspringAttach){
                            switch(relationDirection){
                                case FROM ->
                                        classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                                case TO ->
                                        classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                                case TWO_WAY ->
                                        classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-(:"+RealmConstant.ClassificationClass+")-[classificationPLink"+classificationIdx+":"+RealmConstant.Classification_ClassificationRelationClass+"]->+"+currentClassificationQueryPart);
                            }
                        }else{
                            switch(relationDirection){
                                case FROM -> classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]->"+currentClassificationQueryPart);
                                case TO -> classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)<-[:"+relationKind+"]-"+currentClassificationQueryPart);
                                case TWO_WAY -> classificationsQueryPart.append("MATCH "+fixConceptionEntityAttachQueryPart+" ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)-[:"+relationKind+"]-"+currentClassificationQueryPart);
                            }
                        }
                        classificationsQueryPart.append("\n");
                    }
                    //MATCH (operationResult:`+this.conceptionKindName+`) WHERE ......
                    queryCql = queryCql.replace("MATCH ("+CypherBuilder.operationResultName+":`"+this.conceptionKindName+"`)",classificationsQueryPart.toString());
                }
                logger.debug("Generated Cypher Statement: {}", queryCql);

                List<AttributeKind> containsAttributesKinds = getContainsSingleValueAttributeKinds(workingGraphOperationExecutor);
                GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer =
                        new GetListConceptionEntityValueTransformer(attributeNames,containsAttributesKinds);
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer, queryCql);
                if(resEntityRes != null){
                    List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
                    commonConceptionEntitiesAttributesRetrieveResultImpl.addConceptionEntitiesAttributes(resultEntitiesValues);
                    commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultEntitiesValues.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonConceptionEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonConceptionEntitiesAttributesRetrieveResultImpl;
        }
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKindsWithClassificationsAttached(List<String> attributesViewKindNames, QueryParameters queryParameters, Set<ClassificationAttachParameters> classificationAttachParametersSet, FixConceptionEntityAttachParameters fixConceptionEntityAttachParameters) throws CoreRealmServiceEntityExploreException {
        if(attributesViewKindNames != null && attributesViewKindNames.size()>0){
            List<AttributesViewKind> resultRealAttributesViewKindList = new ArrayList<>();
            for(String currentTargetViewKindName:attributesViewKindNames){
                List<AttributesViewKind> currentAttributesViewKinds = getContainsAttributesViewKinds(currentTargetViewKindName);
                if(currentAttributesViewKinds != null){
                    resultRealAttributesViewKindList.addAll(currentAttributesViewKinds);
                }
            }
            List<AttributeKind> allResultTargetAttributeKindList = new ArrayList<>();
            for(AttributesViewKind resultAttributesViewKind:resultRealAttributesViewKindList){
                List<AttributeKind> currentAttributeKinds = resultAttributesViewKind.getContainsAttributeKinds();
                if(currentAttributeKinds != null){
                    allResultTargetAttributeKindList.addAll(currentAttributeKinds);
                }
            }
            List<String> targetAttributeKindNameList = filterSingleValueAttributeKindNames(allResultTargetAttributeKindList);
            return getSingleValueEntityAttributesByAttributeNamesWithClassificationsAttached(targetAttributeKindNameList,queryParameters,classificationAttachParametersSet,fixConceptionEntityAttachParameters);
        }
        return null;
    }

    @Override
    public ConceptionEntity getEntityByUID(String conceptionEntityUID) {
        if (conceptionEntityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(conceptionEntityUID), null, null);
                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(this.conceptionKindName, this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getSingleConceptionEntityTransformer, queryCql);
                return resEntityRes != null ? (ConceptionEntity) resEntityRes : null;
            }catch (NumberFormatException e){
                e.printStackTrace();
                return null;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKinds(List<String> attributesViewKindNames, QueryParameters exploreParameters) throws CoreRealmServiceEntityExploreException{
        if(attributesViewKindNames != null && attributesViewKindNames.size()>0){
            List<AttributesViewKind> resultRealAttributesViewKindList = new ArrayList<>();
            for(String currentTargetViewKindName:attributesViewKindNames){
                List<AttributesViewKind> currentAttributesViewKinds = getContainsAttributesViewKinds(currentTargetViewKindName);
                if(currentAttributesViewKinds != null){
                    resultRealAttributesViewKindList.addAll(currentAttributesViewKinds);
                }
            }
            List<AttributeKind> allResultTargetAttributeKindList = new ArrayList<>();
            for(AttributesViewKind resultAttributesViewKind:resultRealAttributesViewKindList){
                List<AttributeKind> currentAttributeKinds = resultAttributesViewKind.getContainsAttributeKinds();
                if(currentAttributeKinds != null){
                    allResultTargetAttributeKindList.addAll(currentAttributeKinds);
                }
            }
            List<String> targetAttributeKindNameList = filterSingleValueAttributeKindNames(allResultTargetAttributeKindList);
           return getSingleValueEntityAttributesByAttributeNames(targetAttributeKindNameList,exploreParameters);
        }
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNames(List<String> attributeNames, QueryParameters exploreParameters) throws CoreRealmServiceEntityExploreException{
        if(attributeNames != null && attributeNames.size()>0){
            CommonConceptionEntitiesAttributesRetrieveResultImpl commonConceptionEntitiesAttributesRetrieveResultImpl
                    = new CommonConceptionEntitiesAttributesRetrieveResultImpl();
            commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setQueryParameters(exploreParameters);
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchAttributesWithQueryParameters(this.conceptionKindName,exploreParameters,attributeNames);
                List<AttributeKind> containsAttributesKinds = getContainsSingleValueAttributeKinds(workingGraphOperationExecutor);
                GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer =
                        new GetListConceptionEntityValueTransformer(attributeNames,containsAttributesKinds);
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer, queryCql);
                if(resEntityRes != null){
                    List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
                    commonConceptionEntitiesAttributesRetrieveResultImpl.addConceptionEntitiesAttributes(resultEntitiesValues);
                    commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultEntitiesValues.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonConceptionEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonConceptionEntitiesAttributesRetrieveResultImpl;
        }
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKindsWithRelationsMatch(List<String> attributesViewKindNames, QueryParameters exploreParameters, RelationMatchParameters relationMatchParameters) throws CoreRealmServiceEntityExploreException {
        if(attributesViewKindNames != null && attributesViewKindNames.size()>0){
            List<AttributesViewKind> resultRealAttributesViewKindList = new ArrayList<>();
            for(String currentTargetViewKindName:attributesViewKindNames){
                List<AttributesViewKind> currentAttributesViewKinds = getContainsAttributesViewKinds(currentTargetViewKindName);
                if(currentAttributesViewKinds != null){
                    resultRealAttributesViewKindList.addAll(currentAttributesViewKinds);
                }
            }
            List<AttributeKind> allResultTargetAttributeKindList = new ArrayList<>();
            for(AttributesViewKind resultAttributesViewKind:resultRealAttributesViewKindList){
                List<AttributeKind> currentAttributeKinds = resultAttributesViewKind.getContainsAttributeKinds();
                if(currentAttributeKinds != null){
                    allResultTargetAttributeKindList.addAll(currentAttributeKinds);
                }
            }
            List<String> targetAttributeKindNameList = filterSingleValueAttributeKindNames(allResultTargetAttributeKindList);
            return getSingleValueEntityAttributesByAttributeNamesWithRelationsMatch(targetAttributeKindNameList,exploreParameters,relationMatchParameters);
        }
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNamesWithRelationsMatch(List<String> attributeNames, QueryParameters exploreParameters, RelationMatchParameters relationMatchParameters) throws CoreRealmServiceEntityExploreException {
        if(attributeNames != null && attributeNames.size()>0){
            CommonConceptionEntitiesAttributesRetrieveResultImpl commonConceptionEntitiesAttributesRetrieveResultImpl
                    = new CommonConceptionEntitiesAttributesRetrieveResultImpl();
            commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setQueryParameters(exploreParameters);
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchAttributesWithQueryParameters(this.conceptionKindName,exploreParameters,attributeNames);

                if(relationMatchParameters != null && relationMatchParameters.getDefaultMatchingItem() != null){
                    RelationMatchingItem defaultRelationMatchingItem = relationMatchParameters.getDefaultMatchingItem();
                    RelationMatchParameters.MatchingLogic defaultMatchingLogic = relationMatchParameters.getDefaultRelationMatchingLogic();
                    List<RelationMatchingItem> orRelationMatchingItemList = relationMatchParameters.getOrRelationMatchingItemList();
                    List<RelationMatchingItem> andRelationMatchingItemList = relationMatchParameters.getAndRelationMatchingItemList();

                    String relationMatchCQL = generateRelationMatchCQLPart(CypherBuilder.operationResultName,defaultRelationMatchingItem,null);

                    for(RelationMatchingItem currentRelationMatchingItem:andRelationMatchingItemList){
                        relationMatchCQL = relationMatchCQL + generateRelationMatchCQLPart(CypherBuilder.operationResultName,currentRelationMatchingItem, RelationMatchParameters.MatchingLogic.AND);
                    }

                    for(RelationMatchingItem currentRelationMatchingItem:orRelationMatchingItemList){
                        relationMatchCQL = relationMatchCQL + generateRelationMatchCQLPart(CypherBuilder.operationResultName,currentRelationMatchingItem, RelationMatchParameters.MatchingLogic.OR);
                    }

                    if(queryCql.contains("WHERE")){
                        String relationMatchFullCQL = " AND " + relationMatchCQL;
                        switch (defaultMatchingLogic){
                            case AND -> relationMatchFullCQL = " AND " + relationMatchCQL;
                            case OR -> relationMatchFullCQL = " OR " + relationMatchCQL;
                        }
                        queryCql = queryCql.replace(" RETURN ",relationMatchFullCQL+" RETURN ");
                    }else{
                        String relationMatchFullCQL = " WHERE " + relationMatchCQL;
                        queryCql = queryCql.replace(" RETURN ",relationMatchFullCQL+" RETURN ");
                    }
                    logger.debug("Generated Cypher Statement: {}", queryCql);
                }

                List<AttributeKind> containsAttributesKinds = getContainsSingleValueAttributeKinds(workingGraphOperationExecutor);
                GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer =
                        new GetListConceptionEntityValueTransformer(attributeNames,containsAttributesKinds);
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer, queryCql);
                if(resEntityRes != null){
                    List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
                    commonConceptionEntitiesAttributesRetrieveResultImpl.addConceptionEntitiesAttributes(resultEntitiesValues);
                    commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultEntitiesValues.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonConceptionEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonConceptionEntitiesAttributesRetrieveResultImpl;
        }
        return null;
    }

    @Override
    public boolean attachAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException {
        if(attributesViewKindUID == null){
            return false;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributesViewKindUID), null, null);
            GetSingleAttributesViewKindTransformer getSingleAttributesViewKindTransformer =
                    new GetSingleAttributesViewKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object checkAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributesViewKindTransformer,queryCql);
            if(checkAttributesViewKindRes != null){
                String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(conceptionKindUID),Long.parseLong(attributesViewKindUID),
                        RealmConstant.ConceptionKind_AttributesViewKindRelationClass);

                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.ConceptionKind_AttributesViewKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object existingRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, queryRelationCql);
                if(existingRelationEntityRes != null){
                    return true;
                }

                Map<String,Object> relationPropertiesMap = new HashMap<>();
                CommonOperationUtil.generateEntityMetaAttributes(relationPropertiesMap);
                String createCql = CypherBuilder.createNodesRelationshipByIdsMatch(Long.parseLong(conceptionKindUID),Long.parseLong(attributesViewKindUID),
                        RealmConstant.ConceptionKind_AttributesViewKindRelationClass,relationPropertiesMap);
                getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.ConceptionKind_AttributesViewKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createCql);
                if(newRelationEntityRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
                    return true;
                }
            }else{
                logger.error("AttributesViewKind does not contains entity with UID {}.", attributesViewKindUID);
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("AttributesViewKind does not contains entity with UID " + attributesViewKindUID + ".");
                throw exception;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<AttributesViewKind> getContainsAttributesViewKinds(String attributesViewKindName) {
        if(attributesViewKindName == null){
            return null;
        }else{
            List<AttributesViewKind> resultAttributesViewKindList = new ArrayList<>();
            List<AttributesViewKind> allContainsAttributesViewKinds = this.getContainsAttributesViewKinds();
            if(allContainsAttributesViewKinds != null && allContainsAttributesViewKinds.size()>0){
                for(AttributesViewKind currentAttributesViewKind : allContainsAttributesViewKinds){
                    if(currentAttributesViewKind.getAttributesViewKindName().equals(attributesViewKindName.trim())){
                        resultAttributesViewKindList.add(currentAttributesViewKind);
                    }
                }
            }
            return resultAttributesViewKindList;
        }
    }

    @Override
    public boolean detachAttributesViewKind(String attributesViewKindUID) throws CoreRealmServiceRuntimeException {
        if(attributesViewKindUID == null){
            return false;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(attributesViewKindUID), null, null);
            GetSingleAttributesViewKindTransformer getSingleAttributesViewKindTransformer =
                    new GetSingleAttributesViewKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object checkAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributesViewKindTransformer,queryCql);
            if(checkAttributesViewKindRes != null){
                String queryRelationCql = CypherBuilder.matchRelationshipsByBothNodesId(Long.parseLong(conceptionKindUID),Long.parseLong(attributesViewKindUID),
                        RealmConstant.ConceptionKind_AttributesViewKindRelationClass);

                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.ConceptionKind_AttributesViewKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object existingRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, queryRelationCql);

                if(existingRelationEntityRes == null){
                    return false;
                }
                RelationEntity relationEntity = (RelationEntity)existingRelationEntityRes;

                String deleteCql = CypherBuilder.deleteRelationWithSingleFunctionValueEqual(
                        CypherBuilder.CypherFunctionType.ID,Long.valueOf(relationEntity.getRelationEntityUID()),null,null);

                getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.ConceptionKind_AttributesViewKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object deleteRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, deleteCql);
                if(deleteRelationEntityRes == null){
                    return false;
                }else{
                    return true;
                }
            }else{
                logger.error("AttributesViewKind does not contains entity with UID {}.", attributesViewKindUID);
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("AttributesViewKind does not contains entity with UID " + attributesViewKindUID + ".");
                throw exception;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<AttributesViewKind> getContainsAttributesViewKinds() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(conceptionKindUID),
                    RealmConstant.AttributesViewKindClass,RealmConstant.ConceptionKind_AttributesViewKindRelationClass,RelationDirection.TO, null);
            GetListAttributesViewKindTransformer getListAttributesViewKindTransformer =
                    new GetListAttributesViewKindTransformer(RealmConstant.ConceptionKind_AttributesViewKindRelationClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object attributesViewKindsRes = workingGraphOperationExecutor.executeWrite(getListAttributesViewKindTransformer,queryCql);
            return attributesViewKindsRes != null ? (List<AttributesViewKind>) attributesViewKindsRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public List<AttributeKind> getContainsSingleValueAttributeKinds() {
        return getSingleValueAttributeKinds(null);
    }

    @Override
    public List<AttributeKind> getContainsSingleValueAttributeKinds(String attributeKindName) {
        if(attributeKindName == null){
            return null;
        }else{
          return getSingleValueAttributeKinds(attributeKindName);
        }
    }

    @Override
    public ConceptionEntitiesRetrieveResult getKindDirectRelatedEntities(List<String> startEntityUIDS,String relationKind, RelationDirection relationDirection, String targetConceptionKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        if(relationKind == null){
            logger.error("RelationKind is required.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("RelationKind is required.");
            throw exception;
        }
        RelationDirection realRelationDirection = relationDirection != null ? relationDirection : RelationDirection.TWO_WAY;
        CommonConceptionEntitiesRetrieveResultImpl commonConceptionEntitiesRetrieveResultImpl = new CommonConceptionEntitiesRetrieveResultImpl();
        commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodeWithSpecialRelationAndAttributeFilter(relationKind,realRelationDirection,
                    this.conceptionKindName,startEntityUIDS,targetConceptionKind,queryParameters);
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object queryRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
            if(queryRes != null){
                List<ConceptionEntity> resultConceptionEntityList = (List<ConceptionEntity>)queryRes;
                commonConceptionEntitiesRetrieveResultImpl.addConceptionEntities(resultConceptionEntityList);
                commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultConceptionEntityList.size());
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        commonConceptionEntitiesRetrieveResultImpl.finishEntitiesRetrieving();
        return commonConceptionEntitiesRetrieveResultImpl;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getAttributesOfKindDirectRelatedEntities(List<String> startEntityUIDS, List<String> attributeNames, String relationKind, RelationDirection relationDirection, String targetConceptionKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        if(relationKind == null){
            logger.error("RelationKind is required.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("RelationKind is required.");
            throw exception;
        }
        if(attributeNames != null && attributeNames.size()>0){
            CommonConceptionEntitiesAttributesRetrieveResultImpl commonConceptionEntitiesAttributesRetrieveResultImpl
                    = new CommonConceptionEntitiesAttributesRetrieveResultImpl();
            RelationDirection realRelationDirection = relationDirection != null ? relationDirection : RelationDirection.TWO_WAY;

            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodeWithSpecialRelationAndAttributeFilter(relationKind,realRelationDirection,
                        this.conceptionKindName,startEntityUIDS,targetConceptionKind,queryParameters);

                GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer = new GetListConceptionEntityValueTransformer(attributeNames);
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer, queryCql);
                if(resEntityRes != null){
                    List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
                    commonConceptionEntitiesAttributesRetrieveResultImpl.addConceptionEntitiesAttributes(resultEntitiesValues);
                    commonConceptionEntitiesAttributesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultEntitiesValues.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonConceptionEntitiesAttributesRetrieveResultImpl.finishEntitiesRetrieving();
            return commonConceptionEntitiesAttributesRetrieveResultImpl;
        }else{
            return null;
        }
    }

    @Override
    public ConceptionEntitiesRetrieveResult getEntitiesByDirectRelations(String relationKind, RelationDirection relationDirection, String aimConceptionKind, QueryParameters queryParameters) throws CoreRealmServiceEntityExploreException {
        if(relationKind == null){
            logger.error("RelationKind is required.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("RelationKind is required.");
            throw exception;
        }
        RelationDirection realRelationDirection =  RelationDirection.TWO_WAY;

        if(relationDirection != null){
            switch(relationDirection){
                case FROM:
                    realRelationDirection = RelationDirection.TO;
                    break;
                case TO:
                    realRelationDirection = RelationDirection.FROM;
                    break;
                case TWO_WAY:
                    realRelationDirection =  RelationDirection.TWO_WAY;
            }
        }
        CommonConceptionEntitiesRetrieveResultImpl commonConceptionEntitiesRetrieveResultImpl = new CommonConceptionEntitiesRetrieveResultImpl();
        commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(aimConceptionKind,queryParameters,null);
            DataTransformer<List<String>> aimConceptionKindEntityUIDListDataTransformer = new DataTransformer<List<String>>() {
                @Override
                public List<String> transformResult(Result result) {
                    List<String> conceptionEntityUIDList = new ArrayList<>();
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                        long nodeUID = resultNode.id();
                        String conceptionEntityUID = ""+nodeUID;
                        conceptionEntityUIDList.add(conceptionEntityUID);

                    }
                    return conceptionEntityUIDList;
                }
            };
            Object queryRes = workingGraphOperationExecutor.executeRead(aimConceptionKindEntityUIDListDataTransformer,queryCql);
            List aimConceptionKindEntityUIDList = (List<String>)queryRes;
            queryCql = CypherBuilder.matchNodeWithSpecialRelationAndAttributeFilter(relationKind,realRelationDirection,
                    aimConceptionKind,aimConceptionKindEntityUIDList,this.conceptionKindName,null);
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(null,
                    this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            queryRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
            if(queryRes != null){
                List<ConceptionEntity> resultConceptionEntityList = (List<ConceptionEntity>)queryRes;
                commonConceptionEntitiesRetrieveResultImpl.addConceptionEntities(resultConceptionEntityList);
                commonConceptionEntitiesRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(resultConceptionEntityList.size());
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        commonConceptionEntitiesRetrieveResultImpl.finishEntitiesRetrieving();
        return commonConceptionEntitiesRetrieveResultImpl;
    }

    @Override
    public Set<KindAttributeDistributionInfo> getKindAttributesDistributionStatistics(double sampleRatio) throws CoreRealmServiceRuntimeException {
        if(sampleRatio >1 || sampleRatio<=0){
            logger.error("Sample Ratio should between (0,1] .");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Sample Ratio should between (0,1] .");
            throw exception;
        }
        String cql = "MATCH (n:`"+this.conceptionKindName+"`) WHERE rand() <= "+sampleRatio+"\n" +
                "RETURN\n" +
                "DISTINCT labels(n),max(keys(n)) as PropertyList,count(*) AS SampleSize";
        logger.debug("Generated Cypher Statement: {}", cql);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Set<KindAttributeDistributionInfo>> aimConceptionKindEntityUIDListDataTransformer = new DataTransformer<Set<KindAttributeDistributionInfo>>() {
                @Override
                public Set<KindAttributeDistributionInfo> transformResult(Result result) {
                    Set<KindAttributeDistributionInfo> resultSet = new HashSet<>();
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        List<Object> kindNames = nodeRecord.get("labels(n)").asList();
                        List<Object> attributesNames = nodeRecord.get("PropertyList").asList();

                        String[] kindNamesArray = new String[kindNames.size()];
                        for(int i=0;i<kindNamesArray.length;i++){
                            kindNamesArray[i] = kindNames.get(i).toString();
                        }
                        String[] attributeNamesArray = new String[attributesNames.size()];
                        for(int i=0;i<attributeNamesArray.length;i++){
                            attributeNamesArray[i] = attributesNames.get(i).toString();
                        }
                        KindAttributeDistributionInfo currentKindAttributeDistributionInfo =
                                new KindAttributeDistributionInfo(kindNamesArray,attributeNamesArray);

                        resultSet.add(currentKindAttributeDistributionInfo);
                    }
                    return resultSet;
                }
            };
            Object queryRes = workingGraphOperationExecutor.executeRead(aimConceptionKindEntityUIDListDataTransformer,cql);
            if(queryRes != null){
                return (Set<KindAttributeDistributionInfo>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public Set<KindDataDistributionInfo> getKindDataDistributionStatistics(double sampleRatio) throws CoreRealmServiceRuntimeException {
        if(sampleRatio >1 || sampleRatio<=0){
            logger.error("Sample Ratio should between (0,1] .");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Sample Ratio should between (0,1] .");
            throw exception;
        }
        String cql = "MATCH (n:`"+this.conceptionKindName+"`) WHERE rand() <= "+sampleRatio+"\n" +
                "        RETURN\n" +
                "        DISTINCT labels(n),\n" +
                "        count(*) AS SampleSize,\n" +
                "        avg(size(keys(n))) as Avg_PropertyCount,\n" +
                "        min(size(keys(n))) as Min_PropertyCount,\n" +
                "        max(size(keys(n))) as Max_PropertyCount,\n" +
                "        percentileDisc(size(keys(n)),0.5) as Middle_PropertyCount,\n" +
                "        avg(size([p=(n)-[]-() | p]) ) as Avg_RelationshipCount,\n" +
                "        min(size([p=(n)-[]-() | p]) ) as Min_RelationshipCount,\n" +
                "        max(size([p=(n)-[]-() | p]) ) as Max_RelationshipCount,\n" +
                "        percentileDisc(size([p=(n)-[]-() | p]), 0.5) as Middle_RelationshipCount";
        logger.debug("Generated Cypher Statement: {}", cql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Set<KindDataDistributionInfo>> aimConceptionKindEntityUIDListDataTransformer = new DataTransformer<Set<KindDataDistributionInfo>>() {
                @Override
                public Set<KindDataDistributionInfo> transformResult(Result result) {
                    Set<KindDataDistributionInfo> resultSet = new HashSet<>();
                    while(result.hasNext()){
                        Record nodeRecord = result.next();
                        List<Object> kindNames = nodeRecord.get("labels(n)").asList();
                        long entitySampleSize = nodeRecord.get("SampleSize").asLong();
                        double avgAttributeCount = nodeRecord.get("Avg_PropertyCount").asDouble();
                        int minAttributeCount = nodeRecord.get("Min_PropertyCount").asInt();
                        int maxAttributeCount = nodeRecord.get("Max_PropertyCount").asInt();
                        int medianAttributeCount = nodeRecord.get("Middle_PropertyCount").asInt();
                        double avgRelationCount = nodeRecord.get("Avg_RelationshipCount").asDouble();
                        int minRelationCount = nodeRecord.get("Min_RelationshipCount").asInt();
                        int maxRelationCount = nodeRecord.get("Max_RelationshipCount").asInt();
                        int medianRelationCount = nodeRecord.get("Middle_RelationshipCount").asInt();

                        String[] kindNamesArray = new String[kindNames.size()];
                        for(int i=0;i<kindNamesArray.length;i++){
                            kindNamesArray[i] = kindNames.get(i).toString();
                        }
                        KindDataDistributionInfo currentKindDataDistributionInfo = new KindDataDistributionInfo(kindNamesArray,entitySampleSize,
                                avgAttributeCount,minAttributeCount,maxAttributeCount,medianAttributeCount,
                                avgRelationCount,minRelationCount,maxRelationCount,medianRelationCount);

                        resultSet.add(currentKindDataDistributionInfo);
                    }
                    return resultSet;
                }
            };
            Object queryRes = workingGraphOperationExecutor.executeRead(aimConceptionKindEntityUIDListDataTransformer,cql);
            if(queryRes != null){
                return (Set<KindDataDistributionInfo>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public Set<ConceptionKindCorrelationInfo> getKindRelationDistributionStatistics() {
        String cql ="CALL db.schema.visualization()";
        logger.debug("Generated Cypher Statement: {}", cql);
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            DataTransformer<Set<ConceptionKindCorrelationInfo>> statisticsDataTransformer = new DataTransformer(){
                @Override
                public Set<ConceptionKindCorrelationInfo> transformResult(Result result) {
                    Record currentRecord = result.next();
                    List<Object> nodesList = currentRecord.get("nodes").asList();
                    List<Object> relationshipsList = currentRecord.get("relationships").asList();

                    Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet = new HashSet<>();
                    String currentConceptionKindID = null;
                    Map<String,String> conceptionKindId_nameMapping = new HashMap<>();
                    for(Object currentNodeObj:nodesList){
                        Node currentNode = (Node)currentNodeObj;
                        long currentNodeId = currentNode.id();
                        String currentConceptionKindName = currentNode.labels().iterator().next();
                        if(conceptionKindName.equals(currentConceptionKindName)){
                            currentConceptionKindID = ""+currentNodeId;
                        }
                        conceptionKindId_nameMapping.put(""+currentNodeId,currentConceptionKindName);
                    }

                    for(Object currentRelationshipObj:relationshipsList){
                        Relationship currentRelationship = (Relationship)currentRelationshipObj;
                        //long relationshipId = currentRelationship.id();
                        String relationshipType = currentRelationship.type();
                        String startConceptionKindId = ""+currentRelationship.startNodeId();
                        String endConceptionKindId = ""+currentRelationship.endNodeId();
                        if(startConceptionKindId.equals(currentConceptionKindID)||
                                endConceptionKindId.equals(currentConceptionKindID)){
                            boolean relationExist = checkRelationEntitiesExist(workingGraphOperationExecutor,conceptionKindId_nameMapping.get(startConceptionKindId),conceptionKindId_nameMapping.get(endConceptionKindId),relationshipType);
                            if(relationExist){
                                ConceptionKindCorrelationInfo currentConceptionKindCorrelationInfo =
                                        new ConceptionKindCorrelationInfo(
                                                conceptionKindId_nameMapping.get(startConceptionKindId),
                                                conceptionKindId_nameMapping.get(endConceptionKindId),
                                                relationshipType,1);
                                conceptionKindCorrelationInfoSet.add(currentConceptionKindCorrelationInfo);
                            }
                        }
                    }
                    return conceptionKindCorrelationInfoSet;
                }
            };
            Object queryRes = workingGraphOperationExecutor.executeRead(statisticsDataTransformer,cql);
            if(queryRes != null){
                return (Set<ConceptionKindCorrelationInfo>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public Set<ConceptionEntity> getRandomEntities(int entitiesCount) throws CoreRealmServiceEntityExploreException {
        if(entitiesCount < 1){
            logger.error("entitiesCount must equal or great then 1.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("entitiesCount must equal or great then 1.");
            throw exception;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = "MATCH (n:`"+this.conceptionKindName+"`) RETURN apoc.coll.randomItems(COLLECT(n),"+entitiesCount+") AS " +CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            RandomItemsConceptionEntitySetDataTransformer randomItemsConceptionEntitySetDataTransformer =
                    new RandomItemsConceptionEntitySetDataTransformer(workingGraphOperationExecutor);
            Object queryRes = workingGraphOperationExecutor.executeRead(randomItemsConceptionEntitySetDataTransformer,queryCql);
            if(queryRes != null){
                Set<ConceptionEntity> resultConceptionEntityList = (Set<ConceptionEntity>)queryRes;
                return resultConceptionEntityList;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public Set<ConceptionEntity> getRandomEntities(AttributesParameters attributesParameters, boolean isDistinctMode, int entitiesCount) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        if(entitiesCount < 1){
            logger.error("entitiesCount must equal or great then 1.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("entitiesCount must equal or great then 1.");
            throw exception;
        }
        if (attributesParameters != null) {
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters,null);
                String replaceContent = isDistinctMode ? "RETURN DISTINCT "+CypherBuilder.operationResultName+" LIMIT 100000000":
                        "RETURN "+CypherBuilder.operationResultName+" LIMIT 100000000";
                String newContent = isDistinctMode ? "RETURN apoc.coll.randomItems(COLLECT("+CypherBuilder.operationResultName+"),"+entitiesCount+",false) AS " +CypherBuilder.operationResultName:
                        "RETURN apoc.coll.randomItems(COLLECT("+CypherBuilder.operationResultName+"),"+entitiesCount+",true) AS " +CypherBuilder.operationResultName;
                queryCql = queryCql.replace(replaceContent,newContent);
                logger.debug("Generated Cypher Statement: {}", queryCql);
                RandomItemsConceptionEntitySetDataTransformer randomItemsConceptionEntitySetDataTransformer =
                        new RandomItemsConceptionEntitySetDataTransformer(workingGraphOperationExecutor);
                Object queryRes = workingGraphOperationExecutor.executeRead(randomItemsConceptionEntitySetDataTransformer,queryCql);
                if(queryRes != null){
                    Set<ConceptionEntity> resultConceptionEntityList = (Set<ConceptionEntity>)queryRes;
                    return resultConceptionEntityList;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            return null;

        }else{
            return getRandomEntities(entitiesCount);
        }
    }

    @Override
    public EntitiesOperationStatistics setKindScopeAttributes(Map<String, Object> attributes) throws CoreRealmServiceRuntimeException {
        if(attributes == null || attributes.size() ==0){
            logger.error("attributes Map must have at least 1 attribute value.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("attributes Map must have at least 1 attribute value.");
            throw exception;
        }
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String countQueryCql = CypherBuilder.matchLabelWithSinglePropertyValueAndFunction(getConceptionKindName(), CypherBuilder.CypherFunctionType.COUNT, null, null);
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer, countQueryCql);
            long entitiesCount = (Long)countConceptionEntitiesRes;

            String queryCql = CypherBuilder.setConceptionKindProperties(this.conceptionKindName,attributes);
            if(entitiesCount < 100000){
                Object queryRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,queryCql);
                if(queryRes != null) {
                    Long operationResult =(Long)queryRes;
                    entitiesOperationStatistics.setFinishTime(new Date());
                    entitiesOperationStatistics.setSuccessItemsCount(operationResult);
                    entitiesOperationStatistics.setOperationSummary("setKindScopeAttributes operation success");
                    return entitiesOperationStatistics;
                }
            }else{
                String matchPart = queryCql.substring(0,queryCql.indexOf(" SET"));
                String setPart = queryCql.substring(queryCql.indexOf("SET"),queryCql.indexOf(" RETURN"));
                String periodicQueryCql = "CALL apoc.periodic.iterate(\n" +
                        "\""+matchPart+" RETURN "+CypherBuilder.operationResultName+"\",\n" +
                        "\""+setPart+"\",\n" +
                        " {batchSize:1000, parallel:false}\n" +
                        ")";
                logger.debug("Generated Cypher Statement: {}", periodicQueryCql);
                DataTransformer<Long> periodicSetOperationDataTransformer = new DataTransformer<Long>() {
                    @Override
                    public Long transformResult(Result result) {
                        if(result.hasNext()){
                            Record record = result.next();
                            long committedOperations = 0;
                            long failedOperations = 0;
                            if(record.containsKey("committedOperations")){
                                committedOperations = record.get("committedOperations").asLong();
                            }
                            if(record.containsKey("failedOperations")){
                                failedOperations = record.get("failedOperations").asLong();
                            }
                            return committedOperations - failedOperations;
                        }
                        return 0l;
                    }
                };

                Object queryRes = workingGraphOperationExecutor.executeWrite(periodicSetOperationDataTransformer,periodicQueryCql);
                if(queryRes != null) {
                    Long operationResult =(Long)queryRes;
                    entitiesOperationStatistics.setFinishTime(new Date());
                    entitiesOperationStatistics.setSuccessItemsCount(operationResult);
                    entitiesOperationStatistics.setFailItemsCount(entitiesCount-operationResult);
                    entitiesOperationStatistics.setOperationSummary("setKindScopeAttributes operation success");
                    return entitiesOperationStatistics;
                }
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics removeEntityAttributes(Set<String> attributeNames) throws CoreRealmServiceRuntimeException{
        if(attributeNames == null || attributeNames.size() ==0){
            logger.error("attributeNames must have at least 1 attribute name.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("attributeNames must have at least 1 attribute name.");
            throw exception;
        }
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        //https://neo4j.com/docs/apoc/current/overview/apoc.create/apoc.create.removeProperties/
        String attributeNameStr = "[";
        for(String currentAttribute:attributeNames){
            attributeNameStr = attributeNameStr+"'"+currentAttribute+"'"+",";
        }
        attributeNameStr = attributeNameStr.substring(0,attributeNameStr.length()-1);
        attributeNameStr = attributeNameStr+"]";

        String queryCql = "MATCH (n:`"+this.conceptionKindName+"`) WITH collect(n) AS entities\n" +
                "CALL apoc.create.removeProperties(entities, "+attributeNameStr+")\n" +
                "YIELD node\n" +
                "RETURN count(node) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer();
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer, queryCql);
            if (countConceptionEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
                entitiesOperationStatistics.setFinishTime(new Date());
                entitiesOperationStatistics.setSuccessItemsCount((Long) countConceptionEntitiesRes);
                entitiesOperationStatistics.setOperationSummary("removeEntityAttributes operation success");
                return entitiesOperationStatistics;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public EntitiesOperationStatistics convertEntityAttributeToIntType(String attributeName) {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String queryCql ="MATCH (node:`"+this.conceptionKindName+"`)\n" +
                "SET node."+attributeName+" = toIntegerOrNull(node."+attributeName+") RETURN count(node) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        long operationEntitiesCount = executeEntitiesOperationWithCountResponse(queryCql);
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(operationEntitiesCount);
        entitiesOperationStatistics.setOperationSummary("convertEntityAttributeToIntType operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics convertEntityAttributeToFloatType(String attributeName) {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String queryCql ="MATCH (node:`"+this.conceptionKindName+"`)\n" +
                "SET node."+attributeName+" = toFloatOrNull(node."+attributeName+") RETURN count(node) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        long operationEntitiesCount = executeEntitiesOperationWithCountResponse(queryCql);
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(operationEntitiesCount);
        entitiesOperationStatistics.setOperationSummary("convertEntityAttributeToFloatType operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics convertEntityAttributeToBooleanType(String attributeName) {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String queryCql ="MATCH (node:`"+this.conceptionKindName+"`)\n" +
                "SET node."+attributeName+" = toBooleanOrNull(node."+attributeName+") RETURN count(node) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        long operationEntitiesCount = executeEntitiesOperationWithCountResponse(queryCql);
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(operationEntitiesCount);
        entitiesOperationStatistics.setOperationSummary("convertEntityAttributeToBooleanType operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics convertEntityAttributeToStringType(String attributeName) {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String queryCql ="MATCH (node:`"+this.conceptionKindName+"`)\n" +
                "SET node."+attributeName+" = toString(node."+attributeName+") RETURN count(node) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        long operationEntitiesCount = executeEntitiesOperationWithCountResponse(queryCql);
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(operationEntitiesCount);
        entitiesOperationStatistics.setOperationSummary("convertEntityAttributeToStringType operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics convertEntityAttributeToTemporalType(String attributeName,DateTimeFormatter dateTimeFormatter,
                             TemporalScaleCalculable.TemporalScaleLevel temporalScaleType) throws CoreRealmServiceRuntimeException {
        if(attributeName == null){
            logger.error("attributeName is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("attributeName is required.");
            throw exception;
        }
        if(dateTimeFormatter == null){
            logger.error("dateTimeFormatter is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("dateTimeFormatter is required.");
            throw exception;
        }
        if(temporalScaleType == null){
            logger.error("temporalScaleType is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("temporalScaleType is required.");
            throw exception;
        }

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        List<String> attributeNames = new ArrayList<>();
        attributeNames.add(attributeName);
        QueryParameters exploreParameters = new QueryParameters();
        exploreParameters.setResultNumber(1000000000);

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            String queryCql = CypherBuilder.matchAttributesWithQueryParameters(this.conceptionKindName,exploreParameters,attributeNames);
            List<AttributeKind> containsAttributesKinds = getContainsSingleValueAttributeKinds(workingGraphOperationExecutor);
            GetListConceptionEntityValueTransformer getListConceptionEntityValueTransformer =
                    new GetListConceptionEntityValueTransformer(attributeNames,containsAttributesKinds);
            Object resEntityRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityValueTransformer, queryCql);
            if(resEntityRes != null){
                List<ConceptionEntityValue> resultEntitiesValues = (List<ConceptionEntityValue>)resEntityRes;
                Map<String,Object> operationResult =
                        BatchDataOperationUtil.batchConvertConceptionEntityAttributeToTemporalType(attributeName,resultEntitiesValues,dateTimeFormatter,temporalScaleType,BatchDataOperationUtil.CPUUsageRate.High);
                long successItemCount = 0;
                Collection<Object> resultCountCollection = operationResult.values();
                for(Object currentResultCount :resultCountCollection){
                    if(currentResultCount instanceof Long) {
                        successItemCount = successItemCount + (Long) currentResultCount;
                    }
                }
                entitiesOperationStatistics.setSuccessItemsCount(successItemCount);
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            throw new RuntimeException(e);
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        entitiesOperationStatistics.setOperationSummary("convert String Entity Attribute "+attributeName+" to "+temporalScaleType + " operation finished.");
        entitiesOperationStatistics.setFinishTime(new Date());
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics duplicateEntityAttribute(String originalAttributeName, String newAttributeName) throws CoreRealmServiceRuntimeException {
        if(originalAttributeName == null){
            logger.error("originalAttributeName is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("originalAttributeName is required.");
            throw exception;
        }
        if(newAttributeName == null){
            logger.error("newAttributeName is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("newAttributeName is required.");
            throw exception;
        }

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        String queryCql = "MATCH (node:`"+this.conceptionKindName+"`) WHERE node.`"+ originalAttributeName +"` IS NOT null \n" +
                "SET node.`"+newAttributeName+"` = node.`"+ originalAttributeName +"` RETURN count(node) AS "+CypherBuilder.operationResultName;
        logger.debug("Generated Cypher Statement: {}", queryCql);

        long operationEntitiesCount = executeEntitiesOperationWithCountResponse(queryCql);
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(operationEntitiesCount);
        entitiesOperationStatistics.setOperationSummary("duplicateEntityAttribute operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics attachTimeScaleEvents(QueryParameters queryParameters, String timeEventAttributeName, DateTimeFormatter dateTimeFormatter,
                                 String timeFlowName, String eventComment, Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade)
            throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        QueryParameters filterQueryParameters;
        if(queryParameters != null){
            filterQueryParameters = queryParameters;
        }else{
            filterQueryParameters = new QueryParameters();
            filterQueryParameters.setResultNumber(1000000000);
        }
        if(timeEventAttributeName == null){
            logger.error("timeEventAttributeName is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("timeEventAttributeName is required.");
            throw exception;
        }
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add(timeEventAttributeName);
        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  getSingleValueEntityAttributesByAttributeNames(attributeNamesList,filterQueryParameters);
        long successItemCount = 0;
        if(conceptionEntitiesAttributeResult == null || conceptionEntitiesAttributeResult.getOperationStatistics().getResultEntitiesCount() == 0){
            successItemCount = 0;
        }else{
            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
            ConceptionEntityValue firstConceptionEntityValue = conceptionEntityValueList.get(0);
            Object targetAttribute = firstConceptionEntityValue.getEntityAttributesValue().get(timeEventAttributeName);
            Map<String,Object> operationResult = null;
            if(targetAttribute instanceof String){
                operationResult = BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(
                                conceptionEntityValueList,timeEventAttributeName,timeFlowName,eventComment,dateTimeFormatter,eventData, timeScaleGrade, BatchDataOperationUtil.CPUUsageRate.High);
            }else if(targetAttribute instanceof ZonedDateTime){
                operationResult = BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(
                        conceptionEntityValueList,timeEventAttributeName,timeFlowName,eventComment,null,eventData, timeScaleGrade, BatchDataOperationUtil.CPUUsageRate.High);
            }else if(targetAttribute instanceof LocalDateTime){
                operationResult = BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(
                        conceptionEntityValueList,timeEventAttributeName,timeFlowName,eventComment,null,eventData, timeScaleGrade, BatchDataOperationUtil.CPUUsageRate.High);
            }else if(targetAttribute instanceof LocalDate){
                operationResult = BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(
                        conceptionEntityValueList,timeEventAttributeName,timeFlowName,eventComment,null,eventData, timeScaleGrade, BatchDataOperationUtil.CPUUsageRate.High);
            }else if(targetAttribute instanceof Date){
                operationResult = BatchDataOperationUtil.batchAttachTimeScaleEventsWithStringDateAttributeValue(
                        conceptionEntityValueList,timeEventAttributeName,timeFlowName,eventComment,null,eventData, timeScaleGrade, BatchDataOperationUtil.CPUUsageRate.High);
            }
            Collection<Object> resultCountCollection = operationResult.values();
            for(Object currentResultCount :resultCountCollection){
                if(currentResultCount instanceof Long) {
                    successItemCount = successItemCount + (Long) currentResultCount;
                }
            }
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(successItemCount);
        entitiesOperationStatistics.setOperationSummary("attachTimeScaleEvents operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics attachTimeScaleEvents(QueryParameters queryParameters, String timeEventYearAttributeName, String timeEventMonthAttributeName, String timeEventDayAttributeName, String timeEventHourAttributeName, String timeEventMinuteAttributeName, String timeFlowName, String eventComment, Map<String, Object> eventData, TimeFlow.TimeScaleGrade timeScaleGrade) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        if(timeScaleGrade == null){
            logger.error("timeScaleGrade is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("timeScaleGrade is required.");
            throw exception;
        }
        if(timeEventYearAttributeName == null){
            logger.error("timeEventYearAttributeName is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("timeEventYearAttributeName is required.");
            throw exception;
        }
        switch (timeScaleGrade){
            case MONTH :if(timeEventMonthAttributeName == null){
                logger.error("timeEventMonthAttributeName is required.");
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("timeEventMonthAttributeName is required.");
                throw exception;
            }
            break;
            case DAY:if(timeEventMonthAttributeName == null || timeEventDayAttributeName == null){
                logger.error("timeEventMonthAttributeName and timeEventDayAttributeName is required.");
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("timeEventMonthAttributeName and timeEventDayAttributeName is required.");
                throw exception;
            }
            break;
            case HOUR:if(timeEventMonthAttributeName == null || timeEventDayAttributeName == null || timeEventHourAttributeName == null){
                logger.error("timeEventMonthAttributeName,timeEventDayAttributeName and timeEventHourAttributeName is required.");
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("timeEventMonthAttributeName,timeEventDayAttributeName and timeEventHourAttributeName is required.");
                throw exception;
            }
            break;
            case MINUTE:if(timeEventMonthAttributeName == null || timeEventDayAttributeName == null || timeEventHourAttributeName == null || timeEventMinuteAttributeName == null){
                logger.error("timeEventMonthAttributeName,timeEventDayAttributeName,timeEventHourAttributeName and timeEventMinuteAttributeName is required.");
                CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                exception.setCauseMessage("timeEventMonthAttributeName,timeEventDayAttributeName,timeEventHourAttributeName and timeEventMinuteAttributeName is required.");
                throw exception;
            }
            break;
        }

        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add(timeEventYearAttributeName);
        if(timeEventMonthAttributeName != null){
            attributeNamesList.add(timeEventYearAttributeName);
        }
        if(timeEventDayAttributeName != null){
            attributeNamesList.add(timeEventDayAttributeName);
        }
        if(timeEventHourAttributeName != null){
            attributeNamesList.add(timeEventHourAttributeName);
        }
        if(timeEventMinuteAttributeName != null){
            attributeNamesList.add(timeEventMinuteAttributeName);
        }

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        QueryParameters filterQueryParameters;
        if(queryParameters != null){
            filterQueryParameters = queryParameters;
        }else{
            filterQueryParameters = new QueryParameters();
            filterQueryParameters.setResultNumber(1000000000);
        }

        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult = getSingleValueEntityAttributesByAttributeNames(attributeNamesList,filterQueryParameters);
        long successItemCount = 0;
        if(conceptionEntitiesAttributeResult == null || conceptionEntitiesAttributeResult.getOperationStatistics().getResultEntitiesCount() == 0){
            successItemCount = 0;
        }else{
            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
            Map<String,Object> operationResult = BatchDataOperationUtil.batchAttachTimeScaleEventsWithDateAttributesValue(
                    conceptionEntityValueList,timeEventYearAttributeName, timeEventMonthAttributeName, timeEventDayAttributeName, timeEventHourAttributeName, timeEventMinuteAttributeName,
                    timeFlowName,eventComment,eventData, timeScaleGrade, BatchDataOperationUtil.CPUUsageRate.High);;
            Collection<Object> resultCountCollection = operationResult.values();
            for(Object currentResultCount :resultCountCollection){
                if(currentResultCount instanceof Long) {
                    successItemCount = successItemCount + (Long) currentResultCount;
                }
            }
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(successItemCount);
        entitiesOperationStatistics.setOperationSummary("attachTimeScaleEvents operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics attachGeospatialScaleEvents(QueryParameters queryParameters, String geospatialEventAttributeName, GeospatialRegion.GeospatialProperty geospatialPropertyType, String geospatialRegionName, String eventComment, Map<String, Object> eventData, GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());

        if(geospatialPropertyType == null){
            logger.error("geospatialPropertyType is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("geospatialPropertyType is required.");
            throw exception;
        }
        if (Objects.requireNonNull(geospatialPropertyType) == GeospatialRegion.GeospatialProperty.EnglishName) {
            logger.error("EnglishName geospatialPropertyType is not supported.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("EnglishName geospatialPropertyType is not supported.");
            throw exception;
        }

        QueryParameters filterQueryParameters;
        if(queryParameters != null){
            filterQueryParameters = queryParameters;
        }else{
            filterQueryParameters = new QueryParameters();
            filterQueryParameters.setResultNumber(1000000000);
        }
        if(geospatialEventAttributeName == null){
            logger.error("geospatialEventAttributeName is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("geospatialEventAttributeName is required.");
            throw exception;
        }
        List<String> attributeNamesList = new ArrayList<>();
        attributeNamesList.add(geospatialEventAttributeName);

        ConceptionEntitiesAttributesRetrieveResult conceptionEntitiesAttributeResult =  getSingleValueEntityAttributesByAttributeNames(attributeNamesList,filterQueryParameters);
        long successItemCount = 0;
        if(conceptionEntitiesAttributeResult == null || conceptionEntitiesAttributeResult.getOperationStatistics().getResultEntitiesCount() == 0){
            successItemCount = 0;
        }else{
            List<ConceptionEntityValue> conceptionEntityValueList = conceptionEntitiesAttributeResult.getConceptionEntityValues();
            Map<String,String> entityUIDAndGeospatialChinaNamesMap = new HashMap<>();
            for(ConceptionEntityValue currentConceptionEntityValue : conceptionEntityValueList){
                if(currentConceptionEntityValue.getEntityAttributesValue().containsKey(geospatialEventAttributeName)){
                    entityUIDAndGeospatialChinaNamesMap.put(currentConceptionEntityValue.getConceptionEntityUID(),currentConceptionEntityValue.getEntityAttributesValue().get(geospatialEventAttributeName).toString());
                }
            }
            Map<String,Object> operationResult = null;
            switch(geospatialPropertyType){
                case ChineseName -> operationResult = BatchDataOperationUtil.batchAttachGeospatialScaleEventsByChineseNames(
                        entityUIDAndGeospatialChinaNamesMap,geospatialRegionName,eventComment,eventData,geospatialScaleGrade,BatchDataOperationUtil.CPUUsageRate.High);
                case GeospatialCode -> operationResult = BatchDataOperationUtil.batchAttachGeospatialScaleEventsByGeospatialCode(
                        entityUIDAndGeospatialChinaNamesMap,geospatialRegionName,eventComment,eventData,geospatialScaleGrade,BatchDataOperationUtil.CPUUsageRate.High);
            }
            Collection<Object> resultCountCollection = operationResult.values();
            for(Object currentResultCount :resultCountCollection){
                if(currentResultCount instanceof Long) {
                    successItemCount = successItemCount + (Long) currentResultCount;
                }
            }
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(successItemCount);
        entitiesOperationStatistics.setOperationSummary("attachTimeScaleEvents operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics attachGeospatialScaleEventsByEntityGeometryContent(QueryParameters queryParameters, GeospatialScaleCalculable.SpatialScaleLevel spatialScaleLevel, GeospatialScaleCalculable.SpatialPredicateType spatialPredicateType, GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade, String geospatialRegionName, String eventComment, Map<String, Object> eventData) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        long successItemCount = 0;
        if(spatialScaleLevel == null){
            logger.error("spatialScaleLevel is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("spatialScaleLevel is required.");
            throw exception;
        }
        if(spatialPredicateType == null){
            logger.error("spatialPredicateType is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("spatialPredicateType is required.");
            throw exception;
        }
        if(geospatialScaleGrade == null){
            logger.error("geospatialScaleGrade is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("geospatialScaleGrade is required.");
            throw exception;
        }

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters,null);
            GetListConceptionEntityTransformer getListConceptionEntityTransformer = new GetListConceptionEntityTransformer(this.conceptionKindName,
                    workingGraphOperationExecutor);
            Object queryRes = workingGraphOperationExecutor.executeRead(getListConceptionEntityTransformer,queryCql);
            if(queryRes != null){
                String spatialScaleLevelValue = null;
                switch (spatialScaleLevel){
                    case Local -> spatialScaleLevelValue = RealmConstant._GeospatialLLGeometryContent;
                    case Country -> spatialScaleLevelValue = RealmConstant._GeospatialCLGeometryContent;
                    case Global -> spatialScaleLevelValue = RealmConstant._GeospatialGLGeometryContent;
                }

                String geospatialScaleGradeValue = null;
                switch(geospatialScaleGrade){
                    case CONTINENT -> geospatialScaleGradeValue =RealmConstant.GeospatialScaleContinentEntityClass;
                    case COUNTRY_REGION -> geospatialScaleGradeValue =RealmConstant.GeospatialScaleCountryRegionEntityClass;
                    case PROVINCE -> geospatialScaleGradeValue =RealmConstant.GeospatialScaleProvinceEntityClass;
                    case PREFECTURE -> geospatialScaleGradeValue =RealmConstant.GeospatialScalePrefectureEntityClass;
                    case COUNTY -> geospatialScaleGradeValue =RealmConstant.GeospatialScaleCountyEntityClass;
                    case TOWNSHIP -> geospatialScaleGradeValue =RealmConstant.GeospatialScaleTownshipEntityClass;
                    case VILLAGE -> geospatialScaleGradeValue =RealmConstant.GeospatialScaleVillageEntityClass;
                }

                List<ConceptionEntity> resultConceptionEntityList = (List<ConceptionEntity>)queryRes;

                for(ConceptionEntity currentEntity : resultConceptionEntityList){
                    NullValueFilteringItem nullValueFilteringItem = new NullValueFilteringItem(spatialScaleLevelValue);
                    nullValueFilteringItem.reverseCondition();
                    AttributesParameters attributesParameters = new AttributesParameters();
                    attributesParameters.setDefaultFilteringItem(nullValueFilteringItem);
                    if(geospatialRegionName != null){
                        attributesParameters.addFilteringItem(new EqualFilteringItem(RealmConstant.GeospatialRegionProperty,geospatialRegionName), QueryParameters.FilteringLogic.AND);
                    }
                    try{
                        List<ConceptionEntity> matchedEntitiesList = currentEntity.
                                getSpatialPredicateMatchedConceptionEntities(geospatialScaleGradeValue,attributesParameters, spatialPredicateType, spatialScaleLevel);
                        if(matchedEntitiesList != null && !matchedEntitiesList.isEmpty()){
                            for(ConceptionEntity currentTownship:matchedEntitiesList){
                                String currentGeospatialCode = currentTownship.getAttribute(RealmConstant.GeospatialCodeProperty).getAttributeValue().toString();
                                GeospatialScaleEvent result = currentEntity.attachGeospatialScaleEvent(currentGeospatialCode,eventComment,eventData);
                                if(result != null){
                                    successItemCount++;
                                }
                            }
                        }
                    }catch(CoreRealmServiceRuntimeException e){
                        e.printStackTrace();
                    }
                }
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setSuccessItemsCount(successItemCount);
        entitiesOperationStatistics.setOperationSummary("attachGeospatialScaleEventsByEntityGeometryContent operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public TimeScaleEventAndConceptionEntityPairRetrieveResult getAttachedTimeScaleEventAndConceptionEntityPairs(QueryParameters queryParameters) {
        try {
            CommonTimeScaleEventAndConceptionEntityPairRetrieveResultImpl commonTimeScaleDataPairRetrieveResultImpl = new CommonTimeScaleEventAndConceptionEntityPairRetrieveResultImpl();
            commonTimeScaleDataPairRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.TimeScaleEventClass,queryParameters,null);
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)","(conceptionEntity:`"+this.conceptionKindName+"`)-[:`"+RealmConstant.TimeScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)");
            queryCql = queryCql.replace("RETURN "+CypherBuilder.operationResultName,"RETURN "+CypherBuilder.operationResultName+",conceptionEntity");
            logger.debug("Generated Cypher Statement: {}", queryCql);

            try{
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                GetListTimeScaleEventAndConceptionEntityPairTransformer getListTimeScaleEventAndConceptionEntityPairTransformer = new GetListTimeScaleEventAndConceptionEntityPairTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListTimeScaleEventAndConceptionEntityPairTransformer,queryCql);
                if(queryRes != null){
                    List<TimeScaleEventAndConceptionEntityPair> res = (List<TimeScaleEventAndConceptionEntityPair>)queryRes;
                    commonTimeScaleDataPairRetrieveResultImpl.addTimeScaleEventAndConceptionEntityPairs(res);
                    commonTimeScaleDataPairRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(res.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonTimeScaleDataPairRetrieveResultImpl.finishEntitiesRetrieving();
            return commonTimeScaleDataPairRetrieveResultImpl;

        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TimeScaleEventsRetrieveResult getAttachedTimeScaleEvents(QueryParameters queryParameters) {
        try {
            CommonTimeScaleEventsRetrieveResultImpl commonTimeScaleEventsRetrieveResultImpl = new CommonTimeScaleEventsRetrieveResultImpl();
            commonTimeScaleEventsRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.TimeScaleEventClass,queryParameters,null);
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)","(:`"+this.conceptionKindName+"`)-[:`"+RealmConstant.TimeScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)");
            logger.debug("Generated Cypher Statement: {}", queryCql);

            try{
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                GetListTimeScaleEventTransformer getListTimeScaleEventTransformer = new GetListTimeScaleEventTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListTimeScaleEventTransformer,queryCql);
                if(queryRes != null){
                    List<TimeScaleEvent> res = (List<TimeScaleEvent>)queryRes;
                    commonTimeScaleEventsRetrieveResultImpl.addTimeScaleEvents(res);
                    commonTimeScaleEventsRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(res.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonTimeScaleEventsRetrieveResultImpl.finishEntitiesRetrieving();
            return commonTimeScaleEventsRetrieveResultImpl;

        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long countAttachedTimeScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        QueryParameters queryParameters = null;
        if (attributesParameters != null) {
            queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
        }

        try {
            CommonTimeScaleEventsRetrieveResultImpl commonTimeScaleEventsRetrieveResultImpl = new CommonTimeScaleEventsRetrieveResultImpl();
            commonTimeScaleEventsRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.TimeScaleEventClass,queryParameters,CypherBuilder.CypherFunctionType.COUNT);
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)","(:`"+this.conceptionKindName+"`)-[:`"+RealmConstant.TimeScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.TimeScaleEventClass+"`)");
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer, queryCql);
            if (countConceptionEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
                return (Long) countConceptionEntitiesRes;
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GeospatialScaleEventAndConceptionEntityPairRetrieveResult getAttachedGeospatialScaleEventAndConceptionEntityPairs(QueryParameters queryParameters) {
        try {
            CommonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl = new CommonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl();
            commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleEventClass,queryParameters,null);
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)","(conceptionEntity:`"+this.conceptionKindName+"`)-[:`"+RealmConstant.GeospatialScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)");
            queryCql = queryCql.replace("RETURN "+CypherBuilder.operationResultName,"RETURN "+CypherBuilder.operationResultName+",conceptionEntity");
            logger.debug("Generated Cypher Statement: {}", queryCql);

            try{
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                GetListGeospatialScaleEventAndConceptionEntityPairTransformer getListGeospatialScaleEventAndConceptionEntityPairTransformer = new GetListGeospatialScaleEventAndConceptionEntityPairTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEventAndConceptionEntityPairTransformer,queryCql);
                if(queryRes != null){
                    List<GeospatialScaleEventAndConceptionEntityPair> res = (List<GeospatialScaleEventAndConceptionEntityPair>)queryRes;
                    commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl.addGeospatialScaleEventAndConceptionEntityPairs(res);
                    commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(res.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl.finishEntitiesRetrieving();
            return commonGeospatialScaleEventAndConceptionEntityPairRetrieveResultImpl;
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GeospatialScaleEventsRetrieveResult getAttachedGeospatialScaleEvents(QueryParameters queryParameters) {
        try {
            CommonGeospatialScaleEventsRetrieveResultImpl commonGeospatialScaleEventsRetrieveResultImpl = new CommonGeospatialScaleEventsRetrieveResultImpl();
            commonGeospatialScaleEventsRetrieveResultImpl.getOperationStatistics().setQueryParameters(queryParameters);
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleEventClass,queryParameters,null);
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)","(:`"+this.conceptionKindName+"`)-[:`"+RealmConstant.GeospatialScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)");
            logger.debug("Generated Cypher Statement: {}", queryCql);

            try{
                GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
                GetListGeospatialScaleEventTransformer getListGeospatialScaleEventTransformer = new GetListGeospatialScaleEventTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryRes = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEventTransformer,queryCql);
                if(queryRes != null){
                    List<GeospatialScaleEvent> res = (List<GeospatialScaleEvent>)queryRes;
                    commonGeospatialScaleEventsRetrieveResultImpl.addGeospatialScaleEvents(res);
                    commonGeospatialScaleEventsRetrieveResultImpl.getOperationStatistics().setResultEntitiesCount(res.size());
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            commonGeospatialScaleEventsRetrieveResultImpl.finishEntitiesRetrieving();
            return commonGeospatialScaleEventsRetrieveResultImpl;

        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Long countAttachedGeospatialScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        QueryParameters queryParameters = null;
        if (attributesParameters != null) {
            queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
        }

        try {
            String queryCql = CypherBuilder.matchNodesWithQueryParameters(RealmConstant.GeospatialScaleEventClass,queryParameters,CypherBuilder.CypherFunctionType.COUNT);
            queryCql = queryCql.replace("(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)","(:`"+this.conceptionKindName+"`)-[:`"+RealmConstant.GeospatialScale_AttachToRelationClass+"`]->(operationResult:`"+RealmConstant.GeospatialScaleEventClass+"`)");
            logger.debug("Generated Cypher Statement: {}", queryCql);

            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeRead(getLongFormatAggregatedReturnValueTransformer, queryCql);
            if (countConceptionEntitiesRes == null) {
                throw new CoreRealmServiceRuntimeException();
            } else {
                return (Long) countConceptionEntitiesRes;
            }
        } catch (CoreRealmServiceEntityExploreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EntitiesOperationStatistics joinConceptionKinds(String[] newKindNames) throws CoreRealmServiceRuntimeException {
        if(newKindNames == null || newKindNames.length == 0){
            logger.error("At least one Conception Kind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("At least one Conception Kind Name is required.");
            throw exception;
        }
        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        try{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String updateKindsCql = CypherBuilder.modifyLabelsWithLabelMatch(this.conceptionKindName, CypherBuilder.LabelOperationType.ADD,
                    CypherBuilder.CypherFunctionType.COUNT,newKindNames);
            GetLongFormatReturnValueTransformer getLongFormatReturnValueTransformer = new GetLongFormatReturnValueTransformer("count("+CypherBuilder.operationResultName+")");
            Object queryRes = workingGraphOperationExecutor.executeWrite(getLongFormatReturnValueTransformer, updateKindsCql);
            if(queryRes != null){
                entitiesOperationStatistics.setSuccessItemsCount((Long)queryRes);
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("joinConceptionKinds operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public EntitiesOperationStatistics retreatFromConceptionKind(String kindName) throws CoreRealmServiceRuntimeException {
        if(kindName == null){
            logger.error("Conception Kind Name is required.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Conception Kind Name is required.");
            throw exception;
        }
        if(kindName.equals(this.conceptionKindName)){
            logger.error("Can't retreat from Self Conception Kind.");
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("Can't retreat from Self Conception Kind.");
            throw exception;
        }

        EntitiesOperationStatistics entitiesOperationStatistics = new EntitiesOperationStatistics();
        entitiesOperationStatistics.setStartTime(new Date());
        try{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String[] kindNamesArray = new String[]{kindName};
            String updateKindsCql = CypherBuilder.modifyLabelsWithLabelMatch(this.conceptionKindName, CypherBuilder.LabelOperationType.REMOVE,
                    CypherBuilder.CypherFunctionType.COUNT,kindNamesArray);
            GetLongFormatReturnValueTransformer getLongFormatReturnValueTransformer = new GetLongFormatReturnValueTransformer("count("+CypherBuilder.operationResultName+")");
            Object queryRes = workingGraphOperationExecutor.executeWrite(getLongFormatReturnValueTransformer, updateKindsCql);
            if(queryRes != null){
                entitiesOperationStatistics.setSuccessItemsCount((Long)queryRes);
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        entitiesOperationStatistics.setFinishTime(new Date());
        entitiesOperationStatistics.setOperationSummary("retreatFromConceptionKind operation success");
        return entitiesOperationStatistics;
    }

    @Override
    public ConceptionKindDataCapabilityInfo getConceptionKindDataCapabilityStatistics() {
        ConceptionKindDataCapabilityInfo conceptionKindDataCapabilityInfo = new ConceptionKindDataCapabilityInfo();
        DataTransformer dataTransformer = new DataTransformer() {
            @Override
            public Boolean transformResult(Result result) {
                if(result.hasNext()){
                    return true;
                }else{
                    return false;
                }
            }
        };
        try{
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

            //For ContainsGeospatialAttribute
            /*
            MATCH (n:`ConceptionKind`)
            WHERE n.DOCG_GS_CLGeometryContent IS NOT NULL OR n.DOCG_GS_GLGeometryContent IS NOT NULL OR n.DOCG_GS_LLGeometryContent IS NOT NULL
            RETURN n LIMIT 1
            */
            String containsGeospatialAttributeCheckCql = "MATCH (n:`"+conceptionKindName+"`)\n" +
                    "        WHERE n.DOCG_GS_CLGeometryContent IS NOT NULL OR n.DOCG_GS_GLGeometryContent IS NOT NULL OR n.DOCG_GS_LLGeometryContent IS NOT NULL\n" +
                    "        RETURN n LIMIT 1";
            Object checkRes1 = workingGraphOperationExecutor.executeRead(dataTransformer,containsGeospatialAttributeCheckCql);
            if(checkRes1 != null){
                conceptionKindDataCapabilityInfo.setContainsGeospatialAttribute((Boolean)checkRes1);
            }

            //For AttachedToGeospatialScaleEvent
            String attachedToGeospatialScaleEventCheckCql = "MATCH (n:`"+conceptionKindName+"`)-[:DOCG_AttachToGeospatialScale]->(DOCG_GeospatialScaleEvent) RETURN n LIMIT 1";
            Object checkRes2 = workingGraphOperationExecutor.executeRead(dataTransformer,attachedToGeospatialScaleEventCheckCql);
            if(checkRes2 != null){
                conceptionKindDataCapabilityInfo.setAttachedToGeospatialScaleEvent((Boolean)checkRes2);
            }

            //For AttachedToTimeScaleEvent
            String attachedToTimeScaleEventCheckCql = "MATCH (n:`"+conceptionKindName+"`)-[:DOCG_AttachToTimeScale]->(DOCG_TimeScaleEvent) RETURN n LIMIT 1";
            Object checkRes3 = workingGraphOperationExecutor.executeRead(dataTransformer,attachedToTimeScaleEventCheckCql);
            if(checkRes3 != null){
                conceptionKindDataCapabilityInfo.setAttachedToTimeScaleEvent((Boolean)checkRes3);
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return conceptionKindDataCapabilityInfo;
    }

    private long executeEntitiesOperationWithCountResponse(String cql){
        long operationResultCount = 0;
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer();
            Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer, cql);
            if (countConceptionEntitiesRes != null) {
                operationResultCount =(Long) countConceptionEntitiesRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return operationResultCount;
    }

    private class RandomItemsConceptionEntitySetDataTransformer implements DataTransformer<Set<ConceptionEntity>>{
        GraphOperationExecutor workingGraphOperationExecutor;
        public RandomItemsConceptionEntitySetDataTransformer(GraphOperationExecutor workingGraphOperationExecutor){
            this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        }
        @Override
        public Set<ConceptionEntity> transformResult(Result result) {
            Set<ConceptionEntity> conceptionEntitySet = new HashSet<>();
            if(result.hasNext()){
                List<Value> resultList = result.next().values();
                if(resultList.size() > 0){
                    List<Object> nodeObjList = resultList.get(0).asList();
                    for(Object currentNodeObj : nodeObjList){
                        Node resultNode = (Node)currentNodeObj;
                        List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
                        boolean isMatchedConceptionKind = true;
                        if(allConceptionKindNames.size()>0){
                            isMatchedConceptionKind = allConceptionKindNames.contains(conceptionKindName);
                        }
                        if(isMatchedConceptionKind){
                            long nodeUID = resultNode.id();
                            String conceptionEntityUID = ""+nodeUID;
                            String resultConceptionKindName = conceptionKindName;
                            Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                                    new Neo4JConceptionEntityImpl(resultConceptionKindName,conceptionEntityUID);
                            neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
                            neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                            conceptionEntitySet.add(neo4jConceptionEntityImpl);
                        }
                    }
                }
            }
            return conceptionEntitySet;
        }
    }

    private List<AttributeKind> getContainsSingleValueAttributeKinds(GraphOperationExecutor workingGraphOperationExecutor) {
        return getSingleValueAttributeKinds(null,workingGraphOperationExecutor);
    }

    private List<AttributeKind> getSingleValueAttributeKinds(String attributeKindName) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            return getSingleValueAttributeKinds(attributeKindName,workingGraphOperationExecutor);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    private List<AttributeKind> getSingleValueAttributeKinds(String attributeKindName,GraphOperationExecutor workingGraphOperationExecutor) {
        Map<String,Object> attributeKindNameFilterMap = null;
        if(attributeKindName != null){
            /*
               MATCH (sourceNode)-[:`DOCG_ConceptionContainsViewKindIs`]->
               (middleNode:`DOCG_AttributesViewKind` {viewKindDataForm: 'SINGLE_VALUE'})-[:`DOCG_ViewContainsAttributeKindIs`]->
               (operationResult:`DOCG_AttributeKind` {name: 'attributeKind02'}) WHERE id(sourceNode) = 1415 RETURN operationResult
               */
            attributeKindNameFilterMap = new HashMap<>();
            attributeKindNameFilterMap.put(RealmConstant._NameProperty,attributeKindName);
        }else{
            /*
                  MATCH (sourceNode)-[:`DOCG_ConceptionContainsViewKindIs`]->
                  (middleNode:`DOCG_AttributesViewKind` {viewKindDataForm: 'SINGLE_VALUE'})-[:`DOCG_ViewContainsAttributeKindIs`]->
                  (operationResult:`DOCG_AttributeKind`) WHERE id(sourceNode) = 1399 RETURN operationResult
               */
        }
        String queryCql = CypherBuilder.match2JumpRelatedNodesFromSpecialStartNodes(
                CypherBuilder.CypherFunctionType.ID, Long.parseLong(conceptionKindUID),
                RealmConstant.AttributesViewKindClass,RealmConstant.ConceptionKind_AttributesViewKindRelationClass,RelationDirection.TO,singleValueAttributesViewKindTypeFilter,
                RealmConstant.AttributeKindClass,RealmConstant.AttributesViewKind_AttributeKindRelationClass,RelationDirection.TO,attributeKindNameFilterMap,
                null);
        GetListAttributeKindTransformer getListAttributeKindTransformer = new GetListAttributeKindTransformer(RealmConstant.AttributeKindClass,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
        Object attributeKindsRes = workingGraphOperationExecutor.executeWrite(getListAttributeKindTransformer,queryCql);
        return attributeKindsRes != null ? (List<AttributeKind>) attributeKindsRes : null;
    }

    private List<String> filterSingleValueAttributeKindNames(List<AttributeKind> targetAttributeKindList){
        List<String> singleValueAttributeKindNames = new ArrayList<>();
        List<AttributeKind> singleValueAttributesKindsList = getContainsSingleValueAttributeKinds();

        List<String> singleValueAttributesKindNamesList = new ArrayList<>();
        for(AttributeKind currentAttributeKind:singleValueAttributesKindsList){
            singleValueAttributesKindNamesList.add(currentAttributeKind.getAttributeKindName());
        }
        for(AttributeKind currentTargetAttributeKind:targetAttributeKindList){
            String currentAttributeKindName = currentTargetAttributeKind.getAttributeKindName();
            if(singleValueAttributesKindNamesList.contains(currentAttributeKindName)){
                singleValueAttributeKindNames.add(currentAttributeKindName);
            }
        }
        return singleValueAttributeKindNames;
    }

    private String generateRelationMatchCQLPart(String conceptionEntityCqlAlias,RelationMatchingItem relationMatchingItem,RelationMatchParameters.MatchingLogic matchLogic){
        if(relationMatchingItem != null && relationMatchingItem.getRelationKind() != null && relationMatchingItem.getRelationDirection() != null){
            String relationJumpStepStr=null;
            if(relationMatchingItem.getRelationJump() == null){
                // not define jump step, using *
                relationJumpStepStr="*]";
            }else if(relationMatchingItem.getRelationJump() == 1){
                relationJumpStepStr="]";
            }else{
                relationJumpStepStr="*"+relationMatchingItem.getRelationJump()+"]";
            }

            String _CQLPart = "";
            if(relationMatchingItem.getRelatedConceptionKind() != null){
                _CQLPart = "("+conceptionEntityCqlAlias+")-[:`"+relationMatchingItem.getRelationKind()+"`"+relationJumpStepStr+"-(:`"+relationMatchingItem.getRelatedConceptionKind()+"`)";
            }else{
                _CQLPart = "("+conceptionEntityCqlAlias+")-[:`"+relationMatchingItem.getRelationKind()+"`"+relationJumpStepStr+"-()";
            }

            switch (relationMatchingItem.getRelationDirection()){
                case FROM -> _CQLPart = _CQLPart.replace(relationJumpStepStr+"-(",relationJumpStepStr+"->(");
                case TO -> _CQLPart = _CQLPart.replace(")-[:",")<-[:");
            }
            if(relationMatchingItem.isReversedCondition()){
                _CQLPart = "not("+_CQLPart+")";
            }
            if(matchLogic != null){
                switch (matchLogic){
                    case AND -> _CQLPart = " AND "+_CQLPart;
                    case OR -> _CQLPart = " OR "+_CQLPart;
                }
            }
            return _CQLPart;
        }
        return null;
    }

    private boolean checkRelationEntitiesExist(GraphOperationExecutor workingGraphOperationExecutor,String sourceConceptionKindName,
                                               String targetConceptionKindName,String relationKindName){
        String cql = "MATCH p=(source:`"+sourceConceptionKindName+"`)-[r:`"+relationKindName+"`]->(target:`"+targetConceptionKindName+"`) RETURN r AS operationResult LIMIT 1";
        logger.debug("Generated Cypher Statement: {}", cql);
        DataTransformer<Boolean> dataTransformer = new DataTransformer<>() {
            @Override
            public Boolean transformResult(Result result) {
                boolean relationEntitiesExist = false;
                int resultNumCount = result.list().size();
                if(resultNumCount == 0){
                    relationEntitiesExist = false;
                }else{
                    relationEntitiesExist = true;
                }
                return relationEntitiesExist;
            }
        };
        Object queryRes = workingGraphOperationExecutor.executeRead(dataTransformer,cql);
        return queryRes != null ? ((Boolean)queryRes).booleanValue():false;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return conceptionKindUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
