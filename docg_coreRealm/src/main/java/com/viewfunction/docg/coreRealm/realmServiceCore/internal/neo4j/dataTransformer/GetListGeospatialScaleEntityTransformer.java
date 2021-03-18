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

import java.util.ArrayList;
import java.util.List;

public class GetListGeospatialScaleEntityTransformer implements DataTransformer<List<GeospatialScaleEntity>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String coreRealmName;
    private String geospatialRegionName;

    public GetListGeospatialScaleEntityTransformer(String coreRealmName,String geospatialRegionName,GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        this.coreRealmName = coreRealmName;
        this.geospatialRegionName = geospatialRegionName;
    }

    @Override
    public List<GeospatialScaleEntity> transformResult(Result result) {
        String targetConceptionKindName = RealmConstant.GeospatialScaleEntityClass;
        List<GeospatialScaleEntity> geospatialScaleEntityList = new ArrayList<>();
        while(result.hasNext()){
            Record nodeRecord = result.next();
            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
            List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
            boolean isMatchedConceptionKind = true;
            if(allConceptionKindNames.size()>0){
                if(targetConceptionKindName != null){
                    isMatchedConceptionKind = allConceptionKindNames.contains(targetConceptionKindName);
                }else{
                    isMatchedConceptionKind = true;
                }
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
                geospatialScaleEntityList.add(neo4JGeospatialScaleEntityImpl);
            }
        }
        return geospatialScaleEntityList;
    }
}
