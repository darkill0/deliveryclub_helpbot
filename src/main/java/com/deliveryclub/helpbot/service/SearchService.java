package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private CallRuleService callRuleService;

    // Поиск по всему содержимому
    public List<SearchResult> search(String query) {
        List<SearchResult> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        // Поиск в словарике
        List<DictionaryCategory> dictionaryCategories = dictionaryService.getAllCategories();
        for (DictionaryCategory category : dictionaryCategories) {
            for (DictionaryTerm term : category.getTerms()) {
                if (term.getTerm().toLowerCase().contains(lowerQuery) ||
                        term.getDescription().toLowerCase().contains(lowerQuery)) {
                    results.add(new SearchResult(
                            "Словарик",
                            category.getName(),
                            term.getTerm(),
                            term.getDescription()
                    ));
                }
            }
        }

        // Поиск в полезных ссылках
        List<LinkCategory> linkCategories = linkService.getAllCategories();
        for (LinkCategory category : linkCategories) {
            for (Link link : category.getLinks()) {
                if (link.getName().toLowerCase().contains(lowerQuery) ||
                        link.getDescription().toLowerCase().contains(lowerQuery) ||
                        link.getUrl().toLowerCase().contains(lowerQuery)) {
                    results.add(new SearchResult(
                            "Полезные ссылки",
                            category.getName(),
                            link.getName(),
                            link.getDescription() + "\n🔗 " + link.getUrl()
                    ));
                }
            }
        }

        // Поиск в вопросах


        // Поиск в правилах дозвона
        List<CallRuleCategory> callRuleCategories = callRuleService.getAllCategories();
        for (CallRuleCategory category : callRuleCategories) {
            for (CallRule rule : category.getRules()) {
                if (rule.getDescription().toLowerCase().contains(lowerQuery) ||
                        (rule.getLink() != null && rule.getLink().toLowerCase().contains(lowerQuery))) {
                    String description = rule.getDescription();
                    if (rule.getLink() != null && !rule.getLink().isEmpty()) {
                        description += "\n🔗 " + rule.getLink();
                    }
                    results.add(new SearchResult(
                            "Правила дозвона",
                            category.getName(),
                            null, // У правил нет отдельного "названия", только описание
                            description
                    ));
                }
            }
        }

        List<Question> questions = questionService.getAllQuestions();
        for (Question question : questions) {
            if (question.getQuestion().toLowerCase().contains(lowerQuery) ||
                    question.getCategory().toLowerCase().contains(lowerQuery) ||
                    (question.getAnswer() != null && question.getAnswer().toLowerCase().contains(lowerQuery))) {
                results.add(new SearchResult(
                        "Вопросы",
                        question.getCategory(),
                        question.getQuestion(),
                        question.getAnswer() != null ? question.getAnswer() : "Ответ отсутствует"
                ));
            }
        }

        return results;

    }
}

// Класс для хранения результатов поиска
