package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.Shift;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShiftRepository extends MongoRepository<Shift, String> {
    List<Shift> findByUserId(String userId);
    List<Shift> findByUserIdAndDate(String userId, String date);
    List<Shift> findByUserIdAndDateBetween(String userId, String startDate, String endDate);
}