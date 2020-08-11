package com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmFunctionNotSupportedException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.CheckResultExistenceTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionKindTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.CoreRealmStorageImplTech;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import java.util.HashMap;
import java.util.Map;

public class Neo4JCoreRealmImpl implements CoreRealm {

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
        String queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.ConceptionKindClass,RealmConstant._NameProperty,conceptionKindName,1);

        GetSingleConceptionKindTransformer getSingleConceptionKindTransformer =
                new GetSingleConceptionKindTransformer(coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
        Object createConceptionKindRes = workingGraphOperationExecutor.executeWrite(getSingleConceptionKindTransformer,queryCql);
        this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();

        return createConceptionKindRes != null?(ConceptionKind)createConceptionKindRes:null;
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName,String conceptionKindDesc) {
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
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
        this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();

        return createConceptionKindRes != null ? (ConceptionKind)createConceptionKindRes:null;
    }

    @Override
    public ConceptionKind createConceptionKind(String conceptionKindName, String conceptionKindDesc, String parentConceptionKindName) throws CoreRealmFunctionNotSupportedException {
        CoreRealmFunctionNotSupportedException exception = new CoreRealmFunctionNotSupportedException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
