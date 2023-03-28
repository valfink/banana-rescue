package com.github.valfink.backend.chat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    Optional<Chat> getChatByFoodItemIdAndCandidateId(String foodItemId, String candidateId);
}
