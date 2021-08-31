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

    /**
     * 统计数值类属性信息
     *
     * @param queryParameters QueryParameters 待统计数据查询条件
     * @param statisticCondition List<NumericalAttributeStatisticCondition> 数值类属性数据统计条件列表
     *
     * @return 统计结果键值对
     */
    public Map<String,Number> statisticNumericalAttributes(QueryParameters queryParameters,List<NumericalAttributeStatisticCondition> statisticCondition)  throws CoreRealmServiceEntityExploreException;

    /**
     * 按属性值分组统计数值类属性信息
     *
     * @param groupByAttribute String 待分组属性名称
     * @param queryParameters QueryParameters 待统计数据查询条件
     * @param statisticConditions  List<NumericalAttributeStatisticCondition>  数值类属性数据统计条件列表
     *
     * @return 带分组信息的统计结果键值对列表
     */
    public List<GroupNumericalAttributesStatisticResult> statisticNumericalAttributesByGroup(String groupByAttribute, QueryParameters queryParameters, List<NumericalAttributeStatisticCondition> statisticConditions) throws CoreRealmServiceEntityExploreException;

    /**
     * 统计特定实体对象按照指定关联规律与分类的关联分组
     *
     * @param queryParameters QueryParameters 待统计数据查询条件
     * @param relationKindName String 与分类关联的关系类型名称
     * @param relationDirection RelationDirection 与分类关联的关系方向
     *
     * @return 统计结果键值对，Key 为分类名称， Value 为与其相关的概念实体列表
     */
    public Map<String,List<ConceptionEntity>> statisticRelatedClassifications(QueryParameters queryParameters, String relationKindName, RelationDirection relationDirection);

    public void evaluateEntityAttributesDistribution(long sampleCount);
}
