package com.github.valfink.backend.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{chatId}")
    public void addMessageAndSendToUsers(@DestinationVariable String chatId, String message, Principal principal) {
        ChatMessageDTOResponseWS chatMessage = chatService.addMessageAndReturnDTO(message, chatId, principal);
        messagingTemplate.convertAndSendToUser(chatMessage.recipient().username(), "/queue", chatMessage.actualMessage());
        messagingTemplate.convertAndSendToUser(chatMessage.sender().username(), "/queue", chatMessage.actualMessage());
    }
}
