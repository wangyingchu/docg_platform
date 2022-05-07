package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.ArrayList;
import java.util.List;

public class GetListEntityUIDTransformer implements DataTransformer<List<String>>{

    private String uidPropertyName = "id("+ CypherBuilder.operationResultName+")";

    public GetListEntityUIDTransformer(){}

    public GetListEntityUIDTransformer(String uidPropertyName){
        if(uidPropertyName != null){
            this.uidPropertyName = uidPropertyName;
        }
    }

    @Override
    public List<String> transformResult(Result result) {
        List<String> resultList = new ArrayList();
        while(result.hasNext()){
            Record record = result.next();
            if(record.containsKey(uidPropertyName)){
                long currentUIDValue = record.get(uidPropertyName).asLong();
                resultList.add(""+currentUIDValue);
            }
        }
        return resultList;
    }
}
