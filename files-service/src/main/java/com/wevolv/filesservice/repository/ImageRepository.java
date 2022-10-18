package com.wevolv.filesservice.repository;

import com.wevolv.filesservice.domain.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<Image, String> {

    Page<Image> findAllByIsStandalone(boolean b, PageRequest paging);
}
