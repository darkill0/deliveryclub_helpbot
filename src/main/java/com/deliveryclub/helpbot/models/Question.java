package com.deliveryclub.helpbot.models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "questions")
public class Question {
    @Id
    private String id;
    private String category;
    private String question;
    private String answer;
    private List<Attachment> attachments;
    private Date lastUpdated;
    private String addedBy;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Attachment {
    private String type;
    private String content;
}