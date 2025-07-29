package com.docg.ai.llm.naturalLanguageAnalysis.util;

import com.docg.ai.util.config.PropertiesHandler;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Text2QueryUtil {
    private static Logger logger = LoggerFactory.getLogger(Text2QueryUtil.class);

    public static String generateQueryCypher(String question){
       if(question == null){
           return null;
       }

       // question = "查询王小波写的论文里提到鬼灭之刃的章节的节点UID";

        ChatModel model = OpenAiChatModel.builder()
               .apiKey(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_APIKEY))
               .modelName(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_NAME))
               .baseUrl(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_BASEURL))
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

        logger.debug("Generated Cypher Statement: {} for Question {}", answer, question);
        return answer;
   }
}
