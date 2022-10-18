package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.LocationReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationReviewRepository extends MongoRepository<LocationReview, String> {

    Optional<LocationReview> findByIdAndKeycloakId(String locationReviewId, String keycloakId);

    Page<LocationReview> findAllByLocationId(String locationId, PageRequest paging);

    List<LocationReview> findAllByLocationId(String locationId);
}
