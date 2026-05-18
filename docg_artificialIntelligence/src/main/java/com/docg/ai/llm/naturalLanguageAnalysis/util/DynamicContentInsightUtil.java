package com.docg.ai.llm.naturalLanguageAnalysis.util;

import com.docg.ai.util.config.PropertiesHandler;
import com.docg.ai.util.markdown.MarkdownIllegalCharChecker;
import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceEntityExploreException;
import com.viewfunction.docg.coreRealm.realmServiceCore.operator.CrossKindDataOperator;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.ConceptionEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DynamicContentQueryResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DynamicContentValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.RelationEntityValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.CoreRealm;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.util.factory.RealmTermFactory;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicContentInsightUtil {

    public static String insightToDynamicContent(String queryQuestion,DynamicContentQueryResult dynamicContentQueryResult){
        String content = getDynamicContentText(dynamicContentQueryResult);
        String DYNAMIC_CONTENT_INSIGHT_TECH = PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_TECHNOLOGY);
        ChatModel model = null;
        if("OpenAI".equals(DYNAMIC_CONTENT_INSIGHT_TECH)){
            model = OpenAiChatModel.builder()
                    .apiKey(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_APIKEY))
                    .modelName(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_NAME))
                    .baseUrl(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_BASEURL))
                    .build();
        }else if("Ollama".equals(DYNAMIC_CONTENT_INSIGHT_TECH)){
            model = OllamaChatModel.builder()
                    .modelName(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_NAME))
                    .baseUrl(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_BASEURL))
                    .build();
        }

        String prompt = String.format("""
                以下数据内容是问题 "%s" 的回答：
                %s
               
                要求：
                1. 结合问题进行分析
                2. 不要分析数据结构和可能来源
                3. 不要包含标题
                4. 除了包含常规的文字分析结果外，也尽可能的提供图表，表格类的分析汇总信息
                """, queryQuestion,content);

        if(model != null){
            String answer = model.chat(prompt);
            return answer.trim();
        }
        return null;
    }

    public static String insightToDynamicContent(DynamicContentQueryResult dynamicContentQueryResult){
        String content = getDynamicContentText(dynamicContentQueryResult);
        String DYNAMIC_CONTENT_INSIGHT_TECH = PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_TECHNOLOGY);
        ChatModel model = null;
        if("OpenAI".equals(DYNAMIC_CONTENT_INSIGHT_TECH)){
            model = OpenAiChatModel.builder()
                    .apiKey(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_APIKEY))
                    .modelName(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_NAME))
                    .baseUrl(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_BASEURL))
                    .build();
        }else if("Ollama".equals(DYNAMIC_CONTENT_INSIGHT_TECH)){
            model = OllamaChatModel.builder()
                    .modelName(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_NAME))
                    .baseUrl(PropertiesHandler.getPropertyValue(PropertiesHandler.DYNAMIC_CONTENT_INSIGHT_MODEL_BASEURL))
                    .build();
        }

        String prompt = String.format("""
                分析以下数据内容：
                %s
               
                要求：
                1. 不要分析数据结构和可能来源
                2. 不要包含标题
                3. 除了包含常规的文字分析结果外，也尽可能的提供图表，表格类的分析汇总信息
                """, content);

        if(model != null){
            String answer = model.chat(prompt);
            return answer.trim();
        }
        return null;
    }

    private static String getDynamicContentText(DynamicContentQueryResult dynamicContentQueryResult){
        List<Map<String, DynamicContentValue>> contentValueList = dynamicContentQueryResult.getDynamicContentResultValueList();
        Map<String, DynamicContentValue.ContentValueType> contentValueTypeMap = dynamicContentQueryResult.getDynamicContentAttributesValueTypeMap();
        List<String> fixedProperties = new ArrayList<>();
        fixedProperties.addAll(contentValueTypeMap.keySet());

        StringBuilder fullContentSb = new StringBuilder();

        StringBuilder headerInfo = new StringBuilder();
        fixedProperties.forEach( propertyName -> {
            headerInfo.append(propertyName).append(",");
        });
        headerInfo.deleteCharAt(headerInfo.length() - 1);
        fullContentSb.append(headerInfo).append("\n");

        Map<String,List<String>> conceptionEntitiesMapping = new HashMap<>();
        Map<String,List<String>> relationEntitiesMapping = new HashMap<>();

        for(Map<String, DynamicContentValue> currentDataMap:contentValueList){
            StringBuilder currentDataInfo = new StringBuilder();
            fixedProperties.forEach( propertyName -> {
                DynamicContentValue currentColumnContentValue = currentDataMap.get(propertyName);
                if(currentColumnContentValue != null){
                    DynamicContentValue.ContentValueType currentColumnContentValueType = currentColumnContentValue.getValueType();
                    if(DynamicContentValue.ContentValueType.CONCEPTION_ENTITY.equals(currentColumnContentValueType)){
                        ConceptionEntity conceptionEntity = (ConceptionEntity)currentColumnContentValue.getValueObject();
                        currentDataInfo.append(conceptionEntity.getConceptionEntityUID()).append(",");
                        if(!conceptionEntitiesMapping.containsKey(propertyName)){
                            List<String> conceptionEntityUIDList = new ArrayList<>();
                            conceptionEntitiesMapping.put(propertyName, conceptionEntityUIDList);
                        }
                        conceptionEntitiesMapping.get(propertyName).add(conceptionEntity.getConceptionEntityUID());
                    }else if(DynamicContentValue.ContentValueType.RELATION_ENTITY.equals(currentColumnContentValueType)){
                        RelationEntity relationEntity = (RelationEntity)currentColumnContentValue.getValueObject();
                        currentDataInfo.append(relationEntity.getRelationEntityUID()).append(",");
                        if(!relationEntitiesMapping.containsKey(propertyName)){
                            List<String> relationEntityUIDList = new ArrayList<>();
                            relationEntitiesMapping.put(propertyName, relationEntityUIDList);
                        }
                        relationEntitiesMapping.get(propertyName).add(relationEntity.getRelationEntityUID());
                    }else if(DynamicContentValue.ContentValueType.RELATION_ENTITY.equals(currentColumnContentValueType)){

                    }else{
                        String currentContentStr = MarkdownIllegalCharChecker.escapeMarkdown(currentColumnContentValue.getValueObject().toString());
                        currentDataInfo.append(currentContentStr).append(",");
                    }
                }else{
                    currentDataInfo.append(",");
                }
            });
            currentDataInfo.deleteCharAt(currentDataInfo.length() - 1);
            fullContentSb.append(currentDataInfo).append("\n");
        }

        CoreRealm coreRealm = RealmTermFactory.getDefaultCoreRealm();
        try{
            coreRealm.openGlobalSession();
            if(!conceptionEntitiesMapping.isEmpty() || !relationEntitiesMapping.isEmpty()){
                CrossKindDataOperator crossKindDataOperator = coreRealm.getCrossKindDataOperator();
                if(!conceptionEntitiesMapping.isEmpty()){
                    conceptionEntitiesMapping.keySet().forEach(propertyName -> {
                        StringBuilder currentPropertyDetailInfo = new StringBuilder();
                        currentPropertyDetailInfo.append("属性 ").append(propertyName).append(" 详细信息").append("\n");
                        List<String> conceptionEntityUIDList = conceptionEntitiesMapping.get(propertyName);
                        try {
                            List<ConceptionEntityValue> conceptionEntityValue = crossKindDataOperator.getSingleValueConceptionEntityAttributesByUIDs(conceptionEntityUIDList,null);
                                conceptionEntityValue.forEach(currentConceptionEntityValue -> {
                                    currentPropertyDetailInfo.append(currentConceptionEntityValue.getConceptionEntityUID()+ ":"+currentConceptionEntityValue.getEntityAttributesValue().toString()).append("\n");
                                });
                        } catch (CoreRealmServiceEntityExploreException e) {
                            e.printStackTrace();
                        }
                        currentPropertyDetailInfo.append("\n");
                        fullContentSb.append(currentPropertyDetailInfo);
                    });
                }

                if(!relationEntitiesMapping.isEmpty()){
                    relationEntitiesMapping.keySet().forEach(propertyName -> {
                        StringBuilder currentPropertyDetailInfo = new StringBuilder();
                        currentPropertyDetailInfo.append("属性 ").append(propertyName).append(" 详细信息").append("\n");
                        List<String> relationEntityUIDList = relationEntitiesMapping.get(propertyName);
                        try {
                            List<RelationEntityValue> relationEntityValue = crossKindDataOperator.getSingleValueRelationEntityAttributesByUIDs(relationEntityUIDList,null);
                            relationEntityValue.forEach(currentRelationEntityValue -> {
                                currentPropertyDetailInfo.append(currentRelationEntityValue.getRelationEntityUID()+ ":"+currentRelationEntityValue.getEntityAttributesValue().toString()).append("\n");
                            });
                        } catch (CoreRealmServiceEntityExploreException e) {
                            e.printStackTrace();
                        }
                        currentPropertyDetailInfo.append("\n");
                        fullContentSb.append(currentPropertyDetailInfo);
                    });
                }
            }
        }finally {
            coreRealm.closeGlobalSession();
        }
        return fullContentSb.toString();
    }
}
