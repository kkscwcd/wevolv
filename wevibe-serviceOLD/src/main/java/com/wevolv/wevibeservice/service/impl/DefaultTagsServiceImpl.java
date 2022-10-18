package com.wevolv.wevibeservice.service.impl;

import com.wevolv.wevibeservice.domain.model.DefaultTagsLocation;
import com.wevolv.wevibeservice.domain.model.DefaultTagsVibe;
import com.wevolv.wevibeservice.domain.model.dto.LocationDefaultTagsDto;
import com.wevolv.wevibeservice.domain.model.dto.VibeDefaultTagsDto;
import com.wevolv.wevibeservice.repository.DefaultLocationTagsRepository;
import com.wevolv.wevibeservice.repository.DefaultVibeTagsRepository;
import com.wevolv.wevibeservice.service.DefaultTagService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultTagsServiceImpl implements DefaultTagService {

    private final DefaultLocationTagsRepository defaultLocationTagsRepository;
    private final DefaultVibeTagsRepository defaultVibeTagsRepository;

    public DefaultTagsServiceImpl(DefaultLocationTagsRepository defaultLocationTagsRepository, DefaultVibeTagsRepository defaultVibeTagsRepository) {
        this.defaultLocationTagsRepository = defaultLocationTagsRepository;
        this.defaultVibeTagsRepository = defaultVibeTagsRepository;
    }

    @Override
    public List<DefaultTagsLocation> saveListOfLocationTags(LocationDefaultTagsDto tagsDto) {
        List<DefaultTagsLocation> newTagList = new ArrayList<>();
        tagsDto.getTagName().forEach(t -> {
            var newTag = DefaultTagsLocation.builder()
                    .id(UUID.randomUUID().toString())
                    .tagName(t)
                    .build();
            newTagList.add(newTag);
        });
        return defaultLocationTagsRepository.saveAll(newTagList);
    }

    @Override
    public List<DefaultTagsLocation> getListOfLocationTags(String keycloakId) {
        return defaultLocationTagsRepository.findAll();
    }

    @Override
    public List<DefaultTagsVibe> saveListOfVibeTags(VibeDefaultTagsDto tagsDto) {
        List<DefaultTagsVibe> newTagList = new ArrayList<>();
        tagsDto.getTagName().forEach(t -> {
            var newTag = DefaultTagsVibe.builder()
                    .id(UUID.randomUUID().toString())
                    .tagName(t)
                    .build();
            newTagList.add(newTag);
        });
        return defaultVibeTagsRepository.saveAll(newTagList);
    }

    @Override
    public List<DefaultTagsVibe> getListOfVibeTags(String keycloakId) {
        return defaultVibeTagsRepository.findAll();
    }
}
