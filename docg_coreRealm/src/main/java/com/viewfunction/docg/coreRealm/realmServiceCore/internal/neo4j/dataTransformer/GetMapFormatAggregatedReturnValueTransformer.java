package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.Map;

public class GetMapFormatAggregatedReturnValueTransformer implements DataTransformer<Map>{

    private String aggregationFunctionName;

    public GetMapFormatAggregatedReturnValueTransformer(String aggregationFunctionName){
        this.aggregationFunctionName = aggregationFunctionName;
    }

    @Override
    public Map transformResult(Result result) {
        if(result.hasNext()){
            Record record = result.next();
            String resultDataProperty = aggregationFunctionName+"("+ CypherBuilder.operationResultName+")";
            if(record.containsKey(resultDataProperty)){
                return record.get(resultDataProperty).asMap();
            }
        }
        return null;
    }
}

