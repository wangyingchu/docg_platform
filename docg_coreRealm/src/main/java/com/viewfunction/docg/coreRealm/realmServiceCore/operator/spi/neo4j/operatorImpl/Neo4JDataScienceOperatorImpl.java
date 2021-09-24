package com.viewfunction.docg.coreRealm.realmServiceCore.operator.spi.neo4j.operatorImpl;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetListAnalyzableGraphTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.GetSingleAnalyzableGraphTransformer;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util.GraphOperationExecutorHelper;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.DataScienceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AnalyzableGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult.*;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Neo4JDataScienceOperatorImpl implements DataScienceOperator {

    private String coreRealmName;
    private GraphOperationExecutorHelper graphOperationExecutorHelper;
    private static Logger logger = LoggerFactory.getLogger(DataScienceOperator.class);

    public Neo4JDataScienceOperatorImpl(String coreRealmName){
        this.coreRealmName = coreRealmName;
        this.graphOperationExecutorHelper = new GraphOperationExecutorHelper();
    }

    public void setGlobalGraphOperationExecutor(GraphOperationExecutor graphOperationExecutor) {
        this.graphOperationExecutorHelper.setGlobalGraphOperationExecutor(graphOperationExecutor);
    }

    @Override
    public List<AnalyzableGraph> getAnalyzableGraphs() {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/graph-catalog-ops/#catalog-graph-list
        */
        String cypherProcedureString = "CALL gds.graph.list();";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GetListAnalyzableGraphTransformer getListAnalyzableGraphTransformer =
                new GetListAnalyzableGraphTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Object queryResponse = workingGraphOperationExecutor.executeRead(getListAnalyzableGraphTransformer,cypherProcedureString);
            return queryResponse != null ? (List<AnalyzableGraph>) queryResponse : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean checkAnalyzableGraphExistence(String graphName) {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/graph-catalog-ops/#catalog-graph-exists
        */
        String cypherProcedureString = "CALL gds.graph.exists('"+graphName+"') YIELD exists;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        DataTransformer<Boolean> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                if(result.hasNext()){
                    Record nodeRecord = result.next();
                    return nodeRecord.get("exists").asBoolean();
                }
                return false;
            }
        };

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Object queryResponse = workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            return queryResponse != null ? (Boolean)queryResponse : false;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public AnalyzableGraph getAnalyzableGraph(String graphName) {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/graph-catalog-ops/#catalog-graph-list
        */
        String cypherProcedureString = "CALL gds.graph.list('"+graphName+"');";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        GetSingleAnalyzableGraphTransformer getSingleAnalyzableGraphTransformer =
                new GetSingleAnalyzableGraphTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Object queryResponse = workingGraphOperationExecutor.executeRead(getSingleAnalyzableGraphTransformer,cypherProcedureString);
            return queryResponse != null ? (AnalyzableGraph) queryResponse : null;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public boolean removeAnalyzableGraph(String graphName) throws CoreRealmServiceRuntimeException{
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/graph-catalog-ops/#catalog-graph-drop
        */
        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(!checkGraphExistence){
            logger.error("AnalyzableGraph with name {} does not exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" does not exist");
            throw e;
        }

        String cypherProcedureString = "CALL gds.graph.drop('"+graphName+"') YIELD graphName;";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        DataTransformer<Boolean> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                if(result.hasNext()){
                    Record nodeRecord = result.next();
                    String removedGraphName = nodeRecord.get("graphName").asString();
                    if(graphName.equals(removedGraphName)){
                        return true;
                    }else{
                        return false;
                    }
                }
                return false;
            }
        };

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            Object queryResponse = workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            return queryResponse != null ? (Boolean)queryResponse : false;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public AnalyzableGraph createAnalyzableGraph(String graphName,List<String> conceptionKindList,Set<String> conceptionKindAttributeSet,
                                                 List<String> relationKindList,Set<String> relationKindAttributeSet) throws CoreRealmServiceRuntimeException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/native-projection/
        */
        if(conceptionKindList == null || conceptionKindList.size() == 0){
            logger.error("At least one ConceptionKind is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("At least one ConceptionKind is required");
            throw e;
        }

        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(checkGraphExistence){
            logger.error("AnalyzableGraph with name {} already exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" already exist");
            throw e;
        }

        String conceptionKindsString = getKindNamesArrayString(conceptionKindList);
        String relationKindsString = ( relationKindList == null || relationKindList.size() == 0) ? "'*'" :
                getKindNamesArrayString(relationKindList);
        String globalKindPropertiesString = getGlobalKindPropertiesString(conceptionKindAttributeSet,relationKindAttributeSet);
        String cypherProcedureString = "CALL gds.graph.create('"+graphName+"', "+conceptionKindsString+", "+ relationKindsString+globalKindPropertiesString+")";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        return executeCreateAnalyzableGraphOperation(graphName,cypherProcedureString);
    }

    @Override
    public AnalyzableGraph createAnalyzableGraph(String graphName) throws CoreRealmServiceRuntimeException{
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/native-projection/
        */
        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(checkGraphExistence){
            logger.error("AnalyzableGraph with name {} already exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" already exist");
            throw e;
        }

        String cypherProcedureString = "CALL gds.graph.create('"+graphName+"','*','*')";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        return executeCreateAnalyzableGraphOperation(graphName,cypherProcedureString);
    }

    @Override
    public AnalyzableGraph createAnalyzableGraph(String graphName, Map<String, Set<String>> conceptionKindInfoMap, Map<String, Set<String>> relationKindInfoMap) throws CoreRealmServiceRuntimeException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/native-projection/#native-projection-syntax-node-projections
        */
        if(conceptionKindInfoMap == null || conceptionKindInfoMap.size() ==0){
            logger.error("At least one ConceptionKind is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("At least one ConceptionKind is required");
            throw e;
        }
        if(relationKindInfoMap == null || relationKindInfoMap.size() ==0){
            logger.error("At least one RelationKind is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("At least one RelationKind is required");
            throw e;
        }
        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(checkGraphExistence){
            logger.error("AnalyzableGraph with name {} already exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" already exist");
            throw e;
        }

        String conceptionKindDefinitionStr = getConceptionKindAndAttributesDefinition(conceptionKindInfoMap);
        String relationKindDefinitionStr = getRelationKindAndAttributesDefinition(relationKindInfoMap);
        String cypherProcedureString = "CALL gds.graph.create('"+graphName+"',"+conceptionKindDefinitionStr+","+relationKindDefinitionStr+")";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        return executeCreateAnalyzableGraphOperation(graphName,cypherProcedureString);
    }

    @Override
    public AnalyzableGraph createAnalyzableGraph(String graphName, String conceptionEntitiesQuery, String relationEntitiesQuery) throws CoreRealmServiceRuntimeException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/cypher-projection/
        */
        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(checkGraphExistence){
            logger.error("AnalyzableGraph with name {} already exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" already exist");
            throw e;
        }
        String cypherProcedureString = "CALL gds.graph.create.cypher(\n" +
                "    '"+graphName+"',\n" +
                "    '"+conceptionEntitiesQuery+"',\n" +
                "    '"+relationEntitiesQuery+"'\n" +
                ")";
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        return executeCreateAnalyzableGraphOperation(graphName,cypherProcedureString);
    }

    @Override
    public PageRankAlgorithmResult executePageRankAlgorithm(String graphName, PageRankAlgorithmConfig pageRankAlgorithmConfig) throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException {
        return doExecutePageRankAlgorithms(graphName,null,pageRankAlgorithmConfig);
    }

    @Override
    public PageRankAlgorithmResult executePersonalisedPageRankAlgorithm(String graphName, PersonalizedPageRankAlgorithmConfig personalizedPageRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        if(personalizedPageRankAlgorithmConfig == null ||
                personalizedPageRankAlgorithmConfig.getPersonalizedPageRankEntityUIDs() == null ||
                personalizedPageRankAlgorithmConfig.getPersonalizedPageRankEntityUIDs().size() ==0){
            logger.error("personalized PageRank EntityUIDs is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("personalized PageRank EntityUIDs is required");
            throw e;
        }
        return doExecutePageRankAlgorithms(graphName,personalizedPageRankAlgorithmConfig.getPersonalizedPageRankEntityUIDs(),personalizedPageRankAlgorithmConfig);
    }

    @Override
    public ArticleRankAlgorithmResult executeArticleRankAlgorithm(String graphName, ArticleRankAlgorithmConfig articleRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        return doExecuteArticleRankAlgorithms(graphName,null,articleRankAlgorithmConfig);
    }

    @Override
    public ArticleRankAlgorithmResult executePersonalisedArticleRankAlgorithm(String graphName, PersonalizedArticleRankAlgorithmConfig personalizedArticleRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        if(personalizedArticleRankAlgorithmConfig == null ||
                personalizedArticleRankAlgorithmConfig.getPersonalizedArticleRankEntityUIDs() == null ||
                personalizedArticleRankAlgorithmConfig.getPersonalizedArticleRankEntityUIDs().size() ==0){
            logger.error("personalized ArticleRank EntityUIDs is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("personalized ArticleRank EntityUIDs is required");
            throw e;
        }
        return doExecuteArticleRankAlgorithms(graphName,personalizedArticleRankAlgorithmConfig.getPersonalizedArticleRankEntityUIDs(),personalizedArticleRankAlgorithmConfig);
    }

    @Override
    public EigenvectorCentralityAlgorithmResult executeEigenvectorCentralityAlgorithm(String graphName, EigenvectorCentralityAlgorithmConfig eigenvectorCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        return doExecuteEigenvectorCentrality(graphName,null,eigenvectorCentralityAlgorithmConfig);
    }

    @Override
    public EigenvectorCentralityAlgorithmResult executePersonalisedEigenvectorCentralityAlgorithm(String graphName, PersonalisedEigenvectorCentralityAlgorithmConfig personalisedEigenvectorCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        if(personalisedEigenvectorCentralityAlgorithmConfig == null ||
                personalisedEigenvectorCentralityAlgorithmConfig.getPersonalizedEigenvectorCentralityEntityUIDs() == null ||
                personalisedEigenvectorCentralityAlgorithmConfig.getPersonalizedEigenvectorCentralityEntityUIDs().size() ==0){
            logger.error("personalized EigenvectorCentrality EntityUIDs is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("personalized EigenvectorCentrality EntityUIDs is required");
            throw e;
        }
        return doExecuteEigenvectorCentrality(graphName,personalisedEigenvectorCentralityAlgorithmConfig.getPersonalizedEigenvectorCentralityEntityUIDs(),
                personalisedEigenvectorCentralityAlgorithmConfig);
    }

    @Override
    public BetweennessCentralityAlgorithmResult executeBetweennessCentralityAlgorithm(String graphName, BetweennessCentralityAlgorithmConfig betweennessCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/betweenness-centrality/
        */
        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(!checkGraphExistence){
            logger.error("AnalyzableGraph with name {} does not exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" does not exist");
            throw e;
        }

        BetweennessCentralityAlgorithmConfig betweennessCentralityAlgorithmConfiguration = betweennessCentralityAlgorithmConfig != null ?
                betweennessCentralityAlgorithmConfig : new BetweennessCentralityAlgorithmConfig();
        Set<String> conceptionKindsForCompute = betweennessCentralityAlgorithmConfiguration.getConceptionKindsForCompute();
        Set<String> relationKindsForCompute = betweennessCentralityAlgorithmConfiguration.getRelationKindsForCompute();

        String nodeLabelsCQLPart = "";
        if(conceptionKindsForCompute != null && conceptionKindsForCompute.size()>0){
            nodeLabelsCQLPart = "  nodeLabels: "+getKindNamesSetString(conceptionKindsForCompute)+",\n";
        }
        String relationshipTypes = "";
        if(relationKindsForCompute != null && relationKindsForCompute.size()>0){
            relationshipTypes = "  relationshipTypes: "+getKindNamesSetString(relationKindsForCompute)+",\n";
        }

        String samplingSizeAttributeCQLPart = betweennessCentralityAlgorithmConfig.getSamplingSize() != null ?
                "  samplingSize: "+betweennessCentralityAlgorithmConfig.getSamplingSize().intValue()+",\n" : "";
        String samplingSeedAttributeCQLPart = betweennessCentralityAlgorithmConfig.getSamplingSeed() != null ?
                "  samplingSeed: "+betweennessCentralityAlgorithmConfig.getSamplingSeed().intValue()+",\n" : "";
        String orderCQLPart = betweennessCentralityAlgorithmConfig.getScoreSortingLogic()!= null ?
                "ORDER BY score "+betweennessCentralityAlgorithmConfig.getScoreSortingLogic().toString() : "";

        String cypherProcedureString =
                "CALL gds.betweenness.stream('"+graphName+"', {\n" +
                nodeLabelsCQLPart +
                relationshipTypes +
                samplingSizeAttributeCQLPart +
                samplingSeedAttributeCQLPart +
                "  concurrency: 4 \n" +
                "})\n" +
                "YIELD nodeId, score\n" +
                //"RETURN gds.util.asNode(nodeId) AS node, score\n" +
                "RETURN nodeId AS entityUID, score\n" +
                orderCQLPart+
                getReturnDataControlLogic(betweennessCentralityAlgorithmConfig);
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        BetweennessCentralityAlgorithmResult betweennessCentralityAlgorithmResult = new BetweennessCentralityAlgorithmResult(graphName,betweennessCentralityAlgorithmConfig);
        List<EntityAnalyzeResult> entityAnalyzeResultList = betweennessCentralityAlgorithmResult.getBetweennessCentralityScores();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityUID = nodeRecord.get("entityUID").asLong();
                    double pageRankScore = nodeRecord.get("score").asNumber().doubleValue();
                    entityAnalyzeResultList.add(new EntityAnalyzeResult(""+entityUID,pageRankScore));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            betweennessCentralityAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return betweennessCentralityAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    private PageRankAlgorithmResult doExecutePageRankAlgorithms(String graphName, Set<String> conceptionEntityUIDSet,PageRankAlgorithmConfig pageRankAlgorithmConfig) throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/page-rank/
        */
        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(!checkGraphExistence){
            logger.error("AnalyzableGraph with name {} does not exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" does not exist");
            throw e;
        }
        String cypherProcedureString = getRankAlgorithmsCQL(graphName, "pageRank",conceptionEntityUIDSet,pageRankAlgorithmConfig,true);
        PageRankAlgorithmResult pageRankAlgorithmResult = new PageRankAlgorithmResult(graphName,pageRankAlgorithmConfig);
        List<EntityAnalyzeResult> entityAnalyzeResultList = pageRankAlgorithmResult.getPageRankScores();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityUID = nodeRecord.get("entityUID").asLong();
                    double pageRankScore = nodeRecord.get("score").asNumber().doubleValue();
                    entityAnalyzeResultList.add(new EntityAnalyzeResult(""+entityUID,pageRankScore));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            pageRankAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return pageRankAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    private ArticleRankAlgorithmResult doExecuteArticleRankAlgorithms(String graphName, Set<String> conceptionEntityUIDSet,ArticleRankAlgorithmConfig articleRankAlgorithmConfig) throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/article-rank/
        */
        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(!checkGraphExistence){
            logger.error("AnalyzableGraph with name {} does not exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" does not exist");
            throw e;
        }
        String cypherProcedureString = getRankAlgorithmsCQL(graphName, "articleRank",conceptionEntityUIDSet,articleRankAlgorithmConfig,true);
        ArticleRankAlgorithmResult articleRankAlgorithmResult = new ArticleRankAlgorithmResult(graphName,articleRankAlgorithmConfig);
        List<EntityAnalyzeResult> entityAnalyzeResultList = articleRankAlgorithmResult.getArticleRankScores();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityUID = nodeRecord.get("entityUID").asLong();
                    double pageRankScore = nodeRecord.get("score").asNumber().doubleValue();
                    entityAnalyzeResultList.add(new EntityAnalyzeResult(""+entityUID,pageRankScore));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            articleRankAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return articleRankAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    private EigenvectorCentralityAlgorithmResult doExecuteEigenvectorCentrality(String graphName, Set<String> conceptionEntityUIDSet,EigenvectorCentralityAlgorithmConfig eigenvectorCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException,CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/eigenvector-centrality/
        */
        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(!checkGraphExistence){
            logger.error("AnalyzableGraph with name {} does not exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" does not exist");
            throw e;
        }

        PageRankAlgorithmConfig pageRankAlgorithmConfig = new PageRankAlgorithmConfig();
        if(eigenvectorCentralityAlgorithmConfig != null){
            pageRankAlgorithmConfig.setStartPage(eigenvectorCentralityAlgorithmConfig.getStartPage());
            pageRankAlgorithmConfig.setEndPage(eigenvectorCentralityAlgorithmConfig.getEndPage());
            pageRankAlgorithmConfig.setPageSize(eigenvectorCentralityAlgorithmConfig.getPageSize());
            pageRankAlgorithmConfig.setResultNumber(eigenvectorCentralityAlgorithmConfig.getResultNumber());

            pageRankAlgorithmConfig.setConceptionKindsForCompute(eigenvectorCentralityAlgorithmConfig.getConceptionKindsForCompute());
            pageRankAlgorithmConfig.setRelationKindsForCompute(eigenvectorCentralityAlgorithmConfig.getRelationKindsForCompute());

            pageRankAlgorithmConfig.setMaxIterations(eigenvectorCentralityAlgorithmConfig.getMaxIterations());
            pageRankAlgorithmConfig.setTolerance(eigenvectorCentralityAlgorithmConfig.getTolerance());
            pageRankAlgorithmConfig.setRelationshipWeightAttribute(eigenvectorCentralityAlgorithmConfig.getRelationshipWeightAttribute());
            pageRankAlgorithmConfig.setScoreScalerLogic(eigenvectorCentralityAlgorithmConfig.getScoreScalerLogic());
            pageRankAlgorithmConfig.setScoreSortingLogic(eigenvectorCentralityAlgorithmConfig.getScoreSortingLogic());
        }

        String cypherProcedureString = getRankAlgorithmsCQL(graphName, "eigenvector",conceptionEntityUIDSet,pageRankAlgorithmConfig,false);
        EigenvectorCentralityAlgorithmResult eigenvectorCentralityAlgorithmResult = new EigenvectorCentralityAlgorithmResult(graphName,eigenvectorCentralityAlgorithmConfig);
        List<EntityAnalyzeResult> entityAnalyzeResultList = eigenvectorCentralityAlgorithmResult.getEigenvectorCentralityScores();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityUID = nodeRecord.get("entityUID").asLong();
                    double pageRankScore = nodeRecord.get("score").asNumber().doubleValue();
                    entityAnalyzeResultList.add(new EntityAnalyzeResult(""+entityUID,pageRankScore));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            eigenvectorCentralityAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return eigenvectorCentralityAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    private String getRankAlgorithmsCQL(String graphName, String algorithm,Set<String> conceptionEntityUIDSet,PageRankAlgorithmConfig pageRankAlgorithmConfig,boolean usingDampingFactor) throws CoreRealmServiceEntityExploreException {
        PageRankAlgorithmConfig pageRankAlgorithmConfiguration = pageRankAlgorithmConfig != null ? pageRankAlgorithmConfig :
                new PageRankAlgorithmConfig();

        Set<String> conceptionKindsForCompute = pageRankAlgorithmConfiguration.getConceptionKindsForCompute();
        Set<String> relationKindsForCompute = pageRankAlgorithmConfiguration.getRelationKindsForCompute();
        String relationshipWeightAttribute = pageRankAlgorithmConfiguration.getRelationshipWeightAttribute();

        String queryEntitiesByIDCQLPart = "";
        String sourceNodesCQLPart= "";
        if(conceptionEntityUIDSet != null && conceptionEntityUIDSet.size()>0){
            queryEntitiesByIDCQLPart =  "MATCH (targetNodes) WHERE id(targetNodes) IN " + conceptionEntityUIDSet.toString()+"\n"+
                    "with collect(targetNodes) as pSourceNodes\n";
            sourceNodesCQLPart = "  sourceNodes: pSourceNodes"+",\n";
        }

        String nodeLabelsCQLPart = "";
        if(conceptionKindsForCompute != null && conceptionKindsForCompute.size()>0){
            nodeLabelsCQLPart = "  nodeLabels: "+getKindNamesSetString(conceptionKindsForCompute)+",\n";
        }
        String relationshipTypes = "";
        if(relationKindsForCompute != null && relationKindsForCompute.size()>0){
            relationshipTypes = "  relationshipTypes: "+getKindNamesSetString(relationKindsForCompute)+",\n";
        }
        String relationshipWeightAttributeCQLPart = relationshipWeightAttribute != null ?
                "  relationshipWeightProperty: '"+relationshipWeightAttribute+"',\n" : "";
        String scalerCQLPart = pageRankAlgorithmConfiguration.getScoreScalerLogic() != null ?
                "  scaler: '"+pageRankAlgorithmConfiguration.getScoreScalerLogic()+"',\n" : "";
        String orderCQLPart = pageRankAlgorithmConfig.getScoreSortingLogic()!= null ?
                "ORDER BY score "+pageRankAlgorithmConfig.getScoreSortingLogic().toString() : "";

        // this logic is used for executeEigenvectorCentralityAlgorithm,Eigenvector Centrality does not use DampingFactor
        String dampingFactorCQLPart = usingDampingFactor?
                "  dampingFactor: "+pageRankAlgorithmConfiguration.getDampingFactor()+",\n":"";

        String cypherProcedureString = queryEntitiesByIDCQLPart +
                "CALL gds."+algorithm+".stream('"+graphName+"', {\n" +
                nodeLabelsCQLPart+
                relationshipTypes+
                scalerCQLPart+
                relationshipWeightAttributeCQLPart +
                sourceNodesCQLPart +
                "  maxIterations: "+pageRankAlgorithmConfiguration.getMaxIterations()+",\n" +
                //"  dampingFactor: "+pageRankAlgorithmConfiguration.getDampingFactor()+",\n" +
                dampingFactorCQLPart+
                "  tolerance: "+pageRankAlgorithmConfiguration.getTolerance()+",\n" +
                "  concurrency: 4 \n" +
                "})\n" +
                "YIELD nodeId, score\n" +
                //"RETURN gds.util.asNode(nodeId) AS node, score\n" +
                "RETURN nodeId AS entityUID, score\n" +
                orderCQLPart+
                getReturnDataControlLogic(pageRankAlgorithmConfiguration);
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

       return cypherProcedureString;
    }

    private String getKindNamesArrayString(List<String> kindNamesList){
        String kindNamesArrayString = "[";

        for(int i= 0; i<kindNamesList.size();i++){
            kindNamesArrayString = kindNamesArrayString +"'"+kindNamesList.get(i)+"'";
            if(i<kindNamesList.size()-1){
                kindNamesArrayString = kindNamesArrayString + ",";
            }
        }
        kindNamesArrayString = kindNamesArrayString+"]";
        return kindNamesArrayString;
    }

    private String getKindNamesSetString(Set<String> kindNamesSet){
        String kindNamesArrayString = "[";
        int currentLoopIndex = 0;
        for(String currentKindName : kindNamesSet){
            kindNamesArrayString = kindNamesArrayString +"'"+currentKindName+"'";
            currentLoopIndex++;
            if(currentLoopIndex<kindNamesSet.size()){
                kindNamesArrayString = kindNamesArrayString + ",";
            }
        }
        kindNamesArrayString = kindNamesArrayString+"]";
        return kindNamesArrayString;
    }

    private String getConceptionKindAndAttributesDefinition(Map<String, Set<String>> indInfoMap){
        Set<String>  kindNames = indInfoMap.keySet();
        Iterator<String> kindNameIterator = kindNames.iterator();

        String wholeRelationKindsDefinitionStr ="";
        while(kindNameIterator.hasNext()){
            String kindName = kindNameIterator.next();
            Set<String> kindAttributesSet = indInfoMap.get(kindName);

            String attributeValuesString = "";
            for(String currentAttribute:kindAttributesSet){
                String currentAttributeDefinition = currentAttribute+": {" + "property: '"+currentAttribute+"'"+"}"+"\n"+",";
                attributeValuesString = attributeValuesString + currentAttributeDefinition;
            }
            if(attributeValuesString.length()>1) {
                attributeValuesString = attributeValuesString.substring(0, attributeValuesString.length() - 1);
            }
            String currentKindDefinitionStr = ""+kindName+": {\n" +
                    "label: '"+kindName+"',\n" +
                    "properties: {\n" +
                    attributeValuesString +
                    "}\n" +
                    "},";
            wholeRelationKindsDefinitionStr = wholeRelationKindsDefinitionStr+currentKindDefinitionStr;
        }
        wholeRelationKindsDefinitionStr = wholeRelationKindsDefinitionStr.substring(0,wholeRelationKindsDefinitionStr.length()-1);
        return "{"+wholeRelationKindsDefinitionStr+"}";
    }

    private String getRelationKindAndAttributesDefinition(Map<String, Set<String>> indInfoMap){
        Set<String>  kindNames = indInfoMap.keySet();
        Iterator<String> kindNameIterator = kindNames.iterator();

        String wholeRelationKindsDefinitionStr ="";
        while(kindNameIterator.hasNext()){
            String kindName = kindNameIterator.next();
            Set<String> kindAttributesSet = indInfoMap.get(kindName);

            String attributeValuesString = "";
            for(String currentAttribute:kindAttributesSet){
                String currentAttributeDefinition = currentAttribute+": {" + "property: '"+currentAttribute+"'"+"}"+"\n"+",";
                attributeValuesString = attributeValuesString + currentAttributeDefinition;
            }
            if(attributeValuesString.length()>1) {
                attributeValuesString = attributeValuesString.substring(0, attributeValuesString.length() - 1);
            }
            String currentKindDefinitionStr = ""+kindName+": {\n" +
                    "type: '"+kindName+"',\n" +
                    "properties: {\n" +
                    attributeValuesString +
                    "}\n" +
                    "},";
            wholeRelationKindsDefinitionStr = wholeRelationKindsDefinitionStr+currentKindDefinitionStr;
        }
        wholeRelationKindsDefinitionStr = wholeRelationKindsDefinitionStr.substring(0,wholeRelationKindsDefinitionStr.length()-1);
        return "{"+wholeRelationKindsDefinitionStr+"}";
    }

    private String getGlobalKindPropertiesString(Set<String> conceptionKindAttributeSet,Set<String> relationKindAttributeSet){
        if((conceptionKindAttributeSet != null && conceptionKindAttributeSet.size() > 0) ||
                (relationKindAttributeSet != null && relationKindAttributeSet.size() > 0)){
            String conceptionPropertiesString = "";
            String relationPropertiesString = "";
            boolean hasRelationProperties = false;
            if(conceptionKindAttributeSet != null && conceptionKindAttributeSet.size() > 0){
                String fullKindDefinitionsStr="";
                for(String currentKind:conceptionKindAttributeSet){
                    String currentKindDefinitionStr = "{ "+currentKind+": '"+currentKind+"' },";
                    fullKindDefinitionsStr = fullKindDefinitionsStr+currentKindDefinitionStr;
                }
                if(fullKindDefinitionsStr.length() > 1){
                    fullKindDefinitionsStr = fullKindDefinitionsStr.substring(0,fullKindDefinitionsStr.length()-1);
                }
                conceptionPropertiesString = "nodeProperties: ["+fullKindDefinitionsStr+"]";
                hasRelationProperties = true;
            }
            if(relationKindAttributeSet != null && relationKindAttributeSet.size() > 0){
                String fullKindDefinitionsStr="";
                for(String currentKind:relationKindAttributeSet){
                    String currentKindDefinitionStr = "{ "+currentKind+": '"+currentKind+"' },";
                    fullKindDefinitionsStr = fullKindDefinitionsStr+currentKindDefinitionStr;
                }
                if(fullKindDefinitionsStr.length() > 1){
                    fullKindDefinitionsStr = fullKindDefinitionsStr.substring(0,fullKindDefinitionsStr.length()-1);
                }
                relationPropertiesString = hasRelationProperties? ",relationshipProperties: ["+fullKindDefinitionsStr+"]"
                        :"relationshipProperties: ["+fullKindDefinitionsStr+"]";
            }
            return ",{"+conceptionPropertiesString + relationPropertiesString+"}";
        }else{
            return "";
        }
    }

    private String getReturnDataControlLogic(ResultPaginationableConfig resultPaginationAbleConfig) throws CoreRealmServiceEntityExploreException{
        if (resultPaginationAbleConfig != null) {
            int defaultReturnRecordNumber = 1000;
            int skipRecordNumber = 0;
            int limitRecordNumber = 0;

            int startPage = resultPaginationAbleConfig.getStartPage();
            int endPage = resultPaginationAbleConfig.getEndPage();
            int pageSize = resultPaginationAbleConfig.getPageSize();
            int resultNumber = resultPaginationAbleConfig.getResultNumber();

            if (startPage != 0) {
                if (startPage < 0) {
                    String exceptionMessage = "start page must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }
                if (pageSize < 0) {
                    String exceptionMessage = "page size must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }

                int runtimePageSize = pageSize != 0 ? pageSize : 50;
                int runtimeStartPage = startPage - 1;

                //get data from start page to end page, each page has runtimePageSize number of record
                if (endPage <= startPage) {
                    String exceptionMessage = "end page must great than start page";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }
                int runtimeEndPage = endPage - 1;

                skipRecordNumber = runtimePageSize * runtimeStartPage;
                limitRecordNumber = (runtimeEndPage - runtimeStartPage) * runtimePageSize;
            }

            if (resultNumber != 0) {
                if (resultNumber < 0) {
                    String exceptionMessage = "result number must great then zero";
                    CoreRealmServiceEntityExploreException coreRealmServiceEntityExploreException = new CoreRealmServiceEntityExploreException();
                    coreRealmServiceEntityExploreException.setCauseMessage(exceptionMessage);
                    throw coreRealmServiceEntityExploreException;
                }
                limitRecordNumber = resultNumber;
                skipRecordNumber = 0;
            }
            if (limitRecordNumber == 0) {
                limitRecordNumber = defaultReturnRecordNumber;
            }

            return " SKIP "+skipRecordNumber+" LIMIT "+limitRecordNumber+"";
        }else{
            return "";
        }
    }

    private AnalyzableGraph executeCreateAnalyzableGraphOperation(String graphName,String cypherProcedureString) throws CoreRealmServiceRuntimeException {

        List<Boolean> createGraphSuccessSign = new ArrayList<>();
        DataTransformer dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {

                if(result.hasNext()){
                    Record nodeRecord = result.next();
                    if(nodeRecord != null){
                        createGraphSuccessSign.add(Boolean.TRUE);
                    }
                }
                return null;
            }
        };

        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);

            if(createGraphSuccessSign.size() >0 & createGraphSuccessSign.get(0)){
                cypherProcedureString = "CALL gds.graph.list('"+graphName+"');";
                logger.debug("Generated Cypher Statement: {}", cypherProcedureString);
                GetSingleAnalyzableGraphTransformer getSingleAnalyzableGraphTransformer =
                        new GetSingleAnalyzableGraphTransformer(this.coreRealmName,this.graphOperationExecutorHelper.getGlobalGraphOperationExecutor());
                Object queryResponse = workingGraphOperationExecutor.executeRead(getSingleAnalyzableGraphTransformer,cypherProcedureString);
                return queryResponse != null ? (AnalyzableGraph) queryResponse : null;
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        }finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
        return null;
    }
}
