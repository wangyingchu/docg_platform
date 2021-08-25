package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class GetLongFormatAggregatedReturnValueTransformer implements DataTransformer<Long>{

    private String aggregationFunctionName;
    private String prefixParam;
    private boolean notUseAggregationFunction = false;

    public GetLongFormatAggregatedReturnValueTransformer(String aggregationFunctionName){
        this.aggregationFunctionName = aggregationFunctionName;
    }

    public GetLongFormatAggregatedReturnValueTransformer(String aggregationFunctionName,String prefixParam){
        this.aggregationFunctionName = aggregationFunctionName;
        this.prefixParam = prefixParam;
    }

    public GetLongFormatAggregatedReturnValueTransformer(){
        this.notUseAggregationFunction = true;
    }

    @Override
    public Long transformResult(Result result) {
        if(result.hasNext()){
            Record record = result.next();
            String resultDataProperty = null;
            if(this.notUseAggregationFunction){
                resultDataProperty = CypherBuilder.operationResultName;
            }else{
                String prefixParamText = this.prefixParam != null ? this.prefixParam+" ":"";
                resultDataProperty = aggregationFunctionName+"("+ prefixParamText + CypherBuilder.operationResultName+")";
            }
            if(record.containsKey(resultDataProperty)){
                return record.get(resultDataProperty).asLong();
            }
        }
        return null;
    }
}
