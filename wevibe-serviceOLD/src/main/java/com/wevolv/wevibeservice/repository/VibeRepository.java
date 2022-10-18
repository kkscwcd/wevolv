package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.Vibe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VibeRepository extends MongoRepository<Vibe, String> , VibeCustomRepository{

    Optional<Vibe> findByIdAndVibeAuthor_KeycloakId(String vibeId, String keycloakId);

    //List<Vibe> findByVibeGeoLocationCoordinatesWithin(Circle area);



    List<Vibe> findAllByDeckId(String deckId);

    Page<Vibe> findAllVibesByDeckId(String deckId, PageRequest page);

    Page<Vibe> findAllByKeycloakId(String keycloakId, PageRequest paging);
}
