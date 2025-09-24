package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntityValuesSequence;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;
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
                    List<ConceptionEntity> conceptionEntityList = new ArrayList<>();
                    while(nodeIterator.hasNext()){
                        Node currentNode = nodeIterator.next();
                        List<String> allConceptionKindNames = Lists.newArrayList(currentNode.labels());
                        long nodeUID = currentNode.id();
                        String conceptionEntityUID = ""+nodeUID;
                        if(!conceptionEntitiesConceptionKindsMap.containsKey(conceptionEntityUID)){
                            conceptionEntitiesConceptionKindsMap.put(conceptionEntityUID,allConceptionKindNames);
                        }
                        Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                                new Neo4JConceptionEntityImpl(allConceptionKindNames.get(0),conceptionEntityUID);
                        neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
                        //neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                        conceptionEntityList.add(neo4jConceptionEntityImpl);
                    }

                    Iterator<Relationship> relationIterator = currentPath.relationships().iterator();
                    List<RelationEntity> relationEntityList = new ArrayList<>();
                    while(relationIterator.hasNext()){
                        Relationship resultRelationship = relationIterator.next();
                        String relationType = resultRelationship.type();
                        long relationUID = resultRelationship.id();
                        String relationEntityUID = ""+relationUID;
                        String fromEntityUID = ""+resultRelationship.startNodeId();
                        String toEntityUID = ""+resultRelationship.endNodeId();
                        Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                                new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
                        neo4jRelationEntityImpl.setFromEntityConceptionKindList(conceptionEntitiesConceptionKindsMap.get(fromEntityUID));
                        neo4jRelationEntityImpl.setToEntityConceptionKindList(conceptionEntitiesConceptionKindsMap.get(toEntityUID));
                        //neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                        relationEntityList.add(neo4jRelationEntityImpl);
                    }

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

                    //PathEntitiesSequence pathEntitiesSequence = new PathEntitiesSequence(entitiesSequenceList);
                    //pathEntitiesSequenceSet.add(pathEntitiesSequence);
                }
            }
        }








        return pathEntityValuesSequenceSet;
    }
}
