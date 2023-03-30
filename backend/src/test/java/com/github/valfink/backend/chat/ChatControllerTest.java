package com.github.valfink.backend.chat;

import com.github.valfink.backend.fooditem.FoodItem;
import com.github.valfink.backend.fooditem.FoodItemRepository;
import com.github.valfink.backend.mongouser.MongoUser;
import com.github.valfink.backend.mongouser.MongoUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    MongoUserRepository mongoUserRepository;
    @Autowired
    FoodItemRepository foodItemRepository;
    MongoUser mongoUser1, mongoUser2;
    FoodItem foodItem1;
    Chat chat1;
    ChatMessage chatMessage1;

    @BeforeEach
    void setUp() {
        mongoUser1 = new MongoUser("u1", "user", "pass", "BASIC");
        mongoUser2 = new MongoUser("u2", "user2", "pass", "BASIC");
        foodItem1 = new FoodItem(
                "f1",
                mongoUser1.id(),
                "Food Item 1",
                "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                "Berlin",
                Instant.parse("2023-03-16T11:14:00Z"),
                Instant.parse("2023-03-18T11:00:00Z"),
                "This is my first food item."
        );
        mongoUserRepository.save(mongoUser1);
        mongoUserRepository.save(mongoUser2);
        foodItemRepository.save(foodItem1);
        chat1 = new Chat("c1", foodItem1.id(), mongoUser2.id(), mongoUser1.id());
        chatMessage1 = new ChatMessage("cm1", chat1.id(), "u2", Instant.parse("2023-03-15T11:00:00Z"), "Hey there!");
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void startNewOrReturnExistingChat_whenUserIsDonator_thenReturn403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/chats?foodItemId=" + foodItem1.id())
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    @WithMockUser("user2")
    void startNewOrReturnExistingChat_whenUserIsNotDonator_thenReturnNewChat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/chats?foodItemId=" + foodItem1.id())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DirtiesContext
    @WithMockUser("user2")
    void startNewOrReturnExistingChat_whenUserIsNotDonatorAndChatExists_thenReturnExistingChat() throws Exception {
        chatRepository.save(chat1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/chats?foodItemId=" + foodItem1.id())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(chat1.id()));
    }

    @Test
    @DirtiesContext
    @WithMockUser("user2")
    void getChatById_whenUserIsParticipant_thenReturnChat() throws Exception {
        chatRepository.save(chat1);
        chatMessageRepository.save(chatMessage1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/chats/" + chat1.id()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "id": "c1",
                        "foodItem": {
                            "id": "f1",
                            "donator": {
                                "id": "u1",
                                "username": "user"
                                },
                            "title": "Food Item 1",
                            "photoUri": "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                            "location": "Berlin",
                            "pickupUntil": "2023-03-16T11:14:00Z",
                            "consumeUntil": "2023-03-18T11:00:00Z",
                            "description": "This is my first food item."
                            },
                        "candidate": {
                            "id": "u2",
                            "username": "user2"
                            },
                        "messages": [
                            {
                            "id": "cm1",
                            "chatId": "c1",
                            "senderId": "u2",
                            "timestamp": "2023-03-15T11:00:00Z",
                            "content": "Hey there!"
                            }
                        ]
                        }
                        """));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getChatById_whenChatIsNotInRepo_thenReturn404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/chats/12345"))
                .andExpect(status().isNotFound());
    }
}
