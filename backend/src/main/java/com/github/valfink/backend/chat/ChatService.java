package com.github.valfink.backend.chat;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.fooditem.FoodItemService;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import com.github.valfink.backend.util.IdService;
import com.github.valfink.backend.util.TimestampService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MongoUserService mongoUserService;
    private final FoodItemService foodItemService;
    private final IdService idService;
    private final TimestampService timestampService;

    private Chat checkIfUserIsInChatAndReturnChat(String chatId, MongoUserDTOResponse user) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatExceptionNotFound("The chat with the id " + chatId + " does not exist."));
        FoodItemDTOResponse foodItem = foodItemService.getFoodItemById(chat.foodItemId());

        if (!foodItem.donator().id().equals(user.id()) && !chat.candidateId().equals(user.id())) {
            throw new ChatExceptionAuthorization("You are not participant in this chat!");
        }

        return chat;
    }

    private ChatDTOResponse chatDTOResponseFromChat(Chat chat) {
        return new ChatDTOResponse(
                chat.id(),
                foodItemService.getFoodItemById(chat.foodItemId()),
                mongoUserService.getMongoUserDTOResponseById(chat.candidateId()),
                chatMessageRepository.getChatMessagesByChatIdOrderByTimestampAsc(chat.id())
        );
    }

    public String startNewChatOrReturnExistingChatId(String foodItemId, Principal principal) {
        MongoUserDTOResponse candidate = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName());
        FoodItemDTOResponse foodItem = foodItemService.getFoodItemById(foodItemId);

        if (foodItem.donator().id().equals(candidate.id())) {
            throw new ChatExceptionAuthorization("You may not start a chat about your own item!");
        }

        Chat chat = chatRepository
                .getChatByFoodItemIdAndCandidateId(foodItemId, candidate.id())
                .orElseGet(() -> chatRepository.save(
                        new Chat(idService.generateId(),
                                foodItemId,
                                candidate.id(),
                                foodItem.donator().id()))
                );
        return chat.id();
    }

    public List<ChatDTOResponse> getMyChats(Principal principal) {
        MongoUserDTOResponse user = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName());
        return chatRepository.getChatsByCandidateIdOrDonatorId(user.id(), user.id())
                .stream()
                .map(this::chatDTOResponseFromChat)
                .sorted(Comparator.comparing(ChatDTOResponse::getLastUpdate).reversed())
                .toList();
    }

    public ChatDTOResponse getChatById(String chatId, Principal principal) {
        MongoUserDTOResponse user = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName());
        Chat chat = checkIfUserIsInChatAndReturnChat(chatId, user);

        return chatDTOResponseFromChat(chat);
    }

    public ChatMessageDTOResponseWS addMessageAndReturnDTO(String message, String chatId, Principal principal) {
        MongoUserDTOResponse sender = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName());
        Chat chat = checkIfUserIsInChatAndReturnChat(chatId, sender);
        String recipientId = Objects.equals(chat.donatorId(), sender.id()) ? chat.candidateId() : chat.donatorId();
        MongoUserDTOResponse recipient = mongoUserService.getMongoUserDTOResponseById(recipientId);

        ChatMessage chatMessage = chatMessageRepository.save(new ChatMessage(
                idService.generateId(),
                chatId,
                sender.id(),
                timestampService.generateTimestamp(),
                message,
                true));

        return new ChatMessageDTOResponseWS(
                chatMessage,
                sender,
                recipient
        );
    }
}
