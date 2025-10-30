package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
    Session findBySessionToken(String sessionToken);
}