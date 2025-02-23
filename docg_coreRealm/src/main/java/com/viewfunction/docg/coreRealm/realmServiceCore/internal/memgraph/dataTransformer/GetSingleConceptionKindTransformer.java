package com.viewfunction.docg.coreRealm.realmServiceCore.internal.memgraph.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl.MemGraphConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

public class GetSingleConceptionKindTransformer implements DataTransformer<ConceptionKind> {

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetSingleConceptionKindTransformer(String currentCoreRealmName, GraphOperationExecutor workingGraphOperationExecutor) {
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
                MemGraphConceptionKindImpl memGraphConceptionKindImpl =
                        new MemGraphConceptionKindImpl(coreRealmName,conceptionKindName,conceptionKindDesc,conceptionKindUID);
                memGraphConceptionKindImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                return memGraphConceptionKindImpl;
            }
        }
        return null;
    }
}
