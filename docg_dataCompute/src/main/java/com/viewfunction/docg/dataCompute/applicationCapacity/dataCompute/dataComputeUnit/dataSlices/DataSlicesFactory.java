package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.dataSlices;

import com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.dataComputeUnit.util.DataComputeConfigurationHandler;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.log4j2.Log4J2Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataSlicesFactory {

    public static void main(String[] args) throws IgniteCheckedException {

        final Map<String, Object> filedMap = new HashMap<>();
        filedMap.put("field0", String.class.getName());
        filedMap.put("field1", Integer.class.getName());
        filedMap.put("field2", Boolean.class.getName());
        filedMap.put("pk", String.class.getName());

        IgniteConfiguration cfg = DataComputeConfigurationHandler.getDataComputeIgniteConfiguration();
        //用户自定义值类型 设置为默认
        QueryEntity qe = new QueryEntity(String.class.getName(), "SQL_CUSTOM_VALUE_TYPE");
       // qe.setTableName("tableName001");
        qe.addQueryField("pk", String.class.getName(), null);

        for (Map.Entry<String, Object> filedEntry : filedMap.entrySet()) {
            qe.addQueryField(filedEntry.getKey(), filedEntry.getValue().toString(), null);
        }
        qe.setKeyFieldName("pk");

        CacheConfiguration cacheCfg = new CacheConfiguration().setQueryEntities(Collections.singleton(qe)).setName("DataCube01");
        cacheCfg.setSqlSchema("DataCube01");
        //CacheConfiguration enableCacheMetricsCfg = enableCacheMetrics(cfg, cacheCfg);
        //cfg.setCacheConfiguration(enableCacheMetricsCfg);

        cfg.setClientMode(true);
        Ignite ignite = Ignition.getOrStart(cfg);
        IgniteCache cache = ignite.getOrCreateCache(cacheCfg).withKeepBinary();
        //ignite.active();


        Map<String, Object> input = new HashMap<>();
        input.put("field0", "value0");
        input.put("field1", 0);
        input.put("field2", false);
        input.put("pk", "pk001");


        InsertData insertData = new InsertData("pk", input);
        //StringBuilder sb = new StringBuilder("INSERT INTO ").append("tableName001").append(" (")
        StringBuilder sb = new StringBuilder("INSERT INTO ").append("DEFAULT_CACHE_NAME").append(" (")
                .append(insertData.getInsertFields()).append(") VALUES (")
                .append(insertData.getInsertParams()).append(')');

        SqlFieldsQuery qry = new SqlFieldsQuery(sb.toString());
        qry.setArgs(insertData.getArgs());
        //execute sql
        cache.query(qry).getAll();



        ignite.close();

    }



    private static class InsertData {
        private final Object[] args;
        private final String insertFields;
        private final String insertParams;


        /**
         * @param key    Key.
         * @param values Field values.
         */
        InsertData(String key, Map<String, Object> values) {
            args = new Object[values.size() + 1];

            int idx = 0;
            args[idx++] = key;

            StringBuilder sbFields = new StringBuilder("primaryKey001");
            StringBuilder sbParams = new StringBuilder("?");

            for (Map.Entry<String, Object> e : values.entrySet()) {
                args[idx++] = e.getValue();
                sbFields.append(',').append(e.getKey());
                sbParams.append(", ?");
            }

            insertFields = sbFields.toString();
            insertParams = sbParams.toString();
        }

        public Object[] getArgs() {
            return args;
        }

        public String getInsertFields() {
            return insertFields;
        }

        public String getInsertParams() {
            return insertParams;
        }
    }













}
