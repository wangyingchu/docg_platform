package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GroupNumericalAttributesStatisticResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.NumericalAttributeStatisticCondition;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.List;
import java.util.Map;

public interface StatisticalAndEvaluable {

    public enum StatisticFunction {
        COUNT,AVG,MAX,MIN,STDEV,SUM
    }

    public enum EvaluateFunction {}

    public Map<String,Number> statisticNumericalAttributes(QueryParameters queryParameters,List<NumericalAttributeStatisticCondition> statisticCondition)  throws CoreRealmServiceEntityExploreException;
    public List<GroupNumericalAttributesStatisticResult> statisticNumericalAttributesByGroup(String groupByAttribute, QueryParameters queryParameters, List<NumericalAttributeStatisticCondition> statisticConditions) throws CoreRealmServiceEntityExploreException;
    public Map<String,List<ConceptionEntity>> statisticRelatedClassifications(QueryParameters queryParameters, String relationKindName, RelationDirection relationDirection);
}
