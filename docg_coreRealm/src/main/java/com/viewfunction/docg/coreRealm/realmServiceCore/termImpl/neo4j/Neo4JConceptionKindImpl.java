package com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetLongFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4JConceptionKindImpl implements ConceptionKind {

    private String coreRealmName;
    private String conceptionKindName;
    private String conceptionKindDesc;
    private String conceptionKindUID;

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
        String queryCql = CypherBuilder.matchLabelWithSinglePropertyValueAndFunction(getConceptionKindName(), CypherBuilder.CypherFunctionType.COUNT,null,null);
        GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer = new GetLongFormatAggregatedReturnValueTransformer("count");
        Object countConceptionEntitiesRes = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,queryCql);
        this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        if(countConceptionEntitiesRes == null){
            throw new CoreRealmServiceRuntimeException();
        }else{
            return (Long)countConceptionEntitiesRes;
        }
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
    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue, boolean addPerDefinedRelation) {
        if (conceptionEntityValue != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            Map<String,Object> propertiesMap = conceptionEntityValue.getEntityAttributesValue() != null?
                    conceptionEntityValue.getEntityAttributesValue():new HashMap<>();
            CommonOperationUtil.generateEntityMetaAttributes(propertiesMap);
            String createCql = CypherBuilder.createLabeledNodeWithProperties(this.conceptionKindName,propertiesMap);
            GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                    new GetSingleConceptionEntityTransformer(this.conceptionKindName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,createCql);
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            return newEntityRes != null ? (ConceptionEntity)newEntityRes:null;
        }
        return null;
    }

    @Override
    public ConceptionEntity newEntity(ConceptionEntityValue conceptionEntityValue, List<RelationAttachKind> relationAttachKindList) {
        return null;
    }

    @Override
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, boolean addPerDefinedRelation) {
        return null;
    }

    @Override
    public EntitiesOperationResult newEntities(List<ConceptionEntityValue> conceptionEntityValues, List<RelationAttachKind> relationAttachKindList) {
        return null;
    }

    @Override
    public ConceptionEntity updateEntity(ConceptionEntityValue conceptionEntityValueForUpdate) {
        return null;
    }

    @Override
    public EntitiesOperationResult updateEntities(List<ConceptionEntityValue> entityValues) {
        return null;
    }

    @Override
    public boolean deleteEntity(String conceptionEntityUID) {
        return false;
    }

    @Override
    public EntitiesOperationResult deleteEntities(List<String> conceptionEntityUIDs) {
        return null;
    }

    @Override
    public EntitiesOperationResult purgeAllEntities() throws CoreRealmServiceRuntimeException{
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        String deleteCql = CypherBuilder.deleteLabelWithSinglePropertyValueAndFunction(this.conceptionKindName,
                CypherBuilder.CypherFunctionType.COUNT,null,null);
        GetLongFormatAggregatedReturnValueTransformer getLongFormatAggregatedReturnValueTransformer =
                new GetLongFormatAggregatedReturnValueTransformer("count");
        Object deleteResultObject = workingGraphOperationExecutor.executeWrite(getLongFormatAggregatedReturnValueTransformer,deleteCql);


        System.out.println(deleteResultObject);
        System.out.println(deleteResultObject);
        System.out.println(deleteResultObject);
        System.out.println(deleteResultObject);
        /*
        workingGraphOperationExecutor.executeWrite(new DataTransformer() {
            @Override
            public Object transformResult(Result result) {


                if(result.hasNext()){
                    Record returnValue = result.next();
                    System.out.println(returnValue.asMap());

                }

                System.out.println(result.hasNext());


                return null;
            }
        },deleteCql);
        */

        this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();


        if(deleteResultObject == null){
            throw new CoreRealmServiceRuntimeException();
        }else{

            EntitiesOperationResult EntitiesOperationResult = new EntitiesOperationResult() {
                @Override
                public List<String> getSuccessEntityUIDs() {
                    return null;
                }

                @Override
                public EntitiesOperationStatistics getOperationStatistics() {
                    return null;
                }
            };


        }



        /*

        String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(conceptionEntityUID),null);
        GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                new GetSingleConceptionEntityTransformer(this.conceptionKindName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
        Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,queryCql);

        //return newEntityRes != null ? (ConceptionEntity)newEntityRes:null;
        */

        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getEntities(QueryParameters queryParameters) {
        return null;
    }

    @Override
    public ConceptionEntity getEntityByUID(String conceptionEntityUID) {
        if (conceptionEntityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(conceptionEntityUID),null);
            GetSingleConceptionEntityTransformer getSingleConceptionEntityTransformer =
                    new GetSingleConceptionEntityTransformer(this.conceptionKindName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object newEntityRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionEntityTransformer,queryCql);
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            return newEntityRes != null ? (ConceptionEntity)newEntityRes:null;
        }
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByViewKinds(List<String> attributesViewKindNames, QueryParameters exploreParameters) {
        return null;
    }

    @Override
    public ConceptionEntitiesAttributesRetrieveResult getSingleValueEntityAttributesByAttributeNames(List<String> attributeNames, QueryParameters exploreParameters) {
        return null;
    }

    @Override
    public boolean addAttributesViewKind(String AttributesViewKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public AttributesViewKind getAttributesViewKind(String attributesViewKindName) {
        return null;
    }

    @Override
    public boolean removeAttributesViewKind(String AttributesViewKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<AttributesViewKind> getContainedAttributesViewKinds() {
        return null;
    }

    @Override
    public List<AttributeKind> getSingleValueAttributeKinds() {
        return null;
    }

    @Override
    public AttributeKind getSingleValueAttributeKind(String AttributeKindName) {
        return null;
    }

    @Override
    public RelationEntity attachClassificationEntity(RelationAttachInfo relationAttachInfo, String classificationEntityUID) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public boolean detachClassificationEntity(String classificationEntityUID, String relationKindName, RelationDirection relationDirection) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<ClassificationEntity> getAttachedClassificationEntities(String relationKindName, RelationDirection relationDirection) {
        return null;
    }

    @Override
    public Date getCreateDateTime() {
        return null;
    }

    @Override
    public Date getLastModifyDateTime() {
        return null;
    }

    @Override
    public String getCreatorId() {
        return null;
    }

    @Override
    public String getDataOrigin() {
        return null;
    }

    @Override
    public boolean updateModifyDate() {
        return false;
    }

    @Override
    public boolean updateCreatorId() {
        return false;
    }

    @Override
    public boolean updateDataOrigin() {
        return false;
    }

    @Override
    public boolean addOrUpdateMetaConfigItem(String itemName, Object itemValue) {
        return false;
    }

    @Override
    public Map<String, Object> getMetaConfigItems() {
        return null;
    }

    @Override
    public Object getMetaConfigItem(String itemName) {
        return null;
    }

    @Override
    public boolean deleteMetaConfigItem(String itemName) {
        return false;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
