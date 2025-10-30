package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    Optional<Question> findByCategoryAndQuestion(String category, String question);
    List<Question> findByQuestionContainingIgnoreCase(String question);
}