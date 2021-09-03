package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class GetBooleanFormatReturnValueTransformer implements DataTransformer<Boolean>{

    @Override
    public Boolean transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            return nodeRecord.get(CypherBuilder.operationResultName).asBoolean();
        }
        return false;
    }
}
