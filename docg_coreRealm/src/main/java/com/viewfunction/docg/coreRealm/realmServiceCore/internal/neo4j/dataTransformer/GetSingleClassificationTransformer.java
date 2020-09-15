package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.Classification;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JClassificationImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

public class GetSingleClassificationTransformer implements DataTransformer<Classification>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetSingleClassificationTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public Classification transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                long nodeUID = resultNode.id();
                String coreRealmName = this.currentCoreRealmName;
                String classificationName = resultNode.get(RealmConstant._NameProperty).asString();
                String classificationDesc = null;
                if(resultNode.get(RealmConstant._DescProperty) != null){
                    classificationDesc = resultNode.get(RealmConstant._DescProperty).asString();
                }
                String classificationUID = ""+nodeUID;
                Neo4JClassificationImpl neo4JClassificationImpl =
                        new Neo4JClassificationImpl(coreRealmName,classificationName,classificationDesc,classificationUID);
                neo4JClassificationImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                return neo4JClassificationImpl;
            }
        }
        return null;
    }
}
