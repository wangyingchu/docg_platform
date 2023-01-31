package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class GetLongFormatReturnValueTransformer implements DataTransformer<Long> {
    private String customReturnAttributeName;

    public GetLongFormatReturnValueTransformer(){}

    public GetLongFormatReturnValueTransformer(String customReturnAttributeName){
        this.customReturnAttributeName = customReturnAttributeName;
    }

    @Override
    public Long transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            String propertyName = this.customReturnAttributeName != null ? this.customReturnAttributeName : CypherBuilder.operationResultName;
            return nodeRecord.get(propertyName).asLong();
        }
        return 0l;
    }
}
