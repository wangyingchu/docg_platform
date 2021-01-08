package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import org.neo4j.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeScaleOperationUtil {

    private static Logger logger = LoggerFactory.getLogger(TimeScaleOperationUtil.class);

    public static void generateTimeFlowScaleEntities(GraphOperationExecutor workingGraphOperationExecutor,String timeFlowName,int startYear,int endYear){

        String createTimeFlowEntitiesCql = "////// Create and link year, month and day data\n" +
                "WITH range("+startYear+", "+endYear+") AS years, range(1,12) as months\n" +
                "FOREACH(year IN years |\n" +
                "  MERGE (y:DOCG_TS_Year {year:year,id:year,timeFlow:\""+timeFlowName+"\"})\n" +
                "  FOREACH(month IN months |\n" +
                "    CREATE (m:DOCG_TS_Month {month: month,id:month,timeFlow:\""+timeFlowName+"\"})\n" +
                "    MERGE (y)-[:DOCG_TS_Contains]->(m)    \n" +
                "    FOREACH(month IN CASE WHEN month=1 THEN [1] ELSE [] END | \n" +
                "        MERGE (y)-[:DOCG_TS_FirstChildIs]->(m)\n" +
                "    )\n" +
                "    FOREACH(month IN CASE WHEN month=12 THEN [1] ELSE [] END | \n" +
                "        MERGE (y)-[:DOCG_TS_LastChildIs]->(m)\n" +
                "    )    \n" +
                "    FOREACH(day IN (CASE\n" +
                "                      WHEN month IN [1,3,5,7,8,10,12] THEN range(1,31) \n" +
                "                      WHEN month = 2 THEN\n" +
                "                        CASE\n" +
                "                          WHEN year % 4 <> 0 THEN range(1,28)\n" +
                "                          WHEN year % 100 <> 0 THEN range(1,29)\n" +
                "                          WHEN year % 400 = 0 THEN range(1,29)\n" +
                "                          ELSE range(1,28)\n" +
                "                        END\n" +
                "                      ELSE range(1,30)\n" +
                "                    END) |\n" +
                "        CREATE (d:DOCG_TS_Day {day:day,id:day,timeFlow:\""+timeFlowName+"\"})\n" +
                "        MERGE (m)-[:DOCG_TS_Contains]->(d)        \n" +
                "        FOREACH(day IN CASE WHEN day=1 THEN [1] ELSE [] END | \n" +
                "            MERGE (m)-[:DOCG_TS_FirstChildIs]->(d)\n" +
                "        )        \n" +
                "        FOREACH(day IN CASE WHEN day=31 THEN [1] ELSE [] END | \n" +
                "            MERGE (m)-[:DOCG_TS_LastChildIs]->(d)\n" +
                "        )\n" +
                "        FOREACH(day IN CASE WHEN (day=30 and month IN [4,6,9,11]) THEN [1] ELSE [] END | \n" +
                "            MERGE (m)-[:DOCG_TS_LastChildIs]->(d)\n" +
                "        )\n" +
                "        FOREACH(day IN CASE WHEN (day=29 and month =2) THEN [1] ELSE [] END | \n" +
                "            MERGE (m)-[:DOCG_TS_LastChildIs]->(d)\n" +
                "        )\n" +
                "        FOREACH(day IN CASE WHEN (day=28 and month =2 and (year % 4 <> 0)) THEN [1] ELSE [] END | \n" +
                "            MERGE (m)-[:DOCG_TS_LastChildIs]->(d)\n" +
                "        )\n" +
                "    )\n" +
                "  )\n" +
                ")";

        logger.debug("Generated Cypher Statement: {}", createTimeFlowEntitiesCql);

        DataTransformer dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {
                System.out.println(result);
                return null;
            }
        };

        workingGraphOperationExecutor.executeWrite(dataTransformer,createTimeFlowEntitiesCql);
    }
}
