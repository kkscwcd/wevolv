
package com.wevolv.wevibeservice.repository.impl;

import com.wevolv.wevibeservice.domain.model.AthleteCoordinates;
import com.wevolv.wevibeservice.domain.model.dto.AthleteCoordinatesDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kundankumar
 */
@Repository
public class AthletesCoordinatesCustomRepositoryImpl {
    
    @Autowired
    MongoTemplate mongoTemplate;

    public List<AthleteCoordinates> findByAthletesGeoLocationWithin(AthleteCoordinatesDTO athleteCoordinatesDTO) {
        double radiusInMeter = athleteCoordinatesDTO.getRadius() * 1000;
        BasicQuery query1 = new BasicQuery("{athletesGeoLocation:{ $nearSphere: { $geometry: { type: 'Point',coordinates: [" + athleteCoordinatesDTO.getLongitude() + "," + athleteCoordinatesDTO.getLongitude() + " ] }, $minDistance: 0, $maxDistance: " + radiusInMeter + "}}}");
        
        return mongoTemplate.find(query1, AthleteCoordinates.class);
    
    }

  
    
}
