package com.wevolv.unionservice.service;

import com.wevolv.unionservice.model.Post;
import com.wevolv.unionservice.model.UserDataContext;
import com.wevolv.unionservice.model.dto.PostDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PostService {

    Post createPost(UserDataContext userDataContext, String topicId, PostDto postDto);

    Post createStandAlonePost(String keycloakId, PostDto postDto);

    Post updatePost(String keycloakId, String postId, PostDto postDto);

    void deletePost(String keycloakId, String postId);

    Map<String, Object> getPublishedPosts(String keycloakId, PageRequest paging);

    Map<String, Object> getDraftedPosts(String keycloakId, PageRequest paging);

    Map<String, Object> getTrashedPosts(String keycloakId, PageRequest paging);

    void markPostAsTrashed(String keycloakId, String postId);

    Post getPostById(String keycloakId, String postId);

    Map<String, Object> getAllPostsByTopicId(String keycloakId, String topicId, int page, int size);

    Map<String, Object> getAllPostsByProvider(String keycloakId, PageRequest page);

    Post saveImagePost(MultipartFile multipartFile, String postId, String keycloakId);

    Post deleteImagePost(MultipartFile multipartFile, String postId, String keycloakId);
}
