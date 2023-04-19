package com.viewfunction.docg.coreRealm.realmServiceCore.operator;

import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntitiesOperationStatistics;

public interface EntitiesExchangeOperator {

    /**
     * 输入概念类型名称以及一个 Apache Arrow 格式的数据文件，将数据文件中的概念实体数据导入到概念类型中.数据文件必须由 EntitiesExchangeOperator 中的 exportConceptionEntitiesToArrow 方法生成
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
     * 输入概念类型名称以及一个 CSV 格式的数据文件，将数据文件中的概念实体数据导入到概念类型中.数据文件必须由 EntitiesExchangeOperator 中的 exportConceptionEntitiesToCSV 方法生成
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
}
