package com.viewfunction.ai.llm.rag.graphRAG;

import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LlmService {
    private final OpenAiChatModel model;
    // 每个用户一个历史消息列表
    private final Map<String, List<String>> historyMap = new ConcurrentHashMap<>();
    private final int maxHistory = 10;

    public enum ContextMode {
        ALL, // 全部问答
        USER_ONLY, // 仅用户问题
        AI_ONLY // 仅AI回答
    }

    private ContextMode contextMode = ContextMode.ALL;

    public void setContextMode(ContextMode mode) {
        this.contextMode = mode;
    }

    public LlmService(String apiKey) {
        model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                //.modelName("gpt-3.5-turbo") // 可根据需要更换模型
                .modelName("deepseek-chat")
                .baseUrl("https://api.deepseek.com/v1")
                .build();
    }

    public String chatWithMemory(String userId, String prompt) {
        List<String> history = historyMap.computeIfAbsent(userId, k -> new LinkedList<>());
        // 拼接历史
        StringBuilder fullPrompt = new StringBuilder();
        if (contextMode == ContextMode.ALL) {
            for (String h : history) fullPrompt.append(h).append("\n");
        } else if (contextMode == ContextMode.USER_ONLY) {
            for (String h : history) if (h.startsWith("用户:")) fullPrompt.append(h).append("\n");
        } else if (contextMode == ContextMode.AI_ONLY) {
            for (String h : history) if (h.startsWith("AI:")) fullPrompt.append(h).append("\n");
        }
        fullPrompt.append(prompt);
        String answer = model.chat(fullPrompt.toString());
        // 记录本轮问答
        history.add("用户: " + prompt);
        history.add("AI: " + answer);
        // 保持历史长度
        while (history.size() > maxHistory * 2) { // 一问一答为2条
            history.remove(0);
        }
        return answer;
    }

    // 清空指定用户的记忆
    public void clearHistory(String userId) {
        historyMap.remove(userId);
    }
} 