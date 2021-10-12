package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.configuration.dataScienceConfig.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AnalyzableGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.dataScienceAnalyzeResult.*;

import java.util.List;
import java.util.Set;

public interface DataScienceOperator {
    //墨子悲丝    染于苍则苍，染于黄则黄。五入为五色，不可不慎也。
    //           非独染丝，治国亦然。
    /*
    Document:
    https://neo4j.com/docs/graph-data-science/current/alpha-algorithms/scale-properties/
    */
    /**
     * 数值比例标准化调节逻辑
     * None : 保持原始数值。
     * MinMax : Scales all property values into the range [0, 1] where the minimum value(s) get the scaled value 0 and the maximum value(s) get the scaled value 1。
     * Max : Scales all property values into the range [-1, 1] where the absolute maximum value(s) get the scaled value 1。
     * Mean : Scales all property values into the range [-1, 1] where the average value(s) get the scaled value 0。
     * Log : Transforms all property values using the natural logarithm.。
     * L1Norm : Scales all property values using the Standard Score。
     * L2Norm : Scales all property values into the range [0.0, 1.0]。
     * StdScore : Scales all property values using the L2 Norm。
     */
    enum ValueScalerLogic {None, MinMax, Max, Mean, Log, L1Norm, L2Norm, StdScore}

    /**
     * 数值排序逻辑
     * ASC : 升序排列。
     * DESC : 降序排列。
     */
    enum ValueSortingLogic { ASC, DESC }

    /**
     * 关联关系计算方向
     * NATURAL : 保持原有方向。
     * REVERSE : 反转原有方向。
     * UNDIRECTED : 忽略关系方向。
     */
    enum ComputeOrientation { NATURAL, REVERSE, UNDIRECTED}

    /**
     * 关联关系（边）属性计算聚合方式（Handling of parallel relationships）
     * NONE : 不使用聚合。
     * MIN : 取属性最小值。
     * MAX : 取属性最大值。
     * SUM : 取属性总和值。
     * COUNT : For graphs without relationship properties, we can use the COUNT aggregation。
     * SINGLE : If we do not need the count, we could use the SINGLE aggregation。
     */
    enum ComputeAggregation { NONE, MIN, MAX, SUM, SINGLE, COUNT}

    /**
     * 查询并返回当前领域模型中包含的所有可分析图
     *
     * @return 可分析图列表
     */
    public List<AnalyzableGraph> getAnalyzableGraphs();

    /**
     * 检查当前领域模型中是否包含指定名称的可分析图
     *
     * @param graphName String 可分析图名称
     *
     * @return 如该可分析图存在则返回结果为 true
     */
    public boolean checkAnalyzableGraphExistence(String graphName);

    /**
     * 查询并返回当前领域模型中指定名称的可分析图
     *
     *  @param graphName String 可分析图名称
     *
     * @return 可分析图对象
     */
    public AnalyzableGraph getAnalyzableGraph(String graphName);

    /**
     * 在当前领域模型中删除指定名称的可分析图
     *
     *  @param graphName String 可分析图名称
     *
     * @return 如删除成功则返回结果为 true
     */
    public boolean removeAnalyzableGraph(String graphName) throws CoreRealmServiceRuntimeException;

    /**
     * 在当前领域模型中创建指定名称的可分析图，该图中的关联关系的方向与原始数据中的关联关系方向一致
     *
     *  @param graphName String 可分析图名称
     *  @param conceptionKindList List<String> 可分析图中包含的概念类型列表
     *  @param conceptionKindAttributeSet Set<String> 可分析图所有概念类型均拥有的属性名称集合
     *  @param relationKindList List<String> 可分析图中包含的关系类型列表
     *  @param relationKindAttributeSet Set<String> 可分析图所有关系类型均拥有的属性名称集合
     *
     * @return 本次操作创建成功的可分析图对象
     */
    public AnalyzableGraph createAnalyzableGraph(String graphName,List<String> conceptionKindList,Set<String> conceptionKindAttributeSet,
                                                 List<String> relationKindList,Set<String> relationKindAttributeSet) throws CoreRealmServiceRuntimeException;

