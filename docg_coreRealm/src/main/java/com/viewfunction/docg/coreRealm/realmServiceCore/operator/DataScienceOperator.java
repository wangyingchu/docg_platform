package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AnalyzableGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataScienceOperator {
    //墨子悲丝 - 染于苍则苍，染于黄则黄。五入为五色，不可不慎也。非独染丝，治国亦然。

    /*
    Document:
    https://neo4j.com/docs/graph-data-science/current/alpha-algorithms/scale-properties/
    */
    enum ValueScalerLogic {None, MinMax, Max, Mean, Log, L1Norm, L2Norm, StdScore}

    enum ValueSortingLogic { ASC, DESC }

    enum ComputeOrientation { NATURAL, REVERSE, UNDIRECTED}

    public List<AnalyzableGraph> getAnalyzableGraphs();

    public boolean checkAnalyzableGraphExistence(String graphName);

    public AnalyzableGraph getAnalyzableGraph(String graphName);

    public boolean removeAnalyzableGraph(String graphName) throws CoreRealmServiceRuntimeException;

    public AnalyzableGraph createAnalyzableGraph(String graphName,List<String> conceptionKindList,Set<String> conceptionKindAttributeSet,
                                                 List<String> relationKindList,Set<String> relationKindAttributeSet) throws CoreRealmServiceRuntimeException;

    public AnalyzableGraph createAnalyzableGraph(String graphName) throws CoreRealmServiceRuntimeException;

    public AnalyzableGraph createAnalyzableGraph(String graphName, Map<String, Set<String>> conceptionKindInfoMap,
                                                 Map<String, Set<String>> relationKindInfoMap) throws CoreRealmServiceRuntimeException;

    public AnalyzableGraph createAnalyzableGraph(String graphName, String conceptionEntitiesQuery,
                                                 String relationEntitiesQuery) throws CoreRealmServiceRuntimeException;

    public PageRankAlgorithmResult executePageRankAlgorithm(String graphName, PageRankAlgorithmConfig pageRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    public PageRankAlgorithmResult executePersonalisedPageRankAlgorithm(String graphName, PersonalizedPageRankAlgorithmConfig personalizedPageRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    public ArticleRankAlgorithmResult executeArticleRankAlgorithm(String graphName, ArticleRankAlgorithmConfig articleRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    public ArticleRankAlgorithmResult executePersonalisedArticleRankAlgorithm(String graphName, PersonalizedArticleRankAlgorithmConfig personalizedArticleRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    public EigenvectorCentralityAlgorithmResult executeEigenvectorCentralityAlgorithm(String graphName, EigenvectorCentralityAlgorithmConfig eigenvectorCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    public EigenvectorCentralityAlgorithmResult executePersonalisedEigenvectorCentralityAlgorithm(String graphName, PersonalisedEigenvectorCentralityAlgorithmConfig personalisedEigenvectorCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    public BetweennessCentralityAlgorithmResult executeBetweennessCentralityAlgorithm(String graphName, BetweennessCentralityAlgorithmConfig betweennessCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    public DegreeCentralityAlgorithmResult executeDegreeCentralityAlgorithm(String graphName, DegreeCentralityAlgorithmConfig degreeCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

}
