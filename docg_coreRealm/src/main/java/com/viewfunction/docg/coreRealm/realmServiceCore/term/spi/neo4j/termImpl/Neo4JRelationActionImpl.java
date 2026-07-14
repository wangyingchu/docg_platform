package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction.RelationActionLogicExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributesViewKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JRelationAction;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Relationship;

import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Neo4JRelationActionImpl implements Neo4JRelationAction {

    private String actionName;
    private String actionDesc;
    private String actionImplementationClass;
    private String actionUID;

    public Neo4JRelationActionImpl(String actionName, String actionDesc, String actionUID, String actionImplementationClass){
        this.actionName = actionName;
        this.actionDesc = actionDesc;
        this.actionUID = actionUID;
        this.actionImplementationClass = actionImplementationClass;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public String getActionName() {
        return actionName;
    }

    @Override
    public String getActionDesc() {
        return actionDesc;
    }

    @Override
    public boolean updateActionDesc(String actionDesc) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String,Object> attributeDataMap = new HashMap<>();
            attributeDataMap.put(RealmConstant._DescProperty, actionDesc);
            ZonedDateTime currentDateTime = ZonedDateTime.now();
            attributeDataMap.put(RealmConstant._lastModifyDateProperty, currentDateTime);
            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.actionUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._DescProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.actionUID,false);
            AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(actionDesc)){
                this.actionDesc = actionDesc;
                return true;
            }else{
                return false;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public String getActionUID() {
        return actionUID;
    }

    @Override
    public String getActionImplementationClass() {
        return actionImplementationClass;
    }

    @Override
    public boolean updateActionImplementationClass(String actionImplementationClassFullName) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Map<String,Object> attributeDataMap = new HashMap<>();
            attributeDataMap.put(RealmConstant._actionImplementationClassProperty, actionImplementationClassFullName);
            ZonedDateTime currentDateTime = ZonedDateTime.now();
            attributeDataMap.put(RealmConstant._lastModifyDateProperty, currentDateTime);
            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.actionUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._actionImplementationClassProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.actionUID,false);
            AttributeValue resultAttributeValue = updateResultRes != null ? (AttributeValue) updateResultRes : null;
            if(resultAttributeValue != null && resultAttributeValue.getAttributeValue().toString().equals(actionImplementationClassFullName)){
                this.actionImplementationClass = actionImplementationClassFullName;
                return true;
            }else{
                return false;
            }
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Object executeActionSync(Map<String, Object> actionParameters) throws CoreRealmServiceRuntimeException {
        if(this.actionImplementationClass == null){
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("ActionImplementationClass is required");
            throw exception;
        }else{
            try {
                Class<?> actionLogicExecutorClass = Class.forName(this.actionImplementationClass);
                RelationActionLogicExecutor relationActionLogicExecutor =
                        (RelationActionLogicExecutor)actionLogicExecutorClass.getDeclaredConstructor().newInstance();
                return relationActionLogicExecutor.executeActionSync(actionParameters,this.getContainerRelationKind(),null);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public CompletableFuture<Object> executeActionAsync(Map<String, Object> actionParameters) throws CoreRealmServiceRuntimeException {
        if(this.actionImplementationClass == null){
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("ActionImplementationClass is required");
            throw exception;
        }else{
            try {
                Class<?> actionLogicExecutorClass = Class.forName(this.actionImplementationClass);
                RelationActionLogicExecutor relationActionLogicExecutor =
                        (RelationActionLogicExecutor)actionLogicExecutorClass.getDeclaredConstructor().newInstance();
                return relationActionLogicExecutor.executeActionAsync(actionParameters,this.getContainerRelationKind(),null);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Object executeActionSync(Map<String, Object> actionParameters, RelationEntity... relationEntity) throws CoreRealmServiceRuntimeException {
        if(this.actionImplementationClass == null){
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("ActionImplementationClass is required");
            throw exception;
        }else{
            try {
                Class<?> actionLogicExecutorClass = Class.forName(this.actionImplementationClass);
                RelationActionLogicExecutor relationActionLogicExecutor =
                        (RelationActionLogicExecutor)actionLogicExecutorClass.getDeclaredConstructor().newInstance();
                return relationActionLogicExecutor.executeActionSync(actionParameters,this.getContainerRelationKind(),relationEntity);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public CompletableFuture<Object> executeActionAsync(Map<String, Object> actionParameters, RelationEntity... relationEntity) throws CoreRealmServiceRuntimeException {
        if(this.actionImplementationClass == null){
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("ActionImplementationClass is required");
            throw exception;
        }else{
            try {
                Class<?> actionLogicExecutorClass = Class.forName(this.actionImplementationClass);
                RelationActionLogicExecutor relationActionLogicExecutor =
                        (RelationActionLogicExecutor)actionLogicExecutorClass.getDeclaredConstructor().newInstance();
                return relationActionLogicExecutor.executeActionAsync(actionParameters,this.getContainerRelationKind(),relationEntity);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public RelationKind getContainerRelationKind() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(actionUID),
                    RealmConstant.RelationKindClass,RealmConstant.RelationKind_ActionRelationClass, RelationDirection.FROM, null);
            GetListRelationKindTransformer getListRelationKindTransformer = new GetListRelationKindTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object relationKindsRes = workingGraphOperationExecutor.executeWrite(getListRelationKindTransformer,queryCql);
            if(relationKindsRes!= null){
                List<RelationKind> relationKindList = (List<RelationKind>)relationKindsRes;
                if(!relationKindList.isEmpty()){
                    return relationKindList.get(0);
                }
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public AttributesViewKind getReferencedAttributesViewKind() {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                    CypherBuilder.CypherFunctionType.ID, Long.parseLong(actionUID),
                    RealmConstant.AttributesViewKindClass,RealmConstant.Action_AttributesViewKindRelationClass, RelationDirection.TO, null);
            GetListAttributesViewKindTransformer getListAttributesViewKindTransformer = new GetListAttributesViewKindTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object attributesViewKindsRes = workingGraphOperationExecutor.executeWrite(getListAttributesViewKindTransformer,queryCql);
            if(attributesViewKindsRes!= null){
                List<AttributesViewKind> attributesViewKindList = (List<AttributesViewKind>)attributesViewKindsRes;
                if(!attributesViewKindList.isEmpty()){
                    return attributesViewKindList.get(0);
                }
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public boolean setReferencedAttributesViewKind(AttributesViewKind targetAttributesViewKind) throws CoreRealmServiceRuntimeException {
        if(targetAttributesViewKind == null){
            CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
            coreRealmServiceRuntimeException.setCauseMessage("targetAttributesViewKind is required.");
            throw coreRealmServiceRuntimeException;
        }
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                if(targetAttributesViewKind.getAttributesViewKindUID() == null){
                    CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
                    coreRealmServiceRuntimeException.setCauseMessage("targetAttributesViewKind UID is required.");
                    throw coreRealmServiceRuntimeException;
                }

                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(targetAttributesViewKind.getAttributesViewKindUID()), null, null);
                GetSingleAttributesViewKindTransformer getSingleAttributesViewKindTransformer =
                        new GetSingleAttributesViewKindTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object getAttributesViewKindRes = workingGraphOperationExecutor.executeRead(getSingleAttributesViewKindTransformer,queryCql);

                if(getAttributesViewKindRes == null){
                    CoreRealmServiceRuntimeException coreRealmServiceRuntimeException = new CoreRealmServiceRuntimeException();
                    coreRealmServiceRuntimeException.setCauseMessage("targetAttributesViewKind doesn't exist.");
                    throw coreRealmServiceRuntimeException;
                }

                QueryParameters relationshipQueryParameters = new QueryParameters();
                relationshipQueryParameters.setEntityKind(RealmConstant.Action_AttributesViewKindRelationClass);
                relationshipQueryParameters.setResultNumber(10000000);
                boolean ignoreDirection = false;
                String sourceNodeProperty = getEntityUID();
                String targetNodeProperty = null;

                queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,sourceNodeProperty,targetNodeProperty,ignoreDirection,relationshipQueryParameters, null);

                List<Object> relationEntitiesUIDList = new ArrayList<>();
                DataTransformer queryRelationshipOperationDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if (result.hasNext()) {
                            while (result.hasNext()) {
                                org.neo4j.driver.Record nodeRecord = result.next();
                                if (nodeRecord != null) {
                                    Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
                                    Long relationEntityUID = resultRelationship.id();
                                    relationEntitiesUIDList.add(relationEntityUID);
                                }
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(queryRelationshipOperationDataTransformer, queryCql);

                String detachCql = CypherBuilder.deleteRelationsWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, relationEntitiesUIDList);
                DataTransformer detachRelationshipOperationDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        List<String> resultEntitiesUIDList = new ArrayList<>();
                        if (result.hasNext()) {
                            while (result.hasNext()) {
                                org.neo4j.driver.Record nodeRecord = result.next();
                                if (nodeRecord != null) {
                                    Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
                                    Long relationEntityUID = resultRelationship.id();
                                    resultEntitiesUIDList.add("" + relationEntityUID);
                                }
                            }
                        }
                        return resultEntitiesUIDList;
                    }
                };
                workingGraphOperationExecutor.executeWrite(detachRelationshipOperationDataTransformer, detachCql);

                String createCql = CypherBuilder.createNodesRelationshipByIdsMatch(Long.parseLong(this.getEntityUID()),Long.parseLong(targetAttributesViewKind.getAttributesViewKindUID()),
                        RealmConstant.Action_AttributesViewKindRelationClass,null);
                GetSingleRelationEntityTransformer getSingleRelationEntityTransformer = new GetSingleRelationEntityTransformer
                        (RealmConstant.Action_AttributesViewKindRelationClass,getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                Object newRelationEntityRes = workingGraphOperationExecutor.executeWrite(getSingleRelationEntityTransformer, createCql);

                if(newRelationEntityRes != null){
                    return true;
                }else{
                    return false;
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                throw new RuntimeException(e);
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    @Override
    public void clearReferencedAttributesViewKind() {
        if(this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                QueryParameters relationshipQueryParameters = new QueryParameters();
                relationshipQueryParameters.setEntityKind(RealmConstant.Action_AttributesViewKindRelationClass);
                relationshipQueryParameters.setResultNumber(10000000);
                boolean ignoreDirection = false;
                String sourceNodeProperty = getEntityUID();
                String targetNodeProperty = null;

                String queryCql = CypherBuilder.matchRelationshipsWithQueryParameters(CypherBuilder.CypherFunctionType.ID,sourceNodeProperty,targetNodeProperty,ignoreDirection,relationshipQueryParameters, null);

                List<Object> relationEntitiesUIDList = new ArrayList<>();
                DataTransformer queryRelationshipOperationDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if (result.hasNext()) {
                            while (result.hasNext()) {
                                org.neo4j.driver.Record nodeRecord = result.next();
                                if (nodeRecord != null) {
                                    Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
                                    Long relationEntityUID = resultRelationship.id();
                                    relationEntitiesUIDList.add(relationEntityUID);
                                }
                            }
                        }
                        return null;
                    }
                };
                workingGraphOperationExecutor.executeRead(queryRelationshipOperationDataTransformer, queryCql);

                String detachCql = CypherBuilder.deleteRelationsWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, relationEntitiesUIDList);
                DataTransformer detachRelationshipOperationDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        List<String> resultEntitiesUIDList = new ArrayList<>();
                        if (result.hasNext()) {
                            while (result.hasNext()) {
                                Record nodeRecord = result.next();
                                if (nodeRecord != null) {
                                    Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
                                    Long relationEntityUID = resultRelationship.id();
                                    resultEntitiesUIDList.add("" + relationEntityUID);
                                }
                            }
                        }
                        return resultEntitiesUIDList;
                    }
                };
                workingGraphOperationExecutor.executeWrite(detachRelationshipOperationDataTransformer, detachCql);
            } catch (CoreRealmServiceEntityExploreException e) {
                throw new RuntimeException(e);
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public String getEntityUID() {
        return actionUID;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return graphOperationExecutorHelper;
    }
}
