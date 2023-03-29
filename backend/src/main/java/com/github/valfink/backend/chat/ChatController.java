package com.github.valfink.backend.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public String startNewChatOrReturnExistingChatId(@RequestParam String foodItemId, Principal principal) {
        return chatService.startNewChatOrReturnExistingChatId(foodItemId, principal);
    }

    @GetMapping("/{id}")
    public ChatDTOResponse getChatById(@PathVariable String id, Principal principal) {
        return chatService.getChatById(id, principal);
    }
}
