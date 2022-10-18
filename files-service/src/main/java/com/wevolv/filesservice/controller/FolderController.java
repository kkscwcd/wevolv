package com.wevolv.filesservice.controller;

import com.wevolv.filesservice.domain.GenericApiResponse;
import com.wevolv.filesservice.domain.dto.FolderDto;
import com.wevolv.filesservice.domain.dto.LinkDto;
import com.wevolv.filesservice.domain.model.Link;
import com.wevolv.filesservice.service.FolderService;
import com.wevolv.filesservice.util.TokenDecoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RequestMapping("/folder")
@RestController
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<GenericApiResponse> createFolder(@RequestBody FolderDto folderDto,
                                                           HttpServletRequest request) throws IOException {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var folder = folderService.createFolder(folderDto, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(folder)
                .message("Folder created.")
                .build());
    }

    @PostMapping(value = "/create/subfolder/{folderId}")
    public ResponseEntity<GenericApiResponse> createFolderInFolder(@PathVariable String folderId,
                                                                   @RequestBody FolderDto folderDto,
                                                           HttpServletRequest request) throws IOException {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var folder = folderService.createFolderInFolder(folderDto, folderId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(folder.get())
                .message("Folder created.")
                .build());
    }

    @GetMapping(value = "/get/{folderId}")
    public ResponseEntity<GenericApiResponse> getFolder(@PathVariable(name = "folderId") String folderId, HttpServletRequest request) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var folders = folderService.getFolder(folderId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(folders)
                .message(String.format("Folder with folderId: %s ", folderId))
                .build());
    }

    @GetMapping(value = "/get/all")
    public ResponseEntity<GenericApiResponse> getAllFolders(HttpServletRequest request,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "3") int size) {

        String jwt = request.getHeader("Authorization");
        TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size, Sort.by("timeOfCreation").descending());
        var folders = folderService.getAllFolders(paging);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(folders)
                .message("All folders")
                .build());
    }

    @PostMapping(value = "/upload/file/{folderId}")
    public ResponseEntity<GenericApiResponse> uploadFileToFolder(MultipartFile multipartFile,
                                                                 @PathVariable(name = "folderId") String folderId,
                                                                 HttpServletRequest request) throws IOException {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var file = folderService.uploadFileToFolder(multipartFile, keycloakId, folderId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(file)
                .message(String.format("File uploaded to folder %s.", folderId))
                .build());
    }

    @PostMapping(value = "/upload/image/{folderId}")
    public ResponseEntity<GenericApiResponse> uploadImageToFolder(MultipartFile multipartFile,
                                                                  @PathVariable(name = "folderId") String folderId,
                                                                  HttpServletRequest request) throws IOException {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var image = folderService.saveImageToFolder(multipartFile, keycloakId, folderId);
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(image)
                .message("Image uploaded to folder.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/upload/link/{folderId}")
    public ResponseEntity<GenericApiResponse> uploadFileLinkToFolder(@RequestBody LinkDto linkDto,
                                                                     @PathVariable(name = "folderId") String folderId,
                                                                     HttpServletRequest request) throws IOException {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        Link link = folderService.saveLinkToFolder(linkDto, keycloakId, folderId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(link)
                .message("Link uploaded to folder.")
                .build());
    }

    @DeleteMapping(value = "/delete/file")
    public ResponseEntity<GenericApiResponse> deleteFile(@RequestBody List<String> fileId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        folderService.deleteFile(fileId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("File deleted.")
                .build());
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<GenericApiResponse> deleteFolder(@RequestBody List<String> folderId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        folderService.deleteFolder(folderId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Folder deleted.")
                .build());
    }

    @GetMapping("/search/{folderName}")
    public ResponseEntity<GenericApiResponse> searchFoldersByName(@PathVariable(name = "folderName") String folderName,
                                                             HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var folder = folderService.searchFoldersByName(folderName, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(folder)
                .message("Found folders.")
                .build());
    }
}
