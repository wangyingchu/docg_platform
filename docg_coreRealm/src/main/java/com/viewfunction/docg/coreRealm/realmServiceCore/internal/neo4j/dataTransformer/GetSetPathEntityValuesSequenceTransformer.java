package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntityValuesSequence;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.util.*;

public class GetSetPathEntityValuesSequenceTransformer implements DataTransformer<Set<PathEntityValuesSequence>> {

    private List<String> conceptionEntitiesAliasList;
    private List<String> relationEntitiesAliasList;
    private Multimap<String, String> entityAliasMultimap;

    public GetSetPathEntityValuesSequenceTransformer(List<String> conceptionEntitiesAliasList,List<String> relationEntitiesAliasList,
                                                     Multimap<String, String> entityAliasMultimap){
        this.conceptionEntitiesAliasList = conceptionEntitiesAliasList;
        this.relationEntitiesAliasList = relationEntitiesAliasList;
        this.entityAliasMultimap = entityAliasMultimap;
    }

    @Override
    public Set<PathEntityValuesSequence> transformResult(Result result) {
        Set<PathEntityValuesSequence> pathEntityValuesSequenceSet = new HashSet<>();
        if(result.hasNext()){
            Map<String, List<String>> conceptionEntitiesConceptionKindsMap = new HashMap<>();
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Path currentPath = nodeRecord.get(CypherBuilder.operationResultName).asPath();
                    Iterator<String> conceptionEntitiesAliasIterator = this.conceptionEntitiesAliasList.iterator();
                    Iterator<String> relationEntitiesAliasIterator = this.relationEntitiesAliasList.iterator();

                    Iterator<Node> nodeIterator = currentPath.nodes().iterator();
                    List<ConceptionEntityValue> conceptionEntityValueList = new ArrayList<>();

                    while(nodeIterator.hasNext()){
                        Node currentNode = nodeIterator.next();
                        String entityAlias = conceptionEntitiesAliasIterator.next();
                        Collection<String> entityAttributes = this.entityAliasMultimap.get(entityAlias);

                        Map<String,Object> entityAttributesValue = new HashMap<>();
                        if(entityAttributes != null && !entityAttributes.isEmpty()){
                            for(String entityAttribute:entityAttributes){
                                Object entityAttributeValue = nodeRecord.get(entityAttribute).asObject();
                                entityAttributesValue.put(entityAttribute,entityAttributeValue);
                            }
                        }

                        List<String> allConceptionKindNames = Lists.newArrayList(currentNode.labels());
                        long nodeUID = currentNode.id();
                        String conceptionEntityUID = ""+nodeUID;
                        if(!conceptionEntitiesConceptionKindsMap.containsKey(conceptionEntityUID)){
                            conceptionEntitiesConceptionKindsMap.put(conceptionEntityUID,allConceptionKindNames);
                        }
                        ConceptionEntityValue currentConceptionEntityValue = new ConceptionEntityValue(conceptionEntityUID,entityAttributesValue);
                        currentConceptionEntityValue.setAllConceptionKindNames(allConceptionKindNames);
                        conceptionEntityValueList.add(currentConceptionEntityValue);
                    }

                    Iterator<Relationship> relationIterator = currentPath.relationships().iterator();
                    List<RelationEntityValue> relationEntityValueList = new ArrayList<>();
                    while(relationIterator.hasNext()){
                        Relationship resultRelationship = relationIterator.next();
                        String entityAlias = relationEntitiesAliasIterator.next();
                        Collection<String> entityAttributes = this.entityAliasMultimap.get(entityAlias);

                        Map<String,Object> entityAttributesValue = new HashMap<>();
                        if(entityAttributes != null && !entityAttributes.isEmpty()){
                            for(String entityAttribute:entityAttributes){
                                Object entityAttributeValue = nodeRecord.get(entityAttribute).asObject();
                                entityAttributesValue.put(entityAttribute,entityAttributeValue);
                            }
                        }

                        String relationType = resultRelationship.type();
                        long relationUID = resultRelationship.id();
                        String relationEntityUID = ""+relationUID;
                        String fromEntityUID = ""+resultRelationship.startNodeId();
                        String toEntityUID = ""+resultRelationship.endNodeId();

                        RelationEntityValue relationEntityValue = new RelationEntityValue(relationEntityUID,fromEntityUID,toEntityUID,entityAttributesValue);
                        relationEntityValue.setRelationKindName(relationType);
                        relationEntityValueList.add(relationEntityValue);
                    }

                    LinkedList<PathEntityValue> entitiesSequenceList = new LinkedList<>();

                    Iterator<ConceptionEntityValue> conceptionEntityItor = conceptionEntityValueList.iterator();
                    Iterator<RelationEntityValue> relationEntityItor = relationEntityValueList.iterator();
                    while (conceptionEntityItor.hasNext() || relationEntityItor.hasNext()) {
                        if (conceptionEntityItor.hasNext()) {
                            entitiesSequenceList.add(conceptionEntityItor.next());
                        }
                        if (relationEntityItor.hasNext()) {
                            entitiesSequenceList.add(relationEntityItor.next());
                        }
                    }

                    PathEntityValuesSequence pathEntityValuesSequence = new PathEntityValuesSequence(entitiesSequenceList);
                    pathEntityValuesSequenceSet.add(pathEntityValuesSequence);
                }
            }
        }

        return pathEntityValuesSequenceSet;
    }
}
