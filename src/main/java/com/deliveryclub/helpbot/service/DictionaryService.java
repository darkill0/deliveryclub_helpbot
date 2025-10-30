package com.deliveryclub.helpbot.service;

import com.deliveryclub.helpbot.models.DictionaryCategory;
import com.deliveryclub.helpbot.models.DictionaryTerm;
import com.deliveryclub.helpbot.repository.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DictionaryService {

    @Autowired
    private DictionaryRepository dictionaryRepository;

    // Получить все категории
    public List<DictionaryCategory> getAllCategories() {
        return dictionaryRepository.findAll();
    }

    // Получить категорию по названию
    public DictionaryCategory getCategoryByName(String name) {
        return dictionaryRepository.findByName(name);
    }

    // Получить описание термина по категории и названию термина
    public String getTermDescription(String categoryName, String term) {
        DictionaryCategory category = dictionaryRepository.findByName(categoryName);
        if (category != null && category.getTerms() != null) {
            Optional<DictionaryTerm> foundTerm = category.getTerms().stream()
                    .filter(t -> t.getTerm().equalsIgnoreCase(term))
                    .findFirst();
            return foundTerm.map(DictionaryTerm::getDescription).orElse("Термин не найден.");
        }
        return "Категория или термин не найдены.";
    }

    // Добавить новую категорию (для инициализации)
    public void addCategory(DictionaryCategory category) {
        dictionaryRepository.save(category);
    }
}