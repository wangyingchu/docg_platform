package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult.EntityTraversalResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import java.util.ArrayList;
import java.util.List;

public class GetSingleEntityTraversalResultTransformer implements DataTransformer<EntityTraversalResult>{

    private GraphOperationExecutor workingGraphOperationExecutor;

    public GetSingleEntityTraversalResultTransformer(GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor =  workingGraphOperationExecutor;
    }

    @Override
    public EntityTraversalResult transformResult(Result result) {
        while(result.hasNext()){
            Record nodeRecord = result.next();
            long sourceNodeId = nodeRecord.get("startNodeId").asLong();
            List targetNodeIds = nodeRecord.get("nodeIds").asList();
            Path resultPath = nodeRecord.get("path").asPath();

            String sourceNodeUID = ""+sourceNodeId;
            List<String> entityTraversalFootprints = new ArrayList<>();
            for(Object currentId:targetNodeIds){
                entityTraversalFootprints.add(""+currentId);
            }
            List<ConceptionEntity> conceptionEntityList = new ArrayList<>();

            Iterable<Node> entityNode = resultPath.nodes();
            for(Node currentEntityNode:entityNode){
                long nodeUID = currentEntityNode.id();
                List<String> allConceptionKindNames = Lists.newArrayList(currentEntityNode.labels());
                String conceptionEntityUID = ""+nodeUID;
                Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                        new Neo4JConceptionEntityImpl(allConceptionKindNames.get(0),conceptionEntityUID);
                neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
                neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                conceptionEntityList.add(neo4jConceptionEntityImpl);
            }
            EntityTraversalResult entityTraversalResult =
                    new EntityTraversalResult(sourceNodeUID,entityTraversalFootprints,conceptionEntityList);
            return entityTraversalResult;
        }
        return null;
    }
}
