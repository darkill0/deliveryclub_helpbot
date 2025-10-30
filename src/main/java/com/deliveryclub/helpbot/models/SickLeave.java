package com.deliveryclub.helpbot.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sick_leaves")
public class SickLeave {
    @Id
    private String id;
    private String userId; // ID пользователя
    private String startDate; // Начало больничного
    private String endDate; // Конец больничного
    private String status; // "PENDING", "APPROVED", "REJECTED"

    public SickLeave() {}

    public SickLeave(String userId, String startDate, String endDate) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "PENDING"; // По умолчанию на рассмотрении
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}