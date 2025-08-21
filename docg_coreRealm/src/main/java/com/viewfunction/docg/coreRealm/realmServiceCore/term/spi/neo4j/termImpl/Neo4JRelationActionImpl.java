package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction.ConceptionActionLogicExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.external.customizedAction.RelationActionLogicExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributeValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JRelationAction;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Neo4JRelationActionImpl implements Neo4JRelationAction {

    private static Logger logger = LoggerFactory.getLogger(Neo4JConceptionActionImpl.class);

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
            String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.actionUID),attributeDataMap);
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._actionImplementationClassProperty);
            Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
            CommonOperationUtil.updateEntityMetaAttributes(workingGraphOperationExecutor,this.actionUID,false);
            AttributeValue resultAttributeValue =  updateResultRes != null ? (AttributeValue) updateResultRes : null;
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
                RelationActionLogicExecutor conceptionActionLogicExecutor =
                        (RelationActionLogicExecutor)actionLogicExecutorClass.getDeclaredConstructor().newInstance();
                return conceptionActionLogicExecutor.executeActionSync(actionParameters,this.getContainerRelationKind(),null);
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
                ConceptionActionLogicExecutor conceptionActionLogicExecutor =
                        (ConceptionActionLogicExecutor)actionLogicExecutorClass.getDeclaredConstructor().newInstance();
                return conceptionActionLogicExecutor.executeActionAsync(actionParameters,this.getContainerConceptionKind(),null);
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
    public Object executeActionSync(Map<String, Object> actionParameters, ConceptionEntity... conceptionEntity) throws CoreRealmServiceRuntimeException {
        if(this.actionImplementationClass == null){
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("ActionImplementationClass is required");
            throw exception;
        }else{
            try {
                Class<?> actionLogicExecutorClass = Class.forName(this.actionImplementationClass);
                ConceptionActionLogicExecutor conceptionActionLogicExecutor =
                        (ConceptionActionLogicExecutor)actionLogicExecutorClass.getDeclaredConstructor().newInstance();
                return conceptionActionLogicExecutor.executeActionSync(actionParameters,this.getContainerConceptionKind(),conceptionEntity);
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
    public CompletableFuture<Object> executeActionAsync(Map<String, Object> actionParameters, ConceptionEntity... conceptionEntity) throws CoreRealmServiceRuntimeException {
        if(this.actionImplementationClass == null){
            CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
            exception.setCauseMessage("ActionImplementationClass is required");
            throw exception;
        }else{
            try {
                Class<?> actionLogicExecutorClass = Class.forName(this.actionImplementationClass);
                ConceptionActionLogicExecutor conceptionActionLogicExecutor =
                        (ConceptionActionLogicExecutor)actionLogicExecutorClass.getDeclaredConstructor().newInstance();
                return conceptionActionLogicExecutor.executeActionAsync(actionParameters,this.getContainerConceptionKind(),conceptionEntity);
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
                    RealmConstant.ConceptionKindClass,RealmConstant.ConceptionKind_ActionRelationClass, RelationDirection.FROM, null);
            GetListConceptionKindTransformer getListConceptionKindTransformer = new GetListConceptionKindTransformer(null,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object conceptionKindsRes = workingGraphOperationExecutor.executeWrite(getListConceptionKindTransformer,queryCql);
            if(conceptionKindsRes!= null){
                List<ConceptionKind> conceptionKindList = (List<ConceptionKind>)conceptionKindsRes;
                if(!conceptionKindList.isEmpty()){
                    return conceptionKindList.get(0);
                }
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
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




























    @Override
    public Object executeActionSync(Map<String, Object> actionParameters, RelationEntity... relationEntity) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public CompletableFuture<Object> executeActionAsync(Map<String, Object> actionParameters, RelationEntity... relationEntity) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public RelationKind getContainerRelationKind() {
        return null;
    }
}
