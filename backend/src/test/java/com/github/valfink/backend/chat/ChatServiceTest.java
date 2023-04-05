package com.github.valfink.backend.chat;

import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.fooditem.FoodItemService;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
import com.github.valfink.backend.mongouser.MongoUserService;
import com.github.valfink.backend.util.IdService;
import com.github.valfink.backend.util.TimestampService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    TimestampService timestampService;
    MongoUserDTOResponse mongoUserDTOResponse1, mongoUserDTOResponse2, mongoUserDTOResponse3;
    FoodItemDTOResponse foodItemDTOResponse1;
    Principal principal;
    Chat chat1;
    ChatMessage chatMessage1;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        chatMessageRepository = mock(ChatMessageRepository.class);
        mongoUserService = mock(MongoUserService.class);
        foodItemService = mock(FoodItemService.class);
        idService = mock(IdService.class);
        timestampService = mock(TimestampService.class);
        chatService = new ChatService(chatRepository, chatMessageRepository, mongoUserService, foodItemService, idService, timestampService);
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
        chat1 = new Chat("c1", foodItemDTOResponse1.id(), mongoUserDTOResponse2.id(), mongoUserDTOResponse1.id());
        chatMessage1 = new ChatMessage("cm1", chat1.id(), mongoUserDTOResponse2.id(), mongoUserDTOResponse1.id(), Instant.parse("2023-03-14T11:14:00Z"), "Hello!", true);
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

    @Test
    void addMessageAndSendIntoChat_whenUserIsParticipant_thenReturnSavedMessage() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse2.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse2.username())).thenReturn(mongoUserDTOResponse2);
        when(chatRepository.findById(chat1.id())).thenReturn(Optional.of(chat1));
        when(foodItemService.getFoodItemById(foodItemDTOResponse1.id())).thenReturn(foodItemDTOResponse1);
        when(idService.generateId()).thenReturn(chatMessage1.id());
        when(timestampService.generateTimestamp()).thenReturn(chatMessage1.timestamp());
        when(chatMessageRepository.save(chatMessage1)).thenReturn(chatMessage1);
        when(mongoUserService.getMongoUserDTOResponseById(mongoUserDTOResponse1.id())).thenReturn(mongoUserDTOResponse1);

        // WHEN
        ChatMessageDTOResponseWS expected = new ChatMessageDTOResponseWS(chatMessage1, mongoUserDTOResponse2, mongoUserDTOResponse1);
        ChatMessageDTOResponseWS actual = chatService.addMessageAndReturnDTO(chatMessage1.content(), chatMessage1.chatId(), principal);

        // THEN
        assertEquals(expected, actual);
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void getMyChats_whenUserHasOneChat_thenReturnListOfIt() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse2.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse2.username())).thenReturn(mongoUserDTOResponse2);
        when(chatRepository.getChatsByCandidateIdOrDonatorId(mongoUserDTOResponse2.id(), mongoUserDTOResponse2.id())).thenReturn(List.of(chat1));
        when(foodItemService.getFoodItemById(foodItemDTOResponse1.id())).thenReturn(foodItemDTOResponse1);
        when(mongoUserService.getMongoUserDTOResponseById(mongoUserDTOResponse2.id())).thenReturn(mongoUserDTOResponse2);

        // WHEN
        List<ChatDTOResponse> expected = List.of(new ChatDTOResponse(chat1.id(), foodItemDTOResponse1, mongoUserDTOResponse2, List.of()));
        List<ChatDTOResponse> actual = chatService.getMyChats(principal);

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void getMyChats_whenUserHasNoChat_thenReturnEmptyList() {
        // GIVEN
        when(principal.getName()).thenReturn(mongoUserDTOResponse2.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse2.username())).thenReturn(mongoUserDTOResponse2);
        when(chatRepository.getChatsByCandidateIdOrDonatorId(mongoUserDTOResponse2.id(), mongoUserDTOResponse2.id())).thenReturn(List.of());

        // WHEN
        List<ChatDTOResponse> expected = List.of();
        List<ChatDTOResponse> actual = chatService.getMyChats(principal);

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void markMessageAsRead_whenUserIsRecipient_thenReturnReadMessage() {
        // GIVEN
        ChatMessage readMessage = new ChatMessage(chatMessage1.id(), chatMessage1.chatId(), chatMessage1.senderId(), chatMessage1.recipientId(), chatMessage1.timestamp(), chatMessage1.content(), false);
        when(chatMessageRepository.findById(chatMessage1.id())).thenReturn(Optional.of(chatMessage1));
        when(principal.getName()).thenReturn(mongoUserDTOResponse1.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse1.username())).thenReturn(mongoUserDTOResponse1);
        when(chatMessageRepository.save(readMessage)).thenReturn(readMessage);

        // WHEN
        ChatMessage expected = readMessage;
        ChatMessage actual = chatService.markMessageAsRead(chatMessage1.id(), principal);

        // THEN
        assertEquals(expected, actual);
    }

    @Test
    void markMessageAsRead_whenUserIsNotRecipient_thenThrowException() {
        // GIVEN
        when(chatMessageRepository.findById(chatMessage1.id())).thenReturn(Optional.of(chatMessage1));
        when(principal.getName()).thenReturn(mongoUserDTOResponse2.username());
        when(mongoUserService.getMongoUserDTOResponseByUsername(mongoUserDTOResponse2.username())).thenReturn(mongoUserDTOResponse2);
        String messageId = chatMessage1.id();

        // WHEN & THEN
        assertThrows(ChatExceptionAuthorization.class, () -> chatService.markMessageAsRead(messageId, principal));
    }

    @Test
    void markMessageAsRead_whenMessageDoesNotExist_thenThrowException() {
        // GIVEN
        when(chatMessageRepository.findById(chatMessage1.id())).thenReturn(Optional.empty());
        String messageId = chatMessage1.id();

        // WHEN & THEN
        assertThrows(ChatExceptionNotFound.class, () -> chatService.markMessageAsRead(messageId, principal));
    }
}
