package com.viewfunction.docg.coreRealm.realmServiceCore.feature;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.*;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesGraph;
import com.viewfunction.docg.coreRealm.realmServiceCore.structure.EntitiesPath;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesSpanningTree;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationDirection;

import java.util.List;

public interface PathTravelable {
    /**
     * 从当前实体对象出发展开路径
     *
     * @param relationKindMatchLogics List<RelationKindMatchLogic> 路径上允许的关系类型名称与关系方向组合，如存在该参数至少需要输入一项数值
     * @param defaultDirectionForNoneRelationKindMatch RelationDirection 未输入目标关系类型名称与关系方向组合时使用的全局关系方向，必须为 RelationDirection.FROM 或 RelationDirection.TO
     * @param conceptionKindMatchLogics List<ConceptionKindMatchLogic> 路径上允许的概念类型名称与搜索规则
     * @param minJump int 路径展开的最小跳数
     * @param maxJump int 路径展开的最大跳数
     *
     * @return 符合条件的实体路径列表
     */
    public List<EntitiesPath> expandPath(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                         List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int minJump, int maxJump);
    /**
     * 从当前实体对象出发展开成图
     *
     * @param relationKindMatchLogics List<RelationKindMatchLogic> 图中允许的关系类型名称与关系方向组合，如存在该参数至少需要输入一项数值
     * @param defaultDirectionForNoneRelationKindMatch RelationDirection 未输入目标关系类型名称与关系方向组合时使用的全局关系方向，必须为 RelationDirection.FROM 或 RelationDirection.TO
     * @param conceptionKindMatchLogics List<ConceptionKindMatchLogic> 图中允许的概念类型名称与搜索规则
     * @param containsSelf boolean 是否在图中包含当前对象自身
     * @param maxJump int 图展开的最大跳数
     *
     * @return 符合条件的实体组成的图
     */
    public EntitiesGraph expandGraph(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                        List<ConceptionKindMatchLogic> conceptionKindMatchLogics,boolean containsSelf,int maxJump);

    /**
     * 从当前实体对象出发展开成生成树
     *
     * @param relationKindMatchLogics List<RelationKindMatchLogic> 图中允许的关系类型名称与关系方向组合，如存在该参数至少需要输入一项数值
     * @param defaultDirectionForNoneRelationKindMatch RelationDirection 未输入目标关系类型名称与关系方向组合时使用的全局关系方向，必须为 RelationDirection.FROM 或 RelationDirection.TO
     * @param conceptionKindMatchLogics List<ConceptionKindMatchLogic> 图中允许的概念类型名称与搜索规则
     * @param maxJump int 生成树径展开的最大跳数
     *
     * @return 符合条件的实体组成的图
     */
    public EntitiesSpanningTree expandSpanningTree(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                   List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int maxJump);

    public List<EntitiesPath> advancedExpandPath(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    public EntitiesGraph advancedExpandGraph(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    public EntitiesSpanningTree advancedExpandSpanningTree(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    public List<ConceptionEntity> getEndConceptionEntitiesByPathPattern(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    public List<EntitiesPath> getAllPathBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics,
                                                      RelationDirection defaultDirectionForNoneRelationKindMatch,int maxJump,
                                                      PathEntityFilterParameters relationPathEntityFilterParameters,
                                                      PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public EntitiesPath getShortestPathBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                     PathEntityFilterParameters relationPathEntityFilterParameters,
                                                     PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public EntitiesPath getShortestPathWithWeightBetweenEntity(String targetEntityUID, List<RelationKindMatchLogic> relationKindMatchLogics,
                                                                RelationDirection defaultDirectionForNoneRelationKindMatch,String relationWeightProperty,
                                                                PathEntityFilterParameters relationPathEntityFilterParameters,
                                                                PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public List<EntitiesPath> getShortestPathsWithWeightBetweenEntity(String targetEntityUID, List<RelationKindMatchLogic> relationKindMatchLogics,
                                                                RelationDirection defaultDirectionForNoneRelationKindMatch,String relationWeightProperty,float defaultWeight,int maxPathNumber,
                                                                PathEntityFilterParameters relationPathEntityFilterParameters,
                                                                PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public List<EntitiesPath> getAllShortestPathsBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                     PathEntityFilterParameters relationPathEntityFilterParameters,
                                                               PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    public List<EntitiesPath> getLongestPathsBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics,
                                                      RelationDirection defaultDirectionForNoneRelationKindMatch,int maxJump,int maxPathNumber,
                                                      PathEntityFilterParameters relationPathEntityFilterParameters,
                                                           PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;
}
