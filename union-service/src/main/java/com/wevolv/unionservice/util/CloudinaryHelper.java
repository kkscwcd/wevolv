package com.wevolv.unionservice.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.wevolv.unionservice.config.CloudinaryConfig;
import com.wevolv.unionservice.model.dto.UploadedImageDto;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CloudinaryHelper {

    private final CloudinaryConfig cloudinaryConfig;

    public CloudinaryHelper(CloudinaryConfig cloudinaryConfig) {
        this.cloudinaryConfig = cloudinaryConfig;
    }

    public UploadedImageDto uploadOnCloudinary(MultipartFile multipartFile) throws IOException {

        File file = convertMultipartFileToFile(multipartFile);

        Cloudinary cloudinary =
                new Cloudinary("cloudinary://" + cloudinaryConfig.getApiKey() + ":"
                        + cloudinaryConfig.getApiSecret() + "@"
                        + cloudinaryConfig.getCloudName());

        Map<?, ?> result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        file.delete();
        return UploadedImageDto.builder()
                .public_id((String) result.get("public_id"))
                .url((String) result.get("secure_url"))
                .build();
    }

    public void deleteImageFromCloudinary(String publicId) throws Exception {
        Cloudinary cloudinary =
                new Cloudinary("cloudinary://" + cloudinaryConfig.getApiKey() + ":"
                        + cloudinaryConfig.getApiSecret() + "@"
                        + cloudinaryConfig.getCloudName());
        cloudinary.api().deleteResources(new ArrayList<>(List.of(publicId)),ObjectUtils.emptyMap());
    }

    /**
     * Convert multipart file to file
     *
     * @param multipartFile
     */
    //Convert MultipartFile in File
    public File convertMultipartFileToFile(MultipartFile multipartFile) {
        File convFile = new File(multipartFile.getOriginalFilename());
        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(multipartFile.getBytes());
            fos.close();

        } catch (IOException e) {

        }

        return convFile;
    }

}