package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.google.common.collect.Lists;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleEventAndConceptionEntityPair;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEvent;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JGeospatialScaleEventImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.List;

public class GetListGeospatialScaleEventAndConceptionEntityPairTransformer implements DataTransformer<List<GeospatialScaleEventAndConceptionEntityPair>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String geospatialRegionName;
    private String targetConceptionKindName;

    public GetListGeospatialScaleEventAndConceptionEntityPairTransformer(String geospatialRegionName, GraphOperationExecutor workingGraphOperationExecutor){
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        this.geospatialRegionName = geospatialRegionName;
    }

    @Override
    public List<GeospatialScaleEventAndConceptionEntityPair> transformResult(Result result) {
        List<GeospatialScaleEventAndConceptionEntityPair> geospatialScaleEventAndConceptionEntityPairList = new ArrayList<>();
        while(result.hasNext()){
            Record nodeRecord = result.next();
            Node geospatialScaleEventNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
            GeospatialScaleEvent targetGeospatialScaleEvent = getGeospatialScaleEvent(geospatialScaleEventNode);

            Node conceptionEntityNode = nodeRecord.get("conceptionEntity").asNode();
            ConceptionEntity targetConceptionEntity = getConceptionEntity(conceptionEntityNode);
            if(targetGeospatialScaleEvent != null && targetConceptionEntity != null){
                GeospatialScaleEventAndConceptionEntityPair currentGeospatialScaleEventAndConceptionEntityPair = new GeospatialScaleEventAndConceptionEntityPair(targetGeospatialScaleEvent,targetConceptionEntity);
                geospatialScaleEventAndConceptionEntityPairList.add(currentGeospatialScaleEventAndConceptionEntityPair);
            }
        }
        return geospatialScaleEventAndConceptionEntityPairList;
    }

    private GeospatialScaleEvent getGeospatialScaleEvent(Node resultNode){
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
            String currentTimeFlowName = geospatialRegionName != null ? geospatialRegionName : resultNode.get(RealmConstant._GeospatialScaleEventGeospatialRegion).asString();
            Neo4JGeospatialScaleEventImpl neo4JGeospatialScaleEventImpl = new Neo4JGeospatialScaleEventImpl(currentTimeFlowName,eventComment,referLocation,getGeospatialScaleGrade(geospatialScaleGrade.trim()),geospatialScaleEventUID);
            neo4JGeospatialScaleEventImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            return neo4JGeospatialScaleEventImpl;
        }
        return null;
    }

    private ConceptionEntity getConceptionEntity(Node resultNode){
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
            String resultConceptionKindName = targetConceptionKindName != null? targetConceptionKindName:allConceptionKindNames.get(0);
            Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                    new Neo4JConceptionEntityImpl(resultConceptionKindName,conceptionEntityUID);
            neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
            neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);
            return neo4jConceptionEntityImpl;
        }
        return null;
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

    public String getTargetConceptionKindName() {
        return targetConceptionKindName;
    }

    public void setTargetConceptionKindName(String targetConceptionKindName) {
        this.targetConceptionKindName = targetConceptionKindName;
    }
}
