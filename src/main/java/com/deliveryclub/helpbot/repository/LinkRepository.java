package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.LinkCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LinkRepository extends MongoRepository<LinkCategory, String> {
    LinkCategory findByName(String name);
}