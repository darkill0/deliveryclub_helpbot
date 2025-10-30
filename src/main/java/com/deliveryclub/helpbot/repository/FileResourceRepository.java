package com.deliveryclub.helpbot.repository;

import com.deliveryclub.helpbot.models.FileResource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileResourceRepository extends MongoRepository<FileResource, String> {
    FileResource findByFileUrl(String fileUrl);
    List<FileResource> findByStatus(String status);
}