    /**
     * 使用当前领域模型中的所有数据创建一个指定名称的可分析图，该图中的关联关系的方向与原始数据中的关联关系方向一致
     *
     *  @param graphName String 可分析图名称
     *
     * @return 本次操作创建成功的可分析图对象
     */
    public AnalyzableGraph createAnalyzableGraph(String graphName) throws CoreRealmServiceRuntimeException;

    /**
     * 在当前领域模型中创建指定名称的可分析图
     *
     *  @param graphName String 可分析图名称
     *  @param conceptionKindsConfig Set<ConceptionKindComputeConfig> 可分析图中包含的各项概念类型的计算配置集合
     *  @param relationKindsConfig Set<RelationKindComputeConfig> 可分析图中包含的各项关系类型的计算配置集合
     *
     * @return 本次操作创建成功的可分析图对象
     */
    public AnalyzableGraph createAnalyzableGraph(String graphName, Set<ConceptionKindComputeConfig> conceptionKindsConfig,
                                                 Set<RelationKindComputeConfig> relationKindsConfig) throws CoreRealmServiceRuntimeException;

    /**
     * 在当前领域模型中创建指定名称的可分析图
     *
     *  @param graphName String 可分析图名称
     *  @param conceptionEntitiesQuery String 可分析图中包含的所有概念实体的筛选查询语句
     *  @param relationEntitiesQuery String 可分析图中包含的所有关系实体的筛选查询语句
     *
     * @return 本次操作创建成功的可分析图对象
     */
    public AnalyzableGraph createAnalyzableGraph(String graphName, String conceptionEntitiesQuery,
                                                 String relationEntitiesQuery) throws CoreRealmServiceRuntimeException;

