package com.deliveryclub.helpbot.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "dictionary_categories")
public class DictionaryCategory {
    @Id
    private String id;
    private String name; // Например, "Админка", "Заказ" и т.д.
    private List<DictionaryTerm> terms;

    // Конструкторы
    public DictionaryCategory() {}

    public DictionaryCategory(String name, List<DictionaryTerm> terms) {
        this.name = name;
        this.terms = terms;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<DictionaryTerm> getTerms() { return terms; }
    public void setTerms(List<DictionaryTerm> terms) { this.terms = terms; }
}