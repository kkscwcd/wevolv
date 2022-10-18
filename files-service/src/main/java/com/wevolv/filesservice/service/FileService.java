package com.wevolv.filesservice.service;

import com.wevolv.filesservice.domain.dto.LinkDto;
import com.wevolv.filesservice.domain.model.File;
import com.wevolv.filesservice.domain.model.Folder;
import com.wevolv.filesservice.domain.model.Image;
import com.wevolv.filesservice.domain.model.Link;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FileService {

    File uploadFile(MultipartFile multipartFile, String keycloakId) throws IOException;

    File getFileById(String fileId);

    Image uploadImage(MultipartFile multipartFile, String keycloakId) throws IOException;

    Image getImageById(String imageId, String keycloakId);

    Link uploadLink(LinkDto linkDto, String keycloakId) throws IOException;

    void duplicateFile(String newFolderId, List<String> fileId, String keycloakId);

    void moveFile(String newFolderId, String currentFolderId, List<String> fileId, String keycloakId);

    void deleteFile(List<String> fileId, String keycloakId);

    List<File> searchFilesByName(String fileName, String keycloakId);

    void moveFileToFolder(String folderId, List<String> fileId, String keycloakId);

    Map<String, Object> getStandAloneFiles(PageRequest paging);

    Map<String, Object> getStandAloneImages(PageRequest paging);

    Map<String, Object> getStandAloneLinks(PageRequest paging);
}
