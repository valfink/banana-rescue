package com.github.valfink.backend.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valfink.backend.fooditem.FoodItem;
import com.github.valfink.backend.fooditem.FoodItemRepository;
import com.github.valfink.backend.geolocation.Coordinate;
import com.github.valfink.backend.geolocation.Location;
import com.github.valfink.backend.mongouser.MongoUser;
import com.github.valfink.backend.mongouser.MongoUserRepository;
import com.github.valfink.backend.util.IdService;
import com.github.valfink.backend.util.TimestampService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketChatControllerTest {
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MongoUserRepository mongoUserRepository;
    @Autowired
    FoodItemRepository foodItemRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    MongoUser mongoUser1_participant, mongoUser2_participant, mongoUser3_hacker;
    FoodItem foodItem1;
    Chat chat1;
    ChatMessage chatMessage1, chatMessage2;
    @LocalServerPort
    private int port;
    WebSocketStompClient stompClient;
    StompSession stompSession1, stompSession2, stompSession3;
    String rawPasswordForAllTestUsers;
    StompFrameHandler stompFrameHandler1, stompFrameHandler2, stompFrameHandler3;
    CompletableFuture<ChatMessage> completableFuture1, completableFuture2, completableFuture3;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    IdService idService;
    @MockBean
    TimestampService timestampService;

    @BeforeEach
    void setUp() {
        rawPasswordForAllTestUsers = "password";
        mongoUser1_participant = new MongoUser("u1", "user", passwordEncoder.encode(rawPasswordForAllTestUsers), "BASIC");
        mongoUser2_participant = new MongoUser("u2", "user2", passwordEncoder.encode(rawPasswordForAllTestUsers), "BASIC");
        mongoUser3_hacker = new MongoUser("u3", "user3", passwordEncoder.encode(rawPasswordForAllTestUsers), "BASIC");
        foodItem1 = new FoodItem(
                "f1",
                mongoUser1_participant.id(),
                "Food Item 1",
                "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                new Location("Berlin", new Coordinate(new BigDecimal("52.5170365"), new BigDecimal("13.3888599"))),
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
        );
        chat1 = new Chat("c1", foodItem1.id(), mongoUser2_participant.id(), mongoUser1_participant.id());
        chatMessage1 = new ChatMessage(
                "cm1",
                chat1.id(),
                mongoUser2_participant.id(),
                mongoUser1_participant.id(),
                Instant.parse("2023-03-15T11:00:00Z"),
                "Hey there!",
                true);
        chatMessage2 = new ChatMessage(
                "cm2",
                chat1.id(),
                mongoUser1_participant.id(),
                mongoUser2_participant.id(),
                Instant.parse("2023-03-15T12:00:00Z"),
                "Hello again!",
                true);
        mongoUserRepository.save(mongoUser1_participant);
        mongoUserRepository.save(mongoUser2_participant);
        mongoUserRepository.save(mongoUser3_hacker);
        foodItemRepository.save(foodItem1);
        chatRepository.save(chat1);
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        mappingJackson2MessageConverter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(mappingJackson2MessageConverter);
        completableFuture1 = new CompletableFuture<>();
        stompFrameHandler1 = new ChatMessageStompFrameHandler(completableFuture1);
        completableFuture2 = new CompletableFuture<>();
        stompFrameHandler2 = new ChatMessageStompFrameHandler(completableFuture2);
        completableFuture3 = new CompletableFuture<>();
        stompFrameHandler3 = new ChatMessageStompFrameHandler(completableFuture3);
    }

    @Test
    @DirtiesContext
    void addMessageAndSendIntoChat_whenUserIsParticipant_thenSendReceivedMessage() throws Exception {
        // GIVEN
        when(timestampService.generateTimestamp()).thenReturn(chatMessage1.timestamp());
        when(idService.generateId()).thenReturn(chatMessage1.id());

        // WHEN
        stompSession1 = connectToChatAsUser(mongoUser2_participant);
        stompSession1.subscribe("/user/queue", stompFrameHandler1);
        stompSession1.send("/api/ws/chat/" + chat1.id(), chatMessage1.content());
        ChatMessage actual = completableFuture1.get(1, TimeUnit.SECONDS);

        // THEN
        assertTrue(stompSession1.isConnected());
        assertEquals(chatMessage1, actual);
    }

    @Test
    @DirtiesContext
    void connectToChat_whenUserIsNotLoggedIn_thenThrowException() {
        // WHEN & THEN
        assertThrows(ExecutionException.class, () -> stompClient
                .connectAsync("ws://localhost:" + port + "/api/ws/chat", new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS));
    }

    @Test
    @DirtiesContext
    void addMessageAndSendIntoChat_whenSubscribedToWrongChat_thenDontReturnMessage() throws Exception {
        // WHEN
        stompSession1 = connectToChatAsUser(mongoUser2_participant);
        stompSession1.subscribe("/user/queueueueue", stompFrameHandler1);
        stompSession1.send("/api/ws/chat/" + chat1.id(), chatMessage1.content());

        // THEN
        assertTrue(stompSession1.isConnected());
        assertThrows(TimeoutException.class, () -> completableFuture1.get(1, TimeUnit.SECONDS));
    }

    @Test
    @DirtiesContext
    void addMessageAndSendIntoChat_whenNotParticipant_thenDontSaveAndReturnSentMessageAndDontShowMessageFromLegitUser() throws Exception {
        // GIVEN
        when(timestampService.generateTimestamp()).thenReturn(chatMessage2.timestamp());
        when(idService.generateId()).thenReturn(chatMessage2.id());

        // WHEN & THEN
        stompSession1 = connectToChatAsUser(mongoUser1_participant);
        stompSession1.subscribe("/user/queue", stompFrameHandler1);
        stompSession2 = connectToChatAsUser(mongoUser2_participant);
        stompSession2.subscribe("/user/queue", stompFrameHandler2);
        stompSession3 = connectToChatAsUser(mongoUser3_hacker);
        stompSession3.subscribe("/user/queue", stompFrameHandler3);

        stompSession3.send("/api/ws/chat/" + chat1.id(), "I AM A HACKER!");
        assertThrows(TimeoutException.class, () -> completableFuture1.get(1, TimeUnit.SECONDS));
        assertThrows(TimeoutException.class, () -> completableFuture2.get(1, TimeUnit.SECONDS));
        assertThrows(TimeoutException.class, () -> completableFuture3.get(1, TimeUnit.SECONDS));

        stompSession1.send("/api/ws/chat/" + chat1.id(), chatMessage2.content());
        ChatMessage actual1 = completableFuture1.get(1, TimeUnit.SECONDS);
        ChatMessage actual2 = completableFuture2.get(1, TimeUnit.SECONDS);
        assertThrows(TimeoutException.class, () -> completableFuture3.get(1, TimeUnit.SECONDS));
        assertEquals(chatMessage2, actual1);
        assertEquals(chatMessage2, actual2);
    }


    private StompSession connectToChatAsUser(MongoUser user) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        WebSocketHttpHeaders wsHeaders = new WebSocketHttpHeaders(httpHeaders);
        String authorization = user.username() + ":" + rawPasswordForAllTestUsers;
        httpHeaders.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(authorization.getBytes())));
        return stompClient
                .connectAsync("ws://localhost:" + port + "/api/ws", wsHeaders, new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);
    }

    private record ChatMessageStompFrameHandler(
            CompletableFuture<ChatMessage> completableFuture) implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return ChatMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            if (payload instanceof ChatMessage) {
                completableFuture.complete((ChatMessage) payload);
            }
        }
    }
}
