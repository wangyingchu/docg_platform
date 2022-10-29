package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class GetBooleanFormatReturnValueTransformer implements DataTransformer<Boolean>{

    private String customReturnAttributeName;

    public GetBooleanFormatReturnValueTransformer(){}

    public GetBooleanFormatReturnValueTransformer(String customReturnAttributeName){
        this.customReturnAttributeName = customReturnAttributeName;
    }

    @Override
    public Boolean transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            String propertyName = this.customReturnAttributeName != null ? this.customReturnAttributeName : CypherBuilder.operationResultName;
            return nodeRecord.get(propertyName).asBoolean();
        }
        return false;
    }
}
