package com.github.valfink.backend.chat;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.fooditem.FoodItemService;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import com.github.valfink.backend.util.IdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ChatServiceTest {
    ChatService chatService;
    ChatRepository chatRepository;
    MongoUserService mongoUserService;
    FoodItemService foodItemService;
    IdService idService;
    MongoUserDTOResponse mongoUserDTOResponse1, mongoUserDTOResponse2;
    FoodItemDTOResponse foodItemDTOResponse1;
    Principal principal;
    Chat chat1;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        mongoUserService = mock(MongoUserService.class);
        foodItemService = mock(FoodItemService.class);
        idService = mock(IdService.class);
        chatService = new ChatService(chatRepository, mongoUserService, foodItemService, idService);
        principal = mock(Principal.class);

        mongoUserDTOResponse1 = new MongoUserDTOResponse("u1", "user");
        mongoUserDTOResponse2 = new MongoUserDTOResponse("u2", "user2");
        foodItemDTOResponse1 = new FoodItemDTOResponse(
                "f1",
                mongoUserDTOResponse1,
                "Food Item 1",
                "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                "Berlin",
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
        );
        chat1 = new Chat("c1", foodItemDTOResponse1.id(), mongoUserDTOResponse2.id());
    }

    @Test
    void startNewOrReturnExistingChat_whenUserIsDonator_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(foodItemService.getFoodItemById(foodItemDTOResponse1.id())).thenReturn(foodItemDTOResponse1);
        String foodItemId = foodItemDTOResponse1.id();

        // WHEN & THEN
        assertThrows(ChatExceptionAuthorization.class, () -> chatService.startNewChatOrReturnExistingChatId(foodItemId, principal));
    }

    @Test
    void startNewOrReturnExistingChat_whenChatExists_thenReturnChat() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse2.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse2.username())).thenReturn(mongoUserDTOResponse2);
        when(foodItemService.getFoodItemById(foodItemDTOResponse1.id())).thenReturn(foodItemDTOResponse1);
        when(chatRepository.getChatByFoodItemIdAndCandidateId(foodItemDTOResponse1.id(), mongoUserDTOResponse2.id())).thenReturn(Optional.of(chat1));

        // WHEN
        String expected = chat1.id();
        String actual = chatService.startNewChatOrReturnExistingChatId(foodItemDTOResponse1.id(), principal);

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void startNewOrReturnExistingChat_whenNoChatExists_thenReturnNewChat() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse2.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse2.username())).thenReturn(mongoUserDTOResponse2);
        when(foodItemService.getFoodItemById(foodItemDTOResponse1.id())).thenReturn(foodItemDTOResponse1);
        when(chatRepository.getChatByFoodItemIdAndCandidateId(foodItemDTOResponse1.id(), mongoUserDTOResponse2.id())).thenReturn(Optional.empty());
        when(idService.generateId()).thenReturn(chat1.id());
        when(chatRepository.save(chat1)).thenReturn(chat1);

        // WHEN
        String expected = chat1.id();
        String actual = chatService.startNewChatOrReturnExistingChatId(foodItemDTOResponse1.id(), principal);

        // THEN
        assertEquals(expected, actual);
        verify(chatRepository).save(chat1);
    }
}
