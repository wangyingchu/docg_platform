package com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.MetaConfigItemFeatureSupportable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.Map;

public class Neo4jMetaConfigItemFeatureSupportableImpl implements MetaConfigItemFeatureSupportable {

    private String entityUID;

    public Neo4jMetaConfigItemFeatureSupportableImpl(String entityUID){
        this.entityUID = entityUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
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
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String storageNodeQueryCql = CypherBuilder.matchRelatedNodesFromSpecialStartNodes(
                        CypherBuilder.CypherFunctionType.ID, Long.parseLong(entityUID),
                        RealmConstant.MetaConfigItemsStorageClass,RealmConstant.Kind_MetaConfigItemsStorageRelationClass, RelationDirection.TO, null);





            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    @Override
    public boolean deleteMetaConfigItem(String itemName) {
        return false;
    }

    private Object getAttributeValue(String attributeName){
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),new String[]{attributeName});
                DataTransformer dataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Record returnRecord = result.next();
                            Map<String,Object> returnValueMap = returnRecord.asMap();
                            String attributeNameFullName= CypherBuilder.operationResultName+"."+ attributeName;
                            Object attributeValueObject = returnValueMap.get(attributeNameFullName);
                            if(attributeValueObject!= null){
                                return attributeValueObject;
                            }
                        }
                        return null;
                    }
                };
                Object resultRes = workingGraphOperationExecutor.executeRead(dataTransformer,queryCql);
                return resultRes;
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
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
        return null;
    }

    @Override
    public GraphOperationExecutorHelper getGraphOperationExecutorHelper() {
        return null;
    }
}
