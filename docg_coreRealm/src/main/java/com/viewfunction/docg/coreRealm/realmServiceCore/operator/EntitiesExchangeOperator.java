package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.analysis.query.QueryParameters;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationStatistics;

public interface EntitiesExchangeOperator {

    /**
     * 输入概念类型名称以及一个 Apache Arrow 格式的数据文件，将数据文件中的概念实体数据导入到概念类型中。数据文件必须由 EntitiesExchangeOperator 中的 exportConceptionEntitiesToArrow 方法生成
     *
     * @param conceptionKindName String 概念类型名称
     * @param arrowFileLocation String Apache Arrow 格式的数据文件存储路径
     *
     * @return 导入操作的执行结果统计信息
     */
    public EntitiesOperationStatistics importConceptionEntitiesFromArrow(String conceptionKindName, String arrowFileLocation);

    /**
     * 输入概念类型名称以及一个 Apache Arrow 格式的数据文件存储路径，将概念类型的所有实体数据导出到数据文件中
     *
     * @param conceptionKindName String 概念类型名称
     * @param arrowFileLocation String Apache Arrow 格式的数据文件存储路径
     *
     * @return 导出操作的执行结果统计信息
     */
    public EntitiesOperationStatistics exportConceptionEntitiesToArrow(String conceptionKindName,String arrowFileLocation);

    /**
     * 输入概念类型名称以及一个 CSV 格式的数据文件，将数据文件中的概念实体数据导入到概念类型中。数据文件必须由 EntitiesExchangeOperator 中的 exportConceptionEntitiesToCSV 方法生成
     *
     * @param conceptionKindName String 概念类型名称
     * @param csvFileLocation String CSV 格式的数据文件存储路径
     *
     * @return 导入操作的执行结果统计信息
     */
    public EntitiesOperationStatistics importConceptionEntitiesFromCSV(String conceptionKindName,String csvFileLocation);

    /**
     * 输入概念类型名称以及一个 CSV 格式的数据文件存储路径，将概念类型的所有实体数据导出到数据文件中
     *
     * @param conceptionKindName String 概念类型名称
     * @param csvFileLocation String CSV 格式的数据文件存储路径
     *
     * @return 导出操作的执行结果统计信息
     */
    public EntitiesOperationStatistics exportConceptionEntitiesToCSV(String conceptionKindName,String csvFileLocation);

    /**
     * 输入一个 Apache Arrow 格式的数据文件存储路径，将领域模型中的所有实体数据导出到数据文件中
     *
     * @param arrowFileLocation String Apache Arrow 格式的数据文件存储路径
     *
     * @return 导出操作的执行结果统计信息
     */
    public EntitiesOperationStatistics exportCoreRealmEntitiesToArrow(String arrowFileLocation);

    /**
     * 输入一个 Apache Arrow 格式的数据文件，将数据文件中的所有数据导入到领域模型中。数据文件必须由 EntitiesExchangeOperator 中的 exportCoreRealmEntitiesToArrow 方法生成
     *
     * @param arrowFileLocation String Apache Arrow 格式的数据文件存储路径
     *
     * @return 导入操作的执行结果统计信息
     */
    public EntitiesOperationStatistics importCoreRealmEntitiesFromArrow(String arrowFileLocation);

    /**
     * 输入概念类型名称,查询过滤条件以及一个 Apache Arrow 格式的数据文件存储路径,查询符合过滤条件的概念实体对象，将所有实体数据导出到数据文件中
     *
     * @param conceptionKindName String 概念类型名称
     * @param queryParameters QueryParameters 查询过滤条件
     * @param arrowFileLocation String Apache Arrow 格式的数据文件存储路径
     *
     * @return 导出操作的执行结果统计信息
     */
    public EntitiesOperationStatistics exportConceptionEntitiesToArrow(String conceptionKindName,QueryParameters queryParameters,String arrowFileLocation);

    /**
     * 输入概念类型名称,查询过滤条件以及一个 CSV 格式的数据文件存储路径，查询符合过滤条件的概念实体对象，将所有实体数据导出到数据文件中
     *
     * @param conceptionKindName String 概念类型名称
     * @param queryParameters QueryParameters 查询过滤条件
     * @param csvFileLocation String CSV 格式的数据文件存储路径
     *
     * @return 导出操作的执行结果统计信息
     */
    public EntitiesOperationStatistics exportConceptionEntitiesToCSV(String conceptionKindName,QueryParameters queryParameters,String csvFileLocation);
}
