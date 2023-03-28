package com.github.valfink.backend.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public String startNewOrReturnExistingChat(@RequestParam String foodItemId, Principal principal) {
        return chatService.startNewOrReturnExistingChat(foodItemId, principal);
    }
}
