package com.docg.ai.llm.naturalLanguageAnalysis.util;

import com.docg.ai.util.config.PropertiesHandler;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeSystemInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.EntityStatisticsInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Text2QueryUtil {
    private static Logger logger = LoggerFactory.getLogger(Text2QueryUtil.class);

    public static String generateQueryCypher(String question){
       if(question == null){
           return null;
       }

       ChatModel model = OpenAiChatModel.builder()
               .apiKey(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_APIKEY))
               .modelName(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_NAME))
               .baseUrl(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_BASEURL))
               .build();

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
                """, getGraphSchema(), question);

        return "==================";
       //String answer = model.chat(prompt);

       //logger.debug("Generated Cypher Statement: [{}] For Question: [{}]", answer, question);
      // return answer;
   }

   private static String getGraphSchema(){
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

        CoreRealm targetCoreRealm = RealmTermFactory.getDefaultCoreRealm();
        try {
           List<EntityStatisticsInfo> realtimeConceptionList = targetCoreRealm.getConceptionEntitiesStatistics();
           List<EntityStatisticsInfo> realtimeRelationList = targetCoreRealm.getRelationEntitiesStatistics();


       } catch (CoreRealmServiceEntityExploreException e) {
           throw new RuntimeException(e);
       }


       SystemMaintenanceOperator systemMaintenanceOperator = targetCoreRealm.getSystemMaintenanceOperator();
        if(systemMaintenanceOperator != null){






            Map<String, List<AttributeSystemInfo>> conceptionKindsAttributesSystemInfo = systemMaintenanceOperator.getAllConceptionKindsAttributesSystemInfo();
            //System.out.println(conceptionKindsAttributesSystemInfo);
            String nodePropertiesContent = getNodePropertiesContent(conceptionKindsAttributesSystemInfo);
            System.out.println("--------------");
            System.out.println(nodePropertiesContent);
            System.out.println("--------------");


            Map<String, List<AttributeSystemInfo>> relationKindsAttributesSystemInfo = systemMaintenanceOperator.getAllRelationKindsAttributesSystemInfo();
            System.out.println("++++++++++++++++++++");
            System.out.println("++++++++++++++++++++");
            System.out.println(relationKindsAttributesSystemInfo);
            System.out.println("++++++++++++++++++++");
            System.out.println("++++++++++++++++++++");
        }

       return graphSchema;
   }

   private static String getNodePropertiesContent(Map<String, List<AttributeSystemInfo>> conceptionKindsAttributesSystemInfo){
        StringBuilder nodePropertiesSb = new StringBuilder();
        nodePropertiesSb.append("Node properties:\n");
        if(conceptionKindsAttributesSystemInfo != null){
            Set<String> conceptionKindNameSet = conceptionKindsAttributesSystemInfo.keySet();
            for(String conceptionKindName : conceptionKindNameSet){
                nodePropertiesSb.append(conceptionKindName + " {");
                List<AttributeSystemInfo> attributeSystemInfoList = conceptionKindsAttributesSystemInfo.get(conceptionKindName);
                for(AttributeSystemInfo attributeSystemInfo : attributeSystemInfoList){
                    nodePropertiesSb.append(attributeSystemInfo.getAttributeName() + ": " + attributeSystemInfo.getDataType() + ",");
                }
                nodePropertiesSb.deleteCharAt(nodePropertiesSb.length() - 1);
                nodePropertiesSb.append("}\n");
            }
        }
        return nodePropertiesSb.toString();
   }
}
