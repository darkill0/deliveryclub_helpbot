package com.deliveryclub.helpbot.models;

public class CallRule {
    private String description; // Текст правила
    private String link; // Ссылка (если есть)

    public CallRule() {}

    public CallRule(String description, String link) {
        this.description = description;
        this.link = link;
    }

    // Геттеры и сеттеры
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}