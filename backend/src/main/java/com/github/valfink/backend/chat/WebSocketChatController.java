package com.github.valfink.backend.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/{chatId}")
    public ChatMessage addMessageAndSendIntoChat(@DestinationVariable String chatId, String message, Principal principal) {
        return chatService.addMessageAndSendIntoChat(message, chatId, principal);
    }
}
