package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DynamicContentValue;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

public class GetListDynamicContentValueTransformer implements DataTransformer<List<DynamicContentValue>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetListDynamicContentValueTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<DynamicContentValue> transformResult(Result result) {
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();







               // nodeRecord.get(0).as











                nodeRecord.fields().stream().forEach(recordField -> {
                    String key = recordField.key();
                    Object value = recordField.value();






                });






                //nodeRecord.fields().stream().filter(recordField -> recordField.key().equals(currentKey)).forEach(recordField -> {});







                List<String> recordKeyList = nodeRecord.keys();
                for(String currentKey : recordKeyList){




                }



            }
        }

        return List.of();
    }

    private void createAttributeEntity(Object entityObject){
        if(entityObject instanceof Boolean){

        }else if(entityObject instanceof byte[]){

        }
        else if(entityObject instanceof Double){

        }
        else if(entityObject instanceof Float){

        }
        else if(entityObject instanceof Integer){

        }
        else if(entityObject instanceof LocalDate){

        }
        else if(entityObject instanceof LocalTime){

        }
        else if(entityObject instanceof LocalDateTime){

        }
        else if(entityObject instanceof Long){

        }
        else if(entityObject instanceof Node){

        }
        else if(entityObject instanceof Number){

        }
        else if(entityObject instanceof Path){

        }
        else if(entityObject instanceof Relationship){

        }
        else if(entityObject instanceof String){

        }
        else if(entityObject instanceof ZonedDateTime){

        }else{

        }

    }
}
