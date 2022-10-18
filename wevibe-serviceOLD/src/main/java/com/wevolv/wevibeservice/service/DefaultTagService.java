package com.wevolv.wevibeservice.service;

import com.wevolv.wevibeservice.domain.model.DefaultTagsLocation;
import com.wevolv.wevibeservice.domain.model.DefaultTagsVibe;
import com.wevolv.wevibeservice.domain.model.dto.LocationDefaultTagsDto;
import com.wevolv.wevibeservice.domain.model.dto.VibeDefaultTagsDto;

import java.util.List;

public interface DefaultTagService {
    List<DefaultTagsLocation> saveListOfLocationTags(LocationDefaultTagsDto tagsDto);

    List<DefaultTagsLocation> getListOfLocationTags(String keycloakId);

    List<DefaultTagsVibe> saveListOfVibeTags(VibeDefaultTagsDto tagsDto);

    List<DefaultTagsVibe> getListOfVibeTags(String keycloakId);
}
