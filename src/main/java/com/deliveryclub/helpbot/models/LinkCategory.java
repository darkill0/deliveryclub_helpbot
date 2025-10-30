package com.deliveryclub.helpbot.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "link_categories")
public class LinkCategory {
    @Id
    private String id;
    private String name; // Например, "Рабочие инструменты", "Формы"
    private List<Link> links;

    public LinkCategory() {}

    public LinkCategory(String name, List<Link> links) {
        this.name = name;
        this.links = links;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Link> getLinks() { return links; }
    public void setLinks(List<Link> links) { this.links = links; }
}