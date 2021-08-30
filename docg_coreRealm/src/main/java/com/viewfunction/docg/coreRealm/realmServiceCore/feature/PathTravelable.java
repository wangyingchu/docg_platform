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
     * @return 符合条件的实体组成的生成树
     */
    public EntitiesSpanningTree expandSpanningTree(List<RelationKindMatchLogic> relationKindMatchLogics, RelationDirection defaultDirectionForNoneRelationKindMatch,
                                                   List<ConceptionKindMatchLogic> conceptionKindMatchLogics, int maxJump);

    /**
     * 使用高级自定义参数从当前实体对象出发展开路径
     *
     * @param travelParameters TravelParameters 路径展开所需配置参数
     *
     * @return 符合条件的实体路径列表
     */
    public List<EntitiesPath> advancedExpandPath(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 使用高级自定义参数从当前实体对象出发展开成图
     *
     * @param travelParameters TravelParameters 图展开所需配置参数
     *
     * @return 符合条件的实体组成的图
     */
    public EntitiesGraph advancedExpandGraph(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 使用高级自定义参数从当前实体对象出发展开成生成树
     *
     * @param travelParameters TravelParameters 图展开所需配置参数
     *
     * @return 符合条件的实体组成的生成树
     */
    public EntitiesSpanningTree advancedExpandSpanningTree(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 使用高级自定义参数从当前实体对象出发展开路径并返回路径终点的概念实体
     *
     * @param travelParameters TravelParameters 路径展开所需配置参数
     *
     * @return 符合条件的概念实体列表
     */
    public List<ConceptionEntity> getEndConceptionEntitiesByPathPattern(TravelParameters travelParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前实体对象与指定概念实体对象间的所有路径
     *
     * @param targetEntityUID String 目标概念实体对象的唯一ID
     * @param relationKindMatchLogics List<RelationKindMatchLogic> 路径上允许的关系类型名称与关系方向组合，如存在该参数至少需要输入一项数值
     * @param defaultDirectionForNoneRelationKindMatch RelationDirection 未输入目标关系类型名称与关系方向组合时使用的全局关系方向，必须为 RelationDirection.FROM 或 RelationDirection.TO
     * @param maxJump int 路径展开的最大跳数
     * @param relationPathEntityFilterParameters PathEntityFilterParameters 路径中的关系实体的属性过滤条件与规则
     * @param conceptionPathEntityFilterParameters PathEntityFilterParameters 路径中的概念实体的属性过滤条件与规则
     *
     * @return 符合条件的实体路径列表
     */
    public List<EntitiesPath> getAllPathBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics,
                                                      RelationDirection defaultDirectionForNoneRelationKindMatch,int maxJump,
                                                      PathEntityFilterParameters relationPathEntityFilterParameters,
                                                      PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前实体对象与指定概念实体对象间的一条最短路径
     *
     * @param targetEntityUID String 目标概念实体对象的唯一ID
     * @param pathAllowedRelationKinds List<String> 路径上允许出现的关系类型名称列表
     * @param maxJump int 路径展开的最大跳数
     * @param relationPathEntityFilterParameters PathEntityFilterParameters 路径中的关系实体的属性过滤条件与规则
     * @param conceptionPathEntityFilterParameters PathEntityFilterParameters 路径中的概念实体的属性过滤条件与规则
     *
     * @return 符合条件的实体路径
     */
    public EntitiesPath getShortestPathBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                     PathEntityFilterParameters relationPathEntityFilterParameters,
                                                     PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前实体对象与指定概念实体对象间的一条带权重的最短路径
     *
     * @param targetEntityUID String 目标概念实体对象的唯一ID
     * @param relationKindMatchLogics List<RelationKindMatchLogic> 路径上允许的关系类型名称与关系方向组合，如存在该参数至少需要输入一项数值
     * @param defaultDirectionForNoneRelationKindMatch RelationDirection 未输入目标关系类型名称与关系方向组合时使用的全局关系方向，必须为 RelationDirection.FROM 或 RelationDirection.TO
     * @param relationWeightProperty String 路径中的关系实体上的参与权重计算的属性名称
     * @param relationPathEntityFilterParameters PathEntityFilterParameters 路径中的关系实体的属性过滤条件与规则
     * @param conceptionPathEntityFilterParameters PathEntityFilterParameters 路径中的概念实体的属性过滤条件与规则
     *
     * @return 符合条件的实体路径
     */
    public EntitiesPath getShortestPathWithWeightBetweenEntity(String targetEntityUID, List<RelationKindMatchLogic> relationKindMatchLogics,
                                                                RelationDirection defaultDirectionForNoneRelationKindMatch,String relationWeightProperty,
                                                                PathEntityFilterParameters relationPathEntityFilterParameters,
                                                                PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前实体对象与指定概念实体对象间的若干条带权重的最短路径
     *
     * @param targetEntityUID String 目标概念实体对象的唯一ID
     * @param relationKindMatchLogics List<RelationKindMatchLogic> 路径上允许的关系类型名称与关系方向组合，如存在该参数至少需要输入一项数值
     * @param defaultDirectionForNoneRelationKindMatch RelationDirection 未输入目标关系类型名称与关系方向组合时使用的全局关系方向，必须为 RelationDirection.FROM 或 RelationDirection.TO
     * @param relationWeightProperty String 路径中的关系实体上的参与权重计算的属性名称
     * @param defaultWeight String 权重计算的默认值，（当关系实体没有relationWeightProperty 指定的属性时使用此值）
     * @param maxPathNumber int 返回的路径的最大跳数
     * @param relationPathEntityFilterParameters PathEntityFilterParameters 路径中的关系实体的属性过滤条件与规则
     * @param conceptionPathEntityFilterParameters PathEntityFilterParameters 路径中的概念实体的属性过滤条件与规则
     *
     * @return 符合条件的实体路径列表
     */
    public List<EntitiesPath> getShortestPathsWithWeightBetweenEntity(String targetEntityUID, List<RelationKindMatchLogic> relationKindMatchLogics,
                                                                RelationDirection defaultDirectionForNoneRelationKindMatch,String relationWeightProperty,float defaultWeight,int maxPathNumber,
                                                                PathEntityFilterParameters relationPathEntityFilterParameters,
                                                                PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前实体对象与指定概念实体对象间的所有最短路径
     *
     * @param targetEntityUID String 目标概念实体对象的唯一ID
     * @param pathAllowedRelationKinds List<String> 路径上允许出现的关系类型名称列表
     * @param maxJump int 路径展开的最大跳数
     * @param relationPathEntityFilterParameters PathEntityFilterParameters 路径中的关系实体的属性过滤条件与规则
     * @param conceptionPathEntityFilterParameters PathEntityFilterParameters 路径中的概念实体的属性过滤条件与规则
     *
     * @return 符合条件的实体路径列表
     */
    public List<EntitiesPath> getAllShortestPathsBetweenEntity(String targetEntityUID, List<String> pathAllowedRelationKinds, int maxJump,
                                                        PathEntityFilterParameters relationPathEntityFilterParameters,
                                                               PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;

    /**
     * 获取当前实体对象与指定概念实体对象间的若干条最长路径
     *
     * @param targetEntityUID String 目标概念实体对象的唯一ID
     * @param relationKindMatchLogics List<RelationKindMatchLogic> 路径上允许的关系类型名称与关系方向组合，如存在该参数至少需要输入一项数值
     * @param defaultDirectionForNoneRelationKindMatch RelationDirection 未输入目标关系类型名称与关系方向组合时使用的全局关系方向，必须为 RelationDirection.FROM 或 RelationDirection.TO
     * @param maxJump int 路径展开的最大跳数
     * @param maxPathNumber int 返回的路径的最大跳数
     * @param relationPathEntityFilterParameters PathEntityFilterParameters 路径中的关系实体的属性过滤条件与规则
     * @param conceptionPathEntityFilterParameters PathEntityFilterParameters 路径中的概念实体的属性过滤条件与规则
     *
     * @return 符合条件的实体路径列表
     */
    public List<EntitiesPath> getLongestPathsBetweenEntity(String targetEntityUID,List<RelationKindMatchLogic> relationKindMatchLogics,
                                                      RelationDirection defaultDirectionForNoneRelationKindMatch,int maxJump,int maxPathNumber,
                                                      PathEntityFilterParameters relationPathEntityFilterParameters,
                                                           PathEntityFilterParameters conceptionPathEntityFilterParameters) throws CoreRealmServiceEntityExploreException;
}
