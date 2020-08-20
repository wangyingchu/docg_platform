package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.CommonOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeValue;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.Map;

public class GetSingleAttributeValueTransformer implements DataTransformer<AttributeValue>{

    private String attributeName;

    public GetSingleAttributeValueTransformer(String attributeName){
        this.attributeName = attributeName;
    }

    @Override
    public AttributeValue transformResult(Result result) {
        if(result.hasNext()){
            Record returnRecord = result.next();
            Map<String,Object> returnValueMap = returnRecord.asMap();
            String attributeNameFullName= CypherBuilder.operationResultName+"."+this.attributeName;
            Object attributeValueObject = returnValueMap.get(attributeNameFullName);
            if(attributeValueObject!= null){
                return CommonOperationUtil.getAttributeValue(this.attributeName,attributeValueObject);
            }
        }
        return null;
    }
}
