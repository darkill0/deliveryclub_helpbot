package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.DictionaryCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DictionaryRepository extends MongoRepository<DictionaryCategory, String> {
    DictionaryCategory findByName(String name);
}