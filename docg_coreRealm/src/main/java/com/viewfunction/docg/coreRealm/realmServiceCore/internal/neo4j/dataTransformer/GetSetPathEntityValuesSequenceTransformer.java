package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntityValuesSequence;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.util.*;

public class GetSetPathEntityValuesSequenceTransformer implements DataTransformer<Set<PathEntityValuesSequence>>{

    @Override
    public Set<PathEntityValuesSequence> transformResult(Result result) {
        Set<PathEntityValuesSequence> pathEntityValuesSequenceSet = new HashSet<>();
        if(result.hasNext()){
            Map<String, List<String>> conceptionEntitiesConceptionKindsMap = new HashMap<>();
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Path currentPath = nodeRecord.get(CypherBuilder.operationResultName).asPath();

                    Iterator<Node> nodeIterator = currentPath.nodes().iterator();
                    List<ConceptionEntityValue> conceptionEntityValueList = new ArrayList<>();
                    while(nodeIterator.hasNext()){
                        Node currentNode = nodeIterator.next();
                        List<String> allConceptionKindNames = Lists.newArrayList(currentNode.labels());
                        long nodeUID = currentNode.id();
                        String conceptionEntityUID = ""+nodeUID;
                        if(!conceptionEntitiesConceptionKindsMap.containsKey(conceptionEntityUID)){
                            conceptionEntitiesConceptionKindsMap.put(conceptionEntityUID,allConceptionKindNames);
                        }

                        Map<String,Object> entityAttributesValue = new HashMap<>();
                        ConceptionEntityValue currentConceptionEntityValue = new ConceptionEntityValue(conceptionEntityUID,entityAttributesValue);
                        currentConceptionEntityValue.setAllConceptionKindNames(allConceptionKindNames);
                        conceptionEntityValueList.add(currentConceptionEntityValue);
                    }

                    Iterator<Relationship> relationIterator = currentPath.relationships().iterator();
                    List<RelationEntityValue> relationEntityValueList = new ArrayList<>();
                    while(relationIterator.hasNext()){
                        Relationship resultRelationship = relationIterator.next();
                        String relationType = resultRelationship.type();
                        long relationUID = resultRelationship.id();
                        String relationEntityUID = ""+relationUID;
                        String fromEntityUID = ""+resultRelationship.startNodeId();
                        String toEntityUID = ""+resultRelationship.endNodeId();
                        Map<String,Object> entityAttributesValue = new HashMap<>();

                        RelationEntityValue relationEntityValue = new RelationEntityValue(relationEntityUID,fromEntityUID,toEntityUID,entityAttributesValue);




                        relationEntityValueList.add(relationEntityValue);
                    }



                    /*
                    LinkedList<PathEntity> entitiesSequenceList = new LinkedList<>();

                    Iterator<ConceptionEntity> conceptionEntityItor = conceptionEntityList.iterator();
                    Iterator<RelationEntity> relationEntityItor = relationEntityList.iterator();
                    while (conceptionEntityItor.hasNext() || relationEntityItor.hasNext()) {
                        if (conceptionEntityItor.hasNext()) {
                            entitiesSequenceList.add(conceptionEntityItor.next());
                        }
                        if (relationEntityItor.hasNext()) {
                            entitiesSequenceList.add(relationEntityItor.next());
                        }
                    }
                    */
                    //PathEntitiesSequence pathEntitiesSequence = new PathEntitiesSequence(entitiesSequenceList);
                    //pathEntitiesSequenceSet.add(pathEntitiesSequence);
                }
            }
        }








        return pathEntityValuesSequenceSet;
    }
}
