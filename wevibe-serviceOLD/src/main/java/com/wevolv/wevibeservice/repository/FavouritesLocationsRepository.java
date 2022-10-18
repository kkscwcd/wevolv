package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.FavoriteLocation;
import com.wevolv.wevibeservice.domain.model.Image;
import com.wevolv.wevibeservice.domain.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavouritesLocationsRepository extends MongoRepository<FavoriteLocation, String> {
    Optional<FavoriteLocation> findByLocationIdAndKeycloakId(String locationId, String keycloakId);

    Page<FavoriteLocation> findAllByKeycloakId(String keycloakId, PageRequest paging);
}
