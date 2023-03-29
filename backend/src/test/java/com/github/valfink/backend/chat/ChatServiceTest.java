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
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ChatServiceTest {
    ChatService chatService;
    ChatRepository chatRepository;
    ChatMessageRepository chatMessageRepository;
    MongoUserService mongoUserService;
    FoodItemService foodItemService;
    IdService idService;
    MongoUserDTOResponse mongoUserDTOResponse1, mongoUserDTOResponse2, mongoUserDTOResponse3;
    FoodItemDTOResponse foodItemDTOResponse1;
    Principal principal;
    Chat chat1;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        chatMessageRepository = mock(ChatMessageRepository.class);
        mongoUserService = mock(MongoUserService.class);
        foodItemService = mock(FoodItemService.class);
        idService = mock(IdService.class);
        chatService = new ChatService(chatRepository, chatMessageRepository, mongoUserService, foodItemService, idService);
        principal = mock(Principal.class);

        mongoUserDTOResponse1 = new MongoUserDTOResponse("u1", "user");
        mongoUserDTOResponse2 = new MongoUserDTOResponse("u2", "user2");
        mongoUserDTOResponse3 = new MongoUserDTOResponse("u3", "user3");
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

    @Test
    void getChatById_whenChatDoesNotExist_thenThrowException() {
        // GIVEN
        when(chatRepository.findById("1")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ChatExceptionNotFound.class, () -> chatService.getChatById("1", principal));
    }

    @Test
    void getChatById_whenUserIsNotParticipant_thenThrowException() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse3.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse3.username())).thenReturn(mongoUserDTOResponse3);
        when(chatRepository.findById(chat1.id())).thenReturn(Optional.of(chat1));
        when(foodItemService.getFoodItemById(foodItemDTOResponse1.id())).thenReturn(foodItemDTOResponse1);
        String chatId = chat1.id();

        // WHEN & THEN
        assertThrows(ChatExceptionAuthorization.class, () -> chatService.getChatById(chatId, principal));
    }

    @Test
    void getChatById_whenUserIsParticipant_thenReturnChat() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse2.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse2.username())).thenReturn(mongoUserDTOResponse2);
        when(chatRepository.findById(chat1.id())).thenReturn(Optional.of(chat1));
        when(foodItemService.getFoodItemById(foodItemDTOResponse1.id())).thenReturn(foodItemDTOResponse1);
        when(mongoUserService.getMongoUserDTOResponseById(mongoUserDTOResponse2.id())).thenReturn(mongoUserDTOResponse2);

        // WHEN
        ChatDTOResponse expected = new ChatDTOResponse(chat1.id(), foodItemDTOResponse1, mongoUserDTOResponse2, new ArrayList<>());
        ChatDTOResponse actual = chatService.getChatById(chat1.id(), principal);

        // THEN
        assertEquals(expected, actual);
    }
}
