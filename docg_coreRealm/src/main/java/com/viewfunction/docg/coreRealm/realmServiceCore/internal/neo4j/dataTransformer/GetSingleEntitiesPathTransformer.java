package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JRelationEntityImpl;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GetSingleEntitiesPathTransformer implements DataTransformer<EntitiesPath>{

    private GraphOperationExecutor workingGraphOperationExecutor;

    public GetSingleEntitiesPathTransformer(GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor =  workingGraphOperationExecutor;
    }

    @Override
    public EntitiesPath transformResult(Result result) {
        if(result.hasNext()){
            Record currentRecord = result.next();
            org.neo4j.driver.types.Path currentPath = currentRecord.get("path").asPath();
            String startEntityType = currentPath.start().labels().iterator().next();
            String startEntityUID = ""+currentPath.start().id();
            String endEntityType = currentPath.end().labels().iterator().next();
            String endEntityUID = ""+currentPath.end().id();
            int pathJumps = currentPath.length();
            LinkedList<ConceptionEntity> pathConceptionEntities = new LinkedList<>();
            LinkedList<RelationEntity> pathRelationEntities = new LinkedList<>();

            EntitiesPath currentEntitiesPath = new EntitiesPath(startEntityType,startEntityUID,
                    endEntityType,endEntityUID,pathJumps,pathConceptionEntities,pathRelationEntities);
            Iterator<Node> nodeIterator = currentPath.nodes().iterator();
            while(nodeIterator.hasNext()){
                Node currentNode = nodeIterator.next();
                List<String> allConceptionKindNames = Lists.newArrayList(currentNode.labels());
                long nodeUID = currentNode.id();
                String conceptionEntityUID = ""+nodeUID;
                Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                        new Neo4JConceptionEntityImpl(allConceptionKindNames.get(0),conceptionEntityUID);
                neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
                neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                pathConceptionEntities.add(neo4jConceptionEntityImpl);
            }

            Iterator<Relationship> relationIterator = currentPath.relationships().iterator();
            while(relationIterator.hasNext()){
                Relationship resultRelationship = relationIterator.next();
                String relationType = resultRelationship.type();
                long relationUID = resultRelationship.id();
                String relationEntityUID = ""+relationUID;
                String fromEntityUID = ""+resultRelationship.startNodeId();
                String toEntityUID = ""+resultRelationship.endNodeId();
                Neo4JRelationEntityImpl neo4jRelationEntityImpl =
                        new Neo4JRelationEntityImpl(relationType,relationEntityUID,fromEntityUID,toEntityUID);
                neo4jRelationEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                pathRelationEntities.add(neo4jRelationEntityImpl);
            }
            return currentEntitiesPath;
        }
        return null;
    }
}
