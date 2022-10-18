package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.geo.Circle;

@Repository
public interface LocationRepository extends MongoRepository<Location, String>, LocationCustomRepository {

    List<Location> findByLocationGeoLocationWithin(Circle area);

    Optional<Location> findByIdAndKeycloakId(String locationId, String keycloakId);

    List<Location> findAllByDeckId(String deckId);

    Page<Location> findAllLocationsByDeckId(String deckId, PageRequest page);

    Page<Location> findAllByKeycloakId(String keycloakId, PageRequest paging);
}
