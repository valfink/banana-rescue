package com.github.valfink.backend.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public String startNewChatOrReturnExistingChatId(@RequestParam String foodItemId, Principal principal) {
        return chatService.startNewChatOrReturnExistingChatId(foodItemId, principal);
    }

    @GetMapping
    public List<ChatDTOResponse> getMyChats(Principal principal) {
        return chatService.getMyChats(principal);
    }

    @GetMapping("/{id}")
    public ChatDTOResponse getChatById(@PathVariable String id, Principal principal) {
        return chatService.getChatById(id, principal);
    }

    @PutMapping("/read/{messageId}")
    public ChatMessage markMessageAsRead(@PathVariable String messageId, Principal principal) {
        return chatService.markMessageAsRead(messageId, principal);
    }
}
