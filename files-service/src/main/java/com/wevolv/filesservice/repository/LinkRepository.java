package com.wevolv.filesservice.repository;

import com.wevolv.filesservice.domain.model.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LinkRepository extends MongoRepository<Link, String> {

    Page<Link> findAllByIsStandalone(boolean b, PageRequest paging);
}
