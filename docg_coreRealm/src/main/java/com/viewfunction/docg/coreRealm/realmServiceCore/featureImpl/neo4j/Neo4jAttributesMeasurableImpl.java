package com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetMapFormatAggregatedReturnValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleConceptionEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import org.neo4j.driver.Result;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Neo4jAttributesMeasurableImpl implements AttributesMeasurable {

    private String entityUID;

    public Neo4jAttributesMeasurableImpl(String entityUID){
        this.entityUID = entityUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public boolean removeAttribute(String attributeName) throws CoreRealmServiceRuntimeException {
        return false;
    }

    @Override
    public List<AttributeValue> getAttributes() {
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),CypherBuilder.CypherFunctionType.PROPERTIES);
            GetMapFormatAggregatedReturnValueTransformer getMapFormatAggregatedReturnValueTransformer = new GetMapFormatAggregatedReturnValueTransformer("properties");
            Object resultRes = workingGraphOperationExecutor.executeRead(getMapFormatAggregatedReturnValueTransformer,queryCql);
            Map attributeValuesMap = (Map)resultRes;
            List<AttributeValue> attributeValueList = CommonOperationUtil.getAttributeValueList(attributeValuesMap);
            return attributeValueList;
        }
        return null;
    }

    @Override
    public boolean hasAttribute(String attributeName) {
        return false;
    }

    @Override
    public List<String> getAttributeNames() {
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),CypherBuilder.CypherFunctionType.KEYS);
            GetListFormatAggregatedReturnValueTransformer getListFormatAggregatedReturnValueTransformer = new GetListFormatAggregatedReturnValueTransformer("keys");
            Object resultRes = workingGraphOperationExecutor.executeRead(getListFormatAggregatedReturnValueTransformer,queryCql);
            List<String> returnAttributeNameList = (List<String>)resultRes;
            List<String> resultAttributeNameList = CommonOperationUtil.clearSystemBuiltinAttributeNames(returnAttributeNameList);
            return resultAttributeNameList;
        }
        return null;
    }

    @Override
    public AttributeValue getAttribute(String attributeName) {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, boolean attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, int attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, short attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, long attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, float attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, double attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Date attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, String attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, byte[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, byte attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, BigDecimal attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Boolean[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Integer[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Short[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Long[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Float[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Double[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Date[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, String[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Byte[][] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, BigDecimal[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, boolean attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, int attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, short attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, long attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, float attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, double attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Date attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, String attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, byte[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, byte attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, BigDecimal attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Boolean[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Integer[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Short[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Long[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Float[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Double[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Date[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, String[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Byte[][] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, BigDecimal[] attributeValue) throws CoreRealmServiceRuntimeException {
        return null;
    }

    @Override
    public List<String> addAttributes(Map<String, Object> properties) {
        return null;
    }

    @Override
    public List<String> updateAttributes(Map<String, Object> properties) {
        return null;
    }

    @Override
    public List<String> addNewOrUpdateAttributes(Map<String, Object> properties) {
        return null;
    }

    @Override
    public Object getInitAttribute(String attributeName) {
        return null;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
