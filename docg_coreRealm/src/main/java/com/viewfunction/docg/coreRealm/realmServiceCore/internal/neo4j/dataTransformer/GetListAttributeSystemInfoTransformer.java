package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.AttributeKind;
import org.neo4j.driver.Result;

import java.util.List;

public class GetListAttributeSystemInfoTransformer implements DataTransformer<List<AttributeKind>>{

    private GraphOperationExecutor workingGraphOperationExecutor;

    public GetListAttributeSystemInfoTransformer(GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<AttributeKind> transformResult(Result result) {
        return null;
    }
}
