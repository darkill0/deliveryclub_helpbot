package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.CompensationRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CompensationRequestRepository extends MongoRepository<CompensationRequest, String> {
    List<CompensationRequest> findByUserId(String userId);
    List<CompensationRequest> findByStatus(String status);
}