    /**
     * 在指定名称的可分析图上执行 PageRank 算法
     *
     *  @param graphName String 可分析图名称
     *  @param pageRankAlgorithmConfig PageRankAlgorithmConfig PageRank算法执行配置参数
     *
     * @return PageRank算法计算结果集
     */
    public PageRankAlgorithmResult executePageRankAlgorithm(String graphName, PageRankAlgorithmConfig pageRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 PersonalisedPageRank 算法
     *
     *  @param graphName String 可分析图名称
     *  @param personalizedPageRankAlgorithmConfig PersonalizedPageRankAlgorithmConfig PersonalisedPageRank算法执行配置参数
     *
     * @return PageRank算法计算结果集
     */
    public PageRankAlgorithmResult executePersonalisedPageRankAlgorithm(String graphName, PersonalizedPageRankAlgorithmConfig personalizedPageRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 ArticleRank 算法
     *
     *  @param graphName String 可分析图名称
     *  @param articleRankAlgorithmConfig ArticleRankAlgorithmConfig ArticleRank算法执行配置参数
     *
     * @return ArticleRank算法计算结果集
     */
    public ArticleRankAlgorithmResult executeArticleRankAlgorithm(String graphName, ArticleRankAlgorithmConfig articleRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 PersonalisedArticleRank 算法
     *
     *  @param graphName String 可分析图名称
     *  @param personalizedArticleRankAlgorithmConfig PersonalizedArticleRankAlgorithmConfig PersonalisedArticleRank算法执行配置参数
     *
     * @return ArticleRank算法计算结果集
     */
    public ArticleRankAlgorithmResult executePersonalisedArticleRankAlgorithm(String graphName, PersonalizedArticleRankAlgorithmConfig personalizedArticleRankAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 EigenvectorCentrality 算法
     *
     *  @param graphName String 可分析图名称
     *  @param eigenvectorCentralityAlgorithmConfig EigenvectorCentralityAlgorithmConfig EigenvectorCentrality算法执行配置参数
     *
     * @return EigenvectorCentrality算法计算结果集
     */
    public EigenvectorCentralityAlgorithmResult executeEigenvectorCentralityAlgorithm(String graphName, EigenvectorCentralityAlgorithmConfig eigenvectorCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 PersonalisedEigenvectorCentrality 算法
     *
     *  @param graphName String 可分析图名称
     *  @param personalisedEigenvectorCentralityAlgorithmConfig PersonalisedEigenvectorCentralityAlgorithmConfig PersonalisedEigenvectorCentrality算法执行配置参数
     *
     * @return EigenvectorCentrality算法计算结果集
     */
    public EigenvectorCentralityAlgorithmResult executePersonalisedEigenvectorCentralityAlgorithm(String graphName, PersonalisedEigenvectorCentralityAlgorithmConfig personalisedEigenvectorCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 BetweennessCentrality 算法
     *
     *  @param graphName String 可分析图名称
     *  @param betweennessCentralityAlgorithmConfig BetweennessCentralityAlgorithmConfig BetweennessCentrality算法执行配置参数
     *
     * @return BetweennessCentrality算法计算结果集
     */
    public BetweennessCentralityAlgorithmResult executeBetweennessCentralityAlgorithm(String graphName, BetweennessCentralityAlgorithmConfig betweennessCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 DegreeCentrality 算法
     *
     *  @param graphName String 可分析图名称
     *  @param degreeCentralityAlgorithmConfig DegreeCentralityAlgorithmConfig DegreeCentrality算法执行配置参数
     *
     * @return DegreeCentrality算法计算结果集
     */
    public DegreeCentralityAlgorithmResult executeDegreeCentralityAlgorithm(String graphName, DegreeCentralityAlgorithmConfig degreeCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 ClosenessCentrality 算法
     *
     *  @param graphName String 可分析图名称
     *  @param closenessCentralityAlgorithmConfig ClosenessCentralityAlgorithmConfig ClosenessCentrality算法执行配置参数
     *
     * @return ClosenessCentrality算法计算结果集
     */
    public ClosenessCentralityAlgorithmResult executeClosenessCentralityAlgorithm(String graphName, ClosenessCentralityAlgorithmConfig closenessCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 HarmonicCentrality 算法
     *
     *  @param graphName String 可分析图名称
     *  @param harmonicCentralityAlgorithmConfig HarmonicCentralityAlgorithmConfig HarmonicCentrality算法执行配置参数
     *
     * @return HarmonicCentrality算法计算结果集
     */
    public HarmonicCentralityAlgorithmResult executeHarmonicCentralityAlgorithm(String graphName, HarmonicCentralityAlgorithmConfig harmonicCentralityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 HyperlinkInducedTopicSearch(HITS) 算法
     *
     *  @param graphName String 可分析图名称
     *  @param hyperlinkInducedTopicSearchAlgorithmConfig HyperlinkInducedTopicSearchAlgorithmConfig HyperlinkInducedTopicSearch(HITS)算法执行配置参数
     *
     * @return HyperlinkInducedTopicSearch(HITS)算法计算结果集
     */
    public HyperlinkInducedTopicSearchAlgorithmResult executeHyperlinkInducedTopicSearchAlgorithm(String graphName, HyperlinkInducedTopicSearchAlgorithmConfig hyperlinkInducedTopicSearchAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 Greedy influence maximization 算法
     *
     *  @param graphName String 可分析图名称
     *  @param greedyInfluenceMaximizationAlgorithmConfig GreedyInfluenceMaximizationAlgorithmConfig GreedyInfluenceMaximization算法执行配置参数
     *
     * @return Greedy Influence Maximization算法计算结果集
     */
    public GreedyInfluenceMaximizationAlgorithmResult executeGreedyInfluenceMaximizationAlgorithm(String graphName, GreedyInfluenceMaximizationAlgorithmConfig greedyInfluenceMaximizationAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 Louvain 算法
     *
     *  @param graphName String 可分析图名称
     *  @param louvainAlgorithmConfig LouvainAlgorithmConfig Louvain算法执行配置参数
     *
     * @return Louvain算法计算结果集
     */
    public LouvainAlgorithmResult executeLouvainAlgorithm(String graphName, LouvainAlgorithmConfig louvainAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 LabelPropagation 算法
     *
     *  @param graphName String 可分析图名称
     *  @param labelPropagationAlgorithmConfig LabelPropagationAlgorithmConfig LabelPropagation算法执行配置参数
     *
     * @return LabelPropagation算法计算结果集
     */
    public LabelPropagationAlgorithmResult executeLabelPropagationAlgorithm(String graphName, LabelPropagationAlgorithmConfig labelPropagationAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 WeaklyConnectedComponents 算法
     *
     *  @param graphName String 可分析图名称
     *  @param weaklyConnectedComponentsAlgorithmConfig WeaklyConnectedComponentsAlgorithmConfig WeaklyConnectedComponents算法执行配置参数
     *
     * @return WeaklyConnectedComponents算法计算结果集
     */
    public WeaklyConnectedComponentsAlgorithmResult executeWeaklyConnectedComponentsAlgorithm(String graphName, WeaklyConnectedComponentsAlgorithmConfig weaklyConnectedComponentsAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 TriangleCount 算法
     *
     *  @param graphName String 可分析图名称
     *  @param triangleCountAlgorithmConfig TriangleCountAlgorithmConfig TriangleCount算法执行配置参数
     *
     * @return TriangleCount算法计算结果集
     */
    public TriangleCountAlgorithmResult executeTriangleCountAlgorithm(String graphName, TriangleCountAlgorithmConfig triangleCountAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 LocalClusteringCoefficient 算法
     *
     *  @param graphName String 可分析图名称
     *  @param localClusteringCoefficientAlgorithmConfig LocalClusteringCoefficientAlgorithmConfig LocalClusteringCoefficient算法执行配置参数
     *
     * @return LocalClusteringCoefficient算法计算结果集
     */
    public LocalClusteringCoefficientAlgorithmResult executeLocalClusteringCoefficientAlgorithm(String graphName, LocalClusteringCoefficientAlgorithmConfig localClusteringCoefficientAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 NodeSimilarity 算法
     *
     *  @param graphName String 可分析图名称
     *  @param nodeSimilarityAlgorithmConfig NodeSimilarityAlgorithmConfig NodeSimilarity算法执行配置参数
     *
     * @return NodeSimilarity算法计算结果集
     */
    public NodeSimilarityAlgorithmResult executeNodeSimilarityAlgorithm(String graphName, NodeSimilarityAlgorithmConfig nodeSimilarityAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 DijkstraSourceTarget 算法
     *
     *  @param graphName String 可分析图名称
     *  @param dijkstraSourceTargetAlgorithmConfig DijkstraSourceTargetAlgorithmConfig DijkstraSourceTarget算法执行配置参数
     *
     * @return DijkstraSourceTarget算法计算结果集
     */
    public DijkstraSourceTargetAlgorithmResult executeDijkstraSourceTargetAlgorithm(String graphName, DijkstraSourceTargetAlgorithmConfig dijkstraSourceTargetAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 DijkstraSingleSource 算法
     *
     *  @param graphName String 可分析图名称
     *  @param dijkstraSingleSourceAlgorithmConfig DijkstraSingleSourceAlgorithmConfig DijkstraSingleSource算法执行配置参数
     *
     * @return DijkstraSingleSource算法计算结果集
     */
    public DijkstraSingleSourceAlgorithmResult executeDijkstraSingleSourceAlgorithm(String graphName, DijkstraSingleSourceAlgorithmConfig dijkstraSingleSourceAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 A* ShortestPath 算法
     *
     *  @param graphName String 可分析图名称
     *  @param aStarShortestPathAlgorithmConfig AStarShortestPathAlgorithmConfig A* ShortestPath算法执行配置参数
     *
     * @return A* ShortestPath算法计算结果集
     */
    public AStarShortestPathAlgorithmResult executeAStarShortestPathAlgorithm(String graphName, AStarShortestPathAlgorithmConfig aStarShortestPathAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;

    /**
     * 在指定名称的可分析图上执行 Yen's KShortestPath 算法
     *
     *  @param graphName String 可分析图名称
     *  @param yensKShortestPathAlgorithmConfig YensKShortestPathAlgorithmConfig Yen's KShortestPath算法执行配置参数
     *
     * @return Yen's KShortestPath算法计算结果集
     */
    public YensKShortestPathAlgorithmResult executeYensKShortestPathAlgorithm(String graphName, YensKShortestPathAlgorithmConfig yensKShortestPathAlgorithmConfig) throws CoreRealmServiceRuntimeException, CoreRealmServiceEntityExploreException;
}
