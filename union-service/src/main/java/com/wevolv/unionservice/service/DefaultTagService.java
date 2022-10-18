package com.wevolv.unionservice.service;

import com.wevolv.unionservice.model.DefaultTags;
import com.wevolv.unionservice.model.dto.TagsDto;

import java.util.List;

public interface DefaultTagService {
    List<DefaultTags> saveListOfTags(TagsDto tagsDto);

    List<DefaultTags> getTagsList(String keycloakId);
}
