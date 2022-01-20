package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesAttributesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonConceptionEntitiesAttributesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonConceptionEntitiesRetrieveResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonEntitiesOperationResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
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
    public EntitiesOperationResult purgeAllEntities() throws CoreRealmServiceRuntimeException{
        CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String deleteCql = CypherBuilder.deleteLabelWithSinglePropertyValueAndFunction(this.conceptionKindName,
                    CypherBuilder.CypherFunctionType.COUNT,null,null);
            GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer =
                    new GetLongFormatAggregatedReturnValueTransformer("count");
            Object deleteResultObject = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);

            if(deleteResultObject == null){
                throw new CoreRealmServiceRuntimeException();
            }else{
                commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount((Long)deleteResultObject);
                commonEntitiesOperationResultImpl.getOperationStatistics().
                        setOperationSummary("purgeAllEntities operation for conceptionKind "+this.conceptionKindName+" success.");
            }
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
    public ConceptionEntity getEntityByUID(String conceptionEntityUID) {
        if (conceptionEntityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(conceptionEntityUID), null, null);
                GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                        new GetSingleConceptionEntityTransformer(this.conceptionKindName, this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object resEntityRes = workingGraphOperationExecutor.executeRead(getSingleConceptionEntityTransformer, queryCql);
                return resEntityRes != null ? (ConceptionEntity) resEntityRes : null;
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
                String createCql = CypherBuilder.createNodesRelationshipByIdMatch(Long.parseLong(conceptionKindUID),Long.parseLong(attributesViewKindUID),
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
