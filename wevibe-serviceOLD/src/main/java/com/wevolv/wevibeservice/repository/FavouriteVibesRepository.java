package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.FavoriteLocation;
import com.wevolv.wevibeservice.domain.model.FavouriteVibes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FavouriteVibesRepository extends MongoRepository<FavouriteVibes, String> {
    Optional<FavouriteVibes> findByVibeIdAndKeycloakId(String vibeId, String keycloakId);

    Page<FavouriteVibes> findAllByKeycloakId(String keycloakId, PageRequest paging);
}
