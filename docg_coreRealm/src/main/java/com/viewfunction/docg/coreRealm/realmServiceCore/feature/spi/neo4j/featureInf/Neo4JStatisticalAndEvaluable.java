package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.StatisticalAndEvaluable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributeValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GroupNumericalAttributesStatisticResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.*;

public interface Neo4JStatisticalAndEvaluable extends StatisticalAndEvaluable,Neo4JKeyResourcesRetrievable{

    default Map<String,Number> statisticNumericalAttributes(QueryParameters queryParameters, Map<String, StatisticFunction> statisticConditions) throws CoreRealmServiceEntityExploreException {
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String checkCql = CypherBuilder.matchNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.getEntityUID()),new String[]{RealmConstant._NameProperty});
                GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._NameProperty);
                Object resultRes = workingGraphOperationExecutor.executeRead(getSingleAttributeValueTransformer,checkCql);
                String statisticTargetLabel = ((AttributeValue)resultRes).getAttributeValue().toString();

                QueryParameters realQueryParameters = queryParameters != null ?queryParameters:new QueryParameters();
                String statisticCql = CypherBuilder.statistNodesWithQueryParametersAndStatisticFunctions(statisticTargetLabel,realQueryParameters,statisticConditions,null);

                DataTransformer resultHandleDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        if(result.hasNext()){
                            Map<String,Number> resultStatisticMap = new HashMap<>();
                            Record returnRecord = result.next();
                            Map<String,Object> returnValueMap = returnRecord.asMap();
                            Set<String> keySet = returnValueMap.keySet();
                            for(String currentKey : keySet){
                                String currentStatisticKey = currentKey.replace(CypherBuilder.operationResultName+".","");
                                Number currentStatisticValue = (Number)returnValueMap.get(currentKey);
                                resultStatisticMap.put(currentStatisticKey,currentStatisticValue);
                            }
                            return resultStatisticMap;
                        }
                        return null;
                    }
                };

                Object statisticCqlRes = workingGraphOperationExecutor.executeRead(resultHandleDataTransformer,statisticCql);
                if(statisticCqlRes != null){
                    return (Map<String,Number>)statisticCqlRes;
                }
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }

    default List<GroupNumericalAttributesStatisticResult> statisticNumericalAttributesByGroup(String groupByAttribute,
                                                                                              QueryParameters queryParameters,
                                                                                              Map<String, StatisticFunction> statisticCondition)  throws CoreRealmServiceEntityExploreException{
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String checkCql = CypherBuilder.matchNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.getEntityUID()),new String[]{RealmConstant._NameProperty});
                GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._NameProperty);
                Object resultRes = workingGraphOperationExecutor.executeRead(getSingleAttributeValueTransformer,checkCql);
                String statisticTargetLabel = ((AttributeValue)resultRes).getAttributeValue().toString();

                QueryParameters realQueryParameters = queryParameters != null ?queryParameters:new QueryParameters();
                String statisticCql = CypherBuilder.statistNodesWithQueryParametersAndStatisticFunctions(statisticTargetLabel,realQueryParameters,statisticCondition,groupByAttribute);

                DataTransformer resultHandleDataTransformer = new DataTransformer() {
                    @Override
                    public Object transformResult(Result result) {
                        List<GroupNumericalAttributesStatisticResult> resultList = new ArrayList<>();
                        while(result.hasNext()){
                            Object groupValueObject = null;
                            Map<String,Number> resultStatisticMap = new HashMap<>();
                            Record returnRecord = result.next();
                            Map<String,Object> returnValueMap = returnRecord.asMap();
                            Set<String> keySet = returnValueMap.keySet();

                            for(String currentKey : keySet){
                                if(currentKey.equals(CypherBuilder.operationResultName+"."+groupByAttribute)){
                                    groupValueObject = returnValueMap.get(currentKey);
                                }else{
                                    String currentStatisticKey = currentKey.replace(CypherBuilder.operationResultName+".","");
                                    Number currentStatisticValue = (Number)returnValueMap.get(currentKey);
                                    resultStatisticMap.put(currentStatisticKey,currentStatisticValue);
                                }
                            }
                            GroupNumericalAttributesStatisticResult groupNumericalAttributesStatisticResult
                                    = new GroupNumericalAttributesStatisticResult(groupValueObject,resultStatisticMap);
                            resultList.add(groupNumericalAttributesStatisticResult);
                        }
                        return resultList;
                    }
                };

                Object statisticCqlRes = workingGraphOperationExecutor.executeRead(resultHandleDataTransformer,statisticCql);
                if(statisticCqlRes != null){
                    return (List<GroupNumericalAttributesStatisticResult>)statisticCqlRes;
                }
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
        }
        return null;
    }
}
