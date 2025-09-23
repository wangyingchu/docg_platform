package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.PathEntitiesSequence;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Path;

import java.util.HashSet;
import java.util.Set;

public class GetSetPathEntitiesSequenceTransformer implements DataTransformer<Set<PathEntitiesSequence>>{

    private GraphOperationExecutor workingGraphOperationExecutor;

    public GetSetPathEntitiesSequenceTransformer(GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public Set<PathEntitiesSequence> transformResult(Result result) {
        Set<PathEntitiesSequence> pathEntitiesSequenceSet = new HashSet<>();
        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                if(nodeRecord != null){
                    Path currentPath = nodeRecord.get(CypherBuilder.operationResultName).asPath();

                }
            }
        }


        return pathEntitiesSequenceSet;
    }
}
