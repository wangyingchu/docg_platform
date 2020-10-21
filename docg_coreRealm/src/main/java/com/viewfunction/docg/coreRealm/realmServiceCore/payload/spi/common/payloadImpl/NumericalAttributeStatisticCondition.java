package com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.feature.StatisticalAndEvaluable;

public class NumericalAttributeStatisticCondition {

    private String attributeName;
    private StatisticalAndEvaluable.StatisticFunction statisticFunction;

    public NumericalAttributeStatisticCondition(){}

    public NumericalAttributeStatisticCondition(String attributeName,StatisticalAndEvaluable.StatisticFunction statisticFunction){
        this.attributeName = attributeName;
        this.statisticFunction = statisticFunction;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public StatisticalAndEvaluable.StatisticFunction getStatisticFunction() {
        return statisticFunction;
    }

    public void setStatisticFunction(StatisticalAndEvaluable.StatisticFunction statisticFunction) {
        this.statisticFunction = statisticFunction;
    }
}
