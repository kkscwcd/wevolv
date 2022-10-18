package com.wevolv.filesservice.config;

import com.wevolv.filesservice.repository.FileRepository;
import com.wevolv.filesservice.repository.FolderRepository;
import com.wevolv.filesservice.repository.ImageRepository;
import com.wevolv.filesservice.repository.LinkRepository;
import com.wevolv.filesservice.service.FileService;
import com.wevolv.filesservice.service.FolderService;
import com.wevolv.filesservice.service.impl.FileServiceImpl;
import com.wevolv.filesservice.service.impl.FolderServiceImpl;
import com.wevolv.filesservice.util.CloudinaryHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServiceIntegrationConfig {

    @Bean
    FileService getFileService(FileRepository fileRepository, CloudinaryHelper cloudinaryHelper,
                               ImageRepository imageRepository, LinkRepository linkRepository, FolderRepository folderRepository){
        return new FileServiceImpl(fileRepository, cloudinaryHelper, imageRepository, linkRepository, folderRepository);
    }

    @Bean
    FolderService getFolderService(FolderRepository folderRepository,
                                   FileRepository fileRepository,
                                   CloudinaryHelper cloudinaryHelper,
                                   ImageRepository imageRepository,
                                   LinkRepository linkRepository){
        return new FolderServiceImpl(folderRepository, fileRepository,
                cloudinaryHelper, imageRepository, linkRepository);
    }


}
