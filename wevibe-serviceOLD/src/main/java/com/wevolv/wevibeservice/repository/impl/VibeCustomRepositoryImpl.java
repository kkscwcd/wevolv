package com.wevolv.wevibeservice.repository.impl;

import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.enums.VibeTag;
import com.wevolv.wevibeservice.repository.VibeCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Repository
public class VibeCustomRepositoryImpl implements VibeCustomRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Stream<Vibe> findVibeByTag(List<String> vibeTags, Pageable page) {
        final Query query = new Query().with(page);
        final List<Criteria> criteria = new ArrayList<>();
        if (vibeTags != null)
            criteria.add(Criteria.where("tags.tagName").in(vibeTags));

        if (!criteria.isEmpty())
        query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));
        return mongoTemplate.find(query, Vibe.class).stream();

    }

    @Override
    public List<Vibe> findByVibeGeoLocationGeometry(Point basePoint, double radius, List<String> vibeTags, Pageable page) {

        double radiusInMeter = radius * 1000;
        BasicQuery query1 = new BasicQuery("{vibeGeoLocation:{ $nearSphere: { $geometry: { type: 'Point',coordinates: [" + basePoint.getX() + "," + basePoint.getY() + " ] }, $minDistance: 0, $maxDistance: " + radiusInMeter + "}}}");
        if (vibeTags != null && !vibeTags.isEmpty()){
            query1.addCriteria(Criteria.where("tags.tagName").in(vibeTags));
        }
        return mongoTemplate
                .find(query1, Vibe.class);
    }


}
