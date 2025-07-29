package com.docg;

public class Neo4jText2CypherTest01 {

    private static final String NEO4J_URI = "bolt://localhost:7687";
    private static final String NEO4J_USERNAME = "neo4j";
    private static final String NEO4J_PASSWORD = "your_password";
    private static final String OPENAI_API_KEY = "sk-ea43df124d9c4d45a684af8c8fee1fed";

    public static void main(String[] args) {
        // 1. 初始化 OpenAI 模型

        /*
        ChatLanguageModel model =  OpenAiChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .modelName("deepseek-chat")
                .baseUrl("https://api.deepseek.com/v1")// 可以使用 gpt-4 如果您有权限
                .temperature(0.0)
                .build();
*/
        // 2. 定义图数据库结构
        String graphSchema = """
                Node properties:
                Paper {title: STRING, year: INTEGER, abstract: STRING}
                Author {name: STRING, affiliation: STRING}
                Topic {name: STRING}
                
                Relationship properties:
                WROTE {corresponding: BOOLEAN}
                
                Relationships:
                (:Author)-[:WROTE]->(:Paper)
                (:Paper)-[:HAS_TOPIC]->(:Topic)
                """;

        // 3. 用户自然语言问题
        //String question = "找出所有与核工业领域相关的论文及其作者";

        String question = "查询王小波写的论文里涉及双缝干涉的章节的节点UID";

        // 4. 生成 Cypher 查询
        String prompt = String.format("""
                你是一个 Neo4j Cypher 查询生成专家。基于以下图数据库结构：
                %s
                
                请为以下问题生成一个 Cypher 查询：
                %s
                
                要求：
                1. 只返回 Cypher 查询
                2. 不要包含任何解释或额外文本
                3. 确保查询语法正确
                4. 使用英文节点和关系标签
                """, graphSchema, question);

        // 修正后的生成方式
      //  String cypherQuery = model.chat(prompt);
    //    System.out.println("生成的 Cypher 查询: " + cypherQuery);



        /*
        // 5. 执行 Cypher 查询
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USERNAME, NEO4J_PASSWORD));
             Session session = driver.session()) {

            Result result = session.run(cypherQuery);

            // 6. 处理并显示结果
            System.out.println("\n查询结果:");
            while (result.hasNext()) {
                Record record = result.next();
                System.out.println("论文: " + record.get("paper").asString() +
                        ", 作者: " + record.get("author").asString());
            }
        }
        */


        /*
        *
        *
        try {
    String cypherQuery = model.generate(prompt);
    if (cypherQuery == null || cypherQuery.trim().isEmpty()) {
        throw new RuntimeException("未能生成有效的 Cypher 查询");
    }

    // 执行查询...
} catch (Exception e) {
    System.err.println("发生错误: " + e.getMessage());
    e.printStackTrace();
}


        * */



    }
}
