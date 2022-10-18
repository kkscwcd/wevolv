package com.wevolv.unionservice.service.impl;

import com.wevolv.unionservice.exceptions.NotFoundException;
import com.wevolv.unionservice.integration.forum.service.ForumService;
import com.wevolv.unionservice.integration.profile.service.ProfileService;
import com.wevolv.unionservice.model.*;
import com.wevolv.unionservice.model.dto.PostDto;
import com.wevolv.unionservice.model.dto.UploadedImageDto;
import com.wevolv.unionservice.repository.*;
import com.wevolv.unionservice.service.PostService;
import com.wevolv.unionservice.util.CloudinaryHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ProfileService profileService;
    private final TopicRepository topicRepository;
    private final TagsRepository tagsRepository;
    private final ForumService forumService;
    private final DraftPostRepository draftPostRepository;
    private final PublishedPostsRepository publishedPostsRepository;
    private final TrashedPostRepository trashedPostRepository;
    private final CloudinaryHelper cloudinaryHelper;
    private final ImageRepository imageRepository;


    public PostServiceImpl(PostRepository postRepository, ProfileService profileService, TopicRepository topicRepository, TagsRepository tagsRepository, ForumService forumService, DraftPostRepository draftPostRepository, PublishedPostsRepository publishedPostsRepository, TrashedPostRepository trashedPostRepository, CloudinaryHelper cloudinaryHelper, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.profileService = profileService;
        this.topicRepository = topicRepository;
        this.tagsRepository = tagsRepository;
        this.forumService = forumService;
        this.draftPostRepository = draftPostRepository;
        this.publishedPostsRepository = publishedPostsRepository;
        this.trashedPostRepository = trashedPostRepository;
        this.cloudinaryHelper = cloudinaryHelper;
        this.imageRepository = imageRepository;
    }

    @Override
    public Post createPost(UserDataContext udc, String topicId, PostDto postDto) {
        var psi = getProfileShortInfo(udc.keycloakId);
        var existingTopic = getExistingTopic(topicId);
        var author = getAuthor(psi, udc.keycloakId);
        List<Tags> newTagList = new ArrayList<>();

        if(postDto.getTags() == null){
            postDto.setTags(new ArrayList<>());
        }

        var newPost = populateTopicRelatedPost(topicId, postDto, author);

        if(existingTopic.getIsPublishedToForum()){
            forumService.addPostToPublishedTopic(udc, topicId, postDto);
        }

        var savedPost = postRepository.save(newPost);
        postDto.getTags().forEach(t -> {
            var newTag = Tags.builder()
                    .id(UUID.randomUUID().toString())
                    .postId(savedPost.getId())
                    .tagName(t.getTagName())
                    .topicId(topicId)
                    .build();
            newTagList.add(newTag);
        });

        savedPost.setTags(newTagList);
        var existingTopicPosts = postRepository.findAllByTopicId(topicId);
        existingTopic.setNumberOfPosts(existingTopicPosts.size());
        topicRepository.save(existingTopic);
        tagsRepository.saveAll(newTagList);

        if(postDto.getIsPublished() != null && postDto.getIsPublished()){
            PublishedPosts publishedPost = PublishedPosts.builder()
                    .id(UUID.randomUUID().toString())
                    .post(newPost)
                    .build();
            publishedPostsRepository.save(publishedPost);
        }

        if(postDto.getIsDraft() != null && postDto.getIsDraft()){
            DraftPost draftPost = DraftPost.builder()
                    .id(UUID.randomUUID().toString())
                    .post(newPost)
                    .build();
            draftPostRepository.save(draftPost);
        }

        return postRepository.save(newPost);
    }

    private Post populateTopicRelatedPost(String topicId, PostDto postDto, Author author) {
        return Post.builder()
                .id(UUID.randomUUID().toString())
                .topicId(topicId)
                .title(postDto.getTitle())
                .text(postDto.getText())
                .timePosted(Instant.now())
                .author(author)
                .isPublished(postDto.getIsPublished())
                .isTrashed(postDto.getIsTrashed())
                .isDraft(postDto.getIsDraft())
                .isPublishedToForum(postDto.getIsPublishedToForum())
                .build();
    }

    @Override
    public Post createStandAlonePost(String keycloakId, PostDto postDto) {
        var psi = getProfileShortInfo(keycloakId);
        var author = getAuthor(psi, keycloakId);
        List<Tags> newTagList = new ArrayList<>();

        if(postDto.getTags() == null){
            postDto.setTags(new ArrayList<>());
        }

        var newPost = populateNewPost(postDto, author);
        var savedPost = postRepository.save(newPost);

        postDto.getTags().forEach(t -> {
            var newTag = Tags.builder()
                    .id(UUID.randomUUID().toString())
                    .postId(savedPost.getId())
                    .tagName(t.getTagName())
                    .build();
            newTagList.add(newTag);
        });

        savedPost.setTags(newTagList);
        tagsRepository.saveAll(newTagList);
        return postRepository.save(savedPost);
    }

    private Post populateNewPost(PostDto postDto, Author author) {
        return Post.builder()
                .id(UUID.randomUUID().toString())
                .title(postDto.getTitle())
                .text(postDto.getText())
                .timePosted(Instant.now())
                .author(author)
                .timePosted(Instant.now())
                .build();
    }

    @Override
    public Post updatePost(String keycloakId, String postId, PostDto postDto) {
        var existingPost = getExistingPost(postId);

        if(existingPost.getTags() == null){
            existingPost.setTags(new ArrayList<>());
        }

        List<Tags> addTags = postDto.getTags().stream().filter(o1 -> existingPost.getTags().stream()
                        .noneMatch(o2 -> o2.getTagName().name().equals(o1.getTagName().name())))
                .collect(Collectors.toList());

        List<Tags> removeTags = existingPost.getTags().stream().filter(o1 -> postDto.getTags().stream()
                        .noneMatch(o2 -> o2.getTagName().name().equals(o1.getTagName().name())))
                .collect(Collectors.toList());

        //remove tags from post
        existingPost.getTags().removeAll(removeTags);
        //remove tags from parent topic
        //removeTagsFromParentTopic(postId, existingTopic, removeTags);
        tagsRepository.deleteAll(removeTags);

        addTags.forEach(t -> {
            t.setPostId(postId);
        });

        //add tags to post
        existingPost.getTags().addAll(addTags);
        //add tags to parent topic
        // addTagsToParentTopic(postId, existingTopic, addTags);
        tagsRepository.saveAll(addTags);

        //update existing post
        existingPost.setTitle(postDto.getTitle());
        existingPost.setText(postDto.getText());
        existingPost.setIsPublished(postDto.getIsPublished());
        existingPost.setIsDraft(postDto.getIsDraft());
        var post = postRepository.save(existingPost);

        if(postDto.getIsPublished() != null && postDto.getIsPublished()){
            PublishedPosts publishedPost = PublishedPosts.builder()
                    .id(UUID.randomUUID().toString())
                    .post(post)
                    .build();
            publishedPostsRepository.save(publishedPost);
        }

        if(postDto.getIsDraft() != null && postDto.getIsDraft()){
            DraftPost draftPost = DraftPost.builder()
                    .id(UUID.randomUUID().toString())
                    .post(post)
                    .build();
            draftPostRepository.save(draftPost);
        }
        //topicRepository.save(existingTopic);

        return existingPost;
    }

    @Override
    public void deletePost(String keycloakId, String postId) {
        //var post = postRepository.findById(postId);
        postRepository.deleteById(postId);
    }

    @Override
    public Map<String, Object> getPublishedPosts(String keycloakId, PageRequest paging) {
        var posts = publishedPostsRepository.findAll(paging);
        return populateMapPublishedPosts(posts);
    }

    @Override
    public Map<String, Object> getDraftedPosts(String keycloakId, PageRequest paging) {
        var posts = draftPostRepository.findAll(paging);
        return populateMapDraftedPosts(posts);
    }

    @Override
    public Map<String, Object> getTrashedPosts(String keycloakId, PageRequest paging) {
        var posts = trashedPostRepository.findAll(paging);
        return populateMapTrashedPosts(posts);
    }

    @Override
    public void markPostAsTrashed(String keycloakId, String postId) {
        var post = getExistingPost(postId);
        post.setIsTrashed(true);
        post.setIsDraft(false);
        post.setIsPublished(false);
        TrashedPost tp = TrashedPost.builder()
                .id(UUID.randomUUID().toString())
                .post(post)
                .build();
        postRepository.save(post);
        trashedPostRepository.save(tp);
    }

    @Override
    public Post getPostById(String keycloakId, String postId) {
        return getExistingPost(postId);
    }

    @Override
    public Map<String, Object> getAllPostsByTopicId(String keycloakId, String topicId, int page, int size) {
        var paging = PageRequest.of(page, size);
        var pagePost = postRepository.findAllByTopicId(topicId, paging);
        return populateMapResponse(pagePost);
    }

    @Override
    public Map<String, Object> getAllPostsByProvider(String keycloakId, PageRequest paging) {
        var pagePost = postRepository.findAll(paging);
        return populateMapResponse(pagePost);
    }

    @Override
    public Post saveImagePost(MultipartFile multipartFile, String postId, String keycloakId) {
        var post = getExistingPost(postId);
        var image = Image.builder().build();
        UploadedImageDto newImageLink;
        try {
            newImageLink = cloudinaryHelper.uploadOnCloudinary(multipartFile);
            image.setId(UUID.randomUUID().toString());
            image.setPostId(postId);
            image.setCreatedTime(new Date().getTime());
            image.setPublic_id(newImageLink.getPublic_id());
            image.setLink(newImageLink.getUrl());
            imageRepository.save(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        post.setImage(image);
        return postRepository.save(post);

    }

    @Override
    public Post deleteImagePost(MultipartFile multipartFile, String postId, String keycloakId) {
        var post = getExistingPost(postId);
        try {
            cloudinaryHelper.deleteImageFromCloudinary(post.getImage().getPublic_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
        post.setImage(null);
        imageRepository.deleteById(post.getImage().getId());
        return postRepository.save(post);
    }

    private Map<String, Object> populateMapResponse(Page<Post> pagePost) {
        Map<String, Object> response = new HashMap<>();
        response.put("posts", pagePost.getContent());
        response.put("currentPage", pagePost.getNumber());
        response.put("totalItems", pagePost.getTotalElements());
        response.put("totalPages", pagePost.getTotalPages());
        response.put("hasPrevious", pagePost.hasPrevious());
        response.put("hasNext", pagePost.hasNext());
        return response;
    }

    private Post getExistingPost(String postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(String.format("Post with postId %s does not exist in database", postId)));
    }

    private Map<String, Object> populateMapTrashedPosts(Page<TrashedPost> posts) {
        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        response.put("hasPrevious", posts.hasPrevious());
        response.put("hasNext", posts.hasNext());
        return response;
    }


    private Map<String, Object> populateMapDraftedPosts(Page<DraftPost> posts) {
        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        response.put("hasPrevious", posts.hasPrevious());
        response.put("hasNext", posts.hasNext());
        return response;
    }

    private Map<String, Object> populateMapPublishedPosts(Page<PublishedPosts> posts) {
        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        response.put("hasPrevious", posts.hasPrevious());
        response.put("hasNext", posts.hasNext());
        return response;
    }

    private ProfileShortInfo getProfileShortInfo(String keycloakId) {
        return profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("User with keycloakId %s doesn't exist", keycloakId)));
    }

    private Topic getExistingTopic(String topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(String.format("Topic with topicId %s does not exist in database!", topicId)));
    }

    private Author getAuthor(ProfileShortInfo psi, String keycloakId) {
        return new Author(UUID.randomUUID().toString(), keycloakId, psi.getFirstName(), psi.getLastName(), psi.getImage());
    }
}
