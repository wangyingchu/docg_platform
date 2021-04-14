package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Relationship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetListRelationEntityValueTransformer  implements DataTransformer<List<RelationEntityValue>>{

    private List<String> returnedAttributeList;
    private String targetRelationKindName;

    public GetListRelationEntityValueTransformer(String targetRelationKindName,List<String> returnedAttributeList){
        this.targetRelationKindName = targetRelationKindName;
        this.returnedAttributeList = returnedAttributeList;
    }

    @Override
    public List<RelationEntityValue> transformResult(Result result) {
        List<RelationEntityValue> relationEntityValueList = new ArrayList<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Relationship resultRelationship = nodeRecord.get(CypherBuilder.operationResultName).asRelationship();
                    String relationType = resultRelationship.type();
                    boolean isMatchedKind;
                    if(this.targetRelationKindName == null){
                        isMatchedKind = true;
                    }else{
                        isMatchedKind = relationType.equals(targetRelationKindName)? true : false;
                    }
                    if(isMatchedKind){
                        long relationUID = resultRelationship.id();
                        String relationEntityUID = ""+relationUID;
                        String fromEntityUID = ""+resultRelationship.startNodeId();
                        String toEntityUID = ""+resultRelationship.endNodeId();

                        Map<String,Object> entityAttributesValue = new HashMap<>();
                        if(returnedAttributeList != null && returnedAttributeList.size() > 0){
                            for(String currentAttributeName : returnedAttributeList){
                                Value targetValue = resultRelationship.get(currentAttributeName);
                                if(targetValue != null){
                                    entityAttributesValue.put(currentAttributeName,targetValue);
                                }
                            }
                        }
                        RelationEntityValue relationEntityValue = new RelationEntityValue(relationEntityUID,fromEntityUID,toEntityUID,entityAttributesValue);
                        relationEntityValueList.add(relationEntityValue);
                    }
                }
            }
        }
        return relationEntityValueList;
    }
}
