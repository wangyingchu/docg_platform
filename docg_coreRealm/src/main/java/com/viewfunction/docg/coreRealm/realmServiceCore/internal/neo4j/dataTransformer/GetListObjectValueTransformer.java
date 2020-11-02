package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.ArrayList;
import java.util.List;

public class GetListObjectValueTransformer<T> implements DataTransformer<List<T>>{

    private String aggregationFunctionName;
    private String prefixParam;

    public GetListObjectValueTransformer(String aggregationFunctionName){
        this.aggregationFunctionName = aggregationFunctionName;
    }

    public GetListObjectValueTransformer(String aggregationFunctionName,String prefixParam){
        this.aggregationFunctionName = aggregationFunctionName;
        this.prefixParam = prefixParam;
    }

    @Override
    public List<T> transformResult(Result result) {
        List<T> resultList = new ArrayList();
        String prefixParamText = this.prefixParam != null ? this.prefixParam+" ":"";
        String resultDataProperty = aggregationFunctionName+"("+ prefixParamText + CypherBuilder.operationResultName+")";
        while(result.hasNext()){
            Record record = result.next();
            if(record.containsKey(resultDataProperty)){
                T currentValue = (T)(record.get(resultDataProperty).asObject());
                resultList.add(currentValue);
            }
        }
        return resultList;
    }
}
