package com.example.aicodereview.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    /**
     * Spring AI auto-configures OpenAiChatModel from application.yml properties.
     * We expose a ChatClient bean with a system prompt so every call
     * automatically carries the "senior engineer" persona.
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem("""
                        You are a Senior Software Engineer and Expert Code Reviewer
                        with 10+ years of experience. Be technical, specific, and practical.
                        Never give generic feedback. Always explain the 'why' behind each issue.
                        """)
                .build();
    }
}