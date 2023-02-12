package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeSystemInfo;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetMapAttributeSystemInfoTransformer implements DataTransformer<Map<String, List<AttributeSystemInfo>>>{

    private GraphOperationExecutor workingGraphOperationExecutor;

    public GetMapAttributeSystemInfoTransformer(GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }
    @Override
    public Map<String, List<AttributeSystemInfo>> transformResult(Result result) {
        Map<String, List<AttributeSystemInfo>> kindsAndAttributeSystemInfoListMap = new HashMap<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    String attributeName = nodeRecord.get("property").asString();
                    String dataType = nodeRecord.get("type").asString();
                    String kindName = nodeRecord.get("label").asString();
                    boolean usedInIndex = false;
                    boolean uniqueAttribute = false;
                    boolean constraintAttribute = false;
                    if(!nodeRecord.get("isIndexed").isNull()){
                        usedInIndex = nodeRecord.get("isIndexed").asBoolean();
                    }
                    if(!nodeRecord.get("uniqueConstraint").isNull()){
                        uniqueAttribute = nodeRecord.get("uniqueConstraint").asBoolean();
                    }
                    if(!nodeRecord.get("existenceConstraint").isNull()){
                        constraintAttribute = nodeRecord.get("existenceConstraint").asBoolean();
                    }
                    if(!kindsAndAttributeSystemInfoListMap.containsKey(kindName)){
                        ArrayList<AttributeSystemInfo> currentKindAttributeSystemInfoList = new ArrayList<>();
                        kindsAndAttributeSystemInfoListMap.put(kindName,currentKindAttributeSystemInfoList);
                    }
                    AttributeSystemInfo attributeSystemInfo = new AttributeSystemInfo(attributeName,dataType,usedInIndex,
                            uniqueAttribute,constraintAttribute);
                    kindsAndAttributeSystemInfoListMap.get(kindName).add(attributeSystemInfo);
                }
            }
        }
        return kindsAndAttributeSystemInfoListMap;
    }
}
