package com.hcl.promptsearch.Controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultSystem("You are a helpful, concise AI assistant.").build();
    }

    // 1. Simple GET endpoint (query parameter)
    // Usage: GET /api/chat?prompt=Tell me a joke
    @GetMapping
    public Map<String, String> generateByGet(@RequestParam(defaultValue = "Hello!") String prompt) {
        String response = chatClient.prompt().user(prompt).call().content();

        return Map.of("prompt", prompt, "response", response);
    }

    // 2. POST endpoint (JSON body)
    // Usage: POST /api/chat/ask
    @PostMapping("/ask")
    public Map<String, String> generateByPost(@RequestBody PromptRequest request) {
        String response = chatClient.prompt().user(request.prompt()).call().content();

        return Map.of("response", response);
    }

    public record PromptRequest(String prompt) {
    }


    @PostMapping(value = "/ask-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamGemini(@RequestBody PromptRequest request) {
        return chatClient.prompt()
                .user(request.prompt())
                .stream()   // switches from blocking .call() to reactive stream
                .content();
        }
}

