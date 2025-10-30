package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission, String> {
    Submission findByTitle(String title);
    List<Submission> findByStatus(String status);
    Long countByStatus(String status);
    Submission findByTitleAndSubmittedBy(String title, String submittedBy);
    List<Submission> findBySubmittedBy(String submittedBy);
}