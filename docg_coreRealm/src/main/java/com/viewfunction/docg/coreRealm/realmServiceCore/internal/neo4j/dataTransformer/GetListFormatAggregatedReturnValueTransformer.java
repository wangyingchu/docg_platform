package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.List;

public class GetListFormatAggregatedReturnValueTransformer implements DataTransformer<List<Object>>{

    private String aggregationFunctionName;

    public GetListFormatAggregatedReturnValueTransformer(String aggregationFunctionName){
        this.aggregationFunctionName = aggregationFunctionName;
    }

    @Override
    public List<Object> transformResult(Result result) {
        if(result.hasNext()){
            Record record = result.next();
            String resultDataProperty = aggregationFunctionName+"("+ CypherBuilder.operationResultName+")";
            if(record.containsKey(resultDataProperty)){
                return record.get(resultDataProperty).asList();
            }
        }
        return null;
    }
}
