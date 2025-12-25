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
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class Text2QueryUtil {
    private static Logger logger = LoggerFactory.getLogger(Text2QueryUtil.class);

    public static String generateQueryCypher(String question){
       if(question == null){
           return null;
       }
       String TEXT2CYPHER_TECH = PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_TECHNOLOGY);
       ChatModel model = null;
       if("OpenAI".equals(TEXT2CYPHER_TECH)){
           model = OpenAiChatModel.builder()
                   .apiKey(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_APIKEY))
                   .modelName(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_NAME))
                   .baseUrl(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_BASEURL))
                   .temperature(0.0)
                   .build();
       }else if("Ollama".equals(TEXT2CYPHER_TECH)){
           model = OllamaChatModel.builder()
                   .modelName(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_NAME))
                   .baseUrl(PropertiesHandler.getPropertyValue(PropertiesHandler.TEXT2CYPHER_MODEL_BASEURL))
                   .temperature(0.0)
                   .build();
       }

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

       if(model != null){
           String answer = model.chat(prompt);
           int finishPartOfThink = answer.indexOf("</think>");
           if(finishPartOfThink != -1){
               answer = answer.substring(finishPartOfThink+8, answer.length());
               answer = answer.trim();
           }
           answer=answer.replace("```cypher","");
           answer=answer.replace("```","");
           logger.debug("Generated Cypher Statement: [{}] For Question: [{}]", answer, question);
           return answer.trim();
       }
       return null;
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
            ZonedDateTime currentDateTime = ZonedDateTime.now();
            if(systemMaintenanceOperator != null){
                List<EntityStatisticsInfo> realtimeConceptionList = targetCoreRealm.getConceptionEntitiesStatistics();
                List<EntityStatisticsInfo> realtimeRelationList = targetCoreRealm.getRelationEntitiesStatistics();

                Map<String, List<AttributeSystemInfo>> conceptionKindsAttributesSystemInfo = systemMaintenanceOperator.getPeriodicCollectedConceptionKindsAttributesSystemRuntimeInfo();
                if(conceptionKindsAttributesSystemInfo == null || conceptionKindsAttributesSystemInfo.isEmpty()){
                    conceptionKindsAttributesSystemInfo = systemMaintenanceOperator.getAllConceptionKindsAttributesSystemInfo();
                }else{
                    ZonedDateTime _latestRecordDatetime = conceptionKindsAttributesSystemInfo.values().iterator().next().get(0).getCreateDate();
                    ZonedDateTime oneDayBeforeCurrent = currentDateTime.minusDays(1);
                    if(_latestRecordDatetime.isBefore(oneDayBeforeCurrent)){
                        conceptionKindsAttributesSystemInfo = systemMaintenanceOperator.getAllConceptionKindsAttributesSystemInfo();
                    }
                }
                String nodePropertiesContent = getTypePropertiesContent("Node properties:\n",realtimeConceptionList,conceptionKindsAttributesSystemInfo);

                Map<String, List<AttributeSystemInfo>> relationKindsAttributesSystemInfo = systemMaintenanceOperator.getPeriodicCollectedRelationKindsAttributesSystemRuntimeInfo();
                if(relationKindsAttributesSystemInfo == null || relationKindsAttributesSystemInfo.isEmpty()){
                    relationKindsAttributesSystemInfo = systemMaintenanceOperator.getAllRelationKindsAttributesSystemInfo();
                }else{
                    ZonedDateTime _latestRecordDatetime = relationKindsAttributesSystemInfo.values().iterator().next().get(0).getCreateDate();
                    ZonedDateTime oneDayBeforeCurrent = currentDateTime.minusDays(1);
                    if(_latestRecordDatetime.isBefore(oneDayBeforeCurrent)){
                        relationKindsAttributesSystemInfo = systemMaintenanceOperator.getAllRelationKindsAttributesSystemInfo();
                    }
                }
                String relationPropertiesContent = getTypePropertiesContent("Relationship properties:\n",realtimeRelationList,relationKindsAttributesSystemInfo);

                List<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoList = systemMaintenanceOperator.getPeriodicCollectedConceptionKindCorrelationRuntimeInfo(SystemMaintenanceOperator.PeriodicCollectedInfoRetrieveLogic.LATEST);
                if(conceptionKindCorrelationInfoList == null || conceptionKindCorrelationInfoList.isEmpty()){
                    conceptionKindCorrelationInfoList = systemMaintenanceOperator.getConceptionKindCorrelationRuntimeInfo(1);
                }else{
                    ZonedDateTime _latestRecordDatetime = conceptionKindCorrelationInfoList.get(0).getCreateDate();
                    ZonedDateTime oneDayBeforeCurrent = currentDateTime.minusDays(1);
                    if(_latestRecordDatetime.isBefore(oneDayBeforeCurrent)){
                        conceptionKindCorrelationInfoList = systemMaintenanceOperator.getConceptionKindCorrelationRuntimeInfo(1);
                    }
                }

                String relationsContent = getRelationshipsContent(conceptionKindCorrelationInfoList);
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
                typePropertiesSb.append("`" + kindName + "`"+" {");
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

    private static String getRelationshipsContent(List<ConceptionKindCorrelationInfo> conceptionKindCorrelationInfoSet){
        StringBuilder relationshipInfoSb = new StringBuilder();
        relationshipInfoSb.append("Relationships:\n");

        if(conceptionKindCorrelationInfoSet != null && !conceptionKindCorrelationInfoSet.isEmpty()){
            for(ConceptionKindCorrelationInfo currentConceptionKindCorrelationInfo:conceptionKindCorrelationInfoSet){
                String currentRelationInfoText = "(:"+"`"+currentConceptionKindCorrelationInfo.getSourceConceptionKindName()+"`"+")-[:"+"`"+currentConceptionKindCorrelationInfo.getRelationKindName()+"`"+"]->(:"+"`"+currentConceptionKindCorrelationInfo.getTargetConceptionKindName()+"`"+")\n";
                relationshipInfoSb.append(currentRelationInfoText);
            }
        }
        return relationshipInfoSb.toString();
    }
}
