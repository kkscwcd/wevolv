package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.Location;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.stream.Stream;


public interface LocationCustomRepository {
    Stream<Location> findLocationByTag(List<String> locationTags, Pageable page);

    List<Location> findByLocationGeoLocationGeometry(Point basePoint, double radius, List<String> locationTags, Pageable page);
}
