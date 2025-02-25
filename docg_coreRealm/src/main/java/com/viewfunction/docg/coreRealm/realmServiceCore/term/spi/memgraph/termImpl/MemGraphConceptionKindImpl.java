package com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termImpl;

import com.google.common.collect.Lists;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.AttributesParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.filteringItem.FilteringItem;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.CypherBuilder;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.spi.common.payloadImpl.CommonEntitiesOperationResultImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.memgraph.termInf.MemGraphConceptionKind;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionEntityImpl;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.spi.neo4j.termImpl.Neo4JConceptionKindImpl;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemGraphConceptionKindImpl extends Neo4JConceptionKindImpl implements MemGraphConceptionKind {

    private static Logger logger = LoggerFactory.getLogger(MemGraphConceptionKindImpl.class);
    private String coreRealmName;
    private String conceptionKindName;
    private String conceptionKindDesc;
    private String conceptionKindUID;

    public MemGraphConceptionKindImpl(String coreRealmName, String conceptionKindName, String conceptionKindDesc, String conceptionKindUID) {
        super(coreRealmName, conceptionKindName, conceptionKindDesc, conceptionKindUID);
        this.coreRealmName = coreRealmName;
        this.conceptionKindName = conceptionKindName;
        this.conceptionKindDesc = conceptionKindDesc;
        this.conceptionKindUID = conceptionKindUID;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    @Override
    public EntitiesOperationResult purgeAllEntities() throws CoreRealmServiceRuntimeException {
        try{
            CommonEntitiesOperationResultImpl commonEntitiesOperationResultImpl = new CommonEntitiesOperationResultImpl();
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();

            String cql = "MATCH (n:`"+this.conceptionKindName+"`) DETACH DELETE n" ;
            logger.debug("Generated Cypher Statement: {}", cql);
            DataTransformer dataTransformer = new DataTransformer() {
                @Override
                public Object transformResult(Result result) {
                    if(result.hasNext()){
                        Record resultRecord = result.next();
                        if(resultRecord.containsKey("total")){
                            long deletedRecordCount = resultRecord.get("total").asLong();
                            commonEntitiesOperationResultImpl.getOperationStatistics().setSuccessItemsCount(deletedRecordCount);
                        }
                    }
                    return null;
                }
            };
            workingGraphOperationExecutor.executeWrite(dataTransformer, cql);
            commonEntitiesOperationResultImpl.getOperationStatistics().
                    setOperationSummary("purgeAllEntities operation for conceptionKind "+this.conceptionKindName+" success.");

            commonEntitiesOperationResultImpl.finishEntitiesOperation();
            return commonEntitiesOperationResultImpl;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public Set<ConceptionEntity> getRandomEntities(int entitiesCount) throws CoreRealmServiceEntityExploreException {
        if(entitiesCount < 1){
            logger.error("entitiesCount must equal or great then 1.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("entitiesCount must equal or great then 1.");
            throw exception;
        }
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try{
            String queryCql = "MATCH (n:`"+this.conceptionKindName+"`) RETURN uniformSample(COLLECT(n),"+entitiesCount+") AS " + CypherBuilder.operationResultName;
            logger.debug("Generated Cypher Statement: {}", queryCql);
            RandomItemsConceptionEntitySetDataTransformer randomItemsConceptionEntitySetDataTransformer =
                    new RandomItemsConceptionEntitySetDataTransformer(workingGraphOperationExecutor);
            Object queryRes = workingGraphOperationExecutor.executeRead(randomItemsConceptionEntitySetDataTransformer,queryCql);
            if(queryRes != null){
                Set<ConceptionEntity> resultConceptionEntityList = (Set<ConceptionEntity>)queryRes;
                return resultConceptionEntityList;
            }
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }

    @Override
    public Set<ConceptionEntity> getRandomEntities(AttributesParameters attributesParameters, boolean isDistinctMode, int entitiesCount) throws CoreRealmServiceEntityExploreException, CoreRealmServiceRuntimeException {
        if(entitiesCount < 1){
            logger.error("entitiesCount must equal or great then 1.");
            CoreRealmServiceEntityExploreException exception = new CoreRealmServiceEntityExploreException();
            exception.setCauseMessage("entitiesCount must equal or great then 1.");
            throw exception;
        }
        if (attributesParameters != null) {
            QueryParameters queryParameters = new QueryParameters();
            queryParameters.setDistinctMode(isDistinctMode);
            queryParameters.setResultNumber(100000000);
            queryParameters.setDefaultFilteringItem(attributesParameters.getDefaultFilteringItem());
            if (attributesParameters.getAndFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getAndFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.AND);
                }
            }
            if (attributesParameters.getOrFilteringItemsList() != null) {
                for (FilteringItem currentFilteringItem : attributesParameters.getOrFilteringItemsList()) {
                    queryParameters.addFilteringItem(currentFilteringItem, QueryParameters.FilteringLogic.OR);
                }
            }
            GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
            try{
                String queryCql = CypherBuilder.matchNodesWithQueryParameters(this.conceptionKindName,queryParameters,null);
                String replaceContent = isDistinctMode ? "RETURN DISTINCT "+CypherBuilder.operationResultName+" LIMIT 100000000":
                        "RETURN "+CypherBuilder.operationResultName+" LIMIT 100000000";
                String newContent = isDistinctMode ? "RETURN uniformSample(COLLECT("+CypherBuilder.operationResultName+"),"+entitiesCount+",false) AS " +CypherBuilder.operationResultName:
                        "RETURN apoc.coll.randomItems(COLLECT("+CypherBuilder.operationResultName+"),"+entitiesCount+",true) AS " +CypherBuilder.operationResultName;
                queryCql = queryCql.replace(replaceContent,newContent);
                logger.debug("Generated Cypher Statement: {}", queryCql);
                RandomItemsConceptionEntitySetDataTransformer randomItemsConceptionEntitySetDataTransformer =
                        new RandomItemsConceptionEntitySetDataTransformer(workingGraphOperationExecutor);
                Object queryRes = workingGraphOperationExecutor.executeRead(randomItemsConceptionEntitySetDataTransformer,queryCql);
                if(queryRes != null){
                    Set<ConceptionEntity> resultConceptionEntityList = (Set<ConceptionEntity>)queryRes;
                    return resultConceptionEntityList;
                }
            }finally {
                this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
            }
            return null;

        }else{
            return getRandomEntities(entitiesCount);
        }
    }

    private class RandomItemsConceptionEntitySetDataTransformer implements DataTransformer<Set<ConceptionEntity>>{
        GraphOperationExecutor workingGraphOperationExecutor;
        public RandomItemsConceptionEntitySetDataTransformer(GraphOperationExecutor workingGraphOperationExecutor){
            this.workingGraphOperationExecutor = workingGraphOperationExecutor;
        }
        @Override
        public Set<ConceptionEntity> transformResult(Result result) {
            Set<ConceptionEntity> conceptionEntitySet = new HashSet<>();
            if(result.hasNext()){
                List<Value> resultList = result.next().values();
                if(resultList.size() > 0){
                    List<Object> nodeObjList = resultList.get(0).asList();
                    for(Object currentNodeObj : nodeObjList){
                        Node resultNode = (Node)currentNodeObj;
                        List<String> allConceptionKindNames = Lists.newArrayList(resultNode.labels());
                        boolean isMatchedConceptionKind = true;
                        if(allConceptionKindNames.size()>0){
                            isMatchedConceptionKind = allConceptionKindNames.contains(conceptionKindName);
                        }
                        if(isMatchedConceptionKind){
                            long nodeUID = resultNode.id();
                            String conceptionEntityUID = ""+nodeUID;
                            String resultConceptionKindName = conceptionKindName;
                            Neo4JConceptionEntityImpl neo4jConceptionEntityImpl =
                                    new Neo4JConceptionEntityImpl(resultConceptionKindName,conceptionEntityUID);
                            neo4jConceptionEntityImpl.setAllConceptionKindNames(allConceptionKindNames);
                            neo4jConceptionEntityImpl.setGlobalGraphOperationExecutor(getGraphOperationExecutorHelper().getGlobalGraphOperationExecutor());
                            conceptionEntitySet.add(neo4jConceptionEntityImpl);
                        }
                    }
                }
            }
            return conceptionEntitySet;
        }
    }






    //internal graphOperationExecutor management logic
    private GraphOperationExecutorHelper graphOperationExecutorHelper;

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }
}
