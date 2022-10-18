package com.wevolv.filesservice.service;

import com.wevolv.filesservice.domain.dto.FolderDto;
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
import java.util.Optional;

public interface FolderService {

    Folder createFolder(FolderDto folderDto, String keycloakId);

    Optional<Folder> createFolderInFolder(FolderDto folderDto, String folderId, String keycloakId);

    Folder getFolder(String folderId);

    File uploadFileToFolder(MultipartFile multipartFile, String keycloakId, String folderId) throws IOException;

    Image saveImageToFolder(MultipartFile multipartFile, String keycloakId, String folderId) throws IOException;

    Link saveLinkToFolder(LinkDto linkDto, String keycloakId, String folderId) throws IOException;

    void deleteFolder(List<String> folderId, String keycloakId);

    void deleteFile(List<String> fileId, String keycloakId);

    List<Folder> searchFoldersByName(String folderName, String keycloakId);

    Map<String, Object> getAllFolders(PageRequest paging);
}
