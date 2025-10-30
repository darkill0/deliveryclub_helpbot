package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.SickLeave;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SickLeaveRepository extends MongoRepository<SickLeave, String> {
    List<SickLeave> findByUserId(String userId);

    List<SickLeave> findByStatus(String pending);
}
