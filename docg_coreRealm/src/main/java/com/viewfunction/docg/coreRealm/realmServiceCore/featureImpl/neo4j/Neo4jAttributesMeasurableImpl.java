package com.viewfunction.docg.coreRealm.realmServiceCore.featureImpl.neo4j;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.AttributesMeasurable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class Neo4jAttributesMeasurableImpl implements AttributesMeasurable {

    private static Logger logger = LoggerFactory.getLogger(Neo4jAttributesMeasurableImpl.class);
    private String entityUID;

    public Neo4jAttributesMeasurableImpl(String entityUID){
        this.entityUID = entityUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public boolean removeAttribute(String attributeName) throws CoreRealmServiceRuntimeException {
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(this.entityUID), CypherBuilder.CypherFunctionType.EXISTS, attributeName);
                GetBooleanFormatAggregatedReturnValueTransformer getBooleanFormatAggregatedReturnValueTransformer = new GetBooleanFormatAggregatedReturnValueTransformer("exists", attributeName);
                Object resultRes = workingGraphOperationExecutor.executeRead(getBooleanFormatAggregatedReturnValueTransformer, queryCql);
                boolean existCheckKResult = resultRes != null ? ((Boolean) resultRes).booleanValue() : false;
                if (!existCheckKResult) {
                    logger.error("Attribute {} of entity with UID {} does not exist.", attributeName, this.entityUID);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("Attribute " + attributeName + " of entity with UID " + this.entityUID + " does not exist.");
                    throw exception;
                }else{
                    List<String> targetAttributeNameList = new ArrayList<>();
                    targetAttributeNameList.add(attributeName);
                    String deleteCql = CypherBuilder.removeNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),targetAttributeNameList);
                    GetListFormatAggregatedReturnValueTransformer getListFormatAggregatedReturnValueTransformer = new GetListFormatAggregatedReturnValueTransformer("keys");
                    Object removeResultRes = workingGraphOperationExecutor.executeWrite(getListFormatAggregatedReturnValueTransformer,deleteCql);
                    List<String> returnAttributeNameList = (List<String>)removeResultRes;
                    if(returnAttributeNameList.contains(attributeName)){
                        return false;
                    }else{
                        return true;
                    }
                }
            }finally{
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return false;
    }

    @Override
    public List<AttributeValue> getAttributes() {
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),CypherBuilder.CypherFunctionType.PROPERTIES,null);
            GetMapFormatAggregatedReturnValueTransformer getMapFormatAggregatedReturnValueTransformer = new GetMapFormatAggregatedReturnValueTransformer("properties");
            Object resultRes = workingGraphOperationExecutor.executeRead(getMapFormatAggregatedReturnValueTransformer,queryCql);
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            Map attributeValuesMap = (Map)resultRes;
            List<AttributeValue> attributeValueList = CommonOperationUtil.getAttributeValueList(attributeValuesMap);
            return attributeValueList;
        }
        return null;
    }

    @Override
    public boolean hasAttribute(String attributeName) {
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(this.entityUID), CypherBuilder.CypherFunctionType.EXISTS, attributeName);
            GetBooleanFormatAggregatedReturnValueTransformer getBooleanFormatAggregatedReturnValueTransformer = new GetBooleanFormatAggregatedReturnValueTransformer("exists",attributeName);
            Object resultRes = workingGraphOperationExecutor.executeRead(getBooleanFormatAggregatedReturnValueTransformer,queryCql);
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            return resultRes != null ? (Boolean)resultRes : false;
        }
        return false;
    }

    @Override
    public List<String> getAttributeNames() {
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),CypherBuilder.CypherFunctionType.KEYS,null);
            GetListFormatAggregatedReturnValueTransformer getListFormatAggregatedReturnValueTransformer = new GetListFormatAggregatedReturnValueTransformer("keys");
            Object resultRes = workingGraphOperationExecutor.executeRead(getListFormatAggregatedReturnValueTransformer,queryCql);
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            List<String> returnAttributeNameList = (List<String>)resultRes;
            List<String> resultAttributeNameList = CommonOperationUtil.clearSystemBuiltinAttributeNames(returnAttributeNameList);
            return resultAttributeNameList;
        }
        return null;
    }

    @Override
    public AttributeValue getAttribute(String attributeName) {
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            String queryCql = CypherBuilder.matchNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),new String[]{attributeName});
            GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(attributeName);
            Object resultRes = workingGraphOperationExecutor.executeRead(getSingleAttributeValueTransformer,queryCql);
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            return resultRes != null?(AttributeValue)resultRes : null;
        }
        return null;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, boolean attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,Boolean.valueOf(attributeValue));
    }

    @Override
    public AttributeValue addAttribute(String attributeName, int attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,Integer.valueOf(attributeValue));
    }

    @Override
    public AttributeValue addAttribute(String attributeName, short attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,Short.valueOf(attributeValue));
    }

    @Override
    public AttributeValue addAttribute(String attributeName, long attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,Long.valueOf(attributeValue));
    }

    @Override
    public AttributeValue addAttribute(String attributeName, float attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,Float.valueOf(attributeValue));
    }

    @Override
    public AttributeValue addAttribute(String attributeName, double attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,Double.valueOf(attributeValue));
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Date attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, String attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, byte[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, byte attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,Byte.valueOf(attributeValue));
    }

    @Override
    public AttributeValue addAttribute(String attributeName, BigDecimal attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Boolean[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Integer[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Short[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Long[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Float[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Double[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Date[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, String[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue addAttribute(String attributeName, Byte[][] attributeValue) throws CoreRealmServiceRuntimeException {
        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public AttributeValue addAttribute(String attributeName, BigDecimal[] attributeValue) throws CoreRealmServiceRuntimeException {
        return setAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, boolean attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,Boolean.valueOf(attributeValue));
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, int attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,Integer.valueOf(attributeValue));
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, short attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,Short.valueOf(attributeValue));
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, long attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,Long.valueOf(attributeValue));
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, float attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,Float.valueOf(attributeValue));
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, double attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,Double.valueOf(attributeValue));
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Date attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, String attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, byte[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, byte attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,Byte.valueOf(attributeValue));
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, BigDecimal attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Boolean[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Integer[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Short[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Long[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Float[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Double[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Date[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, String[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, Byte[][] attributeValue) throws CoreRealmServiceRuntimeException {
        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
        exception.setCauseMessage("Neo4J storage implements doesn't support this function");
        throw exception;
    }

    @Override
    public AttributeValue updateAttribute(String attributeName, BigDecimal[] attributeValue) throws CoreRealmServiceRuntimeException {
        return checkAndUpdateAttribute(attributeName,attributeValue);
    }

    @Override
    public List<String> addAttributes(Map<String, Object> properties) {
        if (this.entityUID != null && properties != null && properties.size() > 0) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(this.entityUID), CypherBuilder.CypherFunctionType.KEYS, null);
                GetListFormatAggregatedReturnValueTransformer getListFormatAggregatedReturnValueTransformer = new GetListFormatAggregatedReturnValueTransformer("keys");
                Object resultRes = workingGraphOperationExecutor.executeRead(getListFormatAggregatedReturnValueTransformer, queryCql);
                List<String> returnAttributeNameList = (List<String>) resultRes;
                Set<String> newDataKeys = properties.keySet();
                List<String> realTargetAttributeKeys = new ArrayList<>();
                for(String currentKey:newDataKeys){
                    if(!returnAttributeNameList.contains(currentKey)){
                        realTargetAttributeKeys.add(currentKey);
                    }
                }



                String createCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),properties);
                //GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(attributeName);
                //Object resultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,createCql);






            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }







           // List<String> resultAttributeNameList = CommonOperationUtil.clearSystemBuiltinAttributeNames(returnAttributeNameList);
           // return resultAttributeNameList;

        }
        return null;










    }

    @Override
    public List<String> updateAttributes(Map<String, Object> properties) {
        return null;
    }

    @Override
    public List<String> addNewOrUpdateAttributes(Map<String, Object> properties) {
        if (this.entityUID != null) {

        }
        return null;
    }

    private AttributeValue setAttribute(String attributeName, Object attributeValue) throws CoreRealmServiceRuntimeException{
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodeWithSingleFunctionValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(this.entityUID), CypherBuilder.CypherFunctionType.EXISTS, attributeName);
                GetBooleanFormatAggregatedReturnValueTransformer getBooleanFormatAggregatedReturnValueTransformer = new GetBooleanFormatAggregatedReturnValueTransformer("exists",attributeName);
                Object checkExistResultRes = workingGraphOperationExecutor.executeRead(getBooleanFormatAggregatedReturnValueTransformer,queryCql);
                boolean checkExistResult = checkExistResultRes != null? ((Boolean)checkExistResultRes).booleanValue() : false;
                if(checkExistResult){
                    logger.error("Attribute {} of entity with UID {} already exist.", attributeName, this.entityUID);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("Attribute " + attributeName + " of entity with UID " + this.entityUID + " already exist.");
                    throw exception;
                }else{
                    Map<String,Object> attributeDataMap = new HashMap<>();
                    attributeDataMap.put(attributeName,attributeValue);
                    String createCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),attributeDataMap);
                    GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(attributeName);
                    Object resultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,createCql);
                    return resultRes != null?(AttributeValue)resultRes : null;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    private AttributeValue checkAndUpdateAttribute(String attributeName, Object attributeValue) throws CoreRealmServiceRuntimeException {
        if (this.entityUID != null) {
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try {
                String queryCql = CypherBuilder.matchNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID, Long.parseLong(this.entityUID), new String[]{attributeName});
                GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(attributeName);
                Object resultRes = workingGraphOperationExecutor.executeRead(getSingleAttributeValueTransformer, queryCql);
                if (resultRes != null) {
                    AttributeValue originalAttributeValue = (AttributeValue) resultRes;
                    AttributeDataType originalAttributeDataType = originalAttributeValue.getAttributeDataType();
                    boolean isValidatedFormat = CommonOperationUtil.validateValueFormat(originalAttributeDataType, attributeValue);
                    if(!isValidatedFormat){
                        logger.error("Attribute  data type does not match {} of entity with UID {}.", attributeName, this.entityUID);
                        CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                        exception.setCauseMessage("Attribute data type does not match " + attributeName + " of entity with UID " + this.entityUID +".");
                        throw exception;
                    }else{
                        Map<String,Object> attributeDataMap = new HashMap<>();
                        attributeDataMap.put(attributeName,attributeValue);
                        String updateCql = CypherBuilder.setNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.entityUID),attributeDataMap);
                        Object updateResultRes = workingGraphOperationExecutor.executeWrite(getSingleAttributeValueTransformer,updateCql);
                        return updateResultRes != null ? (AttributeValue) updateResultRes : null;
                    }
                }else {
                    logger.error("Attribute {} of entity with UID {} does not exist.", attributeName, this.entityUID);
                    CoreRealmServiceRuntimeException exception = new CoreRealmServiceRuntimeException();
                    exception.setCauseMessage("Attribute " + attributeName + " of entity with UID " + this.entityUID + " does not exist.");
                    throw exception;
                }
            } finally {
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
}
