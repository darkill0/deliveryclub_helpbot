package com.deliveryclub.helpbot.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "call_rule_categories")
public class CallRuleCategory {
    @Id
    private String id;
    private String name; // Например, "Общие правила", "Исключения"
    private List<CallRule> rules;

    public CallRuleCategory() {}

    public CallRuleCategory(String name, List<CallRule> rules) {
        this.name = name;
        this.rules = rules;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<CallRule> getRules() { return rules; }
    public void setRules(List<CallRule> rules) { this.rules = rules; }
}