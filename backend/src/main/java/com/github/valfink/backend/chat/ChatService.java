package com.github.valfink.backend.chat;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.fooditem.FoodItemService;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import com.github.valfink.backend.util.IdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MongoUserService mongoUserService;
    private final FoodItemService foodItemService;
    private final IdService idService;

    private ChatDTOResponse chatDTOResponseFromChatFoodItemAndCandidate(Chat chat, FoodItemDTOResponse foodItem, MongoUserDTOResponse candidate) {
        return new ChatDTOResponse(
                chat.id(),
                foodItem.donator(),
                candidate,
                foodItem,
                chatMessageRepository.getChatMessagesByChatId(chat.id())
        );
    }

    public ChatDTOResponse startNewOrReturnExistingChat(String foodItemId, Principal principal) {
        MongoUserDTOResponse candidate = mongoUserService.getMongoUserDTOResponseByUsername(principal.getName());
        FoodItemDTOResponse foodItem = foodItemService.getFoodItemById(foodItemId);

        if (foodItem.donator().id().equals(candidate.id())) {
            throw new ChatExceptionAuthorization("You may not start a chat about your own item!");
        }

        Chat chat = chatRepository.getChatByFoodItemIdAndCandidateId(foodItemId, candidate.id())
                .orElse(chatRepository.save(new Chat(
                        idService.generateId(),
                        foodItem.donator().id(),
                        candidate.id(),
                        foodItemId))
                );

        return chatDTOResponseFromChatFoodItemAndCandidate(chat, foodItem, candidate);
    }
}
