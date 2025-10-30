package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.CallRuleCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CallRuleRepository extends MongoRepository<CallRuleCategory, String> {
    CallRuleCategory findByName(String name);
}