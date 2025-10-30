package com.deliveryclub.helpbot.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

@Document(collection = "user_requests")
public class UserRequest {
    @Id
    private String id;
    private String userId; // ID пользователя
    private String type; // "DAY_OFF" или "FORCE_MAJEURE"
    private String description; // Описание запроса
    private String status; // "PENDING", "APPROVED", "REJECTED"
    private String createdAt; // Дата создания

    public UserRequest() {}

    public UserRequest(String userId, String type, String description) {
        this.userId = userId;
        this.type = type;
        this.description = description;
        this.status = "PENDING";
        this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}