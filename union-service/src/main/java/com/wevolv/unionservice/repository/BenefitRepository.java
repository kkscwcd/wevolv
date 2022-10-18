package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Benefit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BenefitRepository extends MongoRepository<Benefit, String> {
    @Query(value = "{'title': {$regex : ?0, $options: 'i'}}")
    List<Benefit> findByTitle(String title);
}
