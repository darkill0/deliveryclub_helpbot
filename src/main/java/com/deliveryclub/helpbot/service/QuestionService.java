package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.Question;
import com.deliveryclub.helpbot.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }


    public Optional<Question> getAnswerForQuestion(String userMessage) {
        return questionRepository.findById(userMessage);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Создание нового вопроса
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    // Поиск вопросов по ключевым словам
    public List<Question> searchQuestionsByKeyword(String keyword) {
        return questionRepository.findByQuestionContainingIgnoreCase(keyword);
    }

    // Получение вопроса по ID
    public Question getQuestionById(String id) {
        return questionRepository.findById(id).orElse(null);
    }

    // Обновление существующего вопроса
    public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }

    // Удаление вопроса
    public void deleteQuestion(Question question) {
        questionRepository.delete(question);
    }
}
