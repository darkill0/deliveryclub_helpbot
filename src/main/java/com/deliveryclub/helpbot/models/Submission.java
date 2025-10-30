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
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "submissions")
public class Submission {
    @Id
    private String id;
    private String title;
    private String description;
    private String fileUrl;
    private String submittedBy;
    private Date submittedAt;
    private String status;
    private String reviewedBy;
    private Date reviewedAt;
}