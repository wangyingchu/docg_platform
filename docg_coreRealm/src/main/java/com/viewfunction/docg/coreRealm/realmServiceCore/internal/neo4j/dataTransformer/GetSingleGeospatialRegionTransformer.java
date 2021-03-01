package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JGeospatialRegionImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.List;

public class GetSingleGeospatialRegionTransformer implements DataTransformer<GeospatialRegion>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetSingleGeospatialRegionTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public GeospatialRegion transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                List<String> allLabelNames = Lists.newArrayList(resultNode.labels());
                boolean isMatchedKind = true;
                if(allLabelNames.size()>0){
                    isMatchedKind = allLabelNames.contains(RealmConstant.GeospatialRegionClass);
                }
                if(isMatchedKind){
                    String coreRealmName = this.currentCoreRealmName;
                    String geospatialRegionName = resultNode.get(RealmConstant._NameProperty).asString();
                    long nodeUID = resultNode.id();
                    String geospatialRegionNameUID = ""+nodeUID;
                    Neo4JGeospatialRegionImpl neo4JGeospatialRegionImpl = new Neo4JGeospatialRegionImpl(coreRealmName,geospatialRegionName,geospatialRegionNameUID);
                    neo4JGeospatialRegionImpl.setGlobalGraphOperationExecutor(this.workingGraphOperationExecutor);
                    return neo4JGeospatialRegionImpl;
                }
            }
        }
        return null;
    }
}
