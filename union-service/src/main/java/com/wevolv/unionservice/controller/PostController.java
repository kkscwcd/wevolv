package com.wevolv.unionservice.controller;

import com.wevolv.unionservice.model.GenericApiResponse;
import com.wevolv.unionservice.model.Post;
import com.wevolv.unionservice.model.UserDataContext;
import com.wevolv.unionservice.model.dto.ObjectCreatedResponse;
import com.wevolv.unionservice.model.dto.ObjectDeletedResponse;
import com.wevolv.unionservice.model.dto.PostDto;
import com.wevolv.unionservice.model.dto.PostMarkedTrashedResponse;
import com.wevolv.unionservice.model.enums.PostSelector;
import com.wevolv.unionservice.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import static com.wevolv.unionservice.util.TokenDecoder.getUserIdFromToken;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/create/{topicId}")
    public ObjectCreatedResponse createPost(HttpServletRequest request, @PathVariable String topicId,
                                            @RequestBody PostDto postDto) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var udc = UserDataContext.create(keycloakId, jwt);
        var post = postService.createPost(udc, topicId, postDto);
        return new ObjectCreatedResponse(post.getId());
    }

    @PostMapping(value = "/create")
    public ObjectCreatedResponse createStandAlonePost(HttpServletRequest request, @RequestBody PostDto postDto) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var post = postService.createStandAlonePost(keycloakId, postDto);
        return new ObjectCreatedResponse(post.getId());
    }

    @PostMapping(value = "/update/{postId}")
    public Post updatePost(HttpServletRequest request, @PathVariable String postId, @RequestBody PostDto postDto) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return postService.updatePost(keycloakId, postId, postDto);
    }

    @DeleteMapping(value = "/delete/{postId}")
    public ObjectDeletedResponse deletePost(HttpServletRequest request,
                                            @PathVariable String postId) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        postService.deletePost(keycloakId, postId);
        return new ObjectDeletedResponse(true);
    }

    @PostMapping(value = "/trashed/{postId}")
    public PostMarkedTrashedResponse markPostAsTrashed(HttpServletRequest request,
                                                       @PathVariable String postId) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        postService.markPostAsTrashed(keycloakId, postId);
        return new PostMarkedTrashedResponse(true);
    }

    @GetMapping(value = "/{postId}")
    public Post getPostById(HttpServletRequest request, @PathVariable String postId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return postService.getPostById(keycloakId, postId);
    }

    @GetMapping(value = "/all/topic/{topicId}")
    public Map<String, Object> getPostByTopicId(HttpServletRequest request,
                                                               @PathVariable String topicId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "3") int size){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return postService.getAllPostsByTopicId(keycloakId, topicId, page, size);
    }

    @GetMapping(value = "/get/all")
    public ResponseEntity<GenericApiResponse> getAllPosts(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "3") int size,
                                                          @RequestParam PostSelector postType) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        GenericApiResponse apiResponse = GenericApiResponse.builder().build();
        Map<String, Object> post;

        if (postType.name().equals(PostSelector.PUBLISHED.name())) {
            var paging = PageRequest.of(page, size);
            post = postService.getPublishedPosts(keycloakId, paging);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(post);
            apiResponse.setMessage("All published posts");
        }

        if (postType.name().equals(PostSelector.TRASHED.name())) {
            var paging = PageRequest.of(page, size);
            post = postService.getTrashedPosts(keycloakId, paging);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(post);
            apiResponse.setMessage("All trashed posts");
        }

        if (postType.name().equals(PostSelector.DRAFT.name())) {
            var paging = PageRequest.of(page, size);
            post = postService.getDraftedPosts(keycloakId, paging);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(post);
            apiResponse.setMessage("All draft posts");
        }

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(value = "/get/all/union/providers")
    public Map<String, Object> getAllPosts(HttpServletRequest request,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "3") int size){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return postService.getAllPostsByProvider(keycloakId, paging);
    }

    @PostMapping("/add/image/{postId}")
    public ObjectCreatedResponse saveImagePost(MultipartFile multipartFile, @PathVariable String postId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var post = postService.saveImagePost(multipartFile, postId, keycloakId);
        return new ObjectCreatedResponse(post.getId());
    }

    @PostMapping("/delete/image/{postId}")
    public ObjectDeletedResponse deleteImagePost(MultipartFile multipartFile, @PathVariable String postId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var post = postService.deleteImagePost(multipartFile, postId, keycloakId);
        return new ObjectDeletedResponse(true);
    }
}
