package com.wevolv.filesservice.controller;

import com.wevolv.filesservice.domain.GenericApiResponse;
import com.wevolv.filesservice.domain.dto.LinkDto;
import com.wevolv.filesservice.service.FileService;
import com.wevolv.filesservice.util.TokenDecoder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload/file")
    public ResponseEntity<GenericApiResponse> uploadFile(@RequestBody MultipartFile multipartFile,
                                                         HttpServletRequest request) throws IOException {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var f = fileService.uploadFile(multipartFile, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(f)
                .message("File uploaded.")
                .build());
    }

    @GetMapping(value = "/download/file/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileId) {
        var myFile = fileService.getFileById(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(myFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + myFile.getFileName() + "\"")
                .body(new ByteArrayResource(myFile.getFile()));
    }

    @PostMapping(value = "/upload/image")
    public ResponseEntity<GenericApiResponse> uploadImage(MultipartFile multipartFile,
                                                                  HttpServletRequest request) throws IOException {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var image = fileService.uploadImage(multipartFile, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(image)
                .message("Image uploaded.")
                .build());
    }

    @GetMapping(value = "/get/image/{imageId}")
    public ResponseEntity<GenericApiResponse> getImageById(@PathVariable String imageId,
                                                          HttpServletRequest request) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var image = fileService.getImageById(imageId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(image)
                .message(String.format("Image by imageId: %s.", imageId))
                .build());
    }

    @GetMapping(value = "/get/all/standalone/files")
    public ResponseEntity<GenericApiResponse> getStandAloneFiles(HttpServletRequest request,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "3") int size) {

        String jwt = request.getHeader("Authorization");
        TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size, Sort.by("timeOfCreation").descending());
        var standAloneFiles = fileService.getStandAloneFiles(paging);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(standAloneFiles)
                .message("Standalone files")
                .build());
    }

    @GetMapping(value = "/get/all/standalone/images")
    public ResponseEntity<GenericApiResponse> getStandAloneImages(HttpServletRequest request,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "3") int size) {

        String jwt = request.getHeader("Authorization");
        TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size, Sort.by("timeOfCreation").descending());
        var standAloneImages = fileService.getStandAloneImages(paging);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(standAloneImages)
                .message("Standalone images")
                .build());
    }

    @GetMapping(value = "/get/all/standalone/links")
    public ResponseEntity<GenericApiResponse> getStandAloneLinks(HttpServletRequest request,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "3") int size) {

        String jwt = request.getHeader("Authorization");
        TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size, Sort.by("timeOfCreation").descending());
        var standAloneLinks = fileService.getStandAloneLinks(paging);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(standAloneLinks)
                .message("Standalone links")
                .build());
    }

    @PostMapping(value = "/upload/link")
    public ResponseEntity<GenericApiResponse> uploadLink(@RequestBody LinkDto linkDto,
                                                                     HttpServletRequest request) throws IOException {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var link = fileService.uploadLink(linkDto, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(link)
                .message("Link uploaded to folder.")
                .build());
    }

    @PostMapping(value = "/duplicate/file/{newFolderId}")
    public ResponseEntity<GenericApiResponse> duplicateFile(@PathVariable String newFolderId,
                                                            @RequestBody List<String> fileId,
                                                            HttpServletRequest request) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        fileService.duplicateFile(newFolderId, fileId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(String.format("Files copied to folder %s.", newFolderId))
                .build());
    }

    @PostMapping(value = "/move/file/{currentFolderId}/{newFolderId}")
    public ResponseEntity<GenericApiResponse> moveFile(@PathVariable String newFolderId,
                                                       @PathVariable String currentFolderId,
                                                       @RequestBody List<String> fileId,
                                                       HttpServletRequest request) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        fileService.moveFile(newFolderId, currentFolderId, fileId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(String.format("Files moved to folder %s.", newFolderId))
                .build());
    }

    @PostMapping(value = "/move/file/{folderId}")
    public ResponseEntity<GenericApiResponse> moveFile(@PathVariable String folderId,
                                                       @RequestBody List<String> fileId,
                                                       HttpServletRequest request) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        fileService.moveFileToFolder(folderId, fileId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(String.format("Files moved to folder %s.", folderId))
                .build());
    }


    @DeleteMapping(value = "/delete/file")
    public ResponseEntity<GenericApiResponse> deleteFile(@RequestBody List<String> fileId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        fileService.deleteFile(fileId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("File deleted.")
                .build());
    }

    @GetMapping("/search/{fileName}")
    public ResponseEntity<GenericApiResponse> searchFoldersByName(@PathVariable(name = "fileName") String fileName,
                                                                  HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var file = fileService.searchFilesByName(fileName, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(file)
                .message("File folders.")
                .build());
    }

}
