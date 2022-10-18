package com.wevolv.filesservice.repository;

import com.wevolv.filesservice.domain.model.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.Query;

public interface FolderRepository extends MongoRepository<Folder, String> {
    
     //TODO in future change it to Case Insensitive Indexes on name as currently Spring not support
    @Query(value = "{'name': {$regex : ?0, $options: 'i'}}")
    List<Folder> findAllByName(String folderName);
}
