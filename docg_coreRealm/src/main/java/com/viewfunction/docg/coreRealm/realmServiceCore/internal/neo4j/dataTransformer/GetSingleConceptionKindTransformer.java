package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.termImpl.neo4j.Neo4JConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

public class GetSingleConceptionKindTransformer implements DataTransformer<ConceptionKind>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetSingleConceptionKindTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public ConceptionKind transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                long nodeUID = resultNode.id();
                String coreRealmName = this.currentCoreRealmName;
                String conceptionKindName = resultNode.get(RealmConstant._NameProperty).asString();
                String conceptionKindDesc = null;
                if(resultNode.get(RealmConstant._DescProperty) != null){
                    conceptionKindDesc = resultNode.get(RealmConstant._DescProperty).asString();
                }
                String conceptionKindUID = ""+nodeUID;
                Neo4JConceptionKindImpl neo4JConceptionKindImpl =
                        new Neo4JConceptionKindImpl(coreRealmName,conceptionKindName,conceptionKindDesc,conceptionKindUID);
                neo4JConceptionKindImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                return neo4JConceptionKindImpl;
            }
        }
       return null;
    }
}
