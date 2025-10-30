package com.deliveryclub.helpbot.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shifts")
public class Shift {
    @Id
    private String id;
    private String userId; // ID пользователя
    private String date; // Дата смены (формат: "yyyy-MM-dd")
    private String startTime; // Начало смены (формат: "HH:mm")
    private String endTime; // Конец смены (формат: "HH:mm")

    public Shift() {}

    public Shift(String userId, String date, String startTime, String endTime) {
        this.userId = userId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}