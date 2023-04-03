package com.github.valfink.backend.chat;

import com.github.valfink.backend.mongouser.MongoUserDTOResponse;

public record ChatMessageDTOResponseWS(
        ChatMessage actualMessage,
        MongoUserDTOResponse sender,
        MongoUserDTOResponse recipient
) {
}
