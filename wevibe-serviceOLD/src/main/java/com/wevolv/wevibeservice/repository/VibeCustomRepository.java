package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.enums.VibeTag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.stream.Stream;

public interface VibeCustomRepository {
    Stream<Vibe>  findVibeByTag(List<String> vibeTags, Pageable page);

    List<Vibe> findByVibeGeoLocationGeometry(Point basePoint, double radius, List<String> vibeTags, Pageable page);
}
