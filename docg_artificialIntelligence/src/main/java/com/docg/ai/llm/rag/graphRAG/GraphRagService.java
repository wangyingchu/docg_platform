package com.docg.ai.llm.rag.graphRAG;

import java.util.List;

public class GraphRagService {
    private final Neo4jService neo4jService;
    private final LlmService llmService;

    public GraphRagService(Neo4jService neo4jService, LlmService llmService) {
        this.neo4jService = neo4jService;
        this.llmService = llmService;
    }

    // 单轮对话接口，内部用默认 userId
    public String answerQuestion(String question) {
        return answerQuestion("default", question);
    }

    // 支持多轮对话的接口
    public String answerQuestion(String userId, String question) {
        String cypher = "MATCH (p:Person)-[r:WORKS_AT]->(c:Company) " +
                        "OPTIONAL MATCH (p)-[k:KNOWS]->(f:Person) " +
                        "RETURN p.name AS person, p.age AS age, p.job AS job, c.name AS company, c.location AS location, collect(f.name) AS friends";

        cypher ="MATCH (subwayLine:`SubWay-Line`)<-[r:CanAccessSubWayLine]-(subwayStation:`SubWay-Station`) \n" +
                "OPTIONAL MATCH (subwayStation)-[c:ConnectedBySubWayLine]->(subwayStation) \n" +
                "RETURN subwayLine.Line AS line, subwayStation.station AS station, subwayStation.adname AS stationDist, subwayStation.business_a AS stationLocation;";

        cypher = "MATCH (subwayLine:`SubWay-Line`)<-[r:CanAccessSubWayLine]-(subwayStation:`SubWay-Station`) \n" +
                "OPTIONAL MATCH (subwayStation)-[c:ConnectedBySubWayLine]->(subwayStation) \n" +
                "OPTIONAL MATCH (ss:ShoppingService)<-[rb:SubwayStationNearbyAround]-(subwayStation) \n" +
                "RETURN subwayLine.Line AS line, subwayStation.station AS station, subwayStation.adname AS stationDist, subwayStation.business_a AS stationLocation,ss.name AS shopName,ss.address AS shopAddress,ss.adname AS shopDist LIMIT 800;";

        //只使用我提供的信息，回答：从西单如何去阳澄湖大闸蟹(角门路店)商店
        //哪些地铁站周围有卖水果的商店
        //只使用我提供的信息，回答：坐4号线去哪站可以买珠宝

        List<String> graphResults = neo4jService.queryGraph(cypher);

        StringBuilder info = new StringBuilder();
        for (String row : graphResults) {
            info.append(row).append("\n");
        }

        String prompt = "图数据库中有如下信息：\n" + info + "请根据这些信息回答：" + question;
        return llmService.chatWithMemory(userId, prompt);
    }

    // 设置上下文拼接模式
    public void setContextMode(LlmService.ContextMode mode) {
        llmService.setContextMode(mode);
    }

    // 清空指定用户的记忆
    public void clearUserMemory(String userId) {
        llmService.clearHistory(userId);
    }
} 