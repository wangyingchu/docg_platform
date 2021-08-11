package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesSpanningTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetSingleEntitiesSpanningTreeTransformer implements DataTransformer<EntitiesSpanningTree>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String rootConceptionEntityType;
    private String rootConceptionEntityUID;

    public GetSingleEntitiesSpanningTreeTransformer(String rootConceptionEntityUID,GraphOperationExecutor workingGraphOperationExecutor){
        this.rootConceptionEntityUID = rootConceptionEntityUID;
        this.workingGraphOperationExecutor =  workingGraphOperationExecutor;
    }

    @Override
    public EntitiesSpanningTree transformResult(Result result) {
        List<ConceptionEntity> treeConceptionEntities = new ArrayList<>();
        List<RelationEntity> treeRelationEntities = new ArrayList<>();
        List<String> treeConceptionEntityUIDList = new ArrayList<>();
        List<String> treeRelationEntityUIDList = new ArrayList<>();

        while(result.hasNext()){
            Record currentRecord = result.next();
            org.neo4j.driver.types.Path currentPath = currentRecord.get("path").asPath();
            String startEntityType = currentPath.start().labels().iterator().next();
            String startEntityUID = ""+currentPath.start().id();

            if(startEntityUID.equals(this.rootConceptionEntityUID)){
                this.rootConceptionEntityType = startEntityType;
            }

            Iterator<Node> nodeIterator = currentPath.nodes().iterator();
            while(nodeIterator.hasNext()){
                Node currentNode = nodeIterator.next();
                List<String> allConceptionKindNames = Lists.newArrayList(currentNode.labels());
                long nodeUID = currentNode.id();
                String conceptionEntityUID = ""+nodeUID;
                if(!treeConceptionEntityUIDList.contains(conceptionEntityUID)){
                    treeConceptionEntityUIDList.add(conceptionEntityUID);
                    Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                            new Neo4JConceptionEntityImpl(allConceptionKindNames.get(0),conceptionEntityUID);
                    neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
                    neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    treeConceptionEntities.add(neo4jConceptionEntityImpl);
                }
            }

            Iterator<Relationship> relationIterator = currentPath.relationships().iterator();
            while(relationIterator.hasNext()){
                Relationship resultRelationship = relationIterator.next();
                String relationType = resultRelationship.type();
                long relationUID = resultRelationship.id();
                String relationEntityUID = ""+relationUID;
                if(!treeRelationEntityUIDList.contains(relationEntityUID)){
                    treeRelationEntityUIDList.add(relationEntityUID);
                    String fromEntityUID = ""+resultRelationship.startNodeId();
                    String toEntityUID = ""+resultRelationship.endNodeId();
                    Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                            new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
                    neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    treeRelationEntities.add(neo4jRelationEntityImpl);
                }
            }
        }

        treeConceptionEntityUIDList.clear();
        treeRelationEntityUIDList.clear();

        EntitiesSpanningTree entitiesSpanningTree = new EntitiesSpanningTree(this.rootConceptionEntityType,this.rootConceptionEntityUID,
                treeConceptionEntities,treeRelationEntities);
        return entitiesSpanningTree;
    }
}