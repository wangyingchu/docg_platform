package com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.util;

import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.GraphOperationExecutor;
import com.viewfunction.docg.coreRealm.realmServiceCore.internal.neo4j.dataTransformer.DataTransformer;
import org.neo4j.driver.Result;

public class TimeScaleOperationUtil {

    public static void generateTimeFlowScaleEntities(GraphOperationExecutor workingGraphOperationExecutor,String timeFlowName){





        DataTransformer dataTransformer = new DataTransformer() {
            @Override
            public Object transformResult(Result result) {


                System.out.println(result);

                return null;
            }
        };










        String createTimeFlowEntitiesCql = "WITH range(2011, 2014) AS years, range(1,12) as months\n" +
                "FOREACH(year IN years |\n" +
                "  MERGE (y:Year {year: year})\n" +
                "  FOREACH(month IN months |\n" +
                "    CREATE (m:Month {month: month})\n" +
                "    MERGE (y)-[:HAS_MONTH]->(m)\n" +
                "    FOREACH(day IN (CASE\n" +
                "                      WHEN month IN [1,3,5,7,8,10,12] THEN range(1,31)\n" +
                "                      WHEN month = 2 THEN\n" +
                "                        CASE\n" +
                "                          WHEN year % 4 <> 0 THEN range(1,28)\n" +
                "                          WHEN year % 100 <> 0 THEN range(1,29)\n" +
                "                          WHEN year % 400 = 0 THEN range(1,29)\n" +
                "                          ELSE range(1,28)\n" +
                "                        END\n" +
                "                      ELSE range(1,30)\n" +
                "                    END) |\n" +
                "      CREATE (d:Day {day: day})\n" +
                "      MERGE (m)-[:HAS_DAY]->(d))))\n" +
                "\n" +
                "WITH *\n" +
                "\n" +
                "MATCH (year:Year)-[:HAS_MONTH]->(month)-[:HAS_DAY]->(day)\n" +
                "WITH year,month,day\n" +
                "ORDER BY year.year, month.month, day.day\n" +
                "WITH collect(day) as days\n" +
                "FOREACH(i in RANGE(0, length(days)-2) |\n" +
                "    FOREACH(day1 in [days[i]] |\n" +
                "        FOREACH(day2 in [days[i+1]] |\n" +
                "            CREATE UNIQUE (day1)-[:NEXT]->(day2))))" ;



        workingGraphOperationExecutor.executeWrite(dataTransformer,createTimeFlowEntitiesCql);



    }
}
