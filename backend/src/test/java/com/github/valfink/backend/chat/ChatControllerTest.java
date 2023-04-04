package com.github.valfink.backend.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.valfink.backend.fooditem.FoodItem;
import com.github.valfink.backend.fooditem.FoodItemDTOResponse;
import com.github.valfink.backend.fooditem.FoodItemRepository;
import com.github.valfink.backend.mongouser.MongoUser;
import com.github.valfink.backend.mongouser.MongoUserDTOResponse;
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
import java.util.List;

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
    MongoUserDTOResponse mongoUserDTOResponse1, mongoUserDTOResponse2;
    FoodItem foodItem1, foodItem2;
    FoodItemDTOResponse foodItemDTOResponse1, foodItemDTOResponse2;
    Chat chat1, chat2;
    ChatMessage chatMessage1;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mongoUser1 = new MongoUser("u1", "user", "pass", "BASIC");
        mongoUser2 = new MongoUser("u2", "user2", "pass", "BASIC");
        mongoUserDTOResponse1 = new MongoUserDTOResponse(mongoUser1.id(), mongoUser1.username());
        mongoUserDTOResponse2 = new MongoUserDTOResponse(mongoUser2.id(), mongoUser2.username());
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
        foodItem2 = new FoodItem(
                "f2",
                mongoUser2.id(),
                "Food Item 2",
                "https://res.cloudinary.com/dms477wsv/image/upload/v1679523501/bcqbynehv80oqdxgpdod.jpg",
                "Wilmersdorf",
                Instant.parse("2023-03-20T09:00:00Z"),
                Instant.parse("2023-03-22T09:00:00Z"),
                "This is another food item."
        );
        foodItemDTOResponse1 = new FoodItemDTOResponse(foodItem1.id(), mongoUserDTOResponse1, foodItem1.title(), foodItem1.photoUri(), foodItem1.location(), foodItem1.pickupUntil(), foodItem1.consumeUntil(), foodItem1.description());
        foodItemDTOResponse2 = new FoodItemDTOResponse(foodItem2.id(), mongoUserDTOResponse2, foodItem2.title(), foodItem2.photoUri(), foodItem2.location(), foodItem2.pickupUntil(), foodItem2.consumeUntil(), foodItem2.description());
        mongoUserRepository.save(mongoUser1);
        mongoUserRepository.save(mongoUser2);
        foodItemRepository.save(foodItem1);
        foodItemRepository.save(foodItem2);
        chat1 = new Chat("c1", foodItem1.id(), mongoUser2.id(), mongoUser1.id());
        chat2 = new Chat("c2", foodItem2.id(), mongoUser1.id(), mongoUser2.id());
        chatMessage1 = new ChatMessage("cm1", chat1.id(), mongoUser2.id(), Instant.parse("2023-03-15T11:00:00Z"), "Hey there!", true);
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
                .andExpect(content().json(objectMapper.writeValueAsString(new ChatDTOResponse(chat1.id(), foodItemDTOResponse1, mongoUserDTOResponse2, List.of(chatMessage1)))));
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getChatById_whenChatIsNotInRepo_thenReturn404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/chats/12345"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    @WithMockUser
    void getMyChats_whenUserIsInOneChatAsDonatorAndInOneAsCandidate_thenReturnListOfThem() throws Exception {
        chatRepository.save(chat1);
        chatRepository.save(chat2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/chats"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(
                                new ChatDTOResponse(chat1.id(), foodItemDTOResponse1, mongoUserDTOResponse2, List.of()),
                                new ChatDTOResponse(chat2.id(), foodItemDTOResponse2, mongoUserDTOResponse1, List.of())
                        ))
                ));
    }
}
