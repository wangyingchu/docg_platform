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
import org.neo4j.driver.types.Node;
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
        checkGraphExistence(graphName);

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
    public AnalyzableGraph createAnalyzableGraph(String graphName, Set<ConceptionKindComputeConfig> conceptionKindsConfig, Set<RelationKindComputeConfig> relationKindsConfig) throws CoreRealmServiceRuntimeException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/management-ops/native-projection/#native-projection-syntax-node-projections
        */
        if(conceptionKindsConfig == null || conceptionKindsConfig.size() ==0){
            logger.error("At least one ConceptionKind is required");
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("At least one ConceptionKind is required");
            throw e;
        }
        if(relationKindsConfig == null || relationKindsConfig.size() ==0){
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

        Map<String, Set<String>> conceptionKindsConfigInfoMap = new HashMap<>();
        for(ConceptionKindComputeConfig currentConceptionKindComputeConfig:conceptionKindsConfig){
            conceptionKindsConfigInfoMap.put(currentConceptionKindComputeConfig.getConceptionKindName(),
                    currentConceptionKindComputeConfig.getConceptionKindAttributes());
        }
        String conceptionKindDefinitionStr = getConceptionKindAndAttributesDefinition(conceptionKindsConfigInfoMap);

        Map<String, Set<String>> relationKindsPropertyConfigInfoMap = new HashMap<>();
        Map<String,DataScienceOperator.ComputeOrientation> relationKindsOrientationConfigInfoMap= new HashMap<>();
        Map<String,DataScienceOperator.ComputeAggregation> relationKindsAggregationConfigInfoMap= new HashMap<>();
        for(RelationKindComputeConfig currentRelationKindComputeConfig:relationKindsConfig){
            relationKindsPropertyConfigInfoMap.put(currentRelationKindComputeConfig.getRelationKindName(),
                    currentRelationKindComputeConfig.getRelationKindAttributes());
            relationKindsOrientationConfigInfoMap.put(currentRelationKindComputeConfig.getRelationKindName(),
                    currentRelationKindComputeConfig.getRelationComputeOrientation());
            relationKindsAggregationConfigInfoMap.put(currentRelationKindComputeConfig.getRelationKindName(),
                    currentRelationKindComputeConfig.getRelationComputeAggregation());
        }

        String relationKindDefinitionStr = getRelationKindAndAttributesDefinition(relationKindsPropertyConfigInfoMap,
                relationKindsOrientationConfigInfoMap,relationKindsAggregationConfigInfoMap);
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
        checkGraphExistence(graphName);

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

    @Override
    public DegreeCentralityAlgorithmResult executeDegreeCentralityAlgorithm(String graphName, DegreeCentralityAlgorithmConfig degreeCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/degree-centrality/
        */
        checkGraphExistence(graphName);

        DegreeCentralityAlgorithmConfig degreeCentralityAlgorithmConfiguration = degreeCentralityAlgorithmConfig != null ?
                degreeCentralityAlgorithmConfig : new DegreeCentralityAlgorithmConfig();
        Set<String> conceptionKindsForCompute = degreeCentralityAlgorithmConfiguration.getConceptionKindsForCompute();
        Set<String> relationKindsForCompute = degreeCentralityAlgorithmConfiguration.getRelationKindsForCompute();

        String nodeLabelsCQLPart = "";
        if(conceptionKindsForCompute != null && conceptionKindsForCompute.size()>0){
            nodeLabelsCQLPart = "  nodeLabels: "+getKindNamesSetString(conceptionKindsForCompute)+",\n";
        }
        String relationshipTypes = "";
        if(relationKindsForCompute != null && relationKindsForCompute.size()>0){
            relationshipTypes = "  relationshipTypes: "+getKindNamesSetString(relationKindsForCompute)+",\n";
        }

        String orientationAttributeCQLPart = degreeCentralityAlgorithmConfiguration.getComputeOrientation() != null ?
                "  orientation: '"+degreeCentralityAlgorithmConfiguration.getComputeOrientation()+"',\n" : "";
        String relationshipWeightAttributeCQLPart = degreeCentralityAlgorithmConfiguration.getRelationshipWeightAttribute() != null ?
                "  relationshipWeightProperty: '"+degreeCentralityAlgorithmConfiguration.getRelationshipWeightAttribute()+"',\n" : "";

        String orderCQLPart = degreeCentralityAlgorithmConfiguration.getScoreSortingLogic()!= null ?
                "ORDER BY score "+degreeCentralityAlgorithmConfiguration.getScoreSortingLogic().toString() : "";

        String cypherProcedureString =
                "CALL gds.degree.stream('"+graphName+"', {\n" +
                        nodeLabelsCQLPart +
                        relationshipTypes +
                        orientationAttributeCQLPart +
                        relationshipWeightAttributeCQLPart +
                        "  concurrency: 4 \n" +
                        "})\n" +
                        "YIELD nodeId, score\n" +
                        //"RETURN gds.util.asNode(nodeId) AS node, score\n" +
                        "RETURN nodeId AS entityUID, score\n" +
                        orderCQLPart+
                        getReturnDataControlLogic(degreeCentralityAlgorithmConfiguration);
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        DegreeCentralityAlgorithmResult degreeCentralityAlgorithmResult = new DegreeCentralityAlgorithmResult(graphName,degreeCentralityAlgorithmConfig);
        List<EntityAnalyzeResult> entityAnalyzeResultList = degreeCentralityAlgorithmResult.getDegreeCentralityScores();

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
            degreeCentralityAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return degreeCentralityAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public LouvainAlgorithmResult executeLouvainAlgorithm(String graphName, LouvainAlgorithmConfig louvainAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/louvain/
        */
        checkGraphExistence(graphName);

        LouvainAlgorithmConfig louvainAlgorithmConfiguration = louvainAlgorithmConfig != null ?
                louvainAlgorithmConfig : new LouvainAlgorithmConfig();
        Set<String> conceptionKindsForCompute = louvainAlgorithmConfiguration.getConceptionKindsForCompute();
        Set<String> relationKindsForCompute = louvainAlgorithmConfiguration.getRelationKindsForCompute();
        String nodeLabelsCQLPart = "";
        if(conceptionKindsForCompute != null && conceptionKindsForCompute.size()>0){
            nodeLabelsCQLPart = "  nodeLabels: "+getKindNamesSetString(conceptionKindsForCompute)+",\n";
        }
        String relationshipTypes = "";
        if(relationKindsForCompute != null && relationKindsForCompute.size()>0){
            relationshipTypes = "  relationshipTypes: "+getKindNamesSetString(relationKindsForCompute)+",\n";
        }

        String relationshipWeightAttributeCQLPart = louvainAlgorithmConfiguration.getRelationshipWeightAttribute() != null ?
                "  relationshipWeightProperty: '"+louvainAlgorithmConfiguration.getRelationshipWeightAttribute()+"',\n" : "";
        String seedPropertyAttributeCQLPart = louvainAlgorithmConfiguration.getSeedProperty() != null ?
                "  seedProperty: '"+louvainAlgorithmConfiguration.getSeedProperty()+"',\n" : "";
        String maxLevelsAttributeCQLPart = "  maxLevels: " + louvainAlgorithmConfiguration.getMaxLevels()+",\n";
        String maxIterationsAttributeCQLPart = "  maxIterations: " + louvainAlgorithmConfiguration.getMaxIterations()+",\n";
        String toleranceAttributeCQLPart = "  tolerance: " + louvainAlgorithmConfiguration.getTolerance()+",\n";
        String includeIntermediateCommunitiesAttributeCQLPart = "  includeIntermediateCommunities: " + louvainAlgorithmConfiguration.isIncludeIntermediateCommunities()+",\n";
        String consecutiveIdsAttributeCQLPart = "  consecutiveIds: " + louvainAlgorithmConfiguration.isConsecutiveIds()+",\n";

        String orderCQLPart = louvainAlgorithmConfiguration.getCommunityIdSortingLogic()!= null ?
                "ORDER BY communityId "+louvainAlgorithmConfiguration.getCommunityIdSortingLogic().toString() : "";

        String cypherProcedureString =
                "CALL gds.louvain.stream('"+graphName+"', {\n" +
                        nodeLabelsCQLPart +
                        relationshipTypes +
                        relationshipWeightAttributeCQLPart +
                        seedPropertyAttributeCQLPart +
                        maxLevelsAttributeCQLPart +
                        maxIterationsAttributeCQLPart +
                        toleranceAttributeCQLPart +
                        includeIntermediateCommunitiesAttributeCQLPart +
                        consecutiveIdsAttributeCQLPart +
                        "  concurrency: 4 \n" +
                        "})\n" +
                        "YIELD nodeId, communityId, intermediateCommunityIds\n" +
                        "RETURN nodeId AS entityUID, communityId,intermediateCommunityIds\n" +
                        orderCQLPart+
                        getReturnDataControlLogic(louvainAlgorithmConfiguration);
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        LouvainAlgorithmResult louvainAlgorithmResult = new LouvainAlgorithmResult(graphName,louvainAlgorithmConfig);
        List<CommunityDetectionResult> communityDetectionResultList = louvainAlgorithmResult.getLouvainCommunities();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityUID = nodeRecord.get("entityUID").asLong();
                    int communityId = nodeRecord.get("communityId").asNumber().intValue();
                    List intermediateCommunityIds = nodeRecord.get("intermediateCommunityIds").isNull() ? null:
                            (List)nodeRecord.get("intermediateCommunityIds").asObject();
                    communityDetectionResultList.add(new CommunityDetectionResult(""+entityUID,communityId,intermediateCommunityIds));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            louvainAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return louvainAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public LabelPropagationAlgorithmResult executeLabelPropagationAlgorithm(String graphName, LabelPropagationAlgorithmConfig labelPropagationAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/label-propagation/
        */
        checkGraphExistence(graphName);

        LabelPropagationAlgorithmConfig labelPropagationAlgorithmConfiguration = labelPropagationAlgorithmConfig != null ?
                labelPropagationAlgorithmConfig : new LabelPropagationAlgorithmConfig();
        Set<String> conceptionKindsForCompute = labelPropagationAlgorithmConfiguration.getConceptionKindsForCompute();
        Set<String> relationKindsForCompute = labelPropagationAlgorithmConfiguration.getRelationKindsForCompute();
        String nodeLabelsCQLPart = "";
        if(conceptionKindsForCompute != null && conceptionKindsForCompute.size()>0){
            nodeLabelsCQLPart = "  nodeLabels: "+getKindNamesSetString(conceptionKindsForCompute)+",\n";
        }
        String relationshipTypes = "";
        if(relationKindsForCompute != null && relationKindsForCompute.size()>0){
            relationshipTypes = "  relationshipTypes: "+getKindNamesSetString(relationKindsForCompute)+",\n";
        }

        String relationshipWeightAttributeCQLPart = labelPropagationAlgorithmConfiguration.getRelationshipWeightAttribute() != null ?
                "  relationshipWeightProperty: '"+labelPropagationAlgorithmConfiguration.getRelationshipWeightAttribute()+"',\n" : "";
        String nodeWeightAttributeCQLPart = labelPropagationAlgorithmConfiguration.getNodeWeightAttribute() != null ?
                "  nodeWeightProperty: '"+labelPropagationAlgorithmConfiguration.getNodeWeightAttribute()+"',\n" : "";
        String seedPropertyAttributeCQLPart = labelPropagationAlgorithmConfiguration.getSeedProperty() != null ?
                "  seedProperty: '"+labelPropagationAlgorithmConfiguration.getSeedProperty()+"',\n" : "";
        String maxIterationsAttributeCQLPart = "  maxIterations: " + labelPropagationAlgorithmConfiguration.getMaxIterations()+",\n";
        String consecutiveIdsAttributeCQLPart = "  consecutiveIds: " + labelPropagationAlgorithmConfiguration.isConsecutiveIds()+",\n";

        String orderCQLPart = labelPropagationAlgorithmConfiguration.getCommunityIdSortingLogic()!= null ?
                "ORDER BY communityId "+labelPropagationAlgorithmConfiguration.getCommunityIdSortingLogic().toString() : "";

        String cypherProcedureString =
                "CALL gds.labelPropagation.stream('"+graphName+"', {\n" +
                        nodeLabelsCQLPart +
                        relationshipTypes +
                        relationshipWeightAttributeCQLPart +
                        nodeWeightAttributeCQLPart +
                        seedPropertyAttributeCQLPart +
                        maxIterationsAttributeCQLPart +
                        consecutiveIdsAttributeCQLPart +
                        "  concurrency: 4 \n" +
                        "})\n" +
                        "YIELD nodeId, communityId\n" +
                        "RETURN nodeId AS entityUID, communityId\n" +
                        orderCQLPart+
                        getReturnDataControlLogic(labelPropagationAlgorithmConfiguration);
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        LabelPropagationAlgorithmResult labelPropagationAlgorithmResult = new LabelPropagationAlgorithmResult(graphName,labelPropagationAlgorithmConfig);
        List<CommunityDetectionResult> communityDetectionResultList = labelPropagationAlgorithmResult.getLabelPropagationCommunities();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityUID = nodeRecord.get("entityUID").asLong();
                    int communityId = nodeRecord.get("communityId").asNumber().intValue();
                    communityDetectionResultList.add(new CommunityDetectionResult(""+entityUID,communityId));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            labelPropagationAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return labelPropagationAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public WeaklyConnectedComponentsAlgorithmResult executeWeaklyConnectedComponentsAlgorithm(String graphName, WeaklyConnectedComponentsAlgorithmConfig weaklyConnectedComponentsAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/wcc/
        */
        checkGraphExistence(graphName);

        WeaklyConnectedComponentsAlgorithmConfig weaklyConnectedComponentsAlgorithmConfiguration = weaklyConnectedComponentsAlgorithmConfig != null ?
                weaklyConnectedComponentsAlgorithmConfig : new WeaklyConnectedComponentsAlgorithmConfig();
        Set<String> conceptionKindsForCompute = weaklyConnectedComponentsAlgorithmConfiguration.getConceptionKindsForCompute();
        Set<String> relationKindsForCompute = weaklyConnectedComponentsAlgorithmConfiguration.getRelationKindsForCompute();
        String nodeLabelsCQLPart = "";
        if(conceptionKindsForCompute != null && conceptionKindsForCompute.size()>0){
            nodeLabelsCQLPart = "  nodeLabels: "+getKindNamesSetString(conceptionKindsForCompute)+",\n";
        }
        String relationshipTypes = "";
        if(relationKindsForCompute != null && relationKindsForCompute.size()>0){
            relationshipTypes = "  relationshipTypes: "+getKindNamesSetString(relationKindsForCompute)+",\n";
        }

        String relationshipWeightAttributeCQLPart = weaklyConnectedComponentsAlgorithmConfiguration.getRelationshipWeightAttribute() != null ?
                "  relationshipWeightProperty: '"+weaklyConnectedComponentsAlgorithmConfiguration.getRelationshipWeightAttribute()+"',\n" : "";
        String seedPropertyAttributeCQLPart = weaklyConnectedComponentsAlgorithmConfiguration.getSeedProperty() != null ?
                "  seedProperty: '"+weaklyConnectedComponentsAlgorithmConfiguration.getSeedProperty()+"',\n" : "";
        String thresholdPropertyAttributeCQLPart = weaklyConnectedComponentsAlgorithmConfiguration.getThreshold() != null ?
                "  threshold: "+weaklyConnectedComponentsAlgorithmConfiguration.getThreshold().floatValue()+",\n" : "";
        String consecutiveIdsAttributeCQLPart = "  consecutiveIds: " + weaklyConnectedComponentsAlgorithmConfiguration.isConsecutiveIds()+",\n";

        String orderCQLPart = weaklyConnectedComponentsAlgorithmConfiguration.getCommunityIdSortingLogic()!= null ?
                "ORDER BY componentId "+weaklyConnectedComponentsAlgorithmConfiguration.getCommunityIdSortingLogic().toString() : "";

        String cypherProcedureString =
                "CALL gds.wcc.stream('"+graphName+"', {\n" +
                        nodeLabelsCQLPart +
                        relationshipTypes +
                        relationshipWeightAttributeCQLPart +
                        seedPropertyAttributeCQLPart +
                        thresholdPropertyAttributeCQLPart +
                        consecutiveIdsAttributeCQLPart +
                        "  concurrency: 4 \n" +
                        "})\n" +
                        "YIELD nodeId, componentId\n" +
                        "RETURN nodeId AS entityUID, componentId\n" +
                        orderCQLPart+
                        getReturnDataControlLogic(weaklyConnectedComponentsAlgorithmConfiguration);
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        WeaklyConnectedComponentsAlgorithmResult weaklyConnectedComponentsAlgorithmResult = new WeaklyConnectedComponentsAlgorithmResult(graphName,weaklyConnectedComponentsAlgorithmConfig);
        List<ComponentDetectionResult> communityDetectionResultList = weaklyConnectedComponentsAlgorithmResult.getWCCComponents();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityUID = nodeRecord.get("entityUID").asLong();
                    int communityId = nodeRecord.get("componentId").asNumber().intValue();
                    communityDetectionResultList.add(new ComponentDetectionResult(""+entityUID,communityId));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            weaklyConnectedComponentsAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return weaklyConnectedComponentsAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public TriangleCountAlgorithmResult executeTriangleCountAlgorithm(String graphName, TriangleCountAlgorithmConfig triangleCountAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/triangle-count/
        */
        checkGraphExistence(graphName);

        TriangleCountAlgorithmConfig triangleCountAlgorithmConfiguration = triangleCountAlgorithmConfig != null ?
                triangleCountAlgorithmConfig : new TriangleCountAlgorithmConfig();
        Set<String> conceptionKindsForCompute = triangleCountAlgorithmConfiguration.getConceptionKindsForCompute();
        Set<String> relationKindsForCompute = triangleCountAlgorithmConfiguration.getRelationKindsForCompute();
        String nodeLabelsCQLPart = "";
        if(conceptionKindsForCompute != null && conceptionKindsForCompute.size()>0){
            nodeLabelsCQLPart = "  nodeLabels: "+getKindNamesSetString(conceptionKindsForCompute)+",\n";
        }
        String relationshipTypes = "";
        if(relationKindsForCompute != null && relationKindsForCompute.size()>0){
            relationshipTypes = "  relationshipTypes: "+getKindNamesSetString(relationKindsForCompute)+",\n";
        }

        String maxDegreePropertyAttributeCQLPart = triangleCountAlgorithmConfiguration.getMaxDegree() != null ?
                "  maxDegree: "+triangleCountAlgorithmConfiguration.getMaxDegree().intValue()+",\n" : "";

        String orderCQLPart = triangleCountAlgorithmConfiguration.getTriangleCountSortingLogic()!= null ?
                "ORDER BY triangleCount "+triangleCountAlgorithmConfiguration.getTriangleCountSortingLogic().toString() : "";

        String cypherProcedureString =
                "CALL gds.triangleCount.stream('"+graphName+"', {\n" +
                        nodeLabelsCQLPart +
                        relationshipTypes +
                        maxDegreePropertyAttributeCQLPart +
                        "  concurrency: 4 \n" +
                        "})\n" +
                        "YIELD nodeId, triangleCount\n" +
                        "RETURN nodeId AS entityUID, triangleCount\n" +
                        orderCQLPart+
                        getReturnDataControlLogic(triangleCountAlgorithmConfiguration);
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        TriangleCountAlgorithmResult triangleCountAlgorithmResult = new TriangleCountAlgorithmResult(graphName,triangleCountAlgorithmConfig);
        List<TriangleCountDetectionResult> triangleCountDetectionResultList = triangleCountAlgorithmResult.getTriangleCounts();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityUID = nodeRecord.get("entityUID").asLong();
                    int triangleCount = nodeRecord.get("triangleCount").asNumber().intValue();
                    triangleCountDetectionResultList.add(new TriangleCountDetectionResult(""+entityUID,triangleCount));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            triangleCountAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return triangleCountAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public LocalClusteringCoefficientAlgorithmResult executeLocalClusteringCoefficientAlgorithm(String graphName, LocalClusteringCoefficientAlgorithmConfig localClusteringCoefficientAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/local-clustering-coefficient/
        */
        checkGraphExistence(graphName);

        LocalClusteringCoefficientAlgorithmConfig localClusteringCoefficientAlgorithmConfiguration = localClusteringCoefficientAlgorithmConfig != null ?
                localClusteringCoefficientAlgorithmConfig : new LocalClusteringCoefficientAlgorithmConfig();
        Set<String> conceptionKindsForCompute = localClusteringCoefficientAlgorithmConfiguration.getConceptionKindsForCompute();
        Set<String> relationKindsForCompute = localClusteringCoefficientAlgorithmConfiguration.getRelationKindsForCompute();
        String nodeLabelsCQLPart = "";
        if(conceptionKindsForCompute != null && conceptionKindsForCompute.size()>0){
            nodeLabelsCQLPart = "  nodeLabels: "+getKindNamesSetString(conceptionKindsForCompute)+",\n";
        }
        String relationshipTypes = "";
        if(relationKindsForCompute != null && relationKindsForCompute.size()>0){
            relationshipTypes = "  relationshipTypes: "+getKindNamesSetString(relationKindsForCompute)+",\n";
        }

        String triangleCountPropertyPropertyAttributeCQLPart = localClusteringCoefficientAlgorithmConfiguration.getTriangleCountProperty() != null ?
                "  triangleCountProperty: '"+localClusteringCoefficientAlgorithmConfiguration.getTriangleCountProperty()+"',\n" : "";

        String orderCQLPart = localClusteringCoefficientAlgorithmConfiguration.getCoefficientSortingLogic()!= null ?
                "ORDER BY localClusteringCoefficient "+localClusteringCoefficientAlgorithmConfiguration.getCoefficientSortingLogic().toString() : "";

        String cypherProcedureString =
                "CALL gds.localClusteringCoefficient.stream('"+graphName+"', {\n" +
                        nodeLabelsCQLPart +
                        relationshipTypes +
                        triangleCountPropertyPropertyAttributeCQLPart +
                        "  concurrency: 4 \n" +
                        "})\n" +
                        "YIELD nodeId, localClusteringCoefficient\n" +
                        "RETURN nodeId AS entityUID, localClusteringCoefficient\n" +
                        orderCQLPart+
                        getReturnDataControlLogic(localClusteringCoefficientAlgorithmConfiguration);
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        LocalClusteringCoefficientAlgorithmResult localClusteringCoefficientAlgorithmResult = new LocalClusteringCoefficientAlgorithmResult(graphName,localClusteringCoefficientAlgorithmConfig);
        List<EntityAnalyzeResult> localClusteringCoefficientResultList = localClusteringCoefficientAlgorithmResult.getLocalClusteringCoefficients();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityUID = nodeRecord.get("entityUID").asLong();
                    double localClusteringCoefficient = nodeRecord.get("localClusteringCoefficient").asNumber().doubleValue();
                    localClusteringCoefficientResultList.add(new EntityAnalyzeResult(""+entityUID,localClusteringCoefficient,"localClusteringCoefficient"));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            localClusteringCoefficientAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return localClusteringCoefficientAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public NodeSimilarityAlgorithmResult executeNodeSimilarityAlgorithm(String graphName, NodeSimilarityAlgorithmConfig nodeSimilarityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/node-similarity/
        */
        checkGraphExistence(graphName);

        NodeSimilarityAlgorithmConfig nodeSimilarityAlgorithmConfiguration = nodeSimilarityAlgorithmConfig != null ?
                nodeSimilarityAlgorithmConfig : new NodeSimilarityAlgorithmConfig();
        Set<String> conceptionKindsForCompute = nodeSimilarityAlgorithmConfiguration.getConceptionKindsForCompute();
        Set<String> relationKindsForCompute = nodeSimilarityAlgorithmConfiguration.getRelationKindsForCompute();
        String nodeLabelsCQLPart = "";
        if(conceptionKindsForCompute != null && conceptionKindsForCompute.size()>0){
            nodeLabelsCQLPart = "  nodeLabels: "+getKindNamesSetString(conceptionKindsForCompute)+",\n";
        }
        String relationshipTypes = "";
        if(relationKindsForCompute != null && relationKindsForCompute.size()>0){
            relationshipTypes = "  relationshipTypes: "+getKindNamesSetString(relationKindsForCompute)+",\n";
        }

        String similarityCutoffPropertyAttributeCQLPart = nodeSimilarityAlgorithmConfiguration.getSimilarityCutoff() != null ?
                "  similarityCutoff: "+nodeSimilarityAlgorithmConfiguration.getSimilarityCutoff().floatValue()+",\n" : "";
        String degreeCutoffAttributeCQLPart = "  degreeCutoff: " + nodeSimilarityAlgorithmConfiguration.getDegreeCutoff()+",\n";
        String topKAttributeCQLPart = "  topK: " + nodeSimilarityAlgorithmConfiguration.getTopK()+",\n";
        String bottomKAttributeCQLPart = "  bottomK: " + nodeSimilarityAlgorithmConfiguration.getBottomK()+",\n";
        String topNAttributeCQLPart = "  topN: " + nodeSimilarityAlgorithmConfiguration.getTopN()+",\n";
        String bottomNAttributeCQLPart = "  bottomN: " + nodeSimilarityAlgorithmConfiguration.getBottomN()+",\n";
        String relationshipWeightAttributeCQLPart = nodeSimilarityAlgorithmConfiguration.getRelationshipWeightAttribute() != null ?
                "  relationshipWeightProperty: '"+nodeSimilarityAlgorithmConfiguration.getRelationshipWeightAttribute()+"',\n" : "";
        String orderCQLPart = nodeSimilarityAlgorithmConfiguration.getSimilaritySortingLogic()!= null ?
                "ORDER BY similarity "+ nodeSimilarityAlgorithmConfiguration.getSimilaritySortingLogic().toString() : "";

        String cypherProcedureString =
                "CALL gds.nodeSimilarity.stream('"+graphName+"', {\n" +
                        nodeLabelsCQLPart +
                        relationshipTypes +
                        similarityCutoffPropertyAttributeCQLPart +
                        degreeCutoffAttributeCQLPart +
                        topKAttributeCQLPart +
                        bottomKAttributeCQLPart +
                        topNAttributeCQLPart +
                        bottomNAttributeCQLPart +
                        relationshipWeightAttributeCQLPart +
                        "  concurrency: 4 \n" +
                        "})\n" +
                        "YIELD node1, node2, similarity\n" +
                        "RETURN node1 AS entityAUID, node2 AS entityBUID, similarity\n" +
                        orderCQLPart+
                        getReturnDataControlLogic(nodeSimilarityAlgorithmConfiguration);
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        NodeSimilarityAlgorithmResult nodeSimilarityAlgorithmResult = new NodeSimilarityAlgorithmResult(graphName,nodeSimilarityAlgorithmConfig);
        List<SimilarityDetectionResult> similarityDetectionResultList = nodeSimilarityAlgorithmResult.getNodeSimilarityScores();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();
                    long entityAUID = nodeRecord.get("entityAUID").asLong();
                    long entityBUID = nodeRecord.get("entityBUID").asLong();
                    float similarityScore = nodeRecord.get("similarity").asNumber().floatValue();
                    similarityDetectionResultList.add(new SimilarityDetectionResult(""+entityAUID,""+entityBUID,similarityScore));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            nodeSimilarityAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return nodeSimilarityAlgorithmResult;
        } catch(org.neo4j.driver.exceptions.ClientException e){
            CoreRealmServiceRuntimeException e1 = new CoreRealmServiceRuntimeException();
            e1.setCauseMessage(e.getMessage());
            throw e1;
        } finally {
            this.graphOperationExecutorHelper.closeWorkingGraphOperationExecutor();
        }
    }

    @Override
    public DijkstraSourceTargetAlgorithmResult executeDijkstraSourceTargetAlgorithm(String graphName, DijkstraSourceTargetAlgorithmConfig dijkstraSourceTargetAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException {
        /*
        Example:
        https://neo4j.com/docs/graph-data-science/current/algorithms/dijkstra-source-target/
        */
        checkGraphExistence(graphName);

        if(dijkstraSourceTargetAlgorithmConfig == null || dijkstraSourceTargetAlgorithmConfig.getSourceConceptionEntityUID() == null
                || dijkstraSourceTargetAlgorithmConfig.getTargetConceptionEntityUID() == null){
            logger.error("Both sourceConceptionEntityUID and targetConceptionEntityUID are required",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("Both sourceConceptionEntityUID and targetConceptionEntityUID are required");
            throw e;
        }
        String relationshipWeightPropertyStr = dijkstraSourceTargetAlgorithmConfig.getRelationshipWeightAttribute() != null?
                "  relationshipWeightProperty: '"+dijkstraSourceTargetAlgorithmConfig.getRelationshipWeightAttribute()+"'\n":"";
        String limitStr = dijkstraSourceTargetAlgorithmConfig.getMaxPathNumber() != null?
                "LIMIT "+dijkstraSourceTargetAlgorithmConfig.getMaxPathNumber().intValue() : "";

        String cypherProcedureString =
                "MATCH (startNode) WHERE id(startNode)= "+dijkstraSourceTargetAlgorithmConfig.getSourceConceptionEntityUID()+"\n" +
                "MATCH (endNode) WHERE id(endNode)= "+dijkstraSourceTargetAlgorithmConfig.getTargetConceptionEntityUID()+"\n" +
        "CALL gds.shortestPath.dijkstra.stream('"+graphName+"', {\n" +
        "    sourceNode: startNode,\n" +
        "    targetNode: endNode,\n" +
                        relationshipWeightPropertyStr+
                        "  concurrency: 4 \n" +
        "})\n" +
        "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs, path\n" +
        "RETURN\n" +
        "    index,\n" +
        "    sourceNode AS sourceEntityUID,\n" +
        "    targetNode AS targetEntityUID,\n" +
                     "gds.util.asNode(sourceNode) AS sourceEntity,\n"+
                        "gds.util.asNode(targetNode) AS targetEntity,\n"+
        "    totalCost,\n" +
        "    nodeIds,\n" +
        "    costs,\n" +
        "    nodes(path) as path\n" +
        "ORDER BY index "+limitStr;
        logger.debug("Generated Cypher Statement: {}", cypherProcedureString);

        DijkstraSourceTargetAlgorithmResult dijkstraSourceTargetAlgorithmResult = new DijkstraSourceTargetAlgorithmResult(graphName,dijkstraSourceTargetAlgorithmConfig);
        List<PathFindingResult> pathFindingResultList = dijkstraSourceTargetAlgorithmResult.getDijkstraSourceTargetPaths();

        DataTransformer<Object> dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                while(result.hasNext()){
                    Record nodeRecord = result.next();

                    String sourceEntityUID = ""+nodeRecord.get("sourceEntityUID").asLong();
                    String targetEntityUID = ""+nodeRecord.get("targetEntityUID").asLong();
                    double totalCost = nodeRecord.get("totalCost").asNumber().doubleValue();
                    Node sourceEntity = nodeRecord.get("sourceEntity").asNode();
                    Node targetEntity = nodeRecord.get("targetEntity").asNode();

                    List<String> pathConceptionEntityUIDs = new ArrayList<>();
                    for(Object currentNodeId:nodeRecord.get("nodeIds").asList()){
                        pathConceptionEntityUIDs.add(currentNodeId.toString());
                    }
                    List<Double> entityTraversalCosts = new ArrayList<>();
                    for(Object currentCost:nodeRecord.get("costs").asList()){
                        entityTraversalCosts.add((Double)currentCost);
                    }

                    System.out.println(pathConceptionEntityUIDs);
                    System.out.println(entityTraversalCosts);
                    System.out.println(totalCost);

                    System.out.println(nodeRecord.get("path").asList());


                    System.out.println(nodeRecord);
                    //long entityAUID = nodeRecord.get("entityAUID").asLong();
                    //long entityBUID = nodeRecord.get("entityBUID").asLong();
                    //float similarityScore = nodeRecord.get("similarity").asNumber().floatValue();
                    //similarityDetectionResultList.add(new SimilarityDetectionResult(""+entityAUID,""+entityBUID,similarityScore));
                }
                return null;
            }
        };
        GraphOperationExecutor workingGraphOperationExecutor = this.graphOperationExecutorHelper.getWorkingGraphOperationExecutor();
        try {
            workingGraphOperationExecutor.executeRead(dataTransformer,cypherProcedureString);
            dijkstraSourceTargetAlgorithmResult.setAlgorithmExecuteEndTime(new Date());
            return dijkstraSourceTargetAlgorithmResult;
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
        checkGraphExistence(graphName);

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
        checkGraphExistence(graphName);

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
        checkGraphExistence(graphName);

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
            if(kindAttributesSet != null) {
                for (String currentAttribute : kindAttributesSet) {
                    String currentAttributeDefinition = currentAttribute + ": {" + "property: '" + currentAttribute + "'" + "}" + "\n" + ",";
                    attributeValuesString = attributeValuesString + currentAttributeDefinition;
                }
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

    private String getRelationKindAndAttributesDefinition(Map<String, Set<String>> relationKindsPropertyConfigInfoMap,
                                                          Map<String,DataScienceOperator.ComputeOrientation> relationKindsOrientationConfigInfoMap,
                                                          Map<String,DataScienceOperator.ComputeAggregation> relationKindsAggregationConfigInfoMap){
        Set<String>  kindNames = relationKindsPropertyConfigInfoMap.keySet();
        Iterator<String> kindNameIterator = kindNames.iterator();

        String wholeRelationKindsDefinitionStr ="";
        while(kindNameIterator.hasNext()){
            String kindName = kindNameIterator.next();
            Set<String> kindAttributesSet = relationKindsPropertyConfigInfoMap.get(kindName);

            DataScienceOperator.ComputeOrientation relationComputeOrientation = relationKindsOrientationConfigInfoMap.get(kindName);
            DataScienceOperator.ComputeAggregation relationComputeAggregation = relationKindsAggregationConfigInfoMap.get(kindName);

            String attributeValuesString = "";
            if(kindAttributesSet != null) {
                for (String currentAttribute : kindAttributesSet) {
                    String currentAttributeDefinition = currentAttribute + ": {" + "property: '" + currentAttribute + "'" + "}" + "\n" + ",";
                    attributeValuesString = attributeValuesString + currentAttributeDefinition;
                }
            }
            if(attributeValuesString.length()>1) {
                attributeValuesString = attributeValuesString.substring(0, attributeValuesString.length() - 1);
            }
            String currentKindDefinitionStr = ""+kindName+": {\n" +
                    "type: '"+kindName+"',\n" +
                    "orientation: '"+relationComputeOrientation.toString()+"',\n" +
                    "aggregation: '"+relationComputeAggregation.toString()+"',\n" +
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

    private void checkGraphExistence(String graphName) throws CoreRealmServiceRuntimeException {
        boolean checkGraphExistence = checkAnalyzableGraphExistence(graphName);
        if(!checkGraphExistence){
            logger.error("AnalyzableGraph with name {} does not exist",graphName);
            CoreRealmServiceRuntimeException e = new CoreRealmServiceRuntimeException();
            e.setCauseMessage("AnalyzableGraph with name "+graphName+" does not exist");
            throw e;
        }
    }
}
