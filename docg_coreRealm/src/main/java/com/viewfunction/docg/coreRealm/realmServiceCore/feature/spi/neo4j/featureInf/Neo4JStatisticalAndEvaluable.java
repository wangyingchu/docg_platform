package com.viewfunction.docg.coreRealm.realmServiceCore.feature.spi.neo4j.featureInf;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.feature.StatisticalAndEvaluable;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListObjectValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAttributeValueTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GroupNumericalAttributesStatisticResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.NumericalAttributeStatisticCondition;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.*;

public interface Neo4JStatisticalAndEvaluable extends StatisticalAndEvaluable,Neo4JKeyResourcesRetrievable{

    default Map<String,Number> statisticNumericalAttributes(QueryParameters queryParameters, List<NumericalAttributeStatisticCondition> statisticConditions) throws CoreRealmServiceEntityExploreException {
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
                                                                                              List<NumericalAttributeStatisticCondition> statisticConditions)  throws CoreRealmServiceEntityExploreException{
        if (this.getEntityUID() != null) {
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String checkCql = CypherBuilder.matchNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.getEntityUID()),new String[]{RealmConstant._NameProperty});
                GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._NameProperty);
                Object resultRes = workingGraphOperationExecutor.executeRead(getSingleAttributeValueTransformer,checkCql);
                String statisticTargetLabel = ((AttributeValue)resultRes).getAttributeValue().toString();

                QueryParameters realQueryParameters = queryParameters != null ?queryParameters:new QueryParameters();
                String statisticCql = "";
                if(this instanceof ConceptionKind){
                    statisticCql = CypherBuilder.statistNodesWithQueryParametersAndStatisticFunctions(statisticTargetLabel,realQueryParameters,statisticConditions,groupByAttribute);
                }
                if(this instanceof RelationKind){
                    statisticCql = CypherBuilder.statistRelationsWithQueryParametersAndStatisticFunctions(statisticTargetLabel,realQueryParameters,statisticConditions,groupByAttribute);
                }

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

    default public Map<String,List<ConceptionEntity>> statisticRelatedClassifications(QueryParameters queryParameters, String relationKindName, RelationDirection relationDirection){
        if (this.getEntityUID() != null) {
            Map<String,List<ConceptionEntity>> resultMap = new HashMap<>();
            GraphOperationExecutor workingGraphOperationExecutor = getGraphOperationExecutorHelper().getWorkingGraphOperationExecutor();
            try {
                String checkCql = CypherBuilder.matchNodePropertiesWithSingleValueEqual(CypherBuilder.CypherFunctionType.ID,Long.parseLong(this.getEntityUID()),new String[]{RealmConstant._NameProperty});
                GetSingleAttributeValueTransformer getSingleAttributeValueTransformer = new GetSingleAttributeValueTransformer(RealmConstant._NameProperty);
                Object resultRes = workingGraphOperationExecutor.executeRead(getSingleAttributeValueTransformer,checkCql);
                String statisticTargetLabel = ((AttributeValue)resultRes).getAttributeValue().toString();

                QueryParameters inputQueryParameters;
                if(queryParameters != null){
                    inputQueryParameters = queryParameters;
                }else{
                    inputQueryParameters = new QueryParameters();
                    inputQueryParameters.setResultNumber(10000000);
                }
                String queryEntitiesIDCql = CypherBuilder.matchNodesWithQueryParameters(statisticTargetLabel,inputQueryParameters, CypherBuilder.CypherFunctionType.ID);
                GetListObjectValueTransformer<Long> longListValueTransformer = new GetListObjectValueTransformer<Long>("id");
                Object idList = workingGraphOperationExecutor.executeRead(longListValueTransformer,queryEntitiesIDCql);

                List<Long> resultEntitiesIDList = idList != null? (List<Long>)idList : null;

                if(resultEntitiesIDList != null){
                    String queryPairsCql = CypherBuilder.matchRelatedPairFromSpecialStartNodes(CypherBuilder.CypherFunctionType.ID,
                            CommonOperationUtil.formatListLiteralValue(resultEntitiesIDList),RealmConstant.ClassificationClass,relationKindName,relationDirection);
                    DataTransformer pairRelationDataTransformer = new DataTransformer() {
                        @Override
                        public Object transformResult(Result result) {
                            while(result.hasNext()){
                                Record record = result.next();
                                if(record.containsKey(CypherBuilder.operationResultName) && record.containsKey(CypherBuilder.sourceNodeName)){
                                    Node classificationNode = record.get(CypherBuilder.operationResultName).asNode();
                                    Node entityNode = record.get(CypherBuilder.sourceNodeName).asNode();
                                    List<String> classificationLabelNames = Lists.newArrayList(classificationNode.labels());
                                    List<String> entityLabelNames = Lists.newArrayList(entityNode.labels());

                                    if(classificationLabelNames.contains(RealmConstant.ClassificationClass) && entityLabelNames.contains(statisticTargetLabel)){
                                        String classificationName = classificationNode.get(RealmConstant._NameProperty).asString();
                                        if(!resultMap.containsKey(classificationName)){
                                            resultMap.put(classificationName,new ArrayList<ConceptionEntity>());
                                        }
                                        List<ConceptionEntity> classificationRelatedEntityList = resultMap.get(classificationName);

                                        long nodeUID = entityNode.id();
                                        String conceptionEntityUID = ""+nodeUID;
                                        String resultConceptionKindName = statisticTargetLabel != null ? statisticTargetLabel : entityLabelNames.get(0);
                                        Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                                                new Neo4JConceptionEntityImpl(resultConceptionKindName,conceptionEntityUID);
                                        neo4jConceptionEntityImpl.setAllConceptionKindNames(entityLabelNames);
                                        neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                                        classificationRelatedEntityList.add(neo4jConceptionEntityImpl);
                                    }
                                }
                            }
                            return null;
                        }
                    };
                    workingGraphOperationExecutor.executeRead(pairRelationDataTransformer,queryPairsCql);
                }
            } catch (CoreRealmServiceEntityExploreException e) {
                e.printStackTrace();
            } finally {
                getGraphOperationExecutorHelper().closeWorkingGraphOperationExecutor();
            }
            return resultMap;
        }
        return null;
    }

    default void evaluateEntityAttributesDistribution(long sampleCount){
        /*
        Example:
        https://neo4j.com/labs/apoc/4.1/overview/apoc.meta/apoc.meta.nodeTypeProperties/
        https://neo4j.com/labs/apoc/4.1/overview/apoc.meta/apoc.meta.relTypeProperties/
        */


    }
}
