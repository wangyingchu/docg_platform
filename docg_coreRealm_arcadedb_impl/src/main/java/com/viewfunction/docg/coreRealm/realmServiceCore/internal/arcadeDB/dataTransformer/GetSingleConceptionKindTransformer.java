package com.viewfunction.docg.coreRealm.realmServiceCore.internal.arcadeDB.dataTransformer;

import com.arcadedb.graph.Vertex;
import com.arcadedb.query.sql.executor.Result;
import com.arcadedb.query.sql.executor.ResultSet;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.arcadeDB.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.arcadedb.termImpl.ArcadeDBConceptionKindImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

public class GetSingleConceptionKindTransformer implements DataTransformer<ConceptionKind>{

    private String currentCoreRealmName;
    private GraphOperationExecutor graphOperationExecutor;

    public GetSingleConceptionKindTransformer(String currentCoreRealmName, GraphOperationExecutor graphOperationExecutor){
        this.currentCoreRealmName = currentCoreRealmName;
        this.graphOperationExecutor = graphOperationExecutor;
    }

    @Override
    public ConceptionKind transformResult(ResultSet resultSet) {
        if(resultSet.hasNext()){
            Result result = resultSet.next();
            Vertex vertex = result.getVertex().get();
            String conceptionKindUID = vertex.getIdentity().toString();
            String conceptionKindName = vertex.getString(RealmConstant._NameProperty);
            String conceptionKinDesc = vertex.getString(RealmConstant._DescProperty);
            ConceptionKind arcadeDBConceptionKindImpl = new ArcadeDBConceptionKindImpl(this.currentCoreRealmName,conceptionKindName,conceptionKinDesc,conceptionKindUID);
            return arcadeDBConceptionKindImpl;
        }
        resultSet.close();
        return null;
    }
}
