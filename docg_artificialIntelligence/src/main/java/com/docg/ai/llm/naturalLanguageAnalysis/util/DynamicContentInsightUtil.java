package com.docg.ai.llm.naturalLanguageAnalysis.util;

import com.docg.ai.util.config.PropertiesHandler;
import com.docg.ai.util.markdown.MarkdownIllegalCharChecker;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DynamicContentQueryResult;
import com.viewfunction.docg.coreRealm.realmServiceCore.payload.DynamicContentValue;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.ConceptionEntity;
import com.viewfunction.docg.coreRealm.realmServiceCore.term.RelationEntity;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamicContentInsightUtil {

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

        for(Map<String, DynamicContentValue> currentDataMap:contentValueList){
            StringBuilder currentDataInfo = new StringBuilder();
            fixedProperties.forEach( propertyName -> {
                DynamicContentValue currentColumnContentValue = currentDataMap.get(propertyName);
                if(currentColumnContentValue != null){
                    DynamicContentValue.ContentValueType currentColumnContentValueType = currentColumnContentValue.getValueType();
                    if(DynamicContentValue.ContentValueType.CONCEPTION_ENTITY.equals(currentColumnContentValueType)){
                        ConceptionEntity conceptionEntity = (ConceptionEntity)currentColumnContentValue.getValueObject();
                        currentDataInfo.append(conceptionEntity.getConceptionEntityUID()).append(",");
                    }else if(DynamicContentValue.ContentValueType.RELATION_ENTITY.equals(currentColumnContentValueType)){
                        RelationEntity relationEntity = (RelationEntity)currentColumnContentValue.getValueObject();
                        currentDataInfo.append(relationEntity.getRelationEntityUID()).append(",");
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
        return fullContentSb.toString();
    }
}
