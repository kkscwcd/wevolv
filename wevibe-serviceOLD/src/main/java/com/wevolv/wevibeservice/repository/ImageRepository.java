package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.Image;
import com.wevolv.wevibeservice.domain.model.LocationReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {

    List<Image> findAllByLocationId(String locationId);

    Page<Image> findAllByLocationId(String locationId, PageRequest paging);

    List<Image> findAllByReviewId(String reviewId);

    Page<Image> findAllByReviewId(String reviewId, PageRequest paging);

    Optional<Image> findByIdAndKeycloakId(String id, String keycloakId);

    Optional<Image> findByLocationId(String locationId);
}
