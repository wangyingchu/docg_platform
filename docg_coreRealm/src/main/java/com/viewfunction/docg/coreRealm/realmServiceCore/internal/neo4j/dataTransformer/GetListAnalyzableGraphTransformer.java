package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AnalyzableGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AnalyzableGraphAttributeInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GraphDegreeDistributionInfo;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.time.ZonedDateTime;
import java.util.*;

public class GetListAnalyzableGraphTransformer  implements DataTransformer<List<AnalyzableGraph>>{

    private GraphOperationExecutor workingGraphOperationExecutor;
    private String currentCoreRealmName;

    public GetListAnalyzableGraphTransformer(String currentCoreRealmName,GraphOperationExecutor workingGraphOperationExecutor){
        this.currentCoreRealmName= currentCoreRealmName;
        this.workingGraphOperationExecutor = workingGraphOperationExecutor;
    }

    @Override
    public List<AnalyzableGraph> transformResult(Result result) {
        List<AnalyzableGraph> analyzableGraphList = new ArrayList<>();

        if(result.hasNext()){
            while(result.hasNext()){
                Record nodeRecord = result.next();
                String graphName = nodeRecord.get("graphName").asString();
                Map<String,Object > degreeDistributionMap = nodeRecord.get("degreeDistribution").asMap();

                long p50 = (Long)degreeDistributionMap.get("p50");
                long p75 = (Long)degreeDistributionMap.get("p75");
                long p90 = (Long)degreeDistributionMap.get("p90");
                long p95 = (Long)degreeDistributionMap.get("p95");
                long p99 = (Long)degreeDistributionMap.get("p99");
                long p999 = (Long)degreeDistributionMap.get("p999");
                long max = (Long)degreeDistributionMap.get("max");
                long min = (Long)degreeDistributionMap.get("min");
                double mean = (Double)degreeDistributionMap.get("mean");

                GraphDegreeDistributionInfo graphDegreeDistributionInfo = new GraphDegreeDistributionInfo(graphName,p50,p75,
                        p90,p95,p99,p999,max,min, mean);

                long conceptionEntityCount = nodeRecord.get("nodeCount").asLong();
                long relationEntityCount = nodeRecord.get("relationshipCount").asLong();
                float graphDensity = nodeRecord.get("density").asNumber().floatValue();

                ZonedDateTime createDate = nodeRecord.get("creationTime").asZonedDateTime();
                Date createDateValue = Date.from(createDate.toInstant());
                ZonedDateTime lastModifyDate = nodeRecord.get("modificationTime").asZonedDateTime();
                Date laseModifyDateValue = Date.from(lastModifyDate.toInstant());

                Map<String,Object> schemaMap = nodeRecord.get("schema").asMap();
                Map<String,Object> conceptionKindMetaInfoMap = (Map<String,Object>)schemaMap.get("nodes");
                Map<String,Object> relationKindMetaInfoMap = (Map<String,Object>)schemaMap.get("relationships");

                Map<String,List<AnalyzableGraphAttributeInfo>> conceptionKindsAttributesInfo = new HashMap<>();
                Map<String,List<AnalyzableGraphAttributeInfo>> relationKindsAttributesInfo = new HashMap<>();

                Set<String> conceptionKindNameSet = conceptionKindMetaInfoMap.keySet();
                Set<String> relationKindNameSet = relationKindMetaInfoMap.keySet();

                for(String conceptionKindName:conceptionKindNameSet){
                    List<AnalyzableGraphAttributeInfo> propertiesInfoList = new ArrayList<>();
                    Map<String,Object> propertiesInfoMap = (Map<String,Object>)conceptionKindMetaInfoMap.get(conceptionKindName);
                    if(propertiesInfoMap != null){
                        Set<String> propertyNameSet = propertiesInfoMap.keySet();
                        for(String currentProperty:propertyNameSet){
                            propertiesInfoList.add(new AnalyzableGraphAttributeInfo(currentProperty,propertiesInfoMap.get(currentProperty).toString()));
                        }
                    }
                    conceptionKindsAttributesInfo.put(conceptionKindName,propertiesInfoList);
                }

                for(String relationKindName:relationKindNameSet){
                    List<AnalyzableGraphAttributeInfo> propertiesInfoList = new ArrayList<>();
                    Map<String,Object> propertiesInfoMap = (Map<String,Object>)relationKindMetaInfoMap.get(relationKindName);
                    if(propertiesInfoMap != null){
                        Set<String> propertyNameSet = propertiesInfoMap.keySet();
                        for(String currentProperty:propertyNameSet){
                            propertiesInfoList.add(new AnalyzableGraphAttributeInfo(currentProperty,propertiesInfoMap.get(currentProperty).toString()));
                        }
                    }
                    relationKindsAttributesInfo.put(relationKindName,propertiesInfoList);
                }

                AnalyzableGraph analyzableGraph = new AnalyzableGraph(graphName,createDateValue);
                analyzableGraph.setGraphDegreeDistribution(graphDegreeDistributionInfo);
                analyzableGraph.setConceptionEntityCount(conceptionEntityCount);
                analyzableGraph.setRelationEntityCount(relationEntityCount);
                analyzableGraph.setGraphDensity(graphDensity);
                analyzableGraph.setLastModifyTime(laseModifyDateValue);
                analyzableGraph.setContainsConceptionKinds(conceptionKindNameSet);
                analyzableGraph.setContainsRelationKinds(relationKindNameSet);
                analyzableGraph.setConceptionKindsAttributesInfo(conceptionKindsAttributesInfo);
                analyzableGraph.setRelationKindsAttributesInfo(relationKindsAttributesInfo);
                analyzableGraphList.add(analyzableGraph);
            }
        }
        return analyzableGraphList;
    }
}
