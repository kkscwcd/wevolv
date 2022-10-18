package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {

    List<Like> findAllByImageId(String imageId);

    Page<Like> findAllByImageId(String imageId, PageRequest page);

    List<Like> findAllByReviewId(String reviewId);
}

