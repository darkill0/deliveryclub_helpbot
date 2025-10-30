package com.deliveryclub.helpbot.models;

public class Link {
    private String name; // Например, "БИЛИМ, ЛОГИКА"
    private String url;  // URL-адрес
    private String description; // Описание ссылки

    public Link() {}

    public Link(String name, String url, String description) {
        this.name = name;
        this.url = url;
        this.description = description;
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}