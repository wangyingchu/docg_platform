package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JGeospatialScaleEventImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class GetListGeospatialScaleEventTransformer implements DataTransformer<List<GeospatialScaleEvent>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String geospatialRegionName;

    public GetListGeospatialScaleEventTransformer(String geospatialRegionName, GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        this.geospatialRegionName = geospatialRegionName;
    }

    @Override
    public List<GeospatialScaleEvent> transformResult(Result result) {
        List<GeospatialScaleEvent> geospatialScaleEventList = new ArrayList<>();
        while(result.hasNext()){
            Record nodeRecord = result.next();
            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
            List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
            boolean isMatchedConceptionKind = false;
            if(allConceptionKindNames.size()>0){
                isMatchedConceptionKind = allConceptionKindNames.contains(RealmConstant.GeospatialScaleEventClass);
            }
            if(isMatchedConceptionKind){
                long nodeUID = resultNode.id();
                String geospatialScaleEventUID = ""+nodeUID;
                String eventComment = resultNode.get(RealmConstant._GeospatialScaleEventComment).asString();
                String geospatialScaleGrade = resultNode.get(RealmConstant._GeospatialScaleEventScaleGrade).asString();
                String referLocation = resultNode.get(RealmConstant._GeospatialScaleEventReferLocation).asString();
                String geospatialRegion = resultNode.get(RealmConstant._GeospatialScaleEventGeospatialRegion).asString();

                Neo4JGeospatialScaleEventImpl neo4JGeospatialScaleEventImpl = new Neo4JGeospatialScaleEventImpl(geospatialRegion,eventComment,referLocation,getGeospatialScaleGrade(geospatialScaleGrade.trim()),geospatialScaleEventUID);
                neo4JGeospatialScaleEventImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
                geospatialScaleEventList.add(neo4JGeospatialScaleEventImpl);
            }
        }
        return geospatialScaleEventList;
    }

    private GeospatialRegion.GeospatialScaleGrade getGeospatialScaleGrade(String geospatialScaleGradeValue){
        if(geospatialScaleGradeValue.equals("CONTINENT")){
            return GeospatialRegion.GeospatialScaleGrade.CONTINENT;
        }else if(geospatialScaleGradeValue.equals("COUNTRY_REGION")){
            return GeospatialRegion.GeospatialScaleGrade.COUNTRY_REGION;
        }else if(geospatialScaleGradeValue.equals("PROVINCE")){
            return GeospatialRegion.GeospatialScaleGrade.PROVINCE;
        }else if(geospatialScaleGradeValue.equals("PREFECTURE")){
            return GeospatialRegion.GeospatialScaleGrade.PREFECTURE;
        }else if(geospatialScaleGradeValue.equals("COUNTY")){
            return GeospatialRegion.GeospatialScaleGrade.COUNTY;
        }else if(geospatialScaleGradeValue.equals("TOWNSHIP")){
            return GeospatialRegion.GeospatialScaleGrade.TOWNSHIP;
        }else if(geospatialScaleGradeValue.equals("VILLAGE")){
            return GeospatialRegion.GeospatialScaleGrade.VILLAGE;
        }
        return null;
    }
}
