package com.docg.ai.llm.rag.graphRAG;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // 这里请替换为你自己的 Neo4j 和 OpenAI API Key
        String neo4jUri = "bolt://localhost:7687";
        String neo4jUser = "neo4j";
        String neo4jPassword = "wyc4docg";
        String openAiApiKey = "sk-ea43df124d9c4d45a684af8c8fee1fed";

        try (
            Neo4jService neo4jService = new Neo4jService(neo4jUri, neo4jUser, neo4jPassword)
        ) {
            LlmService llmService = new LlmService(openAiApiKey);
            GraphRagService graphRagService = new GraphRagService(neo4jService, llmService);
            Scanner scanner = new Scanner(System.in);
            String userId = "user1"; // 可根据实际业务用用户ID
            while (true) {
                System.out.println("请输入你的问题（输入 exit 退出，/mode all|user|ai 切换上下文，/clear 清空记忆）：");
                String question = scanner.nextLine();
                if ("exit".equalsIgnoreCase(question.trim())) break;
                if (question.startsWith("/mode ")) {
                    String mode = question.substring(6).trim();
                    if ("all".equalsIgnoreCase(mode)) {
                        graphRagService.setContextMode(LlmService.ContextMode.ALL);
                        System.out.println("已切换为：全部问答上下文");
                    } else if ("user".equalsIgnoreCase(mode)) {
                        graphRagService.setContextMode(LlmService.ContextMode.USER_ONLY);
                        System.out.println("已切换为：仅用户问题上下文");
                    } else if ("ai".equalsIgnoreCase(mode)) {
                        graphRagService.setContextMode(LlmService.ContextMode.AI_ONLY);
                        System.out.println("已切换为：仅AI回答上下文");
                    } else {
                        System.out.println("未知模式，可选：all、user、ai");
                    }
                    continue;
                }
                if ("/clear".equalsIgnoreCase(question.trim())) {
                    graphRagService.clearUserMemory(userId);
                    System.out.println("记忆已清空");
                    continue;
                }
                String answer = graphRagService.answerQuestion(userId, question);
                System.out.println("AI 回答：" + answer);
            }
        }
    }
}
