package com.wevolv.filesservice.service.impl;

import com.wevolv.filesservice.domain.dto.FolderDto;
import com.wevolv.filesservice.domain.dto.LinkDto;
import com.wevolv.filesservice.domain.model.File;
import com.wevolv.filesservice.domain.model.Folder;
import com.wevolv.filesservice.domain.model.Image;
import com.wevolv.filesservice.domain.model.Link;
import com.wevolv.filesservice.exception.AlreadyExistsException;
import com.wevolv.filesservice.exception.NotFoundException;
import com.wevolv.filesservice.repository.FileRepository;
import com.wevolv.filesservice.repository.FolderRepository;
import com.wevolv.filesservice.repository.ImageRepository;
import com.wevolv.filesservice.repository.LinkRepository;
import com.wevolv.filesservice.service.FolderService;
import com.wevolv.filesservice.util.CloudinaryHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final CloudinaryHelper cloudinaryHelper;
    private final ImageRepository imageRepository;
    private final LinkRepository linkRepository;

    public FolderServiceImpl(FolderRepository folderRepository, FileRepository fileRepository, CloudinaryHelper cloudinaryHelper, ImageRepository imageRepository, LinkRepository linkRepository) {
        this.folderRepository = folderRepository;
        this.fileRepository = fileRepository;
        this.cloudinaryHelper = cloudinaryHelper;
        this.imageRepository = imageRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public Folder createFolder(FolderDto folderDto, String keycloakId) {
        Folder folder = Folder.builder()
                .keycloakId(keycloakId)
                .name(folderDto.getName())
                .createdTime(new Date().getTime())
                .files(new ArrayList<>())
                .images(new ArrayList<>())
                .links(new ArrayList<>())
                .folders(new ArrayList<>())
                .build();
        folderRepository.save(folder);

        return folder;
    }

    @Override
    public Optional<Folder> createFolderInFolder(FolderDto folderDto, String folderId, String keycloakId) {
        Folder folder = Folder.builder()
                .keycloakId(keycloakId)
                .name(folderDto.getName())
                .createdTime(new Date().getTime())
                .files(new ArrayList<>())
                .images(new ArrayList<>())
                .links(new ArrayList<>())
                .folders(new ArrayList<>())
                .build();
        folderRepository.save(folder);

       var parentFolder = folderRepository.findById(folderId);
        parentFolder.ifPresent(pf -> {
            boolean ifExist = pf.getFolders().stream()
                    .anyMatch(element -> element.getName().equalsIgnoreCase(folderDto.getName()));
            if (ifExist) {
                throw new AlreadyExistsException("Folder with this name already exist.");
            }
            pf.getFolders().add(folderRepository.save(folder));
            folderRepository.save(pf);
        });

        return parentFolder;
    }

    @Override
    public File uploadFileToFolder(MultipartFile multipartFile, String keycloakId, String folderId) throws IOException {
        var folder = getFolder(folderId);
        File file;
        try {
            file = File.builder()
                    .createdTime(Instant.now())
                    .folderId(folderId)
                    .fileName(multipartFile.getOriginalFilename())
                    .fileType(multipartFile.getContentType())
                    .fileSize(multipartFile.getSize())
                    .fileDescription("")
                    .file(multipartFile.getBytes())
                    .build();
            folder.getFiles().add(fileRepository.save(file));
            folderRepository.save(folder);
        } catch (Exception e) {
            throw new IOException("Error while saving PDF to database.");
        }
        return file;
    }

    @Override
    public Image saveImageToFolder(MultipartFile multipartFile, String keycloakId, String folderId) throws IOException {
        var folder = getFolder(folderId);
        Image image;

        try {
            image = Image.builder()
                    .id(UUID.randomUUID().toString())
                    .createdTime(new Date().getTime())
                    .folderId(folderId)
                    .fileName(multipartFile.getOriginalFilename())
                    .fileType(multipartFile.getContentType())
                    .fileSize(multipartFile.getSize())
                    .fileDescription("")
                    .build();
            var sf = cloudinaryHelper.uploadOnCloudinary(multipartFile);
            image.setLink(sf.getUrl());
            image.setPublic_id(sf.getPublic_id());
            var savedImage = imageRepository.save(image);

            folder.getImages().add(savedImage);
            folderRepository.save(folder);

        } catch (Exception e) {
            throw new IOException("Error while saving image to database.");
        }
        return image;
    }

    @Override
    public Link saveLinkToFolder(LinkDto linkDto, String keycloakId, String folderId) throws IOException {
        var folder = getFolder(folderId);
        Link link;

        try {
            link = Link.builder()
                    .link(linkDto.getLink())
                    .folderId(folderId)
                    .createdTime(Instant.now())
                    .fileName("")
                    .fileType("link")
                    .fileDescription("")
                    .build();

            link = linkRepository.save(link);
            folder.getLinks().add(link);
            folderRepository.save(folder);

        } catch (Exception e) {
            throw new IOException("Error while saving PDF to database.");
        }
        return link;
    }

    @Override
    public void deleteFolder(List<String> folderId, String keycloakId) {
        var folders = folderRepository.findAllById(folderId);
        folders.forEach(f -> {
            var file = f.getFiles();
            var image = f.getImages();
            var fileLink = f.getLinks();
            deleteFilesImagesAndLinks(file, image, fileLink);
            folderRepository.delete(f);
        });
    }

    @Override
    public void deleteFile(List<String> fileId, String keycloakId) {
        var file = getFile(fileId);
        var image = getImage(fileId);
        var link = getLinkById(fileId);

        if(file.size() != 0){
            file.forEach(fi -> {
                var folder = folderRepository.findById(fi.getFolderId());
                folder.ifPresent(fo -> {
                    fo.getFiles().remove(fi);
                    folderRepository.save(fo);
                });
                fileRepository.delete(fi);
            });
        }

        if(image.size() != 0){
            image.forEach(img -> {
                var folder = folderRepository.findById(img.getFolderId());
                folder.ifPresent(fo -> {
                    fo.getImages().remove(img);
                    folderRepository.save(fo);
                });
                try {
                    cloudinaryHelper.deleteImageFromCloudinary(img.getPublic_id());
                } catch (Exception e) {
                    throw new IllegalStateException("Error while deleting image from cloudinary.");
                }
                imageRepository.delete(img);
            });
        }

        if(link.size() != 0){
            link.forEach(l -> {
                var folder = folderRepository.findById(l.getFolderId());
                folder.ifPresent(fo -> {
                    fo.getLinks().remove(l);
                    folderRepository.save(fo);
                });
                linkRepository.delete(l);
            });
        }
    }

    @Override
    public List<Folder> searchFoldersByName(String folderName, String keycloakId) {
        return folderRepository.findAllByName(folderName);
    }

    @Override
    public Map<String, Object> getAllFolders(PageRequest paging) {
        var folders = folderRepository.findAll(paging);
        return populateMapResponse(folders);
    }

    private Map<String, Object> populateMapResponse(Page<Folder> folders) {
        Map<String, Object> response = new HashMap<>();
        response.put("folders", folders.getContent());
        response.put("currentPage", folders.getNumber());
        response.put("totalItems", folders.getTotalElements());
        response.put("totalPages", folders.getTotalPages());
        response.put("hasPrevious", folders.hasPrevious());
        response.put("hasNext", folders.hasNext());
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

    void deleteFilesImagesAndLinks(List<File> files, List<Image> images, List<Link> links) {
        if (!images.isEmpty()) {
            images.forEach(i -> {
                    try {
                        cloudinaryHelper.deleteImageFromCloudinary(i.getLink());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageRepository.delete(i);
            });
        }
        if (!files.isEmpty()) {
            fileRepository.deleteAll(files);
        }
        if (!links.isEmpty()) {
            linkRepository.deleteAll(links);
        }
    }

    public Folder getFolder(String folderId) {
        return folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundException(String.format("Folder with folderId %s does not exist", folderId)));
    }
}
