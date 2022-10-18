package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.File;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileRepository extends MongoRepository<File, String> {
    void findByKeycloakId(String keycloakId);

    List<File> findAllByFolderId(String folderId);
}
