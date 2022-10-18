package com.wevolv.unionservice.controller;

import com.wevolv.unionservice.model.*;
import com.wevolv.unionservice.model.dto.*;
import com.wevolv.unionservice.service.UnionService;
import com.wevolv.unionservice.util.TokenDecoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.wevolv.unionservice.util.TokenDecoder.getUserIdFromToken;

@RestController
public class UnionController {

    private final UnionService unionService;

    public UnionController(UnionService unionService) {
        this.unionService = unionService;
    }

    @PostMapping(value = "/create/about")
    public AboutResponse createAbout(HttpServletRequest request, @RequestBody AboutDto aboutDto) {
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var about = unionService.createAbout(aboutDto);
        return new AboutResponse(about.getId());
    }

    @PostMapping(value = "/update/about/{aboutId}")
    public About updateAbout(HttpServletRequest request, @RequestBody AboutDto aboutDto, @PathVariable String aboutId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        return unionService.updateAbout(aboutDto, aboutId);
    }

    @GetMapping(value = "/about/{unionProvider}")
    public About getAboutById(HttpServletRequest request, @RequestBody UnionProviderRequest unionProvider){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        return unionService.getAboutByUnionProvider(unionProvider.getUnionProvider());
    }

    @PostMapping(value = "/create/faq/category")
    public FaqCategoryResponse createFaqCategory(HttpServletRequest request, @RequestBody FaqCategoryDto faqDto) {
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var category = unionService.createFaqCategory(faqDto);
        return new FaqCategoryResponse(category.getId());
    }

    @PostMapping(value = "/update/faq/category/{categoryId}")
    public FaqCategory updateFaqCategory(HttpServletRequest request, @PathVariable String categoryId, @RequestBody FaqCategoryDto faqDto) {
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        return unionService.updateFaqCategory(faqDto, categoryId);
    }

    @DeleteMapping(value = "/delete/faq/category/{categoryId}")
    public void deleteFaqCategory(HttpServletRequest request, @PathVariable String categoryId) {
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        unionService.deleteFaqCategory(categoryId);
    }

    @GetMapping(value = "/faq/category/{categoryId}")
    public Map<String, Object> allFaqForCategory(HttpServletRequest request, @PathVariable String categoryId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "3") int size) {
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return unionService.allFaqForCategory(categoryId, paging);
    }

    @PostMapping(value = "/create/faq")
    public FaqResponse createFaq(HttpServletRequest request, @RequestBody FaqDto faqDto) {
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var faq = unionService.createFaq(faqDto);
        return new FaqResponse(faq.getId());
    }

    @DeleteMapping(value = "/delete/faq/{faqId}")
    public void deleteFaq(HttpServletRequest request, @PathVariable String faqId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        unionService.deleteFaq(faqId);
    }

    @PostMapping(value = "/update/faq/{faqId}")
    public Faq updateFaq(HttpServletRequest request, @RequestBody FaqDto faqDto, @PathVariable String faqId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        return unionService.updateFaq(faqDto, faqId);
    }

    @GetMapping(value = "/faq/{faqId}")
    public Faq getFaqById(HttpServletRequest request, @PathVariable String faqId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        return unionService.getById(faqId);
    }

    @PostMapping(value = "/save/podcast")
    public ObjectCreatedResponse savePodcast(@RequestBody PodcastDto podcastDto, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var podcast = unionService.addPodcast(keycloakId, podcastDto);
        return new ObjectCreatedResponse(podcast.getId());

    }

    @PostMapping(value = "/update/podcast/{podcastId}")
    public Podcast updatePodcast(@PathVariable String podcastId, @RequestBody PodcastDto podcastDto,
                                 HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        return unionService.updatePodcast(keycloakId, podcastDto, podcastId);
    }

    @GetMapping(value = "/podcast/{podcastId}")
    public Podcast getPodcast(@PathVariable String podcastId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        return unionService.getPodcast(keycloakId, podcastId);
    }

    @GetMapping(value = "/podcast/all")
    public Map<String, Object> getAllPodcast(HttpServletRequest request,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "3") int size) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return unionService.getAllPodcast(keycloakId, paging);
    }

    @DeleteMapping(value = "/delete/podcast/{podcastId}")
    public ObjectDeletedResponse deletePodcast(@PathVariable String podcastId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        unionService.deletePodcast(keycloakId, podcastId);
        return new ObjectDeletedResponse(true);

    }

    @PostMapping(value = "/upload/file")
    public ObjectCreatedResponse uploadFile(MultipartFile multipartFile,
                                                         HttpServletRequest request) throws IOException {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        String userName= TokenDecoder.getUserGivenAndFamilyNameFromToken(jwt);

        var file = unionService.upload(multipartFile, keycloakId,userName);
        return new ObjectCreatedResponse(file.getId());
    }

    @PostMapping(value = "/review/info")
    public ObjectCreatedResponse contractReviewInfo(@RequestBody ContractReviewInfoDto contractInfoDto,
                                                                 HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var folder = unionService.contractReviewInfo(contractInfoDto, keycloakId);
        return new ObjectCreatedResponse(folder.getId());
    }

    @GetMapping(value = "/get/folder/{folderId}")
    public Folder getUserFolder(@PathVariable String folderId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return unionService.getUserFolder(folderId);
        //return new ObjectCreatedResponse();
    }

    @GetMapping(value = "/get/all/files/{folderId}")
    public List<File> getAllFolderFiles(@PathVariable String folderId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return unionService.getAllFolderFiles(folderId);
        //return new ObjectCreatedResponse();
    }

    @GetMapping(value = "/get/all/folders")
    public Map<String, Object> getAllFolders(HttpServletRequest request,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "3") int size) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return unionService.getAllFolders(paging);
        //return new ObjectCreatedResponse();
    }


}
