package com.wevolv.unionservice.service.impl;

import com.wevolv.unionservice.model.DefaultTags;
import com.wevolv.unionservice.model.dto.TagsDto;
import com.wevolv.unionservice.repository.DefaultTagsRepository;
import com.wevolv.unionservice.service.DefaultTagService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultTagServiceImpl implements DefaultTagService {

    private final DefaultTagsRepository defaultTagsRepository;

    public DefaultTagServiceImpl(DefaultTagsRepository defaultTagsRepository) {
        this.defaultTagsRepository = defaultTagsRepository;
    }

    @Override
    public List<DefaultTags> saveListOfTags(TagsDto tagsDto) {
        List<DefaultTags> newTagList = new ArrayList<>();
        tagsDto.getTagName().forEach(t -> {
            newTagList.add(new DefaultTags(UUID.randomUUID().toString(), t));
        });
        return defaultTagsRepository.saveAll(newTagList);
    }

    @Override
    public List<DefaultTags> getTagsList(String keycloakId) {
        return defaultTagsRepository.findAll();
    }
}
