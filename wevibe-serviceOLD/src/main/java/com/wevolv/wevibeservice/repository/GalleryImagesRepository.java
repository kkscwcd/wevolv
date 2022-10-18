package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.Image;
import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.dto.UploadedImageDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Repository
public interface GalleryImagesRepository extends MongoRepository<Image, String> {
}

