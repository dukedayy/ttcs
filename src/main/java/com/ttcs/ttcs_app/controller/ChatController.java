package com.ttcs.ttcs_app.controller;

import com.ttcs.ttcs_app.service.ai.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AiChatService aiChatService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String message = request.get("message");
        
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("response", "Vui lòng nhập tin nhắn."));
        }

        String response = aiChatService.getChatResponse(userId, message);
        return ResponseEntity.ok(Map.of("response", response));
    }
}
