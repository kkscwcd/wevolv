package com.wevolv.filesservice.repository;


import com.wevolv.filesservice.domain.model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import org.springframework.data.mongodb.repository.Query;

public interface FileRepository extends MongoRepository<File, String> {
    
    void findByKeycloakId(String keycloakId);

    //TODO in future change it to Case Insensitive Indexes on fileName as currently Spring not support
    @Query(value = "{'fileName': {$regex : ?0, $options: 'i'}}")
    List<File> findAllByFileName(String fileName);

    Page<File> findAllByIsStandalone(Boolean isStandalone, PageRequest paging);
}
