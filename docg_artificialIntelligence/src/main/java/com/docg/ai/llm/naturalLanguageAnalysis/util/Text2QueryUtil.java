package com.docg.ai.llm.naturalLanguageAnalysis.util;

import com.docg.ai.util.config.PropertiesHandler;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.SystemMaintenanceOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.AttributeSystemInfo;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionKindCorrelationInfo;
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

       String answer = model.chat(prompt);

       logger.debug("Generated Cypher Statement: [{}] For Question: [{}]", answer, question);
       return answer;
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
            SystemMaintenanceOperator systemMaintenanceOperator = targetCoreRealm.getSystemMaintenanceOperator();
            if(systemMaintenanceOperator != null){
                List<EntityStatisticsInfo> realtimeConceptionList = targetCoreRealm.getConceptionEntitiesStatistics();
                List<EntityStatisticsInfo> realtimeRelationList = targetCoreRealm.getRelationEntitiesStatistics();

                Map<String, List<AttributeSystemInfo>> conceptionKindsAttributesSystemInfo = systemMaintenanceOperator.getAllConceptionKindsAttributesSystemInfo();
                String nodePropertiesContent = getTypePropertiesContent("Node properties:\n",realtimeConceptionList,conceptionKindsAttributesSystemInfo);

                Map<String, List<AttributeSystemInfo>> relationKindsAttributesSystemInfo = systemMaintenanceOperator.getAllRelationKindsAttributesSystemInfo();
                String relationPropertiesContent = getTypePropertiesContent("Relationship properties:\n",realtimeRelationList,relationKindsAttributesSystemInfo);

                Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet = systemMaintenanceOperator.getPeriodicCollectedConceptionKindCorrelationRuntimeInfo();
                if(conceptionKindCorrelationInfoSet == null || conceptionKindCorrelationInfoSet.isEmpty()){
                    conceptionKindCorrelationInfoSet = systemMaintenanceOperator.getConceptionKindCorrelationRuntimeInfo(0.01f);
                    systemMaintenanceOperator.executeConceptionKindCorrelationRuntimeInfoPeriodicCollect(7200);
                }
                String relationsContent = getRelationshipsContent(conceptionKindCorrelationInfoSet);
                graphSchema = nodePropertiesContent +"\n"+relationPropertiesContent+"\n"+relationsContent;
            }
        } catch (CoreRealmServiceEntityExploreException e) {
           throw new RuntimeException(e);
        } catch (CoreRealmServiceRuntimeException e) {
            throw new RuntimeException(e);
        }
        return graphSchema;
   }

    private static String getTypePropertiesContent(String titleText,List<EntityStatisticsInfo> kindEntityStatisticsInfoList,Map<String, List<AttributeSystemInfo>> kindsAttributesSystemInfo){
        StringBuilder typePropertiesSb = new StringBuilder();
        typePropertiesSb.append(titleText);
        if(kindEntityStatisticsInfoList != null){
            for(EntityStatisticsInfo currentEntityStatisticsInfo:kindEntityStatisticsInfoList){
                String kindName = currentEntityStatisticsInfo.getEntityKindName();
                typePropertiesSb.append(kindName + " {");
                if(kindsAttributesSystemInfo.containsKey(kindName)){
                    List<AttributeSystemInfo> attributeSystemInfoList = kindsAttributesSystemInfo.get(kindName);
                    for(AttributeSystemInfo attributeSystemInfo : attributeSystemInfoList){
                        typePropertiesSb.append(attributeSystemInfo.getAttributeName() + ": " + attributeSystemInfo.getDataType() + ",");
                    }
                }
                if(typePropertiesSb.charAt(typePropertiesSb.length() - 1) == ','){
                    typePropertiesSb.deleteCharAt(typePropertiesSb.length() - 1);
                }
                typePropertiesSb.append("}\n");
            }
        }
        return typePropertiesSb.toString();
    }

    private static String getRelationshipsContent(Set<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet){
        StringBuilder relationshipInfoSb = new StringBuilder();
        relationshipInfoSb.append("Relationships:\n");

        if(conceptionKindCorrelationInfoSet != null && !conceptionKindCorrelationInfoSet.isEmpty()){
            for(ConceptionKindCorrelationInfo currentConceptionKindCorrelationInfo:conceptionKindCorrelationInfoSet){
                String currentRelationInfoText = "(:"+currentConceptionKindCorrelationInfo.getSourceConceptionKindName()+")-[:"+currentConceptionKindCorrelationInfo.getRelationKindName()+"]->(:"+currentConceptionKindCorrelationInfo.getTargetConceptionKindName()+")\n";
                relationshipInfoSb.append(currentRelationInfoText);
            }
        }
        return relationshipInfoSb.toString();
    }
}
