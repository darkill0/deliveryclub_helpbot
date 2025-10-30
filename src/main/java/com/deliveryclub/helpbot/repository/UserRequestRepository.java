package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.UserRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRequestRepository extends MongoRepository<UserRequest, String> {
    List<UserRequest> findByUserId(String userId);
    List<UserRequest> findByStatus(String status);
}