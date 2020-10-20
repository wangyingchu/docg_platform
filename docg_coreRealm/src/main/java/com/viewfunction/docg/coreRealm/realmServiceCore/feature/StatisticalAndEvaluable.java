package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GroupNumericalAttributesStatisticResult;

import java.util.List;
import java.util.Map;

public interface StatisticalAndEvaluable {

    public enum StatisticFunction {
        COUNT,AVG,MAX,MIN,STDEV,SUM
    }

    public enum EvaluateFunction {}

    public Map<String,Number> statisticNumericalAttributes(QueryParameters queryParameters,Map<String, StatisticFunction> statisticCondition)  throws CoreRealmServiceEntityExploreException;
    public List<GroupNumericalAttributesStatisticResult> statisticNumericalAttributesByGroup(String groupByAttribute, QueryParameters queryParameters, Map<String, StatisticFunction> statisticCondition) throws CoreRealmServiceEntityExploreException;
}
