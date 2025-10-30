package com.deliveryclub.helpbot.models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;
    private String assignedTo; // chatId пользователя, которому назначена задача
    private String description; // Описание задачи
    private Date deadline; // Дедлайн задачи
    private String assignedBy; // chatId модератора, назначившего задачу
    private Date assignedAt; // Дата назначения
    private String status; // "pending", "completed", "overdue" (по умолчанию "pending")

    public Task() {
        this.assignedAt = new Date();
        this.status = "pending";
    }
}