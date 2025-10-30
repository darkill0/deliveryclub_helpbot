package com.deliveryclub.helpbot.models;

public class DictionaryTerm {
    private String term; // Например, "НД", "АСАП"
    private String description; // Описание термина

    // Конструкторы
    public DictionaryTerm() {}

    public DictionaryTerm(String term, String description) {
        this.term = term;
        this.description = description;
    }

    // Геттеры и сеттеры
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}