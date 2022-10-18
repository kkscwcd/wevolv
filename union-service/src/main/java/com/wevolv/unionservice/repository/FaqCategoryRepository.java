package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Faq;
import com.wevolv.unionservice.model.FaqCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FaqCategoryRepository extends MongoRepository<FaqCategory, String> {
}
