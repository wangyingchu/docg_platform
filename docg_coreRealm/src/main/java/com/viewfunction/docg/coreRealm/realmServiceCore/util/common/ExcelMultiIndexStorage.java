package com.viewfunction.docg.coreRealm.realmServiceCore.util.common;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelMultiIndexStorage {

    // 原始数据存储
    private List<Map<String, Object>> dataRows = new ArrayList<>();
    private List<String> columnNames;

    // 多列索引映射：组合键 -> 行索引列表
    private Map<String, List<Integer>> compositeIndex = new HashMap<>();

    // 单列索引（可选）
    private Map<String, Map<Object, List<Integer>>> columnIndex = new HashMap<>();

    /**
     * 加载数据并构建索引
     */
    public void loadFromExcel(File excelFile, String... indexedColumns) {
        ExcelReader reader = ExcelUtil.getReader(excelFile);
        List<List<Object>> rows = reader.read();
        reader.close();

        if (rows.isEmpty()) return;

        // 获取列名
        columnNames = rows.get(0).stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        // 转换为 Map 列表
        for (int i = 1; i < rows.size(); i++) {
            Map<String, Object> rowMap = new LinkedHashMap<>();
            List<Object> row = rows.get(i);
            for (int j = 0; j < columnNames.size() && j < row.size(); j++) {
                rowMap.put(columnNames.get(j), row.get(j));
            }
            dataRows.add(rowMap);
        }

        // 构建索引
        if (indexedColumns.length > 0) {
            buildCompositeIndex(indexedColumns);
        }
    }

    /**
     * 构建组合列索引
     */
    private void buildCompositeIndex(String[] indexedColumns) {
        for (int i = 0; i < dataRows.size(); i++) {
            Map<String, Object> row = dataRows.get(i);
            String compositeKey = buildCompositeKey(row, indexedColumns);

            compositeIndex.computeIfAbsent(compositeKey, k -> new ArrayList<>()).add(i);
        }
    }

    /**
     * 构建单个列索引
     */
    public void buildSingleColumnIndex(String columnName) {
        Map<Object, List<Integer>> index = new HashMap<>();
        for (int i = 0; i < dataRows.size(); i++) {
            Object value = dataRows.get(i).get(columnName);
            index.computeIfAbsent(value, k -> new ArrayList<>()).add(i);
        }
        columnIndex.put(columnName, index);
    }

    /**
     * 使用组合索引查询
     */
    public List<Map<String, Object>> queryByCompositeKey(Map<String, Object> conditions) {
        String[] columns = conditions.keySet().toArray(new String[0]);
        String targetKey = buildCompositeKey(conditions, columns);

        List<Integer> rowIndexes = compositeIndex.getOrDefault(targetKey, Collections.emptyList());
        return rowIndexes.stream()
                .map(dataRows::get)
                .collect(Collectors.toList());
    }

    /**
     * 使用单列索引查询
     */
    public List<Map<String, Object>> queryByColumn(String columnName, Object value) {
        Map<Object, List<Integer>> index = columnIndex.get(columnName);
        if (index == null) {
            // 没有索引则全表扫描
            return dataRows.stream()
                    .filter(row -> Objects.equals(row.get(columnName), value))
                    .collect(Collectors.toList());
        }

        List<Integer> rowIndexes = index.getOrDefault(value, Collections.emptyList());
        return rowIndexes.stream()
                .map(dataRows::get)
                .collect(Collectors.toList());
    }

    /**
     * 多条件组合查询（AND 逻辑）
     */
    public List<Map<String, Object>> queryByAnd(Map<String, Object> conditions) {
        return dataRows.stream()
                .filter(row -> conditions.entrySet().stream()
                        .allMatch(cond -> Objects.equals(row.get(cond.getKey()), cond.getValue())))
                .collect(Collectors.toList());
    }

    /**
     * 多条件组合查询（OR 逻辑）
     */
    public List<Map<String, Object>> queryByOr(Map<String, Object> conditions) {
        return dataRows.stream()
                .filter(row -> conditions.entrySet().stream()
                        .anyMatch(cond -> Objects.equals(row.get(cond.getKey()), cond.getValue())))
                .collect(Collectors.toList());
    }

    /**
     * 聚合查询：按指定列分组统计
     */
    public Map<Object, Long> groupByAndCount(String groupColumn) {
        return dataRows.stream()
                .collect(Collectors.groupingBy(
                        row -> row.get(groupColumn),
                        Collectors.counting()
                ));
    }

    /**
     * 聚合查询：分组求和
     */
    public Map<Object, Double> groupByAndSum(String groupColumn, String sumColumn) {
        return dataRows.stream()
                .collect(Collectors.groupingBy(
                        row -> row.get(groupColumn),
                        Collectors.summingDouble(row -> {
                            Object value = row.get(sumColumn);
                            if (value instanceof Number) {
                                return ((Number) value).doubleValue();
                            }
                            return 0.0;
                        })
                ));
    }

    private String buildCompositeKey(Map<String, Object> row, String[] columns) {
        return Arrays.stream(columns)
                .map(col -> String.valueOf(row.get(col)))
                .collect(Collectors.joining("|"));
    }

    /**
     * 获取所有数据
     */
    public List<Map<String, Object>> getAllData() {
        return dataRows;
    }

    /**
     * 打印数据
     */
    public void printData() {
        System.out.println(String.join("\t", columnNames));
        for (Map<String, Object> row : dataRows) {
            for (String col : columnNames) {
                System.out.print(row.get(col) + "\t");
            }
            System.out.println();
        }
    }
}