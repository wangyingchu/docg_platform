package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GeospatialScaleOperationUtil;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JGeospatialScaleEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.List;

public class GetSingleGeospatialScaleEntityTransformer  implements DataTransformer<GeospatialScaleEntity>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String coreRealmName;
    private String geospatialRegionName;

    public GetSingleGeospatialScaleEntityTransformer(String coreRealmName,String geospatialRegionName,GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        this.coreRealmName = coreRealmName;
        this.geospatialRegionName = geospatialRegionName;
    }

    @Override
    public GeospatialScaleEntity transformResult(Result result) {
        if(result.hasNext()){
            Record nodeRecord = result.next();
            String targetConceptionKindName = RealmConstant.GeospatialScaleEntityClass;
            if(nodeRecord != null){
                Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
                boolean isMatchedConceptionKind = true;
                if(allConceptionKindNames.size()>0 && targetConceptionKindName != null){
                    isMatchedConceptionKind = allConceptionKindNames.contains(targetConceptionKindName);
                }
                if(isMatchedConceptionKind){
                    long nodeUID = resultNode.id();
                    String conceptionEntityUID = ""+nodeUID;
                    String targetGeospatialCode = resultNode.get(GeospatialScaleOperationUtil.GeospatialCodeProperty).asString();
                    String targetGeospatialScaleGradeString = resultNode.get(GeospatialScaleOperationUtil.GeospatialScaleGradeProperty).asString();
                    String _ChineseName = null;
                    String _EnglishName = null;
                    if(resultNode.containsKey("ChineseName")){
                        _ChineseName = resultNode.get("ChineseName").asString();
                    }
                    if(resultNode.containsKey("EnglishName")){
                        _EnglishName = resultNode.get("EnglishName").asString();
                    }

                    GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade = null;
                    switch (targetGeospatialScaleGradeString){
                        case "CONTINENT":geospatialScaleGrade = GeospatialRegion.GeospatialScaleGrade.CONTINENT;break;
                        case "COUNTRY_REGION":geospatialScaleGrade = GeospatialRegion.GeospatialScaleGrade.COUNTRY_REGION;break;
                        case "PROVINCE":geospatialScaleGrade = GeospatialRegion.GeospatialScaleGrade.PROVINCE;break;
                        case "PREFECTURE":geospatialScaleGrade = GeospatialRegion.GeospatialScaleGrade.PREFECTURE;break;
                        case "COUNTY":geospatialScaleGrade = GeospatialRegion.GeospatialScaleGrade.COUNTY;break;
                        case "TOWNSHIP":geospatialScaleGrade = GeospatialRegion.GeospatialScaleGrade.TOWNSHIP;break;
                        case "VILLAGE":geospatialScaleGrade = GeospatialRegion.GeospatialScaleGrade.VILLAGE;break;
                    }
                    Neo4JGeospatialScaleEntityImpl neo4JGeospatialScaleEntityImpl =
                            new Neo4JGeospatialScaleEntityImpl(this.coreRealmName,this.geospatialRegionName,conceptionEntityUID,geospatialScaleGrade,targetGeospatialCode,_ChineseName,_EnglishName);
                    neo4JGeospatialScaleEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                    return neo4JGeospatialScaleEntityImpl;
                }else{
                    return null;
                }
            }
        }
        return null;
    }
}
