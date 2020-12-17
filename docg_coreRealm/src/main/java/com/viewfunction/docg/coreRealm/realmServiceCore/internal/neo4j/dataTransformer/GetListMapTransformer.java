package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetListMapTransformer implements DataTransformer<List<Map<String,Map<String,Object>>>>{

    @Override
    public List<Map<String,Map<String,Object>>> transformResult(Result result) {
        List<Map<String,Map<String,Object>>> dataMapObjectList = new ArrayList<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                Map<String,Map<String,Object>> currentEntityDataMap = new HashMap<>();
                dataMapObjectList.add(currentEntityDataMap);
                if(nodeRecord != null){
                    List<String> resultItemNames = nodeRecord.keys();
                    if(resultItemNames != null){
                        for(String currentItemName : resultItemNames){
                            Map<String,Object> dataMap = new HashMap<>();
                            Value currentValue = nodeRecord.get(currentItemName);
                            Entity currentEntity = currentValue.asEntity();
                            dataMap.putAll(currentEntity.asMap());
                            long id = currentEntity.id();
                            dataMap.put("UID",""+id);
                            currentEntityDataMap.put(currentItemName,dataMap);
                        }
                    }
                }
            }
        }
        return dataMapObjectList;
    }
}
