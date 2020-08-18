package com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.CheckResultExistenceTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributeKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributesViewKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Neo4JCoreRealmImpl implements CoreRealm {

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
            String createCql = CypherBuilder.createLabeledNodeWithProperties(RealmConstant.ConceptionKindClass,propertiesMap);
            GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                    new GetSingleConceptionKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,createCql);
            return createConceptionKindRes != null ? (ConceptionKind)createConceptionKindRes : null;
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
            String createCql = CypherBuilder.createLabeledNodeWithProperties(RealmConstant.AttributesViewKindClass,propertiesMap);

            GetSingleAttributesViewKindTransformer getSingleAttributesViewKindTransformer =
                    new GetSingleAttributesViewKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributesViewKindTransformer,createCql);
            return createAttributesViewKindRes != null ? (AttributesViewKind)createAttributesViewKindRes : null;
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
                if(deletedAttributesViewKindRes == null){
                    throw new CoreRealmServiceRuntimeException();
                }else{
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
    public AttributeKind getAttributeKind(String attributeKindUID) {
        return null;
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
            String createCql = CypherBuilder.createLabeledNodeWithProperties(RealmConstant.AttributeKindClass,propertiesMap);

            GetSingleAttributeKindTransformer getSingleAttributeKindTransformer =
                    new GetSingleAttributeKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
            Object createAttributesViewKindRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeKindTransformer,createCql);
            return createAttributesViewKindRes != null ? (AttributeKind)createAttributesViewKindRes : null;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeAttributeKind(String attributeKindUID) throws CoreRealmServiceRuntimeException {
        return false;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
