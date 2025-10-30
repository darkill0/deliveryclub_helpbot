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
@Document(collection = "files")
public class FileResource {
    @Id
    private String id;
    private String title;
    private String description;
    private String fileUrl;
    private String uploadedBy;
    private String status; // Новое поле для строкового статуса
    private Date uploadedAt;
    private String approvedBy;

    // Инициализация статуса по умолчанию
    public FileResource() {
        this.status = "PENDING"; // Устанавливаем начальный статус
    }
}