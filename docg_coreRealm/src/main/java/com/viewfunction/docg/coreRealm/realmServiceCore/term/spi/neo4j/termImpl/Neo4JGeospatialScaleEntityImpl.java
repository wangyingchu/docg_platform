package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListGeospatialScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleGeospatialScaleEntityTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntitiesRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.GeospatialScaleEventsRetrieveResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.InheritanceTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.spi.common.structureImpl.CommonInheritanceTreeImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialRegion;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.GeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termInf.Neo4JGeospatialScaleEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.RealmConstant;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Neo4JGeospatialScaleEntityImpl implements Neo4JGeospatialScaleEntity {

    private static Logger logger = LoggerFactory.getLogger(Neo4JGeospatialScaleEntityImpl.class);
    private String coreRealmName;
    private String geospatialScaleEntityUID;
    private String geospatialRegionName;
    private GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade;
    private String geospatialCode;
    private String entityChineseName;
    private String entityEnglishName;

    public Neo4JGeospatialScaleEntityImpl(String coreRealmName, String geospatialRegionName, String geospatialScaleEntityUID,
                                          GeospatialRegion.GeospatialScaleGrade geospatialScaleGrade, String geospatialCode,
                                          String entityChineseName,String entityEnglishName){
        this.coreRealmName = coreRealmName;
        this.geospatialRegionName = geospatialRegionName;
        this.geospatialScaleEntityUID = geospatialScaleEntityUID;
        this.geospatialScaleGrade = geospatialScaleGrade;
        this.geospatialCode = geospatialCode;
        this.entityChineseName = entityChineseName;
        this.entityEnglishName = entityEnglishName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public GeospatialRegion.GeospatialScaleGrade getGeospatialScaleGrade() {
        return this.geospatialScaleGrade;
    }

    @Override
    public String getGeospatialCode() {
        return this.geospatialCode;
    }

    @Override
    public String getChineseName() {
        return this.entityChineseName;
    }

    @Override
    public String getEnglishName() {
        return this.entityEnglishName;
    }

    @Override
    public GeospatialScaleEntity getParentEntity() {
        String queryCql = "MATCH(currentEntity:DOCG_GeospatialScaleEntity)<-[:DOCG_GS_SpatialContains]-(targetEntities:DOCG_GeospatialScaleEntity) WHERE id(currentEntity) = "+ this.getGeospatialScaleEntityUID() +" RETURN targetEntities as operationResult ORDER BY targetEntities.id LIMIT 1";
        return getSingleGeospatialScaleEntity(queryCql);
    }

    @Override
    public List<GeospatialScaleEntity> getFellowEntities() {
        GeospatialScaleEntity parentGeospatialScaleEntity = getParentEntity();
        String queryCql;
        if(parentGeospatialScaleEntity == null){
            queryCql = CypherBuilder.matchLabelWithSinglePropertyValue(RealmConstant.GeospatialScaleContinentEntityClass,RealmConstant.GeospatialRegionProperty,geospatialRegionName,100);
        }else{
            String parentEntityUID = ((Neo4JGeospatialScaleEntityImpl) parentGeospatialScaleEntity).getGeospatialScaleEntityUID();
            queryCql = "MATCH(parentEntity:DOCG_GeospatialScaleEntity)-[:DOCG_GS_SpatialContains]->(fellowEntities:DOCG_GeospatialScaleEntity) WHERE id(parentEntity) = "+ parentEntityUID +" RETURN fellowEntities as operationResult ORDER BY fellowEntities.id";
        }
        return getListGeospatialScaleEntity(queryCql);
    }

    @Override
    public List<GeospatialScaleEntity> getChildEntities() {
        String queryCql = "MATCH(currentEntity:DOCG_GeospatialScaleEntity)-[:DOCG_GS_SpatialContains]->(targetEntities:DOCG_GeospatialScaleEntity) WHERE id(currentEntity) = "+ this.getGeospatialScaleEntityUID() +" RETURN targetEntities as operationResult ORDER BY targetEntities.id";
        return getListGeospatialScaleEntity(queryCql);
    }

    @Override
    public InheritanceTree<GeospatialScaleEntity> getOffspringEntities() {
        Table<String,String, GeospatialScaleEntity> treeElementsTable = HashBasedTable.create();
        treeElementsTable.put(InheritanceTree.Virtual_ParentID_Of_Root_Node,this.geospatialScaleEntityUID,this);
        final String currentCoreRealmName = this.coreRealmName;
        final String currentGeospatialRegionName = this.geospatialRegionName;

        String queryCql = "MATCH (currentEntity:DOCG_GeospatialScaleEntity)-[relationResult:`DOCG_GS_SpatialContains`*1..7]->(operationResult:`DOCG_GeospatialScaleEntity`) WHERE id(currentEntity) = "+this.getGeospatialScaleEntityUID()+" RETURN operationResult,relationResult";
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            logger.debug("Generated Cypher Statement: {}", queryCql);

            DataTransformer offspringGeospatialScaleEntitiesDataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    List<Record> recordList = result.list();
                    if(recordList != null){
                        for(Record nodeRecord : recordList){
                            Node resultNode = nodeRecord.get(CypherBuilder.operationResultName).asNode();
                            long nodeUID = resultNode.id();
                            String entityUID = ""+nodeUID;
                            String conceptionEntityUID = ""+nodeUID;
                            String targetGeospatialCode = resultNode.get(RealmConstant.GeospatialCodeProperty).asString();
                            String targetGeospatialScaleGradeString = resultNode.get(RealmConstant.GeospatialScaleGradeProperty).asString();
                            String _ChineseName = null;
                            String _EnglishName = null;
                            if(resultNode.containsKey(RealmConstant.GeospatialChineseNameProperty)){
                                _ChineseName = resultNode.get(RealmConstant.GeospatialChineseNameProperty).asString();
                            }
                            if(resultNode.containsKey(RealmConstant.GeospatialEnglishNameProperty)){
                                _EnglishName = resultNode.get(RealmConstant.GeospatialEnglishNameProperty).asString();
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
                                    new Neo4JGeospatialScaleEntityImpl(currentCoreRealmName,currentGeospatialRegionName,conceptionEntityUID,geospatialScaleGrade,targetGeospatialCode,_ChineseName,_EnglishName);
                            neo4JGeospatialScaleEntityImpl.setGlobalGraphOperationExecutor(workingGraphOperationExecutor);

                            List<Object> relationships = nodeRecord.get(CypherBuilder.relationResultName).asList();
                            String parentClassificationUID = null;
                            for(Object currentRelationship : relationships){
                                Relationship currentTargetRelationship = (Relationship)currentRelationship;
                                String startNodeUID = "" + currentTargetRelationship.startNodeId();
                                String endNodeUID = "" + currentTargetRelationship.endNodeId();
                                if(endNodeUID.equals(entityUID)){
                                    parentClassificationUID = startNodeUID;
                                    break;
                                }
                            }
                            treeElementsTable.put(parentClassificationUID,entityUID,neo4JGeospatialScaleEntityImpl);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeRead(offspringGeospatialScaleEntitiesDataTransformer,queryCql);
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }

        CommonInheritanceTreeImpl<GeospatialScaleEntity> resultInheritanceTree = new CommonInheritanceTreeImpl(this.geospatialScaleEntityUID,treeElementsTable);
        return resultInheritanceTree;
    }

    @Override
    public Long countAttachedGeospatialScaleEvents(AttributesParameters attributesParameters, boolean isDistinctMode, GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }

    @Override
    public GeospatialScaleEventsRetrieveResult getAttachedGeospatialScaleEvents(QueryParameters queryParameters, GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }

    @Override
    public Long countAttachedConceptionEntities(GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }

    @Override
    public Long countAttachedConceptionEntities(String conceptionKindName, AttributesParameters attributesParameters, boolean isDistinctMode, GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }

    @Override
    public ConceptionEntitiesRetrieveResult getAttachedConceptionEntities(String conceptionKindName, QueryParameters queryParameters, GeospatialScaleLevel geospatialScaleLevel) {
        return null;
    }

    private List<GeospatialScaleEntity> getListGeospatialScaleEntity(String queryCql){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetListGeospatialScaleEntityTransformer getListGeospatialScaleEntityTransformer =
                    new GetListGeospatialScaleEntityTransformer(this.coreRealmName, this.geospatialRegionName, workingGraphOperationExecutor);
            Object queryRes = workingGraphOperationExecutor.executeRead(getListGeospatialScaleEntityTransformer,queryCql);
            if(queryRes != null){
                return (List<GeospatialScaleEntity>)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return new ArrayList<>();
    }

    private GeospatialScaleEntity getSingleGeospatialScaleEntity(String queryCql){
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            logger.debug("Generated Cypher Statement: {}", queryCql);
            GetSingleGeospatialScaleEntityTransformer getSingleGeospatialScaleEntityTransformer =
                    new GetSingleGeospatialScaleEntityTransformer(this.coreRealmName, this.geospatialRegionName, workingGraphOperationExecutor);
            Object queryRes = workingGraphOperationExecutor.executeRead(getSingleGeospatialScaleEntityTransformer,queryCql);
            if(queryRes != null){
                return (GeospatialScaleEntity)queryRes;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    public String getGeospatialScaleEntityUID() {
        return geospatialScaleEntityUID;
    }

    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
