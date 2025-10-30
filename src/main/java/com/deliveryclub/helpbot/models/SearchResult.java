package com.deliveryclub.helpbot.models;

public class  SearchResult {
    private String source; // "Словарик", "Полезные ссылки", "Правила дозвона"
    private String category; // Название категории
    private String title; // Название термина/ссылки (может быть null для правил)
    private String description; // Описание или полный текст

    public SearchResult(String source, String category, String title, String description) {
        this.source = source;
        this.category = category;
        this.title = title;
        this.description = description;
    }

    public String getSource() { return source; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}