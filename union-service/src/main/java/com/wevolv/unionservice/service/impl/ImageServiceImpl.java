package com.wevolv.unionservice.service.impl;

import com.wevolv.unionservice.exceptions.NotFoundException;
import com.wevolv.unionservice.model.Image;
import com.wevolv.unionservice.repository.ImageRepository;
import com.wevolv.unionservice.service.ImageService;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Date;

public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
//    private final CloudinaryHelper cloudinaryHelper;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

/*    @Override
    public Image uploadImage(MultipartFile multipartFile, String keycloakId) throws IOException {
        Image image;

        try {
            image = Image.builder()
                    .createdTime(new Date().getTime())
                    .fileName(multipartFile.getOriginalFilename())
                    .fileType(multipartFile.getContentType())
                    .fileSize(multipartFile.getSize())
                    .fileDescription("")
                    .build();
            var sf = cloudinaryHelper.uploadOnCloudinary(multipartFile);
            image.setLink(sf.getUrl());
            image.setPublic_id(sf.getPublic_id());
            imageRepository.save(image);

        } catch (Exception e) {
            throw new IOException("Error while saving image to database.");
        }

        return image;
    }

    @Override
    public Image getImageById(String imageId, String keycloakId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(String.format("Image with imageId: %s does not exist", imageId)));
    }*/
}
