package com.wevolv.filesservice.service.impl;

import com.wevolv.filesservice.domain.dto.LinkDto;
import com.wevolv.filesservice.domain.model.File;
import com.wevolv.filesservice.domain.model.Image;
import com.wevolv.filesservice.domain.model.Link;
import com.wevolv.filesservice.exception.NotFoundException;
import com.wevolv.filesservice.repository.FileRepository;
import com.wevolv.filesservice.repository.FolderRepository;
import com.wevolv.filesservice.repository.ImageRepository;
import com.wevolv.filesservice.repository.LinkRepository;
import com.wevolv.filesservice.service.FileService;
import com.wevolv.filesservice.util.CloudinaryHelper;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final CloudinaryHelper cloudinaryHelper;
    private final ImageRepository imageRepository;
    private final LinkRepository linkRepository;
    private final FolderRepository folderRepository;

    private static final Logger LOGGER = getLogger(FileServiceImpl.class);

    public FileServiceImpl(FileRepository fileRepository, CloudinaryHelper cloudinaryHelper, ImageRepository imageRepository, LinkRepository linkRepository, FolderRepository folderRepository) {
        this.fileRepository = fileRepository;
        this.cloudinaryHelper = cloudinaryHelper;
        this.imageRepository = imageRepository;
        this.linkRepository = linkRepository;
        this.folderRepository = folderRepository;
    }

    @Override
    public File uploadFile(MultipartFile multipartFile, String keycloakId) throws IOException {
        File file = File.builder().build();
        try {
            file.setId(UUID.randomUUID().toString());
            file.setIsStandalone(true);
            file.setKeycloakId(keycloakId);
            file.setCreatedTime(Instant.now());
            file.setFileName(multipartFile.getOriginalFilename());
            file.setFileType(multipartFile.getContentType());
            file.setFileSize(multipartFile.getSize());
            file.setFile(multipartFile.getBytes());
            fileRepository.save(file);

        } catch (Exception e) {
            throw new IOException("Error while saving PDF to database.");
        }
        return file;
    }

    @Override
    public File getFileById(String fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException(String.format("File with fileId %s does not exist", fileId)));

    }

    @Override
    public Image uploadImage(MultipartFile multipartFile, String keycloakId) throws IOException {
        Image image;

        try {
            image = Image.builder()
                    .createdTime(new Date().getTime())
                    .fileName(multipartFile.getOriginalFilename())
                    .fileType(multipartFile.getContentType())
                    .fileSize(multipartFile.getSize())
                    .fileDescription("")
                    .isStandalone(true)
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
    }

    @Override
    public Link uploadLink(LinkDto linkDto, String keycloakId) throws IOException {
        Link link;

        try {
            link = Link.builder()
                    .link(linkDto.getLink())
                    .createdTime(Instant.now())
                    .fileName("")
                    .fileType("link")
                    .fileDescription("")
                    .isStandalone(true)
                    .build();
            linkRepository.save(link);

        } catch (Exception e) {
            throw new IOException("Error while saving PDF to database.");
        }

        return link;
    }

    @Override
    public void duplicateFile(String newFolderId, List<String> fileId, String keycloakId) {
        var newDestinationFolder = folderRepository.findById(newFolderId)
                .orElseThrow(() ->  new NotFoundException(String.format("Folder %s does not exist", newFolderId)));
        var file = getFile(fileId);
        var image = getImage(fileId);
        var link = getLinkById(fileId);

        if (file.size() > 0) {
            file.forEach(fi -> {
                var df = duplicateFileInner(fi, newFolderId);
                fileRepository.save(df);
                newDestinationFolder.getFiles().add(df);
            });
        }

        if (image.size() > 0) {
            image.forEach(img -> {
                var di = duplicateImageInner(img, newFolderId);
                imageRepository.save(di);
                newDestinationFolder.getImages().add(di);
            });
        }

        if (link.size() > 0) {
            link.forEach(l -> {
                var dl = duplicateLinkInner(l, newFolderId);
                linkRepository.save(dl);
                newDestinationFolder.getLinks().add(dl);
            });
        }

        folderRepository.save(newDestinationFolder);
    }

    @Override
    public void moveFile(String newFolderId, String currentFolderId, List<String> fileId, String keycloakId) {
        var currentImageFolder = folderRepository.findById(currentFolderId)
                .orElseThrow(() -> new NotFoundException(String.format("Folder %s does not exist", currentFolderId)));
        var newDestinationFolder = folderRepository.findById(newFolderId)
                .orElseThrow(() -> new NotFoundException(String.format("Folder %s does not exist", newFolderId)));
        var file = getFile(fileId);
        var image = getImage(fileId);
        var link = getLinkById(fileId);

        if (file.size() > 0) {
            file.forEach(fi -> {
                currentImageFolder.getFiles().remove(fi);
                fi.setFolderId(newFolderId);
                fileRepository.save(fi);
                newDestinationFolder.getFiles().add(fi);
            });
        }

        if (image.size() > 0) {
            image.forEach(img -> {
                currentImageFolder.getImages().remove(img);
                img.setFolderId(newFolderId);
                imageRepository.save(img);
                newDestinationFolder.getImages().add(img);
            });
        }

        if (link.size() > 0){
            link.forEach(l -> {
                currentImageFolder.getLinks().remove(l);
                l.setFolderId(newFolderId);
                linkRepository.save(l);
                newDestinationFolder.getLinks().add(l);
            });
        }

        folderRepository.save(currentImageFolder);
        folderRepository.save(newDestinationFolder);
    }

    @Override
    public void deleteFile(List<String> fileId, String keycloakId) {
        var file = getFile(fileId);
        var image = getImage(fileId);
        var link = getLinkById(fileId);

        if(file.size() != 0){
            fileRepository.deleteAll(file);
        }

        if(image.size() != 0){
            image.forEach(img -> {
                try {
                    cloudinaryHelper.deleteImageFromCloudinary(img.getPublic_id());
                } catch (Exception e) {
                    throw new IllegalStateException("Error while deleting image from cloudinary.");
                }
                imageRepository.delete(img);
            });
        }

        if(link.size() != 0){
            linkRepository.deleteAll(link);
        }
    }

    @Override
    public List<File> searchFilesByName(String fileName, String keycloakId) {
        return fileRepository.findAllByFileName(fileName);
    }

    @Override
    public void moveFileToFolder(String folderId, List<String> fileId, String keycloakId) {
        var folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundException(String.format("Folder %s does not exist", folderId)));
        var file = getFile(fileId);
        var image = getImage(fileId);
        var link = getLinkById(fileId);

        if (file.size() > 0) {
            file.forEach(fi -> {
                fi.setFolderId(folderId);
                fi.setIsStandalone(false);
                fileRepository.save(fi);
                folder.getFiles().add(fi);
            });
        }

        if (image.size() > 0) {
            image.forEach(img -> {
                img.setFolderId(folderId);
                img.setStandalone(false);
                imageRepository.save(img);
                folder.getImages().add(img);
            });
        }

        if (link.size() > 0){
            link.forEach(l -> {
                l.setFolderId(folderId);
                l.setStandalone(false);
                linkRepository.save(l);
                folder.getLinks().add(l);
            });
        }

        folderRepository.save(folder);
    }

    @Override
    public Map<String, Object> getStandAloneFiles(PageRequest paging) {
        var files = fileRepository.findAllByIsStandalone(true, paging);
        return standaloneFileResponse(files);
    }

    @Override
    public Map<String, Object> getStandAloneImages(PageRequest paging) {
        var images = imageRepository.findAllByIsStandalone(true, paging);
        return standaloneImageResponse(images);
    }

    @Override
    public Map<String, Object> getStandAloneLinks(PageRequest paging) {
        var links = linkRepository.findAllByIsStandalone(true, paging);
        return standaloneLinkResponse(links);
    }

    private Map<String, Object> standaloneFileResponse(Page<File> files) {
        Map<String, Object> response = new HashMap<>();
        response.put("files", files.getContent());
        response.put("currentPage", files.getNumber());
        response.put("totalItems", files.getTotalElements());
        response.put("totalPages", files.getTotalPages());
        response.put("hasPrevious", files.hasPrevious());
        response.put("hasNext", files.hasNext());
        return response;
    }

    private Map<String, Object> standaloneImageResponse(Page<Image> images) {
        Map<String, Object> response = new HashMap<>();
        response.put("images", images.getContent());
        response.put("currentPage", images.getNumber());
        response.put("totalItems", images.getTotalElements());
        response.put("totalPages", images.getTotalPages());
        response.put("hasPrevious", images.hasPrevious());
        response.put("hasNext", images.hasNext());
        return response;
    }

    private Map<String, Object> standaloneLinkResponse(Page<Link> links) {
        Map<String, Object> response = new HashMap<>();
        response.put("links", links.getContent());
        response.put("currentPage", links.getNumber());
        response.put("totalItems", links.getTotalElements());
        response.put("totalPages", links.getTotalPages());
        response.put("hasPrevious", links.hasPrevious());
        response.put("hasNext", links.hasNext());
        return response;
    }

    private List<File> getFile(List<String> fileId) {
        return (List<File>) fileRepository.findAllById(fileId);
    }

    private List<Image> getImage(List<String> fileId) {
        return (List<Image>) imageRepository.findAllById(fileId);
    }

    private List<Link> getLinkById(List<String> fileId) {
        return (List<Link>) linkRepository.findAllById(fileId);
    }

    private Link duplicateLinkInner(Link l, String newFolderId) {
        return Link.builder()
                .link(l.getLink())
                .folderId(newFolderId)
                .createdTime(Instant.now())
                .fileName(l.getFileName())
                .fileType(l.getFileType())
                .fileDescription(l.getFileDescription())
                .build();
    }

    private Image duplicateImageInner(Image img, String newFolderId) {
        return Image.builder()
                .createdTime(new Date().getTime())
                .folderId(newFolderId)
                .fileName(img.getFileName())
                .fileType(img.getFileType())
                .fileSize(img.getFileSize())
                .fileDescription(img.getFileDescription())
                .build();
    }

    private File duplicateFileInner(File file, String folderId) {
        return File.builder()
                .id(UUID.randomUUID().toString())
                .fileName(file.getFileName())
                .fileSize(file.getFileSize())
                .createdTime(Instant.now())
                .fileDescription(file.getFileDescription())
                .keycloakId(file.getKeycloakId())
                .folderId(folderId)
                .build();
    }

}
