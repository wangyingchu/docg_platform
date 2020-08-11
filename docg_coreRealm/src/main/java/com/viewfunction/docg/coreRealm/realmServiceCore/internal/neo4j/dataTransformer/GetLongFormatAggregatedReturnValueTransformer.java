package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class GetLongFormatAggregatedReturnValueTransformer implements DataTransformer<Long>{

    private String aggregationFunctionName;

    public GetLongFormatAggregatedReturnValueTransformer(String aggregationFunctionName){
        this.aggregationFunctionName = aggregationFunctionName;
    }

    @Override
    public Long transformResult(Result result) {
        if(result.hasNext()){
            Record record = result.next();
            String resultDataProperty = aggregationFunctionName+"("+ CypherBuilder.operationResultName+")";
            if(record.containsKey(resultDataProperty)){
                return record.get(resultDataProperty).asLong();
            }
        }
        return null;
    }
}
