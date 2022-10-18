package com.wevolv.wevibeservice.repository.impl;

import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.repository.LocationCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.mongodb.core.query.BasicQuery;

public class LocationCustomRepositoryImpl implements LocationCustomRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Stream<Location> findLocationByTag(List<String> locationTags, Pageable page) {
        final Query query = new Query().with(page);
        final List<Criteria> criteria = new ArrayList<>();
        if (locationTags != null) {
            criteria.add(Criteria.where("tags.tagName").in(locationTags));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));
        }
        return mongoTemplate.find(query, Location.class).stream();
    }

    @Override
    public List<Location> findByLocationGeoLocationGeometry(Point basePoint, double radius, List<String> locationTags, Pageable page) {
        // final Query query = new Query().with(page);
//        final List<Criteria> criteria = new ArrayList<>();
//        
//        Criteria geoCriteria = Criteria.where("locationGeoLocation.coordinates").withinSphere(new Circle(basePoint, radius));
//        criteria.add(geoCriteria);

//        if (locationTags != null){
//            query.addCriteria(Criteria.where("tags.tagName").in(locationTags).andOperator(Criteria.where("locationGeoLocation").within(new Circle(basePoint, radius))));
//        }else{
//            query.addCriteria(Criteria.where("locationGeoLocation").within(new Circle(basePoint, radius)));
//        }
//            criteria.add(new Criteria().andOperator(Criteria.where("tags.tagName").in(locationTags)));
//
//        if (!criteria.isEmpty())
//             query.addCriteria(Criteria.);
        //query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));
//        NearQuery nq = NearQuery.near(basePoint)
//                .maxDistance(new Distance(radius, Metrics.KILOMETERS))
//                .query(query);

        double radiusInMeter = radius * 1000;
        
//        BasicQuery query1 = new BasicQuery("{ locationGeoLocation: { $geoWithin: { $center: [ ["+basePoint.getX()+","+ basePoint.getY()+"], "+radius+" ] } } }");
//        
         BasicQuery query1 = new BasicQuery("{locationGeoLocation:{ $nearSphere: "
                + "{ $geometry: { type: 'Point',coordinates: [" + basePoint.getX() + "," + basePoint.getY() + " ] }, "
                 + "$minDistance: 0, $maxDistance: " + radiusInMeter + "}}}");
        
        if (locationTags != null && !locationTags.isEmpty()){
        query1.addCriteria(new Criteria().andOperator(Criteria.where("tags.tagName").in(locationTags)));
        }
        return mongoTemplate.find(query1, Location.class);
    }
}
