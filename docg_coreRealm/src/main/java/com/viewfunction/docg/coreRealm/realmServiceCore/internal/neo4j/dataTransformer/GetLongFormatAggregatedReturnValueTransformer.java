package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class GetLongFormatAggregatedReturnValueTransformer implements DataTransformer<Long>{

    private String aggregationFunctionName;
    private String prefixParam;

    public GetLongFormatAggregatedReturnValueTransformer(String aggregationFunctionName){
        this.aggregationFunctionName = aggregationFunctionName;
    }

    public GetLongFormatAggregatedReturnValueTransformer(String aggregationFunctionName,String prefixParam){
        this.aggregationFunctionName = aggregationFunctionName;
        this.prefixParam = prefixParam;
    }

    @Override
    public Long transformResult(Result result) {
        if(result.hasNext()){
            Record record = result.next();
            String prefixParamText = this.prefixParam != null ? this.prefixParam+" ":"";
            String resultDataProperty = aggregationFunctionName+"("+ prefixParamText + CypherBuilder.operationResultName+")";
            if(record.containsKey(resultDataProperty)){
                return record.get(resultDataProperty).asLong();
            }
        }
        return null;
    }
}
