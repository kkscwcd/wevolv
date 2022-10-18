package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FolderRepository extends MongoRepository<Folder, String> {
    Optional<Folder> findByKeycloakId(String keycloakId);
}
