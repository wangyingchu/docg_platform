package com.viewfunction.docg.coreRealm.realmServiceCore.payload;

import java.util.Map;

public class GroupNumericalAttributesStatisticResult {

    private Object groupAttributeValue;
    private Map<String,Number> numericalAttributesStatisticValue;

    public GroupNumericalAttributesStatisticResult(){}

    public GroupNumericalAttributesStatisticResult(Object groupAttributeValue,Map<String,Number> numericalAttributesStatisticValue){
        this.groupAttributeValue = groupAttributeValue;
        this.numericalAttributesStatisticValue = numericalAttributesStatisticValue;
    }

    public Object getGroupAttributeValue() {
        return groupAttributeValue;
    }

    public void setGroupAttributeValue(Object groupAttributeValue) {
        this.groupAttributeValue = groupAttributeValue;
    }

    public Map<String, Number> getNumericalAttributesStatisticValue() {
        return numericalAttributesStatisticValue;
    }

    public void setNumericalAttributesStatisticValue(Map<String, Number> numericalAttributesStatisticValue) {
        this.numericalAttributesStatisticValue = numericalAttributesStatisticValue;
    }
}
