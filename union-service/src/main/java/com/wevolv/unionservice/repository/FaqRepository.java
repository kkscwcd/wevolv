package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FaqRepository extends MongoRepository<Faq, String> {
    List<Faq> findByCategoryId(String categoryId);

    Page<Faq> findByCategoryId(String categoryId, PageRequest page);
}
