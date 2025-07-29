package com.docg;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class TextToCypherText {
    static String openAiApiKey = "sk-ea43df124d9c4d45a684af8c8fee1fed";
    public static  void main(String[] args){
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                //.modelName("gpt-3.5-turbo") // 可根据需要更换模型
                .modelName("deepseek-chat")
                .baseUrl("https://api.deepseek.com/v1")
                .build();

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


        String question = "查询王小波写的论文里提到鬼灭之刃的章节的节点UID";

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








        String answer = model.chat(prompt);
        // 记录本轮问答
       /// history.add("用户: " + prompt);
        //history.add("AI: " + answer);

System.out.println(answer);





    }



}
