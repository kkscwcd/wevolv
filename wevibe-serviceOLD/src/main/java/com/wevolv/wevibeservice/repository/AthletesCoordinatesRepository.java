package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.AthleteCoordinates;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AthletesCoordinatesRepository extends MongoRepository<AthleteCoordinates, String> {
    Optional<AthleteCoordinates> findByKeycloakId(String keycloakId);

    //Optional<List<AthleteCoordinates>> findByGeoLocationNear(Point basePoint, Distance radius);

    Optional<List<AthleteCoordinates>> findByAthletesGeoLocationWithin(Circle area);
}
