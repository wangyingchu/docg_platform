package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class GetBooleanFormatAggregatedReturnValueTransformer  implements DataTransformer<Boolean>{

    private String aggregationFunctionName;
    private String additionalAttributeName;

    public GetBooleanFormatAggregatedReturnValueTransformer(String aggregationFunctionName){
        this.aggregationFunctionName = aggregationFunctionName;
    }

    public GetBooleanFormatAggregatedReturnValueTransformer(String aggregationFunctionName,String additionalAttributeName){
        this.aggregationFunctionName = aggregationFunctionName;
        this.additionalAttributeName = additionalAttributeName;
    }

    @Override
    public Boolean transformResult(Result result) {
        if(result.hasNext()){
            Record record = result.next();
            String resultDataProperty = additionalAttributeName != null ?
                    aggregationFunctionName+"("+ CypherBuilder.operationResultName+"."+additionalAttributeName+")"
                    :aggregationFunctionName+"("+ CypherBuilder.operationResultName+")";
            if(record.containsKey(resultDataProperty)){
                return record.get(resultDataProperty).asBoolean();
            }
        }
        return null;
    }
